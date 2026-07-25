#!/usr/bin/env bash
set -euo pipefail

APP_APK="${1:?usage: run-test-lab-pro-robo.sh APP_APK DEVICE_MODEL [VERSION]}"
DEVICE_MODEL="${2:?physical device model is required}"
ANDROID_VERSION="${3:-}"
ARTIFACT_DIR="${TEST_LAB_ARTIFACT_DIR:-test-lab-artifacts}"
LOCALE="${TEST_LAB_LOCALE:-ja}"
ORIENTATION="${TEST_LAB_ORIENTATION:-portrait}"
TIMEOUT_SECONDS="${TEST_LAB_TIMEOUT_SECONDS:-600}"
RESULTS_BUCKET="${TEST_LAB_RESULTS_BUCKET:?TEST_LAB_RESULTS_BUCKET is required}"
RESULTS_DIR="${TEST_LAB_RESULTS_DIR:-localmd-pro-${GITHUB_RUN_ID:-manual}-$(date -u +%Y%m%dT%H%M%SZ)}"

[[ -f "$APP_APK" ]] || { echo "Test Lab APK not found: $APP_APK" >&2; exit 2; }
[[ "$ORIENTATION" == "portrait" || "$ORIENTATION" == "landscape" ]] || {
  echo "Test Lab orientation must be portrait or landscape" >&2
  exit 2
}
case "$TIMEOUT_SECONDS" in
  300|600|900) ;;
  *) echo "Test Lab timeout must be 300, 600, or 900 seconds" >&2; exit 2 ;;
esac

mkdir -p "$ARTIFACT_DIR/screenshots"

gcloud firebase test android models describe "$DEVICE_MODEL" --format=json \
  > "$ARTIFACT_DIR/device-model.json"
DEVICE_FORM="$(gcloud firebase test android models describe "$DEVICE_MODEL" --format='value(form)')"
[[ "$DEVICE_FORM" == "PHYSICAL" ]] || {
  echo "Refusing Test Lab run: $DEVICE_MODEL is $DEVICE_FORM, not PHYSICAL" >&2
  exit 2
}

DEVICE="model=$DEVICE_MODEL,locale=$LOCALE,orientation=$ORIENTATION"
if [[ -n "$ANDROID_VERSION" ]]; then
  DEVICE="$DEVICE,version=$ANDROID_VERSION"
fi

set +e
gcloud firebase test android run \
  --type robo \
  --app "$APP_APK" \
  --device "$DEVICE" \
  --timeout "${TIMEOUT_SECONDS}s" \
  --no-auto-google-login \
  --results-bucket "$RESULTS_BUCKET" \
  --results-dir "$RESULTS_DIR" \
  --client-details "matrixLabel=LocalMD Pro ${GITHUB_RUN_ID:-manual}" \
  --format=json \
  > "$ARTIFACT_DIR/matrix.json" \
  2> "$ARTIFACT_DIR/run.log"
RUN_STATUS=$?
set -e

cat "$ARTIFACT_DIR/run.log"

if [[ -s "$ARTIFACT_DIR/matrix.json" ]]; then
  python3 "$(dirname "$0")/extract-test-lab-evidence-uris.py" \
    "$ARTIFACT_DIR/matrix.json" > "$ARTIFACT_DIR/screenshot-uris.txt"

  screenshot_count=0
  while IFS= read -r uri && [[ "$screenshot_count" -lt 12 ]]; do
    [[ -n "$uri" ]] || continue
    screenshot_count=$((screenshot_count + 1))
    destination="$(printf '%s/%02d.png' "$ARTIFACT_DIR/screenshots" "$screenshot_count")"
    gcloud storage cp "$uri" "$destination" >> "$ARTIFACT_DIR/evidence-download.log" 2>&1 || true
  done < "$ARTIFACT_DIR/screenshot-uris.txt"
fi

{
  echo "device_model=$DEVICE_MODEL"
  echo "device_form=$DEVICE_FORM"
  echo "android_version=${ANDROID_VERSION:-default}"
  echo "locale=$LOCALE"
  echo "orientation=$ORIENTATION"
  echo "timeout_seconds=$TIMEOUT_SECONDS"
  echo "results_bucket=$RESULTS_BUCKET"
  echo "results_dir=$RESULTS_DIR"
  echo "run_status=$RUN_STATUS"
} > "$ARTIFACT_DIR/summary.txt"

exit "$RUN_STATUS"
