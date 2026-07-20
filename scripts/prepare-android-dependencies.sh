#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
ANDROID_DEPS_DIR="${MDLITE_ANDROID_DEPS_DIR:-$ROOT/.android-deps}"
BILLING_VERSION="${GOOGLE_PLAY_BILLING_VERSION:-9.0.0}"

python3 "$ROOT/scripts/prepare_android_dependencies.py" \
  --deps-dir "$ANDROID_DEPS_DIR" \
  --billing-version "$BILLING_VERSION"
