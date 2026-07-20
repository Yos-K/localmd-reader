#!/data/data/com.termux/files/usr/bin/sh
# Check that interaction-storming states have the commands required to finish,
# escape, and recover from the user's current flow.
set -u

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

SPEC="${1:-docs/harness/interaction-storming-flows.psv}"

if [ ! -f "$SPEC" ]; then
  echo "interaction-storming: missing spec: $SPEC" >&2
  exit 1
fi

failures=0
line_no=0

fail() {
  failures=$((failures + 1))
  echo "interaction-storming: FAIL line $line_no: $1" >&2
}

is_blank_or_dash() {
  case "$1" in
    ""|"-") return 0 ;;
    *) return 1 ;;
  esac
}

field_required() {
  field_name="$1"
  field_value="$2"
  if is_blank_or_dash "$field_value"; then
    fail "$field_name is required"
  fi
}

command_must_be_available() {
  label="$1"
  command="$2"
  available="$3"
  case ",$available," in
    *",$command,"*) : ;;
    *) fail "$label '$command' is not listed in available_commands '$available'" ;;
  esac
}

evidence_must_exist() {
  evidence="$1"
  old_ifs="$IFS"
  IFS=','
  for path in $evidence; do
    IFS="$old_ifs"
    if is_blank_or_dash "$path"; then
      fail "evidence contains an empty path"
    elif [ ! -e "$path" ]; then
      fail "evidence path does not exist: $path"
    fi
    IFS=','
  done
  IFS="$old_ifs"
}

while IFS='|' read -r flow_id state_id event available completion escape recovery evidence extra
do
  line_no=$((line_no + 1))

  case "$flow_id" in
    ""|"#"*) continue ;;
  esac
  if [ "$flow_id" = "flow_id" ]; then
    continue
  fi

  if [ -n "${extra:-}" ]; then
    fail "too many columns"
  fi

  field_required "flow_id" "$flow_id"
  field_required "state_id" "$state_id"
  field_required "event" "$event"
  field_required "available_commands" "$available"
  field_required "completion_command" "$completion"
  field_required "escape_command" "$escape"
  field_required "recovery_command" "$recovery"
  field_required "evidence" "$evidence"

  if ! is_blank_or_dash "$available"; then
    is_blank_or_dash "$completion" || command_must_be_available "completion_command" "$completion" "$available"
    is_blank_or_dash "$escape" || command_must_be_available "escape_command" "$escape" "$available"
    is_blank_or_dash "$recovery" || command_must_be_available "recovery_command" "$recovery" "$available"
  fi

  is_blank_or_dash "$evidence" || evidence_must_exist "$evidence"
done < "$SPEC"

if [ "$failures" -gt 0 ]; then
  echo "interaction-storming: $failures gap(s) found. Add the missing command or document why the state is terminal." >&2
  exit 1
fi

echo "interaction-storming: all modeled states have completion, escape, recovery, and evidence."
