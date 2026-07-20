#!/bin/sh
set -eu

PUBLIC_REPOSITORY='Yos-K/localmd-reader'
RELEASE_REPOSITORY='Yos-K/localmd-reader-release'

# Play upload defaults to the private release repository.
grep -F "REPO=\"\${1:-$RELEASE_REPOSITORY}\"" \
  scripts/play-upload-free-github-actions.sh >/dev/null

# Secret environment setup defaults to the private release repository.
grep -F "REPO=\"\${1:-$RELEASE_REPOSITORY}\"" \
  scripts/setup-github-actions-repo.sh >/dev/null

# Source branch protection defaults to the public repository.
grep -F "REPO=\"\${1:-$PUBLIC_REPOSITORY}\"" \
  scripts/setup-branch-protection.sh >/dev/null

# Release jobs cannot run from the public source repository.
grep -F "if: github.repository == '$RELEASE_REPOSITORY'" \
  .github/workflows/play-release.yml >/dev/null

echo 'Release repository boundary checks passed'
