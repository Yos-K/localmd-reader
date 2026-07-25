#!/data/data/com.termux/files/usr/bin/sh
set -eu

REPO="${1:-Yos-K/localmd-reader-release}"
REF="${2:-main}"
VERSION_CODE="${3:-33}"
TRACK="${4:-beta}"

gh workflow run play-open-test-promotion.yml \
  --repo "$REPO" \
  --ref "$REF" \
  -f version_code="$VERSION_CODE" \
  -f track="$TRACK" \
  -f status=completed \
  -f changes_not_sent_for_review=false

echo "Requested open-test promotion: versionCode=$VERSION_CODE track=$TRACK"
