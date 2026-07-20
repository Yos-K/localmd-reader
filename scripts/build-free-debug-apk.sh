#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
OUT="${1:-/sdcard/Download/localmd-reader-free-debug.apk}"

MDLITE_DEBUG_PRO_FEATURES=false \
MDLITE_DEBUG_ENABLE_PLAY_BILLING=false \
MDLITE_DEBUG_PACKAGE=io.github.yosk.mdlite.free.debug \
MDLITE_DEBUG_APP_NAME="LocalMD Reader Free Dev" \
"$ROOT/build.sh" > /dev/null

cp "$ROOT/app-debug.apk" "$OUT"

echo "Built Free debug APK: $OUT"
