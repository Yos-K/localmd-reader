#!/data/data/com.termux/files/usr/bin/sh
set -eu

REPOSITORY="${1:?usage: setup-test-lab-github-actions.sh OWNER/REPO PROJECT_ID WIF_PROVIDER SERVICE_ACCOUNT RESULTS_BUCKET}"
PROJECT_ID="${2:?Google Cloud project ID is required}"
WIF_PROVIDER="${3:?Workload Identity Provider resource name is required}"
SERVICE_ACCOUNT="${4:?dedicated Test Lab service account email is required}"
RESULTS_BUCKET="${5:?Test Lab results bucket name is required}"
ENVIRONMENT="test-lab"

gh api --method PUT "repos/$REPOSITORY/environments/$ENVIRONMENT" >/dev/null
gh variable set TEST_LAB_PROJECT_ID --body "$PROJECT_ID" --env "$ENVIRONMENT" --repo "$REPOSITORY"
gh variable set TEST_LAB_WORKLOAD_IDENTITY_PROVIDER --body "$WIF_PROVIDER" --env "$ENVIRONMENT" --repo "$REPOSITORY"
gh variable set TEST_LAB_SERVICE_ACCOUNT --body "$SERVICE_ACCOUNT" --env "$ENVIRONMENT" --repo "$REPOSITORY"
gh variable set TEST_LAB_RESULTS_BUCKET --body "$RESULTS_BUCKET" --env "$ENVIRONMENT" --repo "$REPOSITORY"

cat <<EOF
Configured GitHub environment '$ENVIRONMENT' for $REPOSITORY.
No service-account key or application signing secret was stored.

Verify with:
  gh variable list --env $ENVIRONMENT --repo $REPOSITORY
EOF
