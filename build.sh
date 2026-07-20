#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
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

BUILD="$ROOT/build"
OUT_UNSIGNED="$BUILD/app-unsigned.apk"
OUT_ALIGNED="$BUILD/app-aligned.apk"
OUT_SIGNED="$ROOT/app-debug.apk"
KEYSTORE="$ROOT/debug.keystore"
DEBUG_PACKAGE="${MDLITE_DEBUG_PACKAGE:-io.github.yosk.mdlite.debug}"
DEBUG_APP_NAME="${MDLITE_DEBUG_APP_NAME:-LocalMD Reader Dev}"

rm -rf "$BUILD"
mkdir -p "$BUILD/compiled" "$BUILD/generated" "$BUILD/classes" "$BUILD/dex"
MANIFEST="$BUILD/AndroidManifest.debug.xml"
VERSIONED_MANIFEST="$BUILD/AndroidManifest.versioned.xml"
RES_DIR="$BUILD/res"
cp -R "$ROOT/src/main/res" "$RES_DIR"
sh "$ROOT/scripts/version-apply-manifest.sh" "$ROOT/src/main/AndroidManifest.xml" "$VERSIONED_MANIFEST"
if [ "${MDLITE_DEBUG_ENABLE_PLAY_BILLING:-false}" = "true" ]; then
  sh "$ROOT/scripts/apply-billing-manifest.sh" "$VERSIONED_MANIFEST"
fi
sed \
  -e "s/package=\"io.github.yosk.mdlite\"/package=\"$DEBUG_PACKAGE\"/" \
  -e "s/android:name=\".presentation.MainActivity\"/android:name=\"io.github.yosk.mdlite.presentation.MainActivity\"/" \
  "$VERSIONED_MANIFEST" > "$MANIFEST"
sed \
  -e "s/<string name=\"app_name\">[^<]*<\\/string>/<string name=\"app_name\">$DEBUG_APP_NAME<\\/string>/" \
  "$ROOT/src/main/res/values/strings.xml" > "$RES_DIR/values/strings.xml"
mkdir -p "$BUILD/generated/io/github/yosk/mdlite/infrastructure"
cat > "$BUILD/generated/io/github/yosk/mdlite/infrastructure/BuildConfig.java" <<EOF
package io.github.yosk.mdlite.infrastructure;

public final class BuildConfig {
    public static final boolean PRO_FEATURES_ENABLED = ${MDLITE_DEBUG_PRO_FEATURES:-true};
    public static final boolean PLAY_BILLING_ENABLED = ${MDLITE_DEBUG_ENABLE_PLAY_BILLING:-false};

    private BuildConfig() {
    }
}
EOF
find "$ROOT/src/main/java" -name "*.java" > "$BUILD/main-sources.txt"
if [ "${MDLITE_DEBUG_ENABLE_PLAY_BILLING:-false}" = "true" ] && [ -d "$ROOT/src/billing/java" ]; then
  find "$ROOT/src/billing/java" -name "*.java" >> "$BUILD/main-sources.txt"
fi

"$AAPT2" compile --dir "$RES_DIR" -o "$BUILD/compiled/resources.zip"
# --custom-package keeps R in the base package even though the debug manifest
# renames the application package to add ".debug". Without it aapt2 emits
# io.github.yosk.mdlite.debug.R while Gradle (namespace-based) emits
# io.github.yosk.mdlite.R, so a source that imports R only compiles under one
# of the two builds (the divergence that broke #73's vector icons).
"$AAPT2" link \
  -I "$ANDROID_JAR" \
  --manifest "$MANIFEST" \
  --java "$BUILD/generated" \
  --custom-package io.github.yosk.mdlite \
  $AAPT_ASSETS_ARGS \
  -o "$OUT_UNSIGNED" \
  "$BUILD/compiled/resources.zip"

# Collect generated sources AFTER aapt2 link: it writes R.java here, so any
# main source that references R (e.g. vector icon drawables, #73) needs the
# generated tree on the javac source list at compile time.
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

if [ ! -f "$KEYSTORE" ]; then
  keytool -genkeypair \
    -keystore "$KEYSTORE" \
    -storepass android \
    -keypass android \
    -alias androiddebugkey \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -dname "CN=Android Debug,O=Android,C=US"
fi

"$APKSIGNER" sign \
  --ks "$KEYSTORE" \
  --ks-pass pass:android \
  --key-pass pass:android \
  --out "$OUT_SIGNED" \
  "$OUT_ALIGNED"

"$APKSIGNER" verify "$OUT_SIGNED"
echo "Built $OUT_SIGNED"
