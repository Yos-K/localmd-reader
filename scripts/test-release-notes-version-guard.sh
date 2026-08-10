#!/data/data/com.termux/files/usr/bin/sh
set -eu

PROJECT_ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
tmp="${TMPDIR:-/tmp}/mdlite-release-notes-$$"
trap 'rm -rf "$tmp"' EXIT

make_fixture() {
  root="$1"
  marker="$2"
  mkdir -p "$root/scripts" "$root/play-store/release-notes/en-US" \
    "$root/play-store/release-notes/ja-JP" "$root/docs/release"
  cp "$PROJECT_ROOT/scripts/version-env.sh" "$root/scripts/version-env.sh"
  printf 'VERSION_NAME=0.2.15\nVERSION_CODE=36\n' > "$root/VERSION"
  printf '%s\n' "$marker" > "$root/play-store/release-notes/VERSION"
  printf 'Current release notes.\n' > "$root/play-store/release-notes/en-US/whatsnew.txt"
  printf 'Current Japanese release notes.\n' > "$root/play-store/release-notes/ja-JP/whatsnew.txt"
  : > "$root/docs/release/release-notes-v0.2.15.md"
  : > "$root/docs/release/release-notes-v0.2.15.ja.md"
}

make_fixture "$tmp/current" "0.2.15"
ROOT="$tmp/current" sh "$PROJECT_ROOT/scripts/check-release-notes.sh" >/dev/null

make_fixture "$tmp/stale" "0.2.14"
if ROOT="$tmp/stale" sh "$PROJECT_ROOT/scripts/check-release-notes.sh" >/dev/null 2>&1; then
  echo "Expected release notes check to reject a stale Play notes marker" >&2
  exit 1
fi

echo "Release notes version guard test passed"
