#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

TMP_BASE="${TMPDIR:-/tmp}"
TMP_DIR="$TMP_BASE/localmd-interaction-model-test-$$"
mkdir -p "$TMP_DIR"
trap 'rm -rf "$TMP_DIR"' EXIT HUP INT TERM

STATES="$TMP_DIR/states.psv"
STATE_WITHOUT_FLOW="$TMP_DIR/state-without-flow.psv"
STATE_WITHOUT_SURFACE="$TMP_DIR/state-without-surface.psv"
OVERLAY_EXIT_STATES="$TMP_DIR/overlay-exit-states.psv"
VALID_TRANSITIONS="$TMP_DIR/valid-transitions.psv"
STATE_WITHOUT_FLOW_TRANSITIONS="$TMP_DIR/state-without-flow-transitions.psv"
STATE_WITHOUT_SURFACE_TRANSITIONS="$TMP_DIR/state-without-surface-transitions.psv"
BROKEN_TRANSITIONS="$TMP_DIR/broken-transitions.psv"
DUPLICATE_COMMAND_TRANSITIONS="$TMP_DIR/duplicate-command-transitions.psv"
UNMODELED_AVAILABLE_TRANSITIONS="$TMP_DIR/unmodeled-available-transitions.psv"
NO_DIRECT_OVERLAY_EXIT_TRANSITIONS="$TMP_DIR/no-direct-overlay-exit-transitions.psv"
FLOWS="$TMP_DIR/flows.psv"
FLOWS_WITH_EXTRA_AVAILABLE="$TMP_DIR/flows-with-extra-available.psv"
SURFACES="$TMP_DIR/surfaces.psv"
BROKEN_SURFACES="$TMP_DIR/broken-surfaces.psv"
BROKEN_SURFACE_EVIDENCE="$TMP_DIR/broken-surface-evidence.psv"
BROKEN_SURFACE_LOCATOR="$TMP_DIR/broken-surface-locator.psv"

printf '%s\n' \
  '# state_id|kind|root|evidence' \
  'welcome|stable|yes|scripts/check-interaction-model.sh' \
  'dialog|overlay|no|scripts/check-interaction-model.sh' \
  > "$STATES"

printf '%s\n' \
  '# state_id|kind|root|evidence' \
  'welcome|stable|yes|scripts/check-interaction-model.sh' \
  'dialog|overlay|no|scripts/check-interaction-model.sh' \
  'unmodeled-dialog|overlay|no|scripts/check-interaction-model.sh' \
  > "$STATE_WITHOUT_FLOW"

printf '%s\n' \
  '# state_id|kind|root|evidence' \
  'welcome|stable|yes|scripts/check-interaction-model.sh' \
  'dialog|overlay|no|scripts/check-interaction-model.sh' \
  'surface-missing-dialog|overlay|no|scripts/check-interaction-model.sh' \
  > "$STATE_WITHOUT_SURFACE"

printf '%s\n' \
  '# state_id|kind|root|evidence' \
  'welcome|stable|yes|scripts/check-interaction-model.sh' \
  'dialog|overlay|no|scripts/check-interaction-model.sh' \
  'nested|overlay|no|scripts/check-interaction-model.sh' \
  > "$OVERLAY_EXIT_STATES"

printf '%s\n' \
  '# from_state|command|to_state|event' \
  'welcome|open_dialog|dialog|Dialog opened' \
  'dialog|complete|welcome|Completed' \
  'dialog|escape|welcome|Escaped' \
  'dialog|recover|dialog|Recovered' \
  > "$VALID_TRANSITIONS"

printf '%s\n' \
  '# from_state|command|to_state|event' \
  'welcome|open_dialog|dialog|Dialog opened' \
  'dialog|complete|welcome|Completed' \
  'dialog|escape|welcome|Escaped' \
  'dialog|recover|dialog|Recovered' \
  'welcome|open_unmodeled_dialog|unmodeled-dialog|Unmodeled dialog opened' \
  'unmodeled-dialog|close|welcome|Unmodeled dialog closed' \
  > "$STATE_WITHOUT_FLOW_TRANSITIONS"

printf '%s\n' \
  '# from_state|command|to_state|event' \
  'welcome|open_dialog|dialog|Dialog opened' \
  'dialog|complete|welcome|Completed' \
  'dialog|escape|welcome|Escaped' \
  'dialog|recover|welcome|Recovered' \
  'welcome|open_surface_missing_dialog|surface-missing-dialog|Surface missing dialog opened' \
  'surface-missing-dialog|complete|welcome|Completed' \
  'surface-missing-dialog|escape|welcome|Escaped' \
  'surface-missing-dialog|recover|welcome|Recovered' \
  > "$STATE_WITHOUT_SURFACE_TRANSITIONS"

printf '%s\n' \
  '# from_state|command|to_state|event' \
  'welcome|open_dialog|dialog|Dialog opened' \
  'dialog|complete|welcome|Completed' \
  'dialog|recover|dialog|Recovered' \
  > "$BROKEN_TRANSITIONS"

printf '%s\n' \
  '# from_state|command|to_state|event' \
  'welcome|open_dialog|dialog|Dialog opened' \
  'dialog|complete|welcome|Completed' \
  'dialog|complete|dialog|Conflicting completion' \
  'dialog|escape|welcome|Escaped' \
  'dialog|recover|dialog|Recovered' \
  > "$DUPLICATE_COMMAND_TRANSITIONS"

printf '%s\n' \
  '# from_state|command|to_state|event' \
  'welcome|open_dialog|dialog|Dialog opened' \
  'dialog|complete|welcome|Completed' \
  'dialog|escape|welcome|Escaped' \
  'dialog|recover|dialog|Recovered' \
  > "$UNMODELED_AVAILABLE_TRANSITIONS"

printf '%s\n' \
  '# from_state|command|to_state|event' \
  'welcome|open_dialog|dialog|Dialog opened' \
  'dialog|complete|nested|Completed without leaving overlay' \
  'dialog|escape|nested|Escape opens another overlay' \
  'dialog|recover|dialog|Recovered' \
  'nested|close|welcome|Nested overlay closed' \
  > "$NO_DIRECT_OVERLAY_EXIT_TRANSITIONS"

printf '%s\n' \
  '# flow_id|state_id|event|available_commands|completion_command|escape_command|recovery_command|evidence' \
  'flow|dialog|Dialog displayed|complete,escape,recover|complete|escape|recover|scripts/check-interaction-model.sh' \
  > "$FLOWS"

printf '%s\n' \
  '# flow_id|state_id|event|available_commands|completion_command|escape_command|recovery_command|evidence' \
  'flow|dialog|Dialog displayed|complete,escape,recover,orphan|complete|escape|recover|scripts/check-interaction-model.sh' \
  > "$FLOWS_WITH_EXTRA_AVAILABLE"

FLOWS_WITH_SURFACE_MISSING_STATE="$TMP_DIR/flows-with-surface-missing-state.psv"
printf '%s\n' \
  '# flow_id|state_id|event|available_commands|completion_command|escape_command|recovery_command|evidence' \
  'flow|dialog|Dialog displayed|complete,escape,recover|complete|escape|recover|scripts/check-interaction-model.sh' \
  'flow|surface-missing-dialog|Surface missing dialog displayed|complete,escape,recover|complete|escape|recover|scripts/check-interaction-model.sh' \
  > "$FLOWS_WITH_SURFACE_MISSING_STATE"

printf '%s\n' \
  '# surface_id|state_id|evidence|locator' \
  'welcome-surface|welcome|scripts/check-interaction-model.sh|Model-check the interaction-storming state graph' \
  'dialog-surface|dialog|scripts/check-interaction-model.sh|interaction-model: all modeled states' \
  > "$SURFACES"

printf '%s\n' \
  '# surface_id|state_id|evidence|locator' \
  'missing-surface|missing-dialog|scripts/check-interaction-model.sh|Model-check the interaction-storming state graph' \
  > "$BROKEN_SURFACES"

printf '%s\n' \
  '# surface_id|state_id|evidence|locator' \
  'welcome-surface|welcome|scripts/check-interaction-model.sh|Model-check the interaction-storming state graph' \
  'dialog-surface|dialog|scripts/does-not-exist-for-interaction-model-test.sh|missing implementation' \
  > "$BROKEN_SURFACE_EVIDENCE"

printf '%s\n' \
  '# surface_id|state_id|evidence|locator' \
  'welcome-surface|welcome|scripts/check-interaction-model.sh|Model-check the interaction-storming state graph' \
  'dialog-surface|dialog|scripts/check-interaction-model.sh|identifier-that-does-not-exist' \
  > "$BROKEN_SURFACE_LOCATOR"

sh scripts/check-interaction-model.sh "$STATES" "$VALID_TRANSITIONS" "$FLOWS" "$SURFACES" >/dev/null

set +e
sh scripts/check-interaction-model.sh "$STATES" "$BROKEN_TRANSITIONS" "$FLOWS" "$SURFACES" >/dev/null 2>&1
missing_transition_status=$?
set -e

case "$missing_transition_status" in
  0)
    echo "test-interaction-model-check: expected missing transition to fail" >&2
    exit 1
    ;;
  *)
    :
    ;;
esac

set +e
sh scripts/check-interaction-model.sh "$STATES" "$DUPLICATE_COMMAND_TRANSITIONS" "$FLOWS" "$SURFACES" >/dev/null 2>&1
duplicate_command_transition_status=$?
set -e

case "$duplicate_command_transition_status" in
  0)
    echo "test-interaction-model-check: expected duplicate command transition to fail" >&2
    exit 1
    ;;
  *)
    :
    ;;
esac

set +e
sh scripts/check-interaction-model.sh "$STATES" "$UNMODELED_AVAILABLE_TRANSITIONS" "$FLOWS_WITH_EXTRA_AVAILABLE" "$SURFACES" >/dev/null 2>&1
unmodeled_available_status=$?
set -e

case "$unmodeled_available_status" in
  0)
    echo "test-interaction-model-check: expected unmodeled available command to fail" >&2
    exit 1
    ;;
  *)
    :
    ;;
esac

set +e
sh scripts/check-interaction-model.sh "$OVERLAY_EXIT_STATES" "$NO_DIRECT_OVERLAY_EXIT_TRANSITIONS" "$FLOWS" "$SURFACES" >/dev/null 2>&1
no_direct_overlay_exit_status=$?
set -e

case "$no_direct_overlay_exit_status" in
  0)
    echo "test-interaction-model-check: expected overlay without direct stable exit to fail" >&2
    exit 1
    ;;
  *)
    :
    ;;
esac

set +e
sh scripts/check-interaction-model.sh "$STATES" "$VALID_TRANSITIONS" "$FLOWS" "$BROKEN_SURFACES" >/dev/null 2>&1
missing_surface_state_status=$?
set -e

case "$missing_surface_state_status" in
  0)
    echo "test-interaction-model-check: expected surface without modeled state to fail" >&2
    exit 1
    ;;
  *)
    :
    ;;
esac

set +e
sh scripts/check-interaction-model.sh "$STATE_WITHOUT_FLOW" "$STATE_WITHOUT_FLOW_TRANSITIONS" "$FLOWS" "$SURFACES" >/dev/null 2>&1
state_without_flow_status=$?
set -e

case "$state_without_flow_status" in
  0)
    echo "test-interaction-model-check: expected non-stable state without flow to fail" >&2
    exit 1
    ;;
  *)
    :
    ;;
esac

set +e
sh scripts/check-interaction-model.sh "$STATE_WITHOUT_SURFACE" "$STATE_WITHOUT_SURFACE_TRANSITIONS" "$FLOWS_WITH_SURFACE_MISSING_STATE" "$SURFACES" >/dev/null 2>&1
state_without_surface_status=$?
set -e

case "$state_without_surface_status" in
  0)
    echo "test-interaction-model-check: expected state without surface to fail" >&2
    exit 1
    ;;
  *)
    :
    ;;
esac

set +e
sh scripts/check-interaction-model.sh "$STATES" "$VALID_TRANSITIONS" "$FLOWS" "$BROKEN_SURFACE_EVIDENCE" >/dev/null 2>&1
broken_surface_evidence_status=$?
set -e

case "$broken_surface_evidence_status" in
  0)
    echo "test-interaction-model-check: expected surface with missing evidence to fail" >&2
    exit 1
    ;;
  *)
    :
    ;;
esac

set +e
sh scripts/check-interaction-model.sh "$STATES" "$VALID_TRANSITIONS" "$FLOWS" "$BROKEN_SURFACE_LOCATOR" >/dev/null 2>&1
broken_surface_locator_status=$?
set -e

case "$broken_surface_locator_status" in
  0)
    echo "test-interaction-model-check: expected surface with missing implementation locator to fail" >&2
    exit 1
    ;;
  *)
    :
    ;;
esac

echo "test-interaction-model-check: passed"
