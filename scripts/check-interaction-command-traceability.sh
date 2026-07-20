#!/data/data/com.termux/files/usr/bin/sh
# Keep modeled interaction commands, implementation entry points, and behavior tests aligned.
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

CONTRACTS="${1:-docs/harness/interaction-command-contracts.psv}"
TRANSITIONS="${2:-docs/harness/interaction-model-transitions.psv}"
SOURCE_ROOT="${3:-src/main/java}"

for required in "$CONTRACTS" "$TRANSITIONS"; do
  [ -f "$required" ] || {
    echo "interaction-command-traceability: missing spec: $required" >&2
    exit 1
  }
done
[ -d "$SOURCE_ROOT" ] || {
  echo "interaction-command-traceability: missing source directory: $SOURCE_ROOT" >&2
  exit 1
}

failures=0
fail() {
  failures=$((failures + 1))
  echo "interaction-command-traceability: FAIL: $1" >&2
}

contains_locator() {
  grep -F -q -- "$2" "$1"
}

contract_exists() {
  awk -F'|' -v state="$1" -v command="$2" '
    $1 == state && $2 == command { found = 1 }
    END { exit(found ? 0 : 1) }
  ' "$CONTRACTS"
}

transition_exists() {
  awk -F'|' -v state="$1" -v command="$2" '
    $1 == state && $2 == command { found = 1 }
    END { exit(found ? 0 : 1) }
  ' "$TRANSITIONS"
}

DUPLICATE_KEYS="$(awk -F'|' '
  $1 != "" && substr($1, 1, 1) != "#" && $1 != "state_id" {
    key = $1 "|" $2
    if (++seen[key] == 2) print key
  }
' "$CONTRACTS")"
for key in $DUPLICATE_KEYS; do
  fail "duplicate command contract: $key"
done

while IFS='|' read -r state command implementation implementation_locator test test_locator extra
do
  case "$state" in
    ""|"#"*) continue ;;
  esac
  [ "$state" = "state_id" ] && continue
  [ -z "${extra:-}" ] || fail "$state/$command has too many columns"
  for value in "$state" "$command" "$implementation" "$implementation_locator" "$test" "$test_locator"; do
    [ -n "$value" ] && [ "$value" != "-" ] || fail "$state/$command has a required empty field"
  done
  key="$state|$command"
  transition_exists "$state" "$command" || fail "$key has no modeled transition"
  if [ ! -f "$implementation" ]; then
    fail "$key implementation file does not exist: $implementation"
  elif ! contains_locator "$implementation" "$implementation_locator"; then
    fail "$key implementation locator is absent: $implementation_locator"
  fi
  if [ ! -f "$test" ]; then
    fail "$key behavior test does not exist: $test"
  elif ! contains_locator "$test" "$test_locator"; then
    fail "$key behavior-test locator is absent: $test_locator"
  fi
done < "$CONTRACTS"

MARKERS="$(grep -R -h -E 'interaction-command:[[:space:]]*[a-z0-9_-]+' "$SOURCE_ROOT" 2>/dev/null \
  | sed -n 's/^.*interaction-command:[[:space:]]*\([a-z0-9_-]*\).*$/\1/p' | sort -u)"
for command in $MARKERS; do
  if ! awk -F'|' -v command="$command" '$2 == command { found = 1 } END { exit(found ? 0 : 1) }' "$CONTRACTS"; then
    fail "implemented command marker has no contract: $command"
  fi
done

if [ "$failures" -gt 0 ]; then
  echo "interaction-command-traceability: $failures model/implementation/test gap(s) found." >&2
  exit 1
fi

echo "interaction-command-traceability: modeled commands have implementation and behavior-test evidence."
