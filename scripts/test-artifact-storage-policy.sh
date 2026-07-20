#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

TMP_BASE="${TMPDIR:-/tmp}"
TMP_DIR="$TMP_BASE/localmd-artifact-policy-test-$$"
mkdir -p "$TMP_DIR"
trap 'rm -rf "$TMP_DIR"' EXIT HUP INT TERM

VALID="$TMP_DIR/valid.yml"
MISSING_RETENTION="$TMP_DIR/missing-retention.yml"
EXCESSIVE_RETENTION="$TMP_DIR/excessive-retention.yml"
CI_VALID="$TMP_DIR/ci-valid.yml"
CI_UNBOUNDED_BUILD="$TMP_DIR/ci-unbounded-build.yml"

printf '%s\n' \
  'steps:' \
  '  - name: Upload evidence' \
  '    uses: actions/upload-artifact@v5' \
  '    with:' \
  '      path: evidence/' \
  '      retention-days: 3' \
  > "$VALID"

printf '%s\n' \
  'steps:' \
  '  - name: Upload evidence' \
  '    uses: actions/upload-artifact@v5' \
  '    with:' \
  '      path: evidence/' \
  > "$MISSING_RETENTION"

printf '%s\n' \
  'steps:' \
  '  - name: Upload evidence' \
  '    uses: actions/upload-artifact@v5' \
  '    with:' \
  '      path: evidence/' \
  '      retention-days: 30' \
  > "$EXCESSIVE_RETENTION"

printf '%s\n' \
  'steps:' \
  '  - name: Resolve build metadata' \
  "    if: github.event_name == 'pull_request'" \
  '  - name: Build Free debug APK' \
  "    if: github.event_name == 'pull_request'" \
  '  - name: Build Pro preview debug APK' \
  "    if: github.event_name == 'pull_request'" \
  > "$CI_VALID"

printf '%s\n' \
  'steps:' \
  '  - name: Resolve build metadata' \
  "    if: github.event_name == 'pull_request'" \
  '  - name: Build Free debug APK' \
  '    env:' \
  '  - name: Build Pro preview debug APK' \
  "    if: github.event_name == 'pull_request'" \
  > "$CI_UNBOUNDED_BUILD"

ARTIFACT_POLICY_CI_WORKFLOW="$CI_VALID" sh scripts/check-artifact-storage-policy.sh "$VALID" >/dev/null

if ARTIFACT_POLICY_CI_WORKFLOW="$CI_VALID" sh scripts/check-artifact-storage-policy.sh "$MISSING_RETENTION" >/dev/null 2>&1; then
  echo "test-artifact-storage-policy: expected missing retention to fail" >&2
  exit 1
fi

if ARTIFACT_POLICY_CI_WORKFLOW="$CI_VALID" sh scripts/check-artifact-storage-policy.sh "$EXCESSIVE_RETENTION" >/dev/null 2>&1; then
  echo "test-artifact-storage-policy: expected excessive retention to fail" >&2
  exit 1
fi

if ARTIFACT_POLICY_CI_WORKFLOW="$CI_UNBOUNDED_BUILD" sh scripts/check-artifact-storage-policy.sh "$VALID" >/dev/null 2>&1; then
  echo "test-artifact-storage-policy: expected unbounded debug build to fail" >&2
  exit 1
fi

echo "test-artifact-storage-policy: passed"
