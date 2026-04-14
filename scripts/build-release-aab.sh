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
JAVA="${JAVA:-java}"
JARSIGNER="${JARSIGNER:-jarsigner}"

: "${BUNDLETOOL_JAR:?Set BUNDLETOOL_JAR to the bundletool .jar path.}"
: "${MDLITE_RELEASE_KEYSTORE:?Set MDLITE_RELEASE_KEYSTORE to the production keystore path.}"
: "${MDLITE_RELEASE_KEY_ALIAS:?Set MDLITE_RELEASE_KEY_ALIAS to the production key alias.}"

if [ ! -f "$BUNDLETOOL_JAR" ]; then
  echo "Missing bundletool jar: $BUNDLETOOL_JAR" >&2
  exit 1
fi

if [ ! -f "$MDLITE_RELEASE_KEYSTORE" ]; then
  echo "Missing release keystore: $MDLITE_RELEASE_KEYSTORE" >&2
  exit 1
fi

VERSION_NAME="$(sed -n 's/.*android:versionName="\([^"]*\)".*/\1/p' "$ROOT/src/main/AndroidManifest.xml" | head -n 1)"
BUILD="$ROOT/build"
RELEASE_BUILD="$BUILD/release"
PROTO_APK="$RELEASE_BUILD/base-proto.apk"
BASE_DIR="$RELEASE_BUILD/base"
BASE_ZIP="$RELEASE_BUILD/base.zip"
OUT_UNSIGNED_BUNDLE="$RELEASE_BUILD/mdlite-reader-$VERSION_NAME-release-unsigned.aab"
OUT_BUNDLE="${MDLITE_RELEASE_AAB:-$RELEASE_BUILD/mdlite-reader-$VERSION_NAME-release.aab}"

rm -rf "$BUILD"
mkdir -p "$BUILD/compiled" "$BUILD/generated" "$BUILD/classes" "$BUILD/dex" "$RELEASE_BUILD"
find "$ROOT/src/main/java" -name "*.java" > "$BUILD/main-sources.txt"

"$AAPT2" compile --dir "$ROOT/src/main/res" -o "$BUILD/compiled/resources.zip"
"$AAPT2" link \
  --proto-format \
  -I "$ANDROID_JAR" \
  --manifest "$ROOT/src/main/AndroidManifest.xml" \
  --java "$BUILD/generated" \
  -o "$PROTO_APK" \
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

mkdir -p "$BASE_DIR"
unzip -q "$PROTO_APK" -d "$BASE_DIR"
mkdir -p "$BASE_DIR/manifest" "$BASE_DIR/dex"
mv "$BASE_DIR/AndroidManifest.xml" "$BASE_DIR/manifest/AndroidManifest.xml"
cp "$BUILD/dex/classes.dex" "$BASE_DIR/dex/classes.dex"

(cd "$BASE_DIR" && zip -q -D -r "$BASE_ZIP" .)

"$JAVA" -jar "$BUNDLETOOL_JAR" build-bundle \
  --modules="$BASE_ZIP" \
  --output="$OUT_UNSIGNED_BUNDLE"

"$JARSIGNER" \
  -keystore "$MDLITE_RELEASE_KEYSTORE" \
  -signedjar "$OUT_BUNDLE" \
  "$OUT_UNSIGNED_BUNDLE" \
  "$MDLITE_RELEASE_KEY_ALIAS"

"$JARSIGNER" -verify -strict "$OUT_BUNDLE"

"$JAVA" -jar "$BUNDLETOOL_JAR" validate \
  --bundle="$OUT_BUNDLE"

echo "Built release AAB: $OUT_BUNDLE"
