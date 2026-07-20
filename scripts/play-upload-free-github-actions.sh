#!/data/data/com.termux/files/usr/bin/sh
# Dispatch the Free Play upload workflow and print the resulting run report.
set -eu

REPO="${1:-Yos-K/localmd-reader-release}"
REF="${2:-main}"
TRACK="${3:-alpha}"

output="$(gh workflow run play-release.yml \
  --repo "$REPO" \
  --ref "$REF" \
  -f channel=free-play \
  -f track="$TRACK" \
  -f status=completed \
  -f build_system=script \
  -f upload_to_play=true \
  -f changes_not_sent_for_review=false)"

echo "$output"

run_url="$(printf '%s\n' "$output" | sed -n 's#.*actions/runs/\([0-9][0-9]*\).*#\1#p' | tail -1)"
if [ -z "$run_url" ]; then
  echo "Could not parse run id from workflow output. Open the URL above in GitHub Actions." >&2
  exit 1
fi

echo
echo "Run id: $run_url"
echo "Watch with:"
echo "  gh run watch $run_url --repo $REPO --interval 20"
echo
echo "Report with:"
echo "  scripts/github-run-report.sh $run_url $REPO"
