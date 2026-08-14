#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
CAPTURE="$ROOT/scripts/capture-theme-screenshots.sh"
WORKFLOW="$ROOT/.github/workflows/theme-screenshots.yml"

grep -F 'dismiss_system_anr_dialog()' "$CAPTURE" >/dev/null
grep -F "isn't responding" "$CAPTURE" >/dev/null
grep -F 'android:id/aerr_wait' "$CAPTURE" >/dev/null

document_assertion=$(sed -n '/^assert_document_open()/,/^}/p' "$CAPTURE")
printf '%s\n' "$document_assertion" | grep -F 'dismiss_system_anr_dialog' >/dev/null
grep -F 'THEME_FIXTURE' "$CAPTURE" >/dev/null
grep -F 'fixture_title=$(basename "$FIXTURE")' "$CAPTURE" >/dev/null
grep -F -- '--esa "$EX_TITLES" "$fixture_title"' "$CAPTURE" >/dev/null
grep -F 'inputs.fixture' "$WORKFLOW" >/dev/null
if grep -F 'THEME_FIXTURE=' "$WORKFLOW" | grep -F '\' >/dev/null; then
  echo "Emulator runner script must not split environment assignments across commands" >&2
  exit 1
fi

echo "Theme screenshot ANR recovery test passed"
