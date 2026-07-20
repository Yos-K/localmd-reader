#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

TMP_BASE="${TMPDIR:-/tmp}"
TMP_DIR="$TMP_BASE/localmd-interaction-storming-test-$$"
mkdir -p "$TMP_DIR"
trap 'rm -rf "$TMP_DIR"' EXIT HUP INT TERM

VALID_SPEC="$TMP_DIR/valid.psv"
MISSING_ESCAPE_SPEC="$TMP_DIR/missing-escape.psv"

printf '%s\n' \
  '# flow_id|state_id|event|available_commands|completion_command|escape_command|recovery_command|evidence' \
  'flow|state|Event happened|complete,escape,recover|complete|escape|recover|scripts/check-interaction-storming.sh' \
  > "$VALID_SPEC"

printf '%s\n' \
  '# flow_id|state_id|event|available_commands|completion_command|escape_command|recovery_command|evidence' \
  'flow|state|Event happened|complete,recover|complete|-|recover|scripts/check-interaction-storming.sh' \
  > "$MISSING_ESCAPE_SPEC"

sh scripts/check-interaction-storming.sh "$VALID_SPEC" >/dev/null

set +e
sh scripts/check-interaction-storming.sh "$MISSING_ESCAPE_SPEC" >/dev/null 2>&1
missing_escape_status=$?
set -e

case "$missing_escape_status" in
  0)
    echo "test-interaction-storming-guard: expected missing escape command to fail" >&2
    exit 1
    ;;
  *)
    :
    ;;
esac

echo "test-interaction-storming-guard: passed"
