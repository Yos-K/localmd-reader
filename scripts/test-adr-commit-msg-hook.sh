#!/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
HOOK="$ROOT/.githooks/commit-msg"
TMP_BASE="${TMPDIR:-/tmp}"
TMP_DIR="$TMP_BASE/localmd-adr-hook-test-$$"
mkdir -p "$TMP_DIR"
trap 'rm -rf "$TMP_DIR"' EXIT HUP INT TERM

expect_failure() {
  message="$1"
  if sh "$HOOK" "$message" >/dev/null 2>&1; then
    echo "test-adr-commit-msg-hook: expected failure for $message" >&2
    exit 1
  fi
}

expect_success() {
  sh "$HOOK" "$1" >/dev/null
}

printf '%s\n' 'feat: missing review' > "$TMP_DIR/missing"
expect_failure "$TMP_DIR/missing"

printf '%s\n\n%s\n' 'fix: unexplained none' 'ADR-Review: none' > "$TMP_DIR/unexplained"
expect_failure "$TMP_DIR/unexplained"

printf '%s\n\n%s\n' 'feat: reviewed decision' \
  'ADR-Review: docs/adr/0003-markdown-library-navigation.md' > "$TMP_DIR/referenced"
expect_success "$TMP_DIR/referenced"

printf '%s\n\n%s\n' 'fix: local copy only' \
  'ADR-Review: none (no architecture decision is affected)' > "$TMP_DIR/reasoned"
expect_success "$TMP_DIR/reasoned"

printf '%s\n' 'docs: update instructions' > "$TMP_DIR/docs"
expect_success "$TMP_DIR/docs"

echo "test-adr-commit-msg-hook: passed"
