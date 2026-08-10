#!/data/data/com.termux/files/usr/bin/sh
# Fail loudly before a release if:
#   - play-store locale-specific release notes (whatsnew.txt) are missing
#   - any whatsnew.txt exceeds Play Store's 500-character limit
#   - docs/release/ Markdown release notes are missing
#   - the Play notes version marker does not match VERSION_NAME
#
# Uses sh+awk only — no python3 dependency (Termux compatible).
# Checks:
#   1. play-store/release-notes/en-US/whatsnew.txt exists and is ≤500 chars.
#   2. play-store/release-notes/ja-JP/whatsnew.txt exists and is ≤500 chars.
#   3. play-store/release-notes/VERSION matches VERSION_NAME.
#   4. docs/release/release-notes-v<VERSION_NAME>.md exists (English).
#   5. docs/release/release-notes-v<VERSION_NAME>.ja.md exists (Japanese).
set -eu

ROOT="${ROOT:-$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)}"
. "$ROOT/scripts/version-env.sh"

CHAR_LIMIT=500

fail() {
  echo "Release notes check failed: $1" >&2
  exit 1
}

# 1 & 2: locale-based whatsnew.txt presence + character count
for locale in en-US ja-JP; do
  f="$ROOT/play-store/release-notes/$locale/whatsnew.txt"
  if [ ! -f "$f" ]; then
    fail "missing $f"
  fi
  char_count=$(wc -m < "$f")
  if [ "$char_count" -gt "$CHAR_LIMIT" ]; then
    fail "$f exceeds $CHAR_LIMIT chars (got $char_count)"
  fi
done

marker="$ROOT/play-store/release-notes/VERSION"
if [ ! -f "$marker" ]; then
  fail "missing $marker"
fi
notes_version=$(sed -n '1p' "$marker")
if [ "$notes_version" != "$VERSION_NAME" ]; then
  fail "Play notes are for v$notes_version, expected v$VERSION_NAME"
fi

# 4 & 5: docs/release/ Markdown release notes presence (English and Japanese)
for f in \
  "docs/release/release-notes-v$VERSION_NAME.md" \
  "docs/release/release-notes-v$VERSION_NAME.ja.md"; do
  if [ ! -f "$ROOT/$f" ]; then
    fail "missing $f (see the latest docs/release/release-notes-v*.md for the format)"
  fi
done

echo "Release notes present for v$VERSION_NAME"
