#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
WORKFLOW="$ROOT/.github/workflows/play-store-listing.yml"

[ -f "$WORKFLOW" ] || {
  echo "Missing Play Store listing workflow" >&2
  exit 1
}

grep -F "github.repository == 'Yos-K/localmd-reader-release'" "$WORKFLOW" >/dev/null
grep -F 'environment: play-console' "$WORKFLOW" >/dev/null
grep -F 'id-token: write' "$WORKFLOW" >/dev/null
grep -F 'google-github-actions/auth@v3' "$WORKFLOW" >/dev/null
grep -F 'play-update-listing.py' "$WORKFLOW" >/dev/null
grep -F -- '--service-account "$GOOGLE_APPLICATION_CREDENTIALS"' "$WORKFLOW" >/dev/null

echo "Play Store listing workflow contract test passed"
