#!/bin/sh
set -eu

WORKFLOW=.github/workflows/play-open-test-promotion.yml

grep -F 'default: beta' "$WORKFLOW" >/dev/null
grep -F 'version_code:' "$WORKFLOW" >/dev/null
grep -F -- '--version-code "${{ inputs.version_code }}"' "$WORKFLOW" >/dev/null
grep -F -- '--track "${{ inputs.track }}"' "$WORKFLOW" >/dev/null
grep -F 'google-github-actions/auth@v3' "$WORKFLOW" >/dev/null

! grep -F 'MDLITE_RELEASE_KEYSTORE' "$WORKFLOW" >/dev/null
! grep -F 'build-release-aab.sh' "$WORKFLOW" >/dev/null

echo 'Play open-test promotion workflow contract passed'
