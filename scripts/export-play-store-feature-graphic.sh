#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
BUILD="$ROOT/build/play-store-feature-graphic"
OUT="${1:-$ROOT/play-store/feature-graphic-1024x500.png}"

mkdir -p "$BUILD" "$(dirname -- "$OUT")"

javac -d "$BUILD" "$ROOT/scripts/ExportPlayStoreFeatureGraphic.java"
java -cp "$BUILD" ExportPlayStoreFeatureGraphic "$OUT"

echo "Exported Play Store feature graphic: $OUT"
