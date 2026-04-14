#!/data/data/com.termux/files/usr/bin/sh
set -eu

KEYSTORE="${MDLITE_RELEASE_KEYSTORE:-$HOME/AndroidDev/keys/mdlite-reader-release.jks}"
ALIAS="${MDLITE_RELEASE_KEY_ALIAS:-mdlite-release}"

if [ -f "$KEYSTORE" ]; then
  echo "Release keystore already exists: $KEYSTORE" >&2
  exit 1
fi

mkdir -p "$(dirname -- "$KEYSTORE")"

keytool -genkeypair \
  -v \
  -keystore "$KEYSTORE" \
  -alias "$ALIAS" \
  -keyalg RSA \
  -keysize 4096 \
  -validity 10000

echo "Created release keystore: $KEYSTORE"
echo "Release key alias: $ALIAS"
