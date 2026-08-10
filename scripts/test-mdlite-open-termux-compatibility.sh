#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT

mkdir -p "$TMP_DIR/bin"
printf '%s\n' \
  '#!/bin/sh' \
  'printf "%s\\n" "$*" >> "$MDLITE_AM_CAPTURE"' \
  > "$TMP_DIR/bin/am"
chmod +x "$TMP_DIR/bin/am"

printf '# First\n' > "$TMP_DIR/first.md"
printf '# Second\n' > "$TMP_DIR/second.md"

MDLITE_AM_CAPTURE="$TMP_DIR/am-calls.txt" \
MDLITE_PACKAGE="io.github.yosk.mdlite.free.debug" \
PATH="$TMP_DIR/bin:$PATH" \
  sh "$ROOT/scripts/mdlite-open.sh" "$TMP_DIR/first.md" "$TMP_DIR/second.md"

test "$(wc -l < "$TMP_DIR/am-calls.txt")" -eq 1
grep -F -- \
  '-n io.github.yosk.mdlite.free.debug/io.github.yosk.mdlite.presentation.MainActivity' \
  "$TMP_DIR/am-calls.txt" >/dev/null
grep -F -- '-a io.github.yosk.mdlite.action.OPEN_TEXTS_BASE64' "$TMP_DIR/am-calls.txt" >/dev/null
first_title="$(printf '%s' 'first.md' | base64 -w 0)"
first_source="$(printf '%s' "$TMP_DIR/first.md" | base64 -w 0)"
first_text="$(base64 -w 0 "$TMP_DIR/first.md")"
second_title="$(printf '%s' 'second.md' | base64 -w 0)"
second_source="$(printf '%s' "$TMP_DIR/second.md" | base64 -w 0)"
second_text="$(base64 -w 0 "$TMP_DIR/second.md")"
expected_batch="$first_title:$first_source:$first_text,$second_title:$second_source:$second_text"
grep -F -- \
  "--es io.github.yosk.mdlite.extra.MARKDOWN_DOCUMENTS_BASE64 $expected_batch" \
  "$TMP_DIR/am-calls.txt" >/dev/null
! grep -F -- '--esa ' "$TMP_DIR/am-calls.txt" >/dev/null

echo "Termux open-script compatibility test passed"
