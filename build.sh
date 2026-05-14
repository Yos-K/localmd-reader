#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
if [ -f "$ROOT/env.project.sh" ]; then
  . "$ROOT/env.project.sh"
fi
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
RES_DIR="$BUILD/res"
cp -R "$ROOT/src/main/res" "$RES_DIR"
sed \
  -e "s/package=\"io.github.yosk.mdlite\"/package=\"$DEBUG_PACKAGE\"/" \
  "$ROOT/src/main/AndroidManifest.xml" > "$MANIFEST"
sed \
  -e "s/<string name=\"app_name\">[^<]*<\\/string>/<string name=\"app_name\">$DEBUG_APP_NAME<\\/string>/" \
  "$ROOT/src/main/res/values/strings.xml" > "$RES_DIR/values/strings.xml"
mkdir -p "$BUILD/generated/io/github/yosk/mdlite/infrastructure"
cat > "$BUILD/generated/io/github/yosk/mdlite/infrastructure/BuildConfig.java" <<EOF
package io.github.yosk.mdlite.infrastructure;

public final class BuildConfig {
    public static final boolean PRO_FEATURES_ENABLED = ${MDLITE_DEBUG_PRO_FEATURES:-false};

    private BuildConfig() {
    }
}
EOF
find "$ROOT/src/main/java" -name "*.java" > "$BUILD/main-sources.txt"
find "$BUILD/generated" -name "*.java" >> "$BUILD/main-sources.txt"

"$AAPT2" compile --dir "$RES_DIR" -o "$BUILD/compiled/resources.zip"
"$AAPT2" link \
  -I "$ANDROID_JAR" \
  --manifest "$MANIFEST" \
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
