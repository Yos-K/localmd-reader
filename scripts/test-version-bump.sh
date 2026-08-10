#!/data/data/com.termux/files/usr/bin/sh
set -eu

PROJECT_ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
tmp="${TMPDIR:-/tmp}/mdlite-version-bump-$$"
trap 'rm -rf "$tmp"' EXIT

mkdir -p "$tmp/scripts" "$tmp/src/main"
cp "$PROJECT_ROOT/scripts/version-bump.sh" "$tmp/scripts/version-bump.sh"
cp "$PROJECT_ROOT/scripts/version-env.sh" "$tmp/scripts/version-env.sh"
cp "$PROJECT_ROOT/scripts/version-apply-manifest.sh" "$tmp/scripts/version-apply-manifest.sh"
printf 'VERSION_NAME=0.2.15\nVERSION_CODE=36\n' > "$tmp/VERSION"
cat > "$tmp/src/main/AndroidManifest.xml" <<'EOF'
<manifest android:versionCode="36" android:versionName="0.2.15" />
EOF

sh "$tmp/scripts/version-bump.sh" major >/dev/null

actual_version=$(cat "$tmp/VERSION")
expected_version='VERSION_NAME=1.0.0
VERSION_CODE=37'
if [ "$actual_version" != "$expected_version" ]; then
  echo "Major bump must produce 1.0.0 (37), got: $actual_version" >&2
  exit 1
fi

grep -q 'android:versionCode="37"' "$tmp/src/main/AndroidManifest.xml"
grep -q 'android:versionName="1.0.0"' "$tmp/src/main/AndroidManifest.xml"

echo "Version major bump test passed"
