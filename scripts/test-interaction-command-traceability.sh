#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

TMP_DIR="${TMPDIR:-/tmp}/localmd-command-traceability-test-$$"
mkdir -p "$TMP_DIR/src" "$TMP_DIR/test"
trap 'rm -rf "$TMP_DIR"' EXIT HUP INT TERM

TRANSITIONS="$TMP_DIR/transitions.psv"
VALID="$TMP_DIR/valid.psv"
MISSING_MODEL="$TMP_DIR/missing-model.psv"
MISSING_IMPLEMENTATION="$TMP_DIR/missing-implementation.psv"
MISSING_TEST="$TMP_DIR/missing-test.psv"
DUPLICATE="$TMP_DIR/duplicate.psv"

printf '%s\n' \
  '# from_state|command|to_state|event' \
  'menu-open|scroll_menu|menu-open|Menu scrolled' > "$TRANSITIONS"
printf '%s\n' \
  '// interaction-command: scroll_menu' \
  'void scrollMenu() {}' > "$TMP_DIR/src/Menu.java"
printf '%s\n' \
  'void verticalSwipeReachesLowerMenuActions() {}' > "$TMP_DIR/test/MenuTest.java"

printf '%s\n' \
  '# state_id|command|implementation|implementation_locator|test|test_locator' \
  "menu-open|scroll_menu|$TMP_DIR/src/Menu.java|scrollMenu|$TMP_DIR/test/MenuTest.java|verticalSwipeReachesLowerMenuActions" > "$VALID"
printf '%s\n' \
  '# state_id|command|implementation|implementation_locator|test|test_locator' \
  "menu-open|missing_command|$TMP_DIR/src/Menu.java|scrollMenu|$TMP_DIR/test/MenuTest.java|verticalSwipeReachesLowerMenuActions" > "$MISSING_MODEL"
printf '%s\n' \
  '# state_id|command|implementation|implementation_locator|test|test_locator' \
  "menu-open|scroll_menu|$TMP_DIR/src/Menu.java|missingImplementation|$TMP_DIR/test/MenuTest.java|verticalSwipeReachesLowerMenuActions" > "$MISSING_IMPLEMENTATION"
printf '%s\n' \
  '# state_id|command|implementation|implementation_locator|test|test_locator' \
  "menu-open|scroll_menu|$TMP_DIR/src/Menu.java|scrollMenu|$TMP_DIR/test/MenuTest.java|missingBehaviorTest" > "$MISSING_TEST"
printf '%s\n' \
  '# state_id|command|implementation|implementation_locator|test|test_locator' \
  "menu-open|scroll_menu|$TMP_DIR/src/Menu.java|scrollMenu|$TMP_DIR/test/MenuTest.java|verticalSwipeReachesLowerMenuActions" \
  "menu-open|scroll_menu|$TMP_DIR/src/Menu.java|scrollMenu|$TMP_DIR/test/MenuTest.java|verticalSwipeReachesLowerMenuActions" > "$DUPLICATE"

sh scripts/check-interaction-command-traceability.sh "$VALID" "$TRANSITIONS" "$TMP_DIR/src" >/dev/null

expect_failure() {
  label="$1"
  contracts="$2"
  if sh scripts/check-interaction-command-traceability.sh "$contracts" "$TRANSITIONS" "$TMP_DIR/src" >/dev/null 2>&1; then
    echo "test-interaction-command-traceability: expected $label to fail" >&2
    exit 1
  fi
}

expect_failure "missing model transition" "$MISSING_MODEL"
expect_failure "missing implementation locator" "$MISSING_IMPLEMENTATION"
expect_failure "missing behavior-test locator" "$MISSING_TEST"
expect_failure "duplicate command contract" "$DUPLICATE"

printf '%s\n' '// interaction-command: unregistered_command' >> "$TMP_DIR/src/Menu.java"
expect_failure "unregistered implementation marker" "$VALID"

echo "test-interaction-command-traceability: passed"
