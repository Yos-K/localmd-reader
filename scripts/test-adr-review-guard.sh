#!/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
CHECKER="$ROOT/scripts/check-adr-review.sh"
TMP_BASE="${TMPDIR:-/tmp}"
TMP_DIR="$TMP_BASE/localmd-adr-review-test-$$"
mkdir -p "$TMP_DIR"
trap 'rm -rf "$TMP_DIR"' EXIT HUP INT TERM

new_repository() {
  repo="$1"
  mkdir -p "$repo"
  git -C "$repo" init -q
  git -C "$repo" config user.name "ADR Guard Test"
  git -C "$repo" config user.email "adr-guard@example.invalid"
  printf '%s\n' initial > "$repo/README.md"
  git -C "$repo" add README.md
  git -C "$repo" commit -q -m "chore: initialize fixture"
  git -C "$repo" rev-parse HEAD
}

commit_feature() {
  repo="$1"
  subject="$2"
  trailer="${3:-}"
  printf '%s\n' "$subject" >> "$repo/change.txt"
  git -C "$repo" add change.txt
  if [ -n "$trailer" ]; then
    git -C "$repo" commit -q -m "$subject" -m "$trailer"
  else
    git -C "$repo" commit -q -m "$subject"
  fi
}

write_valid_adr_pair() {
  repo="$1"
  mkdir -p "$repo/docs/adr"
  cat > "$repo/docs/adr/0001-example.md" <<'EOF'
# ADR-0001: Example

## Decision
Keep the example.

## Alternatives Considered
Remove the example.

## Why This Decision
It protects the behavior.

## Why Alternatives Were Rejected
Removal loses the behavior.

## Reconsider When
The behavior is no longer required.
EOF
  cat > "$repo/docs/adr/0001-example.ja.md" <<'EOF'
# ADR-0001: 例

## 決定事項
例を維持する。

## 検討した選択肢
例を削除する。

## 選択理由
振る舞いを保護するため。

## 選択しなかった理由
削除すると振る舞いを失うため。

## 決定を見直す契機
振る舞いが不要になったとき。
EOF
}

expect_failure() {
  repo="$1"
  base="$2"
  if (cd "$repo" && sh "$CHECKER" "$base") >/dev/null 2>&1; then
    echo "test-adr-review-guard: expected guard failure for $repo" >&2
    exit 1
  fi
}

expect_success() {
  repo="$1"
  base="$2"
  (cd "$repo" && sh "$CHECKER" "$base") >/dev/null
}

repo="$TMP_DIR/missing-trailer"
base="$(new_repository "$repo")"
commit_feature "$repo" "feat: add behavior"
expect_failure "$repo" "$base"

repo="$TMP_DIR/reasoned-none"
base="$(new_repository "$repo")"
commit_feature "$repo" "fix: correct typo behavior" \
  "ADR-Review: none (no architectural decision is affected)"
expect_success "$repo" "$base"

repo="$TMP_DIR/missing-reference"
base="$(new_repository "$repo")"
commit_feature "$repo" "feat: add behavior" \
  "ADR-Review: docs/adr/0001-missing.md"
expect_failure "$repo" "$base"

repo="$TMP_DIR/valid-reference"
base="$(new_repository "$repo")"
write_valid_adr_pair "$repo"
git -C "$repo" add docs/adr
git -C "$repo" commit -q -m "docs: record example decision"
commit_feature "$repo" "feat: add behavior" \
  "ADR-Review: docs/adr/0001-example.md"
expect_success "$repo" "$base"

repo="$TMP_DIR/malformed-reference"
base="$(new_repository "$repo")"
mkdir -p "$repo/docs/adr"
printf '%s\n' '# ADR-0001: Incomplete' > "$repo/docs/adr/0001-incomplete.md"
printf '%s\n' '# ADR-0001: 不完全' > "$repo/docs/adr/0001-incomplete.ja.md"
git -C "$repo" add docs/adr
git -C "$repo" commit -q -m "docs: add incomplete decision"
commit_feature "$repo" "fix: rely on incomplete decision" \
  "ADR-Review: docs/adr/0001-incomplete.md"
expect_failure "$repo" "$base"

echo "test-adr-review-guard: passed"
