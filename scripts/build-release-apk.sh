#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
if [ -f "$ROOT/env.project.sh" ]; then
  . "$ROOT/env.project.sh"
fi
. "$ROOT/scripts/version-env.sh"
. "$ROOT/scripts/android-dependency-env.sh"
ANDROID_HOME="${ANDROID_HOME:-$HOME/AndroidDev/sdk}"
ANDROID_PLATFORM="${ANDROID_PLATFORM:-android-33}"
ANDROID_BUILD_TOOLS="${ANDROID_BUILD_TOOLS:-35.0.0}"
ANDROID_JAR="$ANDROID_HOME/platforms/$ANDROID_PLATFORM/android.jar"
BUILD_TOOLS="$ANDROID_HOME/build-tools/$ANDROID_BUILD_TOOLS"

AAPT2="${AAPT2:-$BUILD_TOOLS/aapt2}"
D8="${D8:-$BUILD_TOOLS/d8}"
ZIPALIGN="${ZIPALIGN:-$BUILD_TOOLS/zipalign}"
APKSIGNER="${APKSIGNER:-$BUILD_TOOLS/apksigner}"
ASSETS_DIR="$ROOT/src/main/assets"
AAPT_ASSETS_ARGS=""
if [ -d "$ASSETS_DIR" ]; then
  AAPT_ASSETS_ARGS="-A $ASSETS_DIR"
fi

: "${MDLITE_RELEASE_KEYSTORE:?Set MDLITE_RELEASE_KEYSTORE to the production keystore path.}"
: "${MDLITE_RELEASE_KEY_ALIAS:?Set MDLITE_RELEASE_KEY_ALIAS to the production key alias.}"
: "${MDLITE_RELEASE_STORE_PASS:?Set MDLITE_RELEASE_STORE_PASS for apksigner.}"
: "${MDLITE_RELEASE_KEY_PASS:?Set MDLITE_RELEASE_KEY_PASS for apksigner.}"

if [ ! -f "$MDLITE_RELEASE_KEYSTORE" ]; then
  echo "Missing release keystore: $MDLITE_RELEASE_KEYSTORE" >&2
  exit 1
fi

BUILD="$ROOT/build"
RELEASE_BUILD="$BUILD/release"
OUT_UNSIGNED="$RELEASE_BUILD/app-release-unsigned.apk"
OUT_ALIGNED="$RELEASE_BUILD/app-release-aligned.apk"
OUT_SIGNED="${MDLITE_RELEASE_APK:-$RELEASE_BUILD/mdlite-reader-$VERSION_NAME-release.apk}"
MANIFEST="$BUILD/AndroidManifest.release.xml"

rm -rf "$BUILD"
mkdir -p "$BUILD/compiled" "$BUILD/generated" "$BUILD/classes" "$BUILD/dex" "$RELEASE_BUILD"
sh "$ROOT/scripts/version-apply-manifest.sh" "$ROOT/src/main/AndroidManifest.xml" "$MANIFEST"
if [ "${MDLITE_RELEASE_ENABLE_PLAY_BILLING:-false}" = "true" ]; then
  sh "$ROOT/scripts/apply-billing-manifest.sh" "$MANIFEST"
fi
mkdir -p "$BUILD/generated/io/github/yosk/mdlite/infrastructure"
cat > "$BUILD/generated/io/github/yosk/mdlite/infrastructure/BuildConfig.java" <<EOF
package io.github.yosk.mdlite.infrastructure;

public final class BuildConfig {
    public static final boolean PRO_FEATURES_ENABLED = ${MDLITE_RELEASE_PRO_FEATURES:-false};
    public static final boolean PLAY_BILLING_ENABLED = ${MDLITE_RELEASE_ENABLE_PLAY_BILLING:-false};

    private BuildConfig() {
    }
}
EOF
find "$ROOT/src/main/java" -name "*.java" > "$BUILD/main-sources.txt"
if [ "${MDLITE_RELEASE_ENABLE_PLAY_BILLING:-false}" = "true" ] && [ -d "$ROOT/src/billing/java" ]; then
  find "$ROOT/src/billing/java" -name "*.java" >> "$BUILD/main-sources.txt"
fi
"$AAPT2" compile --dir "$ROOT/src/main/res" -o "$BUILD/compiled/resources.zip"
"$AAPT2" link \
  -I "$ANDROID_JAR" \
  --manifest "$MANIFEST" \
  --java "$BUILD/generated" \
  $AAPT_ASSETS_ARGS \
  -o "$OUT_UNSIGNED" \
  "$BUILD/compiled/resources.zip"

# Collect generated sources AFTER aapt2 link: it writes R.java here, so any
# main source that references R (e.g. vector icon drawables, #73) needs the
# generated tree on the javac source list at compile time. Mirrors build.sh.
find "$BUILD/generated" -name "*.java" >> "$BUILD/main-sources.txt"

javac \
  -source 8 \
  -target 8 \
  -parameters \
  -bootclasspath "$ANDROID_JAR" \
  -classpath "$ANDROID_JAR${ANDROID_DEPENDENCY_CLASSPATH:+:$ANDROID_DEPENDENCY_CLASSPATH}" \
  -d "$BUILD/classes" \
  @"$BUILD/main-sources.txt"

"$D8" \
  --lib "$ANDROID_JAR" \
  --output "$BUILD/dex" \
  $(find "$BUILD/classes" -name "*.class") \
  $ANDROID_DEPENDENCY_D8_INPUTS

(cd "$BUILD/dex" && zip -q -D "$OUT_UNSIGNED" classes.dex)
"$ZIPALIGN" -f 4 "$OUT_UNSIGNED" "$OUT_ALIGNED"

"$APKSIGNER" sign \
  --ks "$MDLITE_RELEASE_KEYSTORE" \
  --ks-key-alias "$MDLITE_RELEASE_KEY_ALIAS" \
  --ks-pass env:MDLITE_RELEASE_STORE_PASS \
  --key-pass env:MDLITE_RELEASE_KEY_PASS \
  --out "$OUT_SIGNED" \
  "$OUT_ALIGNED"

"$APKSIGNER" verify "$OUT_SIGNED"
sh "$ROOT/scripts/check-release-basics.sh" "$OUT_SIGNED"

echo "Built release APK: $OUT_SIGNED"
