#!/data/data/com.termux/files/usr/bin/sh
# Model-check the interaction-storming state graph.
set -u

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

STATES="${1:-docs/harness/interaction-model-states.psv}"
TRANSITIONS="${2:-docs/harness/interaction-model-transitions.psv}"
FLOWS="${3:-docs/harness/interaction-storming-flows.psv}"
SURFACES="${4:-docs/harness/interaction-model-surfaces.psv}"

for file in "$STATES" "$TRANSITIONS" "$FLOWS" "$SURFACES"; do
  if [ ! -f "$file" ]; then
    echo "interaction-model: missing spec: $file" >&2
    exit 1
  fi
done

failures=0
line_no=0

fail_evidence() {
  failures=$((failures + 1))
  echo "interaction-model: FAIL line $line_no: $1" >&2
}

is_blank_or_dash() {
  case "$1" in
    ""|"-") return 0 ;;
    *) return 1 ;;
  esac
}

evidence_must_exist() {
  evidence="$1"
  old_ifs="$IFS"
  IFS=','
  for path in $evidence; do
    IFS="$old_ifs"
    if is_blank_or_dash "$path"; then
      fail_evidence "state evidence contains an empty path"
    elif [ ! -e "$path" ]; then
      fail_evidence "state evidence path does not exist: $path"
    fi
    IFS=','
  done
  IFS="$old_ifs"
}

evidence_must_contain_locator() {
  evidence="$1"
  locator="$2"
  old_ifs="$IFS"
  IFS=','
  for path in $evidence; do
    IFS="$old_ifs"
    if [ -f "$path" ] && grep -F -q -- "$locator" "$path"; then
      IFS="$old_ifs"
      return 0
    fi
    IFS=','
  done
  IFS="$old_ifs"
  fail_evidence "surface locator is absent from evidence: $locator"
}

while IFS='|' read -r state_id kind root evidence extra
do
  line_no=$((line_no + 1))
  case "$state_id" in
    ""|"#"*) continue ;;
  esac
  if [ "$state_id" = "state_id" ]; then
    continue
  fi
  if [ -n "${extra:-}" ]; then
    fail_evidence "too many state columns"
  fi
  if is_blank_or_dash "$state_id" || is_blank_or_dash "$kind" || is_blank_or_dash "$root" || is_blank_or_dash "$evidence"; then
    fail_evidence "state_id, kind, root, and evidence are required"
  fi
  case "$kind" in
    stable|overlay|entry) : ;;
    *) fail_evidence "unknown state kind: $kind" ;;
  esac
  case "$root" in
    yes|no) : ;;
    *) fail_evidence "root must be yes or no: $root" ;;
  esac
  is_blank_or_dash "$evidence" || evidence_must_exist "$evidence"
done < "$STATES"

line_no=0
while IFS='|' read -r surface_id surface_state evidence locator extra
do
  line_no=$((line_no + 1))
  case "$surface_id" in
    ""|"#"*) continue ;;
  esac
  if [ "$surface_id" = "surface_id" ]; then
    continue
  fi
  if [ -n "${extra:-}" ]; then
    fail_evidence "too many surface columns"
  fi
  if is_blank_or_dash "$surface_id" || is_blank_or_dash "$surface_state" || is_blank_or_dash "$evidence" || is_blank_or_dash "$locator"; then
    fail_evidence "surface_id, state_id, evidence, and locator are required"
  fi
  is_blank_or_dash "$evidence" || evidence_must_exist "$evidence"
  if ! is_blank_or_dash "$evidence" && ! is_blank_or_dash "$locator"; then
    evidence_must_contain_locator "$evidence" "$locator"
  fi
done < "$SURFACES"

awk -F'|' -v STATES="$STATES" -v TRANSITIONS="$TRANSITIONS" -v FLOWS="$FLOWS" -v SURFACES="$SURFACES" '
function skip_header(first, header) {
  return first == "" || substr(first, 1, 1) == "#" || first == header
}
function fail(message) {
  failures++
  print "interaction-model: FAIL: " message > "/dev/stderr"
}
function require_command(state, command, label) {
  if (command == "" || command == "-") {
    fail(state " has empty " label)
    return
  }
  if (!((state SUBSEP command) in transition)) {
    fail(state " " label " command has no transition: " command)
  }
}
function require_available_commands(state, available, count, idx, available_command) {
  count = split(available, available_commands, ",")
  for (idx = 1; idx <= count; idx++) {
    available_command = available_commands[idx]
    if (available_command == "" || available_command == "-") {
      fail(state " has an empty available command")
    } else if (!((state SUBSEP available_command) in transition)) {
      fail(state " available command has no transition: " available_command)
    }
  }
}
FILENAME == STATES {
  if (skip_header($1, "state_id")) next
  state[$1] = 1
  kind[$1] = $2
  if ($3 == "yes") root[$1] = 1
  next
}
FILENAME == TRANSITIONS {
  if (skip_header($1, "from_state")) next
  transition_key = $1 SUBSEP $2
  if (transition_key in transition) {
    fail("duplicate transition command for state: " $1 " / " $2)
  }
  transition_count++
  from[transition_count] = $1
  command[transition_count] = $2
  to[transition_count] = $3
  transition[transition_key] = $3
  outgoing_to_kind[$1 SUBSEP kind[$3]] = 1
  if (!($1 in state)) fail("transition from unknown state: " $1)
  if (!($3 in state)) fail("transition to unknown state: " $3)
  if ($2 == "" || $2 == "-") fail("transition command is required for " $1 " -> " $3)
  next
}
FILENAME == FLOWS {
  if (skip_header($1, "flow_id")) next
  flow_state = $2
  if (!(flow_state in state)) {
    fail("flow references unknown state: " flow_state)
    next
  }
  flow_seen[flow_state] = 1
  require_available_commands(flow_state, $4)
  require_command(flow_state, $5, "completion")
  require_command(flow_state, $6, "escape")
  require_command(flow_state, $7, "recovery")
  next
}
FILENAME == SURFACES {
  if (skip_header($1, "surface_id")) next
  if ($1 == "" || $1 == "-") fail("surface_id is required")
  if ($2 == "" || $2 == "-") fail("surface state_id is required for " $1)
  if (!($2 in state)) fail("surface references unknown state: " $1 " -> " $2)
  surface_seen[$2] = 1
  next
}
END {
  for (s in state) {
    if (s in root) reachable[s] = 1
    if (kind[s] == "stable") stable_reachable[s] = 1
  }

  changed = 1
  while (changed) {
    changed = 0
    for (i = 1; i <= transition_count; i++) {
      if ((from[i] in reachable) && !(to[i] in reachable)) {
        reachable[to[i]] = 1
        changed = 1
      }
    }
  }

  changed = 1
  while (changed) {
    changed = 0
    for (i = 1; i <= transition_count; i++) {
      if ((to[i] in stable_reachable) && !(from[i] in stable_reachable)) {
        stable_reachable[from[i]] = 1
        changed = 1
      }
    }
  }

  for (s in state) {
    if (!(s in reachable)) fail("state is unreachable from any root: " s)
    if (!(s in stable_reachable)) fail("state cannot reach a stable state: " s)
    if (kind[s] == "overlay" && !((s SUBSEP "stable") in outgoing_to_kind)) {
      fail("overlay state has no direct transition to a stable state: " s)
    }
    if (kind[s] != "stable" && !(s in flow_seen)) {
      fail("non-stable state has no interaction flow: " s)
    }
    if (!(s in surface_seen)) {
      fail("state has no interaction surface: " s)
    }
  }

  if (failures > 0) {
    print "interaction-model: " failures " model gap(s) found." > "/dev/stderr"
    exit 1
  }
}
' "$STATES" "$TRANSITIONS" "$FLOWS" "$SURFACES" || failures=$((failures + 1))

if [ "$failures" -gt 0 ]; then
  exit 1
fi

echo "interaction-model: all modeled states are reachable, stable-recoverable, and backed by transitions."
