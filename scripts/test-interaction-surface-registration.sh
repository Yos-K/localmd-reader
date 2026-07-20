#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

TMP_BASE="${TMPDIR:-/tmp}"
TMP_DIR="$TMP_BASE/localmd-surface-registration-test-$$"
mkdir -p "$TMP_DIR/valid" "$TMP_DIR/unmarked" "$TMP_DIR/unknown"
trap 'rm -rf "$TMP_DIR"' EXIT HUP INT TERM

SURFACES="$TMP_DIR/surfaces.psv"
printf '%s\n' \
  '# surface_id|state_id|evidence|locator' \
  'settings-dialog|settings|Example.java|showSettings' \
  > "$SURFACES"

printf '%s\n' \
  'final class Example {' \
  '  void showSettings() {' \
  '    // interaction-surface: settings-dialog' \
  '    builder.setItems(labels, listener);' \
  '  }' \
  '}' \
  > "$TMP_DIR/valid/Example.java"

printf '%s\n' \
  'final class Example {' \
  '  void showSettings() {' \
  '    builder.setItems(labels, listener);' \
  '  }' \
  '}' \
  > "$TMP_DIR/unmarked/Example.java"

printf '%s\n' \
  'final class Example {' \
  '  void showSettings() {' \
  '    // interaction-surface: missing-dialog' \
  '    builder.setView(content);' \
  '  }' \
  '}' \
  > "$TMP_DIR/unknown/Example.java"

sh scripts/check-interaction-surface-registration.sh "$TMP_DIR/valid" "$SURFACES" >/dev/null

if sh scripts/check-interaction-surface-registration.sh "$TMP_DIR/unmarked" "$SURFACES" >/dev/null 2>&1; then
  echo "test-interaction-surface-registration: expected unmarked choice surface to fail" >&2
  exit 1
fi

if sh scripts/check-interaction-surface-registration.sh "$TMP_DIR/unknown" "$SURFACES" >/dev/null 2>&1; then
  echo "test-interaction-surface-registration: expected unknown surface id to fail" >&2
  exit 1
fi

echo "test-interaction-surface-registration: passed"
