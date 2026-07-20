#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
PASS=0
FAIL=0

fail() {
  echo "  FAIL: $1"
  FAIL=$((FAIL + 1))
}

pass() {
  echo "  PASS: $1"
  PASS=$((PASS + 1))
}

echo "property 1: no OS/editor metadata file is tracked"
bad="$(git -C "$ROOT" ls-files | grep -E '(^|/)(\.DS_Store|Thumbs\.db|desktop\.ini)$' || true)"
[ -z "$bad" ] && pass "no tracked OS metadata" || fail "tracked OS metadata: $bad"

echo "property 2: .gitignore keeps preventing .DS_Store"
grep -qE '(^|/)\.DS_Store' "$ROOT/.gitignore" && pass ".gitignore has .DS_Store" || fail ".gitignore lacks .DS_Store entry"

echo "---- no-os-metadata: PASS=$PASS FAIL=$FAIL ----"
[ "$FAIL" -eq 0 ]
