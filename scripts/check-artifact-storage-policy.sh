#!/data/data/com.termux/files/usr/bin/sh
# Keep GitHub Actions artifacts short-lived so routine verification does not
# exhaust the repository storage allowance.
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

if [ "$#" -gt 0 ]; then
  FILES="$*"
else
  FILES="$(find .github/workflows -type f -name '*.yml' -print)"
fi

awk '
function fail(message) {
  failures++
  print "artifact-storage: FAIL: " FILENAME ":" upload_line ": " message > "/dev/stderr"
}
function finish_upload() {
  if (!uploading) return
  if (!retention_seen) fail("upload-artifact step has no retention-days")
  uploading = 0
  retention_seen = 0
}
FNR == 1 {
  finish_upload()
}
/uses:[[:space:]]*actions\/upload-artifact@/ {
  finish_upload()
  uploading = 1
  upload_line = FNR
  next
}
uploading && /^[[:space:]]*-[[:space:]]+name:/ {
  finish_upload()
}
uploading && /retention-days:/ {
  value = $0
  sub(/^.*retention-days:[[:space:]]*/, "", value)
  sub(/[[:space:]#].*$/, "", value)
  retention_seen = 1
  numeric_value = value + 0
  if (value !~ /^[0-9]+$/ || numeric_value < 1 || numeric_value > 7) {
    fail("retention-days must be an integer from 1 to 7: " value)
  }
}
END {
  finish_upload()
  if (failures > 0) exit 1
}
' $FILES

CI_WORKFLOW="${ARTIFACT_POLICY_CI_WORKFLOW:-.github/workflows/ci.yml}"
for step_name in \
  "Resolve build metadata" \
  "Build Free debug APK" \
  "Build Pro preview debug APK"
do
  if ! awk -v target="$step_name" '
    index($0, "- name: " target) {
      found = 1
      if ((getline next_line) <= 0 || next_line !~ /if: github\.event_name == .pull_request./) {
        exit 1
      }
    }
    END {
      if (!found) exit 1
    }
  ' "$CI_WORKFLOW"; then
    echo "artifact-storage: FAIL: $step_name must run only for pull requests" >&2
    exit 1
  fi
done

echo "artifact-storage: all uploaded artifacts expire within 7 days."
