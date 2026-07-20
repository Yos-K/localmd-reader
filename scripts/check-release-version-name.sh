#!/data/data/com.termux/files/usr/bin/sh
# Refuse a release upload when VERSION_NAME is still equal to the latest
# release tag. VERSION_CODE must still increase for Play, but user-visible
# releases should not be published forever as the same version.
set -eu

SCRIPT_ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
ROOT="${ROOT:-$SCRIPT_ROOT}"
. "$SCRIPT_ROOT/scripts/version-env.sh"

if [ "${MDLITE_ALLOW_VERSION_CODE_ONLY:-false}" = "true" ]; then
  echo "VERSION_NAME reuse allowed by MDLITE_ALLOW_VERSION_CODE_ONLY=true"
  exit 0
fi

latest_tag="$(git -C "$ROOT" tag --list 'v[0-9]*.[0-9]*.[0-9]*' --sort=-v:refname | sed -n '1p')"

if [ -z "$latest_tag" ]; then
  echo "No release tag found; VERSION_NAME=$VERSION_NAME is acceptable"
  exit 0
fi

latest_version="${latest_tag#v}"
if [ "$VERSION_NAME" = "$latest_version" ]; then
  echo "VERSION_NAME=$VERSION_NAME has already been released as $latest_tag" >&2
  echo "Run scripts/version-bump.sh patch or scripts/version-bump.sh minor before the next Play upload." >&2
  exit 1
fi

echo "VERSION_NAME=$VERSION_NAME is newer than latest release tag $latest_tag"
