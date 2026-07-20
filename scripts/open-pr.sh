#!/data/data/com.termux/files/usr/bin/sh
set -eu

if [ "$#" -lt 1 ]; then
  echo "Usage: sh scripts/open-pr.sh <conventional-pr-title> [body-file]" >&2
  exit 2
fi

TITLE="$1"
BODY_FILE="${2:-}"
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
BRANCH="$(git branch --show-current)"

if [ "$BRANCH" = "main" ] || [ "$BRANCH" = "master" ]; then
  echo "Refusing to open a pull request from protected branch: $BRANCH" >&2
  exit 2
fi

if ! sh "$ROOT/scripts/pr-preflight.sh" "$TITLE"; then
  echo "Preflight (fitness) checks failed; not opening a pull request." >&2
  exit 2
fi

sh test.sh
git push -u origin "$BRANCH"

if [ -n "$BODY_FILE" ]; then
  gh pr create --base main --head "$BRANCH" --title "$TITLE" --body-file "$BODY_FILE"
else
  gh pr create --base main --head "$BRANCH" --title "$TITLE" --body ""
fi
