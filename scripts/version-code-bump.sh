#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
. "$ROOT/scripts/version-env.sh"

if [ "${MDLITE_ALLOW_VERSION_CODE_ONLY:-false}" != "true" ]; then
  echo "version-code-only bump is disabled for normal releases." >&2
  echo "Use scripts/version-bump.sh patch or scripts/version-bump.sh minor so VERSION_NAME changes too." >&2
  echo "Set MDLITE_ALLOW_VERSION_CODE_ONLY=true only for an explicitly documented emergency rebuild." >&2
  exit 1
fi

VERSION_CODE=$((VERSION_CODE + 1))

cat > "$ROOT/VERSION" <<EOF
VERSION_NAME=$VERSION_NAME
VERSION_CODE=$VERSION_CODE
EOF

tmp="$ROOT/build/AndroidManifest.version-code-bump.xml"
mkdir -p "$ROOT/build"
"$ROOT/scripts/version-apply-manifest.sh" "$ROOT/src/main/AndroidManifest.xml" "$tmp"
mv "$tmp" "$ROOT/src/main/AndroidManifest.xml"

echo "Bumped versionCode to $VERSION_CODE for versionName $VERSION_NAME"
