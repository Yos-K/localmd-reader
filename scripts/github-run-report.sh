#!/data/data/com.termux/files/usr/bin/sh
# Summarize a GitHub Actions run and print failed-job logs when available.
set -eu

RUN_ID="${1:-}"
REPO="${2:-Yos-K/localmd-reader}"

if [ -z "$RUN_ID" ]; then
  echo "Usage: scripts/github-run-report.sh RUN_ID [OWNER/REPO]" >&2
  exit 2
fi

echo "GitHub Actions run report"
echo "repo=$REPO"
echo "run=$RUN_ID"
echo

gh run view "$RUN_ID" --repo "$REPO" \
  --json name,url,status,conclusion,headBranch,headSha,createdAt,updatedAt \
  --jq '"name=\(.name)\nurl=\(.url)\nstatus=\(.status)\nconclusion=\(.conclusion // "pending")\nbranch=\(.headBranch)\nsha=\(.headSha[0:7])\ncreated=\(.createdAt)\nupdated=\(.updatedAt)"'

echo
echo "Jobs"
gh run view "$RUN_ID" --repo "$REPO" \
  --json jobs \
  --jq '.jobs[] | "\(.conclusion // .status)\t\(.name)\t\(.url)"'

failed_count="$(gh run view "$RUN_ID" --repo "$REPO" --json jobs --jq '[.jobs[] | select(.conclusion == "failure")] | length')"
if [ "$failed_count" = "0" ]; then
  echo
  echo "No failed jobs."
  exit 0
fi

echo
echo "Failed job logs"
gh run view "$RUN_ID" --repo "$REPO" --log-failed
