#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
TARGET="${1:-aab}"

case "$TARGET" in
  apk|aab|all)
    ;;
  *)
    echo "Usage: scripts/build-signed-release.sh [apk|aab|all]" >&2
    exit 2
    ;;
esac

export MDLITE_RELEASE_KEYSTORE="${MDLITE_RELEASE_KEYSTORE:-$HOME/AndroidDev/keys/mdlite-reader-release.jks}"
export MDLITE_RELEASE_KEY_ALIAS="${MDLITE_RELEASE_KEY_ALIAS:-mdlite-release}"
export BUNDLETOOL_JAR="${BUNDLETOOL_JAR:-$HOME/AndroidDev/tools/bundletool.jar}"

if [ ! -f "$MDLITE_RELEASE_KEYSTORE" ]; then
  echo "Missing release keystore: $MDLITE_RELEASE_KEYSTORE" >&2
  exit 1
fi

if [ "$TARGET" = "aab" ] || [ "$TARGET" = "all" ]; then
  if [ ! -f "$BUNDLETOOL_JAR" ]; then
    echo "Missing bundletool jar: $BUNDLETOOL_JAR" >&2
    exit 1
  fi
fi

cleanup() {
  stty echo 2>/dev/null || true
  unset MDLITE_RELEASE_STORE_PASS
  unset MDLITE_RELEASE_KEY_PASS
}
trap cleanup EXIT INT TERM

printf "Keystore password: "
stty -echo
read -r MDLITE_RELEASE_STORE_PASS
stty echo
printf "\nKey password: "
stty -echo
read -r MDLITE_RELEASE_KEY_PASS
stty echo
printf "\n"
export MDLITE_RELEASE_STORE_PASS
export MDLITE_RELEASE_KEY_PASS

case "$TARGET" in
  apk)
    "$ROOT/scripts/build-release-apk.sh"
    ;;
  aab)
    "$ROOT/scripts/build-release-aab.sh"
    ;;
  all)
    "$ROOT/scripts/build-release-apk.sh"
    "$ROOT/scripts/build-release-aab.sh"
    ;;
esac
