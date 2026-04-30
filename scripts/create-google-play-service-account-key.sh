#!/data/data/com.termux/files/usr/bin/sh
set -eu

PROJECT_ID="${1:-}"
SERVICE_ACCOUNT_NAME="${2:-mdlite-play-release}"
OUTPUT_FILE="${3:-google-play-service-account.json}"

if [ -z "$PROJECT_ID" ]; then
  echo "Usage: scripts/create-google-play-service-account-key.sh PROJECT_ID [SERVICE_ACCOUNT_NAME] [OUTPUT_FILE]" >&2
  exit 2
fi

if ! command -v gcloud >/dev/null 2>&1; then
  echo "Missing gcloud. Run this script in Google Cloud Shell or install Google Cloud CLI." >&2
  exit 1
fi

SERVICE_ACCOUNT_EMAIL="$SERVICE_ACCOUNT_NAME@$PROJECT_ID.iam.gserviceaccount.com"

gcloud services enable \
  iam.googleapis.com \
  androidpublisher.googleapis.com \
  --project "$PROJECT_ID"

if gcloud iam service-accounts describe "$SERVICE_ACCOUNT_EMAIL" --project "$PROJECT_ID" >/dev/null 2>&1; then
  echo "Service account already exists: $SERVICE_ACCOUNT_EMAIL"
else
  gcloud iam service-accounts create "$SERVICE_ACCOUNT_NAME" \
    --project "$PROJECT_ID" \
    --display-name "MdLite Reader Play Release"
fi

gcloud iam service-accounts keys create "$OUTPUT_FILE" \
  --iam-account "$SERVICE_ACCOUNT_EMAIL" \
  --project "$PROJECT_ID" \
  --key-file-type json

chmod 600 "$OUTPUT_FILE"

echo "Created key: $OUTPUT_FILE"
echo "Service account email: $SERVICE_ACCOUNT_EMAIL"
echo "Add this email in Play Console -> Users and permissions before using the key."
