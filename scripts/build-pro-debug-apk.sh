#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
OUT="${1:-/sdcard/Download/localmd-reader-pro-debug.apk}"

sh "$ROOT/scripts/prepare-android-dependencies.sh" > /dev/null

MDLITE_DEBUG_PRO_FEATURES=true \
MDLITE_DEBUG_ENABLE_PLAY_BILLING=true \
MDLITE_INCLUDE_ANDROID_DEPS=true \
MDLITE_DEBUG_PACKAGE=io.github.yosk.mdlite.pro.debug \
MDLITE_DEBUG_APP_NAME="LocalMD Reader Pro Dev" \
"$ROOT/build.sh" > /dev/null

cp "$ROOT/app-debug.apk" "$OUT"

echo "Built Pro debug APK: $OUT"
