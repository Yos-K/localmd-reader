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

if ! grep -q 'android:versionName="0.1.0"' "$ROOT/src/main/AndroidManifest.xml"; then
  echo "Unexpected or missing versionName" >&2
  exit 1
fi

if ! grep -q 'android:versionCode="1"' "$ROOT/src/main/AndroidManifest.xml"; then
  echo "Unexpected or missing versionCode" >&2
  exit 1
fi

if ! grep -q 'android:icon="@mipmap/ic_launcher"' "$ROOT/src/main/AndroidManifest.xml"; then
  echo "Missing launcher icon declaration" >&2
  exit 1
fi

if ! grep -q '<string name="app_name">MdLite Reader</string>' "$ROOT/src/main/res/values/strings.xml"; then
  echo "Unexpected or missing app name" >&2
  exit 1
fi

apksigner verify "$APK" >/dev/null
echo "Basic release checks passed"
