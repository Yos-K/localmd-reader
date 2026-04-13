#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
ANDROID_HOME="${ANDROID_HOME:-$HOME/AndroidDev/sdk}"
ANDROID_PLATFORM="${ANDROID_PLATFORM:-android-33}"
ANDROID_BUILD_TOOLS="${ANDROID_BUILD_TOOLS:-35.0.2}"
ANDROID_JAR="$ANDROID_HOME/platforms/$ANDROID_PLATFORM/android.jar"
BUILD_TOOLS="$ANDROID_HOME/build-tools/$ANDROID_BUILD_TOOLS"

AAPT2="${AAPT2:-$BUILD_TOOLS/aapt2}"
D8="${D8:-$BUILD_TOOLS/d8}"
ZIPALIGN="${ZIPALIGN:-$BUILD_TOOLS/zipalign}"
APKSIGNER="${APKSIGNER:-$BUILD_TOOLS/apksigner}"

BUILD="$ROOT/build"
OUT_UNSIGNED="$BUILD/app-unsigned.apk"
OUT_ALIGNED="$BUILD/app-aligned.apk"
OUT_SIGNED="$ROOT/app-debug.apk"
KEYSTORE="$ROOT/debug.keystore"

rm -rf "$BUILD"
mkdir -p "$BUILD/compiled" "$BUILD/generated" "$BUILD/classes" "$BUILD/dex"
find "$ROOT/src/main/java" -name "*.java" > "$BUILD/main-sources.txt"

"$AAPT2" compile --dir "$ROOT/src/main/res" -o "$BUILD/compiled/resources.zip"
"$AAPT2" link \
  -I "$ANDROID_JAR" \
  --manifest "$ROOT/src/main/AndroidManifest.xml" \
  --java "$BUILD/generated" \
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
