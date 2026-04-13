#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
APK="$ROOT/app-debug.apk"

if [ ! -f "$APK" ]; then
  echo "Missing APK: $APK" >&2
  exit 1
fi

if aapt dump permissions "$APK" | grep -q "android.permission.INTERNET"; then
  echo "Unexpected INTERNET permission" >&2
  exit 1
fi

apksigner verify "$APK" >/dev/null
echo "Basic release checks passed"
