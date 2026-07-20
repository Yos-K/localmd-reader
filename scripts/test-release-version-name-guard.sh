#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"

tmp="${TMPDIR:-/tmp}/mdlite-version-guard-$$"
mkdir -p "$tmp"
trap 'rm -rf "$tmp"' EXIT

make_repo() {
  repo="$1"
  version_name="$2"
  version_code="$3"

  mkdir -p "$repo"
  git -C "$repo" init -q
  git -C "$repo" config user.email "test@example.invalid"
  git -C "$repo" config user.name "Test"
  printf 'VERSION_NAME=%s\nVERSION_CODE=%s\n' "$version_name" "$version_code" > "$repo/VERSION"
  git -C "$repo" add VERSION
  git -C "$repo" commit -q -m "test: seed version"
  git -C "$repo" tag "v0.1.0"
}

make_repo "$tmp/same" "0.1.0" "16"
if ROOT="$tmp/same" sh "$ROOT/scripts/check-release-version-name.sh" >/dev/null 2>&1; then
  echo "Expected release version guard to reject reused VERSION_NAME" >&2
  exit 1
fi

ROOT="$tmp/same" MDLITE_ALLOW_VERSION_CODE_ONLY=true sh "$ROOT/scripts/check-release-version-name.sh"

make_repo "$tmp/next" "0.1.1" "17"
ROOT="$tmp/next" sh "$ROOT/scripts/check-release-version-name.sh"

echo "Release version name guard test passed"
