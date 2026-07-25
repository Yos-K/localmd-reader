#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
WORKFLOW="$ROOT/.github/workflows/physical-device-pro.yml"
RUNNER="$ROOT/scripts/run-test-lab-pro-robo.sh"
SETUP="$ROOT/scripts/setup-test-lab-github-actions.sh"
CLOUD_SETUP="$ROOT/scripts/setup-test-lab-google-cloud.sh"

fail() {
  echo "test-test-lab-workflow-contract: $1" >&2
  exit 1
}

[ -f "$WORKFLOW" ] || fail "physical-device-pro.yml must exist"
[ -f "$RUNNER" ] || fail "Test Lab runner must exist"
[ -f "$SETUP" ] || fail "Test Lab GitHub environment setup must be reproducible"
[ -f "$CLOUD_SETUP" ] || fail "least-privilege Google Cloud setup must be reproducible"

grep -q '^  workflow_dispatch:' "$WORKFLOW" || fail "physical device runs must be manually dispatched"
grep -q 'environment: test-lab' "$WORKFLOW" || fail "cloud credentials must be isolated in the test-lab environment"
grep -q 'id-token: write' "$WORKFLOW" || fail "WIF authentication must use a short-lived GitHub OIDC token"
grep -q 'assembleProPreviewDebug' "$WORKFLOW" || fail "physical testing must exercise the Pro preview variant"
grep -q 'google-github-actions/auth@v3' "$WORKFLOW" || fail "workflow must authenticate without a service-account key"
grep -q 'retention-days: 1' "$WORKFLOW" || fail "physical evidence must not consume long-lived Actions storage"
grep -q 'run-test-lab-pro-robo.sh' "$WORKFLOW" || fail "workflow must delegate the Test Lab policy to the tested runner"

grep -q -- '--type robo' "$RUNNER" || fail "runner must use deterministic Robo exploration"
grep -q 'PHYSICAL' "$RUNNER" || fail "runner must reject virtual device models"
grep -q -- '--timeout' "$RUNNER" || fail "runner must bound paid device execution time"
grep -q 'extract-test-lab-evidence-uris.py' "$RUNNER" || fail "runner must collect screenshot evidence for agent review"

grep -q 'TEST_LAB_WORKLOAD_IDENTITY_PROVIDER' "$SETUP" || fail "setup must register the dedicated WIF provider"
grep -q 'TEST_LAB_SERVICE_ACCOUNT' "$SETUP" || fail "setup must register the dedicated service account"
grep -q 'TEST_LAB_RESULTS_BUCKET' "$SETUP" || fail "setup must register the bounded evidence bucket"

grep -q 'roles/cloudtestservice.testAdmin' "$CLOUD_SETUP" || fail "cloud setup must grant the Test Lab execution role"
grep -q 'roles/firebase.analyticsViewer' "$CLOUD_SETUP" || fail "cloud setup must grant the documented result metadata role"
grep -q 'roles/storage.objectAdmin' "$CLOUD_SETUP" || fail "cloud setup must scope result object access to the dedicated bucket"
grep -q 'roles/iam.workloadIdentityUser' "$CLOUD_SETUP" || fail "cloud setup must bind GitHub OIDC without a key"
! grep -q 'roles/editor' "$CLOUD_SETUP" || fail "cloud setup must not grant the broad project Editor role"

echo "Test Lab physical workflow contract test passed"
