#!/data/data/com.termux/files/usr/bin/sh
# Guard the harness Hard Constraints at source level so pull requests fail fast.
#
# Checked invariants:
#   1. The app never requests android.permission.INTERNET.
#   2. The main reader WebView keeps JavaScript disabled.
#
# The Mermaid diagram renderer (MermaidJsRenderEngine) intentionally uses a
# separate, offline WebView with JavaScript enabled and is out of scope here.
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"

MANIFEST="$ROOT/src/main/AndroidManifest.xml"
READER="$ROOT/src/main/java/io/github/yosk/mdlite/presentation/MainActivity.java"

fail() {
  echo "Hard constraint violated: $1" >&2
  exit 1
}

[ -f "$MANIFEST" ] || fail "missing manifest: $MANIFEST"
[ -f "$READER" ] || fail "missing reader activity: $READER"

# 1. The app must never request INTERNET.
if grep -q 'android.permission.INTERNET' "$MANIFEST"; then
  fail "android.permission.INTERNET is declared in $MANIFEST"
fi

# 2. The main reader WebView must keep JavaScript disabled.
COMPACT_READER="$(tr -d '[:space:]' < "$READER")"
if printf '%s' "$COMPACT_READER" | grep -q 'setJavaScriptEnabled(true)'; then
  fail "main reader WebView enables JavaScript in $READER"
fi
if ! printf '%s' "$COMPACT_READER" | grep -q 'setJavaScriptEnabled(false)'; then
  fail "main reader WebView does not explicitly disable JavaScript in $READER"
fi

echo "Hard constraint checks passed"
