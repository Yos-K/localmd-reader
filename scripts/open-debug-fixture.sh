#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
FIXTURE="${1:-/sdcard/Download/localmd-debug-fixture.md}"

"$ROOT/scripts/create-debug-markdown-fixture.sh" "$FIXTURE" > /dev/null
"$ROOT/scripts/mdlite-open.sh" "$FIXTURE"

echo "Opened debug fixture: $FIXTURE"
