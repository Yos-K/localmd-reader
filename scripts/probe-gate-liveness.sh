#!/bin/sh
# [汎用core] ゲート生存証明プローブ — スタック非依存
# Second-order harness check (ROADMAP P4). A *prevention* gate (secrets / PR-title /
# file-size) is supposed to never fire in normal operation — but "never fires" looks
# identical whether the gate is working or has silently broken. This probe injects a
# synthetic violation into each gate and confirms the gate REJECTS it. It is the
# survival proof for prevention layers — the harness analogue of mutation testing
# (deliberately inject a fault and check that the guard catches it).
#
#   ALIVE  the gate rejected the injected violation (it is enforcing)
#   DEAD   the gate accepted the violation (it is NOT enforcing — needs attention)
#
# Each probe runs the gate in a throwaway git repo with scripts/<gate> copied in, so
# the gate resolves its own ROOT exactly as it would in a consumer. Nothing in this
# repo is mutated.
#
# Modes:
#   probe-gate-liveness.sh
#       Probe the prevention gates listed in harness.config.sh PROBE_GATES; if unset,
#       fall back to this kit's own three core gates (dogfood). Exit 1 if any gate is DEAD.
#       This is the survival-proof view: it probes EVERY gate, including escalation-only ones,
#       so a silently-broken human-owned gate still surfaces (for a human / gatecrate-evaluate).
#   probe-gate-liveness.sh --repairable-only
#       Same, but deterministically SKIP gates marked escalation-only (see marker below). This is
#       the view the harness-liveness-converge TAKT loop uses: the loop must only ever see gates
#       an agent may auto-repair. Excluding escalation-only gates here is what structurally prevents
#       the loop from pressuring the agent to edit a human-owned gate (the exp3 failure mode) —
#       a deterministic pre-loop triage, not a persona rule it can rationalize past.
#   probe-gate-liveness.sh --one <gate_path> <kind>
#       Probe a single gate; ALIVE -> exit 0, DEAD -> exit 1.  kind: title | secrets | filesize
#
# Consumer config (optional, from harness.config.sh in the consumer repo root, or env):
#   PROBE_GATES — whitespace/newline separated "<repo-root-relative-path>:<kind>" tokens,
#                 e.g. "scripts/check-no-committed-secrets.sh:secrets scripts/check-title.sh:title".
#                 kind is one of title|secrets|filesize (the injectable violation classes).
#                 Unset -> the kit's own three core gates (backward compatible).
#
# Escalation-only marker: a gate whose repair is reserved for a human (security-owned policy, or a
# gate whose guarded risk vanished -> removal proposal) carries this comment line in its own file:
#       # gatecrate-scope: escalation-only
#   --repairable-only excludes such gates from the converge loop; the default survival-proof mode
#   still probes them (and reports DEAD ones) so a human is not left unaware.
set -eu

ROOT="$(git -C "$(dirname -- "$0")" rev-parse --show-toplevel 2>/dev/null \
  || (CDPATH= cd -- "$(dirname -- "$0")/../.." && pwd))"
# shellcheck source=/dev/null
[ -f "$ROOT/harness.config.sh" ] && . "$ROOT/harness.config.sh"

# The synthetic violation for the PR-title gate (no leading type, no colon).
BAD_TITLE='not a valid conventional title'

# inject_secrets <workdir>: track a keystore-like file the secrets gate must reject.
inject_secrets() {
  printf 'storePassword=hunter2\n' > "$1/key.properties"
  git -C "$1" add key.properties >/dev/null 2>&1
}

# inject_filesize <workdir>: drop a file past the 300-line limit the size gate enforces.
inject_filesize() {
  i=0
  while [ "$i" -lt 301 ]; do echo "# bloat line $i"; i=$((i + 1)); done > "$1/bloat.sh"
}

# run_gate <workdir> <gate_basename> <kind>: execute the copied gate against the
# injected violation and return the gate's own exit code.
run_gate() {
  wd="$1"; base="$2"; kind="$3"
  case "$kind" in
    title)    ( cd "$wd" && sh "scripts/$base" "$BAD_TITLE" ) ;;
    secrets)  ( cd "$wd" && sh "scripts/$base" ) ;;
    filesize) ( cd "$wd" && FILE_LINE_PATHS="." FILE_LINE_NAMES="*.sh" sh "scripts/$base" ) ;;
    *) echo "probe: unknown kind: $kind" >&2; return 2 ;;
  esac
}

# is_escalation_only <gate_path>: true if the gate file is marked escalation-only, i.e. its
# repair is reserved for a human (security-owned policy / premise-vanished removal). The marker is
# a deterministic comment in the gate's own file, so the converge loop can EXCLUDE such gates
# instead of trusting the agent not to edit them under loop pressure (the exp3 finding).
is_escalation_only() {
  [ -f "$1" ] || return 1
  grep -Eq '^[[:space:]]*#[[:space:]]*gatecrate-scope:[[:space:]]*escalation-only([[:space:]]|$)' "$1"
}

# probe_gate <gate_path> <kind>: print "ALIVE <name>" / "DEAD <name>";
# return 0 if alive, 1 if dead, 2 on setup error.
#
# The gate's exit code is interpreted STRICTLY: 0 = it accepted the violation (DEAD),
# 1 = it rejected the violation (ALIVE). Any other code (and an invalid/typo'd kind) is a
# SETUP ERROR, not a survival proof — we must NOT report ALIVE for it. Otherwise a mistyped
# PROBE_GATES kind (e.g. "secret" or a missing colon) would skip the gate entirely yet exit 0,
# a false survival proof — exactly the silent failure this probe exists to catch.
probe_gate() {
  gate="$1"; kind="$2"
  case "$kind" in
    title|secrets|filesize) ;;
    *) echo "probe: invalid kind '$kind' for $gate (expected title|secrets|filesize — check PROBE_GATES)" >&2
       return 2 ;;
  esac
  [ -f "$gate" ] || { echo "probe: gate not found: $gate" >&2; return 2; }
  base="$(basename "$gate")"
  wd="$(mktemp -d)"
  git -C "$wd" init -q
  mkdir -p "$wd/scripts"
  cp "$gate" "$wd/scripts/$base"
  case "$kind" in
    secrets)  inject_secrets "$wd" ;;
    filesize) inject_filesize "$wd" ;;
    title)    : ;;  # injected via argv inside run_gate
  esac
  rc=0
  run_gate "$wd" "$base" "$kind" >/dev/null 2>&1 || rc=$?
  rm -rf "$wd"
  case "$rc" in
    0) echo "DEAD  $base ($kind) — accepted a synthetic violation"; return 1 ;;
    1) echo "ALIVE $base ($kind)"; return 0 ;;
    *) echo "probe: $base ($kind) neither cleanly accepted nor rejected (rc=$rc) — setup error, not a survival proof" >&2
       return 2 ;;
  esac
}

# ---- flag: --repairable-only excludes escalation-only gates (the converge-loop view) ----
REPAIRABLE_ONLY=0
if [ "${1:-}" = "--repairable-only" ]; then
  REPAIRABLE_ONLY=1
  shift
fi

# ---- single-gate mode (used by the tests) ----
if [ "${1:-}" = "--one" ]; then
  probe_gate "$2" "$3"
  exit $?
fi

# ---- default mode: probe the consumer's PROBE_GATES, or the kit's own gates (dogfood) ----
# Each spec is "<path>:<kind>". Consumer-supplied PROBE_GATES uses the same form; when unset we
# fall back to this kit's three core gates so the dogfood path is unchanged.
if [ -n "${PROBE_GATES:-}" ]; then
  specs="$PROBE_GATES"
  echo "Gate liveness probe — survival proof (consumer prevention gates from PROBE_GATES):"
else
  specs="core/scripts/check-conventional-title.sh:title
core/scripts/check-no-committed-secrets.sh:secrets
core/scripts/check-file-line-limit.sh:filesize"
  echo "Gate liveness probe — survival proof of prevention gates:"
fi

dead=0
skipped=0
# shellcheck disable=SC2086  # specs is a controlled list; intentional word-split into tokens
for spec in $specs; do
  gate_path="${spec%:*}"   # everything before the last colon
  kind="${spec##*:}"       # the trailing kind token
  full="$ROOT/$gate_path"
  # Deterministic pre-loop triage: in --repairable-only, an escalation-only gate is EXCLUDED so the
  # converge loop never pressures the agent to edit a human-owned gate. The default mode still probes
  # it (a broken human-owned gate must surface), so this skip is converge-scope only.
  if [ "$REPAIRABLE_ONLY" = 1 ] && is_escalation_only "$full"; then
    echo "SKIP  $(basename "$gate_path") — escalation-only (excluded from converge; triage to a human / gatecrate-evaluate)"
    skipped=$((skipped + 1))
    continue
  fi
  probe_gate "$full" "$kind" || dead=$((dead + 1))
done

echo ""
if [ "$skipped" -gt 0 ]; then
  echo "($skipped escalation-only gate(s) excluded from converge — review them via gatecrate-evaluate / a human.)"
fi
if [ "$dead" -gt 0 ]; then
  echo "Liveness probe FAILED: $dead gate(s) DEAD — a prevention gate is not enforcing." >&2
  echo "Investigate the gate: a recent change may have broken its rejection path." >&2
  exit 1
fi
echo "Liveness probe passed: all probed prevention gates are ALIVE."
