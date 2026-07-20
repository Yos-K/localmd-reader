#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
BUILD="$ROOT/build/play-store-icon"
OUT="${1:-$ROOT/play-store/icon-512.png}"

mkdir -p "$BUILD" "$(dirname -- "$OUT")"

javac -d "$BUILD" "$ROOT/scripts/ExportPlayStoreIcon.java"
java -cp "$BUILD" ExportPlayStoreIcon "$OUT"

echo "Exported Play Store icon: $OUT"
