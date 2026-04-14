#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
NAME="${1:?Usage: scripts/capture-play-store-screenshot.sh <name>}"
OUT="$ROOT/play-store/screenshots/$NAME.png"

mkdir -p "$(dirname -- "$OUT")"
screencap -p "$OUT"

echo "Captured screenshot: $OUT"
