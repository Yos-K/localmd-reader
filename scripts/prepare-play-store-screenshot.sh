#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
INPUT="${1:?Usage: scripts/prepare-play-store-screenshot.sh <input> <output>}"
OUTPUT="${2:?Usage: scripts/prepare-play-store-screenshot.sh <input> <output>}"
BUILD="$ROOT/build/strip-image-metadata"

mkdir -p "$BUILD" "$(dirname -- "$OUTPUT")"

javac -d "$BUILD" "$ROOT/scripts/StripImageMetadata.java"
java -cp "$BUILD" StripImageMetadata "$INPUT" "$OUTPUT"

echo "Prepared screenshot: $OUTPUT"
