#!/data/data/com.termux/files/usr/bin/sh
# Drive an install-free smoke test against a connected Android emulator via adb.
# The caller (CI workflow) installs the debug APK first; this script then walks
# the smoke ladder and fails with a non-zero exit code on the first problem:
#
#   L2  launch the app
#   L3  open one Markdown file by intent (single document renders)
#   L4  open multiple Markdown files by intent (documents open as tabs)
#
# It uses the same custom OPEN_TEXTS intent as scripts/mdlite-open.sh, which
# passes Markdown content directly as base64 extras and therefore avoids
# file:// / scoped-storage permission issues on the emulator.
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
PKG="${1:-io.github.yosk.mdlite.debug}"
ACTIVITY="$PKG/io.github.yosk.mdlite.presentation.MainActivity"

ACTION="io.github.yosk.mdlite.action.OPEN_TEXTS"
EX_TITLES="io.github.yosk.mdlite.extra.MARKDOWN_TITLES"
EX_SOURCES="io.github.yosk.mdlite.extra.MARKDOWN_SOURCES"
EX_TEXTS="io.github.yosk.mdlite.extra.MARKDOWN_TEXTS_BASE64"

FIXTURE_ONE="$ROOT/scripts/smoke-fixtures/smoke-one.md"
FIXTURE_TWO="$ROOT/scripts/smoke-fixtures/smoke-two.md"

ART_DIR="${SMOKE_ARTIFACT_DIR:-$ROOT/smoke-artifacts}"
mkdir -p "$ART_DIR"

# Capture evidence (full logcat + screenshot) so a failed run can be classified
# without local reproduction. Best-effort: never let evidence capture fail the run.
# This smoke run opens only repository fixtures; the app does not log document
# contents, so logcat evidence is designed not to include user documents.
capture_evidence() {
  adb logcat -d -v time > "$ART_DIR/logcat.txt" 2>/dev/null || true
  adb exec-out screencap -p > "$ART_DIR/screen.png" 2>/dev/null || true
}

fail() {
  echo "Emulator smoke failed: $1" >&2
  capture_evidence
  grep -F "FATAL EXCEPTION" -A 8 "$ART_DIR/logcat.txt" >&2 2>/dev/null || true
  exit 1
}

b64() {
  # Read from stdin and strip newlines so it works on both GNU and BSD base64.
  base64 < "$1" | tr -d '\n'
}

assert_alive() {
  sleep 2
  if ! adb shell pidof "$PKG" >/dev/null 2>&1; then
    fail "$1 (process $PKG is not running)"
  fi
  # AndroidRuntime's "FATAL EXCEPTION" line does not carry the package; the
  # crashing package is on the following "Process: <pkg>" line. Matching the
  # FATAL line against $PKG never hits, so correlate the crash block (FATAL plus
  # a few lines) against "Process:.*$PKG" to catch this app's crashes.
  if adb logcat -d -v brief 2>/dev/null | grep -A3 -F "FATAL EXCEPTION" | grep -q "Process:.*$PKG"; then
    fail "$1 (FATAL EXCEPTION for $PKG in logcat)"
  fi
}

open_texts() {
  # open_texts <titles> <sources> <texts_base64>
  adb shell am start -n "$ACTIVITY" -a "$ACTION" --activity-single-top \
    --esa "$EX_TITLES" "$1" \
    --esa "$EX_SOURCES" "$2" \
    --esa "$EX_TEXTS" "$3" >/dev/null
}

adb logcat -c 2>/dev/null || true

# L2: launch
adb shell am start -W -n "$ACTIVITY" >/dev/null
assert_alive "launch"
echo "L2 launch: ok"

# L3: open one Markdown by intent
one_b64="$(b64 "$FIXTURE_ONE")"
open_texts "smoke-one.md" "$FIXTURE_ONE" "$one_b64"
assert_alive "open single Markdown by intent"
echo "L3 single-file intent: ok"

# L4: open multiple Markdown files as tabs
two_b64="$(b64 "$FIXTURE_TWO")"
open_texts "smoke-one.md,smoke-two.md" "$FIXTURE_ONE,$FIXTURE_TWO" "$one_b64,$two_b64"
assert_alive "open multiple Markdown tabs by intent"
echo "L4 multiple-file intent: ok"

capture_evidence
echo "Emulator smoke passed"
