#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
TEST_ROOT="$ROOT/src/test/java"
FAILURES="$ROOT/build/test-smell-failures.txt"

mkdir -p "$ROOT/build"
: > "$FAILURES"

grep_test_sources() {
  pattern="$1"
  grep -R -n -E "$pattern" "$TEST_ROOT" \
    --include="*.java" \
    --exclude="TestAssertions.java" \
    --exclude-dir="build" || true
}

record_matches() {
  smell="$1"
  pattern="$2"
  matches="$(grep_test_sources "$pattern")"
  if [ -n "$matches" ]; then
    {
      printf '%s\n' "$smell"
      printf '%s\n' "$matches"
      printf '\n'
    } >> "$FAILURES"
  fi
}

record_matches "Conditional Test Logic: test source must not use if/for/while/switch." '^[[:space:]]*(if|for|while|switch)[[:space:]]*\('
record_matches "Sleepy Test: test source must not use Thread.sleep." 'Thread[.]sleep[[:space:]]*\('
record_matches "Skip Testing: test source must not mention skipTests or maven.test.skip." 'skipTests|maven[.]test[.]skip'
record_matches "Assertion Roulette: use TestAssertions with explicit messages, not raw AssertionError." 'new[[:space:]]+AssertionError[[:space:]]*\('
record_matches "Ad Hoc Assertions: test source must not declare private assert helpers." 'private[[:space:]]+static[[:space:]]+.*assert'

if [ -s "$FAILURES" ]; then
  cat "$FAILURES" >&2
  exit 1
fi

echo "Test smell checks passed"
