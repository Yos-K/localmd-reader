#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
ANDROID_HOME="${ANDROID_HOME:-$HOME/AndroidDev/sdk}"
ANDROID_PLATFORM="${ANDROID_PLATFORM:-android-33}"
ANDROID_BUILD_TOOLS="${ANDROID_BUILD_TOOLS:-35.0.2}"
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

VERSION_NAME="$(sed -n 's/.*android:versionName="\([^"]*\)".*/\1/p' "$ROOT/src/main/AndroidManifest.xml" | head -n 1)"
BUILD="$ROOT/build"
RELEASE_BUILD="$BUILD/release"
OUT_UNSIGNED="$RELEASE_BUILD/app-release-unsigned.apk"
OUT_ALIGNED="$RELEASE_BUILD/app-release-aligned.apk"
OUT_SIGNED="${MDLITE_RELEASE_APK:-$RELEASE_BUILD/mdlite-reader-$VERSION_NAME-release.apk}"

rm -rf "$BUILD"
mkdir -p "$BUILD/compiled" "$BUILD/generated" "$BUILD/classes" "$BUILD/dex" "$RELEASE_BUILD"
find "$ROOT/src/main/java" -name "*.java" > "$BUILD/main-sources.txt"

"$AAPT2" compile --dir "$ROOT/src/main/res" -o "$BUILD/compiled/resources.zip"
"$AAPT2" link \
  -I "$ANDROID_JAR" \
  --manifest "$ROOT/src/main/AndroidManifest.xml" \
  --java "$BUILD/generated" \
  $AAPT_ASSETS_ARGS \
  -o "$OUT_UNSIGNED" \
  "$BUILD/compiled/resources.zip"

javac \
  -source 8 \
  -target 8 \
  -bootclasspath "$ANDROID_JAR" \
  -d "$BUILD/classes" \
  @"$BUILD/main-sources.txt"

"$D8" \
  --lib "$ANDROID_JAR" \
  --output "$BUILD/dex" \
  $(find "$BUILD/classes" -name "*.class")

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
"$ROOT/scripts/check-release-basics.sh" "$OUT_SIGNED"

echo "Built release APK: $OUT_SIGNED"
