#!/bin/sh
set -eu

WORKFLOW=.github/workflows/play-release.yml

grep -F 'MDLITE_RELEASE_PRO_FEATURES: ${{ inputs.channel == '\''pro-preview-artifact'\'' }}' "$WORKFLOW" >/dev/null
grep -F 'MDLITE_RELEASE_ENABLE_PLAY_BILLING: true' "$WORKFLOW" >/dev/null
grep -F 'MDLITE_INCLUDE_ANDROID_DEPS: true' "$WORKFLOW" >/dev/null

echo 'Play Free billing build policy passed'
