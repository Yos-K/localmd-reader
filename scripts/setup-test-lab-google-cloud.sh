#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
PROJECT_ID="${1:?usage: setup-test-lab-google-cloud.sh PROJECT_ID WORKLOAD_IDENTITY_POOL [RESULTS_BUCKET] [REGION]}"
POOL_ID="${2:?Workload Identity Pool ID is required}"
RESULTS_BUCKET="${3:-${PROJECT_ID}-localmd-test-lab-results}"
REGION="${4:-asia-northeast1}"
REPOSITORY="Yos-K/localmd-reader"
SERVICE_ACCOUNT_ID="localmd-reader-test-lab"
SERVICE_ACCOUNT="$SERVICE_ACCOUNT_ID@$PROJECT_ID.iam.gserviceaccount.com"
PROJECT_NUMBER="$(gcloud projects describe "$PROJECT_ID" --format='value(projectNumber)')"
WIF_MEMBER="principalSet://iam.googleapis.com/projects/$PROJECT_NUMBER/locations/global/workloadIdentityPools/$POOL_ID/attribute.repository/$REPOSITORY"

gcloud services enable \
  testing.googleapis.com \
  toolresults.googleapis.com \
  storage.googleapis.com \
  --project "$PROJECT_ID"

if ! gcloud iam service-accounts describe "$SERVICE_ACCOUNT" --project "$PROJECT_ID" >/dev/null 2>&1; then
  gcloud iam service-accounts create "$SERVICE_ACCOUNT_ID" \
    --display-name="LocalMD Test Lab physical-device runner" \
    --project "$PROJECT_ID"
fi

gcloud projects add-iam-policy-binding "$PROJECT_ID" \
  --member="serviceAccount:$SERVICE_ACCOUNT" \
  --role="roles/cloudtestservice.testAdmin" >/dev/null
gcloud projects add-iam-policy-binding "$PROJECT_ID" \
  --member="serviceAccount:$SERVICE_ACCOUNT" \
  --role="roles/firebase.analyticsViewer" >/dev/null
gcloud iam service-accounts add-iam-policy-binding "$SERVICE_ACCOUNT" \
  --project "$PROJECT_ID" \
  --member="$WIF_MEMBER" \
  --role="roles/iam.workloadIdentityUser" >/dev/null

if ! gcloud storage buckets describe "gs://$RESULTS_BUCKET" --project "$PROJECT_ID" >/dev/null 2>&1; then
  gcloud storage buckets create "gs://$RESULTS_BUCKET" \
    --project "$PROJECT_ID" \
    --location "$REGION" \
    --uniform-bucket-level-access
fi
gcloud storage buckets update "gs://$RESULTS_BUCKET" \
  --lifecycle-file="$ROOT/config/test-lab-bucket-lifecycle.json" >/dev/null
gcloud storage buckets add-iam-policy-binding "gs://$RESULTS_BUCKET" \
  --member="serviceAccount:$SERVICE_ACCOUNT" \
  --role="roles/storage.objectAdmin" >/dev/null

cat <<EOF
Configured the dedicated Test Lab identity and seven-day result bucket.

Project: $PROJECT_ID
Service account: $SERVICE_ACCOUNT
Results bucket: $RESULTS_BUCKET

Next, register these values in GitHub:
  sh $ROOT/scripts/setup-test-lab-github-actions.sh \\
    $REPOSITORY \\
    $PROJECT_ID \\
    projects/$PROJECT_NUMBER/locations/global/workloadIdentityPools/$POOL_ID/providers/PROVIDER_ID \\
    $SERVICE_ACCOUNT \\
    $RESULTS_BUCKET

Replace PROVIDER_ID with the provider ID already configured in the pool.
EOF
