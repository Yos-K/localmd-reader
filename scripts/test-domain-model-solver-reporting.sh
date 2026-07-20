#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

TMP_BASE="${TMPDIR:-/tmp}"
TMP_DIR="$TMP_BASE/localmd-domain-model-solver-test-$$"
mkdir -p "$TMP_DIR/alloy"
trap 'rm -rf "$TMP_DIR"' EXIT HUP INT TERM

MODEL="$TMP_DIR/model.als"
FAKE_JAVA="$TMP_DIR/fake-java"
touch "$TMP_DIR/alloy/alloy-6.2.0.jar"
printf '%s\n' 'assert Safe {}' 'check Safe expect 0' > "$MODEL"

printf '%s\n' \
  '#!/bin/sh' \
  'case " $* " in' \
  '  *" -s SAT4J "*) ;;' \
  '  *) echo "SAT4J was not explicitly selected" >&2; exit 2 ;;' \
  'esac' \
  'echo "[main] ERROR kodkod.solvers.api.NativeCode - findPlatform unknown Linux aarch64" >&2' \
  'echo "00. check Safe 0 UNSAT"' \
  'exit 0' \
  > "$FAKE_JAVA"
chmod +x "$FAKE_JAVA"

OUTPUT="$(DOMAIN_MODEL_JAVA="$FAKE_JAVA" ALLOY_HOME="$TMP_DIR/alloy" \
  sh scripts/check-domain-model.sh "$MODEL" 2>&1)"

printf '%s\n' "$OUTPUT" | grep -q 'solver=SAT4J' || {
  echo "test-domain-model-solver-reporting: expected explicit SAT4J report" >&2
  exit 1
}
printf '%s\n' "$OUTPUT" | grep -q 'native solvers unavailable; verified with SAT4J' || {
  echo "test-domain-model-solver-reporting: expected classified native solver warning" >&2
  exit 1
}
if printf '%s\n' "$OUTPUT" | grep -q 'ERROR kodkod.solvers.api.NativeCode'; then
  echo "test-domain-model-solver-reporting: raw native solver error must not remain" >&2
  exit 1
fi

echo "test-domain-model-solver-reporting: passed"
