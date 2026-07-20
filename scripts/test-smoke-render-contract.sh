#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
FIXTURE="$ROOT/scripts/smoke-fixtures/smoke-render.md"
ASSERT_SCRIPT="$ROOT/scripts/smoke-render-assert-l5.sh"

fail() {
  echo "test-smoke-render-contract: $1" >&2
  exit 1
}

grep -q '```HTML title="render-html-info"' "$FIXTURE" || {
  fail "smoke render fixture must exercise previewable HTML fences with info metadata"
}

grep -q '```Markdown source=render-markdown-info' "$FIXTURE" || {
  fail "smoke render fixture must exercise previewable Markdown fences with info metadata"
}

grep -q 'assert_visible_text "Preview" "code-preview-toggle"' "$ASSERT_SCRIPT" || {
  fail "L5 render assert must check that previewable code fences expose the Preview toggle"
}

grep -q 'tap_visible_text "Preview" "code-preview-preview-label"' "$ASSERT_SCRIPT" || {
  fail "L5 render assert must tap the Preview label to verify the Raw/Preview switch"
}

grep -q 'assert_visible_text "render-markdown-preview-heading" "code-preview-rendered-markdown"' "$ASSERT_SCRIPT" || {
  fail "L5 render assert must verify rendered Markdown preview content after tapping Preview"
}

grep -q 'tap_visible_text_occurrence "Preview" "code-preview-html-preview-label" 2' "$ASSERT_SCRIPT" || {
  fail "L5 render assert must tap the second Preview label to verify multiple previewable blocks"
}

grep -q 'assert_visible_text "render-html-preview-heading" "code-preview-rendered-html"' "$ASSERT_SCRIPT" || {
  fail "L5 render assert must verify rendered HTML preview content after tapping the second Preview"
}

grep -q 'tap_visible_text_occurrence "Raw" "code-preview-html-raw-label" 2' "$ASSERT_SCRIPT" || {
  fail "L5 render assert must tap the second Raw label to verify previewable blocks can return to Raw"
}

grep -q 'assert_visible_text "onclick" "code-preview-html-raw-source"' "$ASSERT_SCRIPT" || {
  fail "L5 render assert must verify raw HTML source content after returning from Preview"
}

echo "smoke render contract test passed"
