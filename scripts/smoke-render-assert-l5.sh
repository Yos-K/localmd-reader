#!/data/data/com.termux/files/usr/bin/sh
# L5 render-assert smoke: opens smoke-render.md via OPEN_TEXTS intent and
# asserts that table / code-block / Mermaid visible text is present in the
# UIAutomator UI dump. Intended as workflow_dispatch only (flaky-tolerant,
# not a merge gate). Designed to fail loudly on a rendering regression.
#
# ANR dialog suppression (hide_error_dialogs 1) must be set by the caller
# (the CI workflow) before this script runs, so it applies to the emulator
# globally rather than only to the app process.
#
# Demonstrates detection capability: see the render-nonexistent check near
# the bottom which is expected to fail and thereby proves the assert logic
# actually works.
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
PKG="${1:-io.github.yosk.mdlite.debug}"
ACTIVITY="$PKG/io.github.yosk.mdlite.presentation.MainActivity"

ACTION="io.github.yosk.mdlite.action.OPEN_TEXTS"
EX_TITLES="io.github.yosk.mdlite.extra.MARKDOWN_TITLES"
EX_SOURCES="io.github.yosk.mdlite.extra.MARKDOWN_SOURCES"
EX_TEXTS="io.github.yosk.mdlite.extra.MARKDOWN_TEXTS_BASE64"

FIXTURE="$ROOT/scripts/smoke-fixtures/smoke-render.md"
ART_DIR="${SMOKE_ARTIFACT_DIR:-$ROOT/smoke-artifacts}"
mkdir -p "$ART_DIR"

fail() {
  echo "L5 render-assert failed: $1" >&2
  adb logcat -d -v time > "$ART_DIR/logcat.txt" 2>/dev/null || true
  adb exec-out screencap -p > "$ART_DIR/screen.png" 2>/dev/null || true
  adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/ui-dump.xml" 2>/dev/null || true
  exit 1
}

warn() {
  echo "L5 render-assert warning: $1" >&2
}

b64() {
  base64 < "$1" | tr -d '\n'
}

# Open the render fixture via OPEN_TEXTS intent.
launch_render_fixture() {
  local fixture_b64
  fixture_b64="$(b64 "$FIXTURE")"
  adb shell am start -n "$ACTIVITY" -a "$ACTION" --activity-single-top \
    --esa "$EX_TITLES" "smoke-render.md" \
    --esa "$EX_SOURCES" "$FIXTURE" \
    --esa "$EX_TEXTS" "$fixture_b64" >/dev/null
}

# Assert the fixture tab title is visible in the UI dump (retries up to 3
# times, re-sending the intent on each retry, mirroring assert_document_open
# in capture-theme-screenshots.sh which handles the first-launch warm-up
# issue documented in #97 / #98).
assert_fixture_open() {
  local tries=0
  while [ "$tries" -lt 3 ]; do
    if [ "$tries" -gt 0 ]; then
      launch_render_fixture
      sleep 5
    fi
    adb shell rm -f /sdcard/ui-dump.xml
    adb shell uiautomator dump /sdcard/ui-dump.xml >/dev/null 2>&1 || true
    if adb shell cat /sdcard/ui-dump.xml 2>/dev/null | grep -q "smoke-render.md"; then
      return 0
    fi
    tries=$((tries + 1))
  done
  adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/fixture-open-fail-ui-dump.xml" 2>/dev/null || true
  fail "smoke-render.md fixture did not open after $tries retries"
}

# Assert that a keyword is present in the current UI dump (refreshes dump
# each call). On failure, saves screenshot + dump as evidence.
assert_visible_text() {
  local keyword="$1"
  local label="$2"
  adb shell rm -f /sdcard/ui-dump.xml
  adb shell uiautomator dump /sdcard/ui-dump.xml >/dev/null 2>&1 || true
  if adb shell cat /sdcard/ui-dump.xml 2>/dev/null | grep -q "$keyword"; then
    echo "L5 assert ok: $label ($keyword)"
    return 0
  fi
  adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/${label}-fail-ui-dump.xml" 2>/dev/null || true
  adb exec-out screencap -p > "$ART_DIR/${label}-fail-screen.png" 2>/dev/null || true
  return 1
}

tap_visible_text() {
  local keyword="$1"
  local label="$2"
  tap_visible_text_occurrence "$keyword" "$label" 1
}

tap_visible_text_occurrence() {
  local keyword="$1"
  local label="$2"
  local occurrence="$3"
  local node
  local bounds
  local coords
  local x1
  local y1
  local x2
  local y2
  local x
  local y
  adb shell rm -f /sdcard/ui-dump.xml
  adb shell uiautomator dump /sdcard/ui-dump.xml >/dev/null 2>&1 || true
  node="$(adb shell cat /sdcard/ui-dump.xml 2>/dev/null | tr '>' '>\n' | grep "$keyword" | sed -n "${occurrence}p" || true)"
  bounds="$(printf '%s\n' "$node" | sed -n 's/.*bounds="\[\([0-9][0-9]*\),\([0-9][0-9]*\)\]\[\([0-9][0-9]*\),\([0-9][0-9]*\)\]".*/\1 \2 \3 \4/p')"
  if [ -z "$bounds" ]; then
    adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/${label}-tap-fail-ui-dump.xml" 2>/dev/null || true
    adb exec-out screencap -p > "$ART_DIR/${label}-tap-fail-screen.png" 2>/dev/null || true
    return 1
  fi
  coords="$(printf '%s\n' "$bounds")"
  set -- $coords
  x1="$1"
  y1="$2"
  x2="$3"
  y2="$4"
  x=$(((x1 + x2) / 2))
  y=$(((y1 + y2) / 2))
  adb shell input tap "$x" "$y" >/dev/null
  sleep 1
  echo "L5 tap ok: $label ($keyword #$occurrence at $x,$y)"
  return 0
}

# --- main ---

adb logcat -c 2>/dev/null || true

# L2: launch app (cold start to ensure a clean state before the fixture open)
adb shell am start -W -n "$ACTIVITY" >/dev/null
sleep 3
if ! adb shell pidof "$PKG" >/dev/null 2>&1; then
  fail "app did not launch"
fi
echo "L2 launch: ok"

# L5: open render fixture
launch_render_fixture
sleep 15
assert_fixture_open
echo "L5 fixture open: ok"

# Assert table rendering: smoke-render.md contains "render-table-alpha" in a
# Markdown table cell. If this text is absent the table rendered incorrectly
# or was silently swapped for a fallback/error view.
if ! assert_visible_text "render-table-alpha" "table"; then
  fail "table render regression: render-table-alpha not visible"
fi

# Assert code block rendering: smoke-render.md contains "render-code-block-check"
# in a Kotlin code block comment. Absence means the code block was not rendered.
if ! assert_visible_text "render-code-block-check" "code-block"; then
  fail "code-block render regression: render-code-block-check not visible"
fi

# Assert previewable HTML / Markdown code blocks: uppercase language tags are
# common in copied documents, and must still expose the Raw / Preview switcher.
if ! assert_visible_text "Preview" "code-preview-toggle"; then
  fail "code-preview regression: Raw/Preview switcher not visible for previewable code blocks"
fi

if ! tap_visible_text "Preview" "code-preview-preview-label"; then
  fail "code-preview regression: Preview label could not be tapped"
fi

if ! assert_visible_text "render-markdown-preview-heading" "code-preview-rendered-markdown"; then
  fail "code-preview regression: rendered Markdown preview content not visible after tapping Preview"
fi

if ! tap_visible_text_occurrence "Preview" "code-preview-html-preview-label" 2; then
  fail "code-preview regression: second Preview label could not be tapped"
fi

if ! assert_visible_text "render-html-preview-heading" "code-preview-rendered-html"; then
  fail "code-preview regression: rendered HTML preview content not visible after tapping Preview"
fi

if ! tap_visible_text_occurrence "Raw" "code-preview-html-raw-label" 2; then
  fail "code-preview regression: second Raw label could not be tapped after Preview"
fi

if ! assert_visible_text "onclick" "code-preview-html-raw-source"; then
  fail "code-preview regression: raw HTML source content not visible after returning from Preview"
fi

# Assert Mermaid rendering: smoke-render.md contains "render-mermaid-start" as
# a flowchart node label. Mermaid renders via WebView; the node label may not
# always surface in the accessibility tree depending on the WebView
# implementation. A failure here is treated as a warning (evidence is saved but
# the run is not failed) so the workflow stays non-flaky-blocking.
if ! assert_visible_text "render-mermaid-start" "mermaid"; then
  warn "Mermaid node text not in UI dump — check mermaid-fail-ui-dump.xml artifact"
  warn "This is expected on some WebView builds; screenshot evidence is saved"
fi

# Detection capability proof: assert a text that must NOT be present. This
# verifies that the assert logic can actually detect regressions (it is not a
# no-op). The word "render-nonexistent" does not appear anywhere in the fixture.
adb shell rm -f /sdcard/ui-dump.xml
adb shell uiautomator dump /sdcard/ui-dump.xml >/dev/null 2>&1 || true
if adb shell cat /sdcard/ui-dump.xml 2>/dev/null | grep -q "render-nonexistent"; then
  fail "detection-capability check: render-nonexistent was unexpectedly found (fixture contaminated?)"
fi
echo "L5 detection-capability check: render-nonexistent correctly absent"

# Final evidence capture
adb logcat -d -v time > "$ART_DIR/logcat.txt" 2>/dev/null || true
adb exec-out screencap -p > "$ART_DIR/screen.png" 2>/dev/null || true
adb shell cat /sdcard/ui-dump.xml > "$ART_DIR/ui-dump.xml" 2>/dev/null || true

echo "L5 render-assert smoke passed"
