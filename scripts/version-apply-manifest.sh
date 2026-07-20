#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
. "$ROOT/scripts/version-env.sh"

INPUT="${1:?Usage: scripts/version-apply-manifest.sh INPUT_MANIFEST OUTPUT_MANIFEST}"
OUTPUT="${2:?Usage: scripts/version-apply-manifest.sh INPUT_MANIFEST OUTPUT_MANIFEST}"

sed \
  -e "s/android:versionCode=\"[^\"]*\"/android:versionCode=\"$VERSION_CODE\"/" \
  -e "s/android:versionName=\"[^\"]*\"/android:versionName=\"$VERSION_NAME\"/" \
  "$INPUT" > "$OUTPUT"
