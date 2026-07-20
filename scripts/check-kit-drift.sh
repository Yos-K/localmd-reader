#!/data/data/com.termux/files/usr/bin/sh
# Consumed-script drift guard (advisory).
#
# The whole consumable-sync design rests on one invariant: every file listed in
# sync-manifest.yaml's `consumed_scripts` is BYTE-IDENTICAL to harness-kit at the
# pinned `harness_kit_version`. That is what makes "diff-zero sync" true and what
# lets the weekly harness-sync PR be trusted. If a consumed script is edited
# locally without upstreaming (or the pin is wrong), the invariant silently
# breaks and the next sync proposes a confusing reverse-diff.
#
# This guard re-checks the invariant: it diffs each consumed_script against the
# kit source at the pinned tag and reports drift. It is ADVISORY — by default it
# never fails the build (exit 0), so it can detect without blocking. Pass
# --strict to make detected drift a non-zero exit (opt-in gating).
#
# Network/kit unavailability is NEVER fatal (not even under --strict): a missing
# network must not turn into a red gate. Unresolvable kit -> SKIP, exit 0.
#
# Usage:
#   sh scripts/check-kit-drift.sh [--strict]
# Env:
#   HARNESS_KIT_REPO  default Yos-K/harness-kit (owner/name)
#   KIT_DIR           use an existing kit checkout instead of cloning (for tests)
#   GH_TOKEN          optional; used for the clone URL if set (rate limits)
set -u

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

STRICT=0
[ "${1:-}" = "--strict" ] && STRICT=1

MANIFEST="$ROOT/sync-manifest.yaml"
KIT_REPO="${HARNESS_KIT_REPO:-Yos-K/harness-kit}"

skip() { echo "kit-drift: SKIP — $1"; exit 0; }

[ -f "$MANIFEST" ] || skip "sync-manifest.yaml not found (nothing consumed yet)"

PINNED="$(grep '^harness_kit_version:' "$MANIFEST" \
  | sed 's/harness_kit_version: *//; s/"//g' | tr -d '[:space:]')"
ADAPTER="$(grep '^adapter:' "$MANIFEST" \
  | sed 's/adapter: *//; s/"//g' | tr -d '[:space:]')"
[ -n "$PINNED" ] || skip "no harness_kit_version pinned in sync-manifest.yaml"
[ -n "$ADAPTER" ] || ADAPTER="android-jvm"

# consumed_scripts: list entries under the `consumed_scripts:` key (strip "- ",
# inline "# ..." comments, trailing spaces). Same parse as harness-sync.yml.
CONSUMED="$(grep -A10000 '^consumed_scripts:' "$MANIFEST" \
  | grep -E '^[[:space:]]*-[[:space:]]' \
  | sed -E 's/^[[:space:]]*-[[:space:]]*//; s/[[:space:]]*#.*$//; s/[[:space:]]*$//')"
[ -n "$CONSUMED" ] || skip "consumed_scripts is empty"

# Resolve a kit checkout: an explicit KIT_DIR (tests/local), else clone the
# pinned tag into a temp dir. Clone failure is non-fatal (SKIP).
CLONED=""
if [ -n "${KIT_DIR:-}" ]; then
  [ -d "$KIT_DIR" ] || skip "KIT_DIR=$KIT_DIR does not exist"
else
  command -v git >/dev/null 2>&1 || skip "git not available to fetch kit"
  KIT_DIR="$(mktemp -d 2>/dev/null || echo /tmp/kit-drift.$$)"
  mkdir -p "$KIT_DIR"
  CLONED="$KIT_DIR"
  URL="https://github.com/${KIT_REPO}.git"
  [ -n "${GH_TOKEN:-}" ] && URL="https://x-access-token:${GH_TOKEN}@github.com/${KIT_REPO}.git"
  if ! git clone --depth 1 --branch "$PINNED" "$URL" "$KIT_DIR" >/dev/null 2>&1; then
    rm -rf "$CLONED"
    skip "could not clone $KIT_REPO@$PINNED (offline or tag missing) — advisory only"
  fi
fi
cleanup() { [ -n "$CLONED" ] && rm -rf "$CLONED"; }
trap cleanup EXIT INT TERM

KIT_WHITELIST="$KIT_DIR/sync-manifests/${ADAPTER}.yaml"
[ -f "$KIT_WHITELIST" ] || skip "kit whitelist $ADAPTER.yaml absent at $PINNED"

drift=0
unresolved=0
ok=0
report=""

for consumer_file in $CONSUMED; do
  [ -n "$consumer_file" ] || continue
  base="$(basename "$consumer_file")"
  # kit source path = the whitelist line ending in /<basename>
  rel_path="$(grep -E "/${base}\$" "$KIT_WHITELIST" \
    | grep -E '^[[:space:]]*-' | sed -E 's/^[[:space:]]*-[[:space:]]*//' | head -1)"
  if [ -z "$rel_path" ]; then
    report="${report}  UNRESOLVED  $consumer_file (not in kit whitelist)
"
    unresolved=$((unresolved + 1))
    continue
  fi
  kit_file="$KIT_DIR/$rel_path"
  if [ ! -f "$kit_file" ]; then
    report="${report}  UNRESOLVED  $consumer_file (kit source $rel_path missing at $PINNED)
"
    unresolved=$((unresolved + 1))
    continue
  fi
  if [ ! -f "$consumer_file" ]; then
    report="${report}  DRIFT       $consumer_file (declared consumed but absent locally)
"
    drift=$((drift + 1))
    continue
  fi
  if diff -q "$kit_file" "$consumer_file" >/dev/null 2>&1; then
    ok=$((ok + 1))
  else
    report="${report}  DRIFT       $consumer_file vs kit:$rel_path @ $PINNED
"
    drift=$((drift + 1))
  fi
done

echo "kit-drift: pinned=$PINNED adapter=$ADAPTER — ok=$ok drift=$drift unresolved=$unresolved"
[ -n "$report" ] && printf '%s' "$report"

if [ "$drift" -gt 0 ]; then
  cat <<MSG

$drift consumed script(s) no longer match harness-kit @ $PINNED.
The diff-zero invariant is broken. Reconcile by either:
  - reverting the local edit (consumed scripts must stay generic), or
  - upstreaming the change to harness-kit + bumping the pin (see
    docs/harness-portability/harness-kit-migration-proposal.md).
MSG
  if [ "$STRICT" -eq 1 ]; then
    echo "kit-drift: FAIL (--strict)" >&2
    exit 1
  fi
  echo "kit-drift: advisory only — not failing the build (use --strict to gate)."
fi
exit 0
