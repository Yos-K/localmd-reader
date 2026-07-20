#!/data/data/com.termux/files/usr/bin/sh
# Require every feat/fix commit to declare which ADRs were reviewed.
set -eu

ROOT="$(git rev-parse --show-toplevel 2>/dev/null || true)"
if [ -z "$ROOT" ]; then
  echo "adr-review: not inside a Git repository" >&2
  exit 1
fi
cd "$ROOT"

BASE="${1:-${ADR_REVIEW_CHECK_BASE:-origin/main}}"
if ! git rev-parse --verify --quiet "$BASE" >/dev/null 2>&1; then
  echo "adr-review: base '$BASE' is not resolvable; refusing to skip ADR review." >&2
  exit 1
fi

english_sections='Decision|Alternatives Considered|Why This Decision|Why Alternatives Were Rejected|Reconsider When'
japanese_sections='決定事項|検討した選択肢|選択理由|選択しなかった理由|決定を見直す契機'

validate_sections() {
  commit="$1"
  path="$2"
  sections="$3"
  content="$(git show "$commit:$path" 2>/dev/null || true)"
  old_ifs="$IFS"
  IFS='|'
  for section in $sections; do
    if ! printf '%s\n' "$content" | grep -qFx "## $section"; then
      IFS="$old_ifs"
      echo "adr-review: $path is missing required section: ## $section" >&2
      return 1
    fi
  done
  IFS="$old_ifs"
}

validate_reference() {
  commit="$1"
  path="$2"
  case "$path" in
    docs/adr/[0-9][0-9][0-9][0-9]-*.md) ;;
    *)
      echo "adr-review: invalid ADR reference '$path' in $commit" >&2
      return 1
      ;;
  esac
  case "$path" in
    *.ja.md)
      echo "adr-review: reference the canonical English ADR, not its Japanese companion: $path" >&2
      return 1
      ;;
  esac
  if ! git cat-file -e "$commit:$path" 2>/dev/null; then
    echo "adr-review: referenced ADR does not exist at $commit: $path" >&2
    return 1
  fi
  japanese="${path%.md}.ja.md"
  if ! git cat-file -e "$commit:$japanese" 2>/dev/null; then
    echo "adr-review: Japanese ADR companion does not exist at $commit: $japanese" >&2
    return 1
  fi
  validate_sections "$commit" "$path" "$english_sections"
  validate_sections "$commit" "$japanese" "$japanese_sections"
}

failures=0
for path in docs/adr/[0-9][0-9][0-9][0-9]-*.md; do
  [ -e "$path" ] || continue
  case "$path" in
    *.ja.md) continue ;;
  esac
  if ! validate_reference HEAD "$path"; then
    failures=$((failures + 1))
  fi
done

commits="$(git rev-list --reverse "$BASE..HEAD")"
for commit in $commits; do
  subject="$(git show -s --format='%s' "$commit")"
  if ! printf '%s\n' "$subject" | grep -Eq '^(feat|fix)(\([^)]*\))?!?: '; then
    continue
  fi

  reviews="$(git show -s --format='%B' "$commit" | sed -n 's/^ADR-Review:[[:space:]]*//p')"
  count="$(printf '%s\n' "$reviews" | sed '/^$/d' | wc -l | tr -d ' ')"
  if [ "$count" -ne 1 ]; then
    echo "adr-review: $commit '$subject' must have exactly one ADR-Review trailer" >&2
    failures=$((failures + 1))
    continue
  fi

  review="$(printf '%s\n' "$reviews" | sed -n '1p')"
  if printf '%s\n' "$review" | grep -Eq '^none \(.+\)$'; then
    continue
  fi
  if printf '%s\n' "$review" | grep -Eq '^none([[:space:]]*)$'; then
    echo "adr-review: $commit must explain why no ADR applies" >&2
    failures=$((failures + 1))
    continue
  fi

  old_ifs="$IFS"
  IFS=','
  for raw_path in $review; do
    path="$(printf '%s' "$raw_path" | sed 's/^[[:space:]]*//; s/[[:space:]]*$//')"
    if ! validate_reference "$commit" "$path"; then
      failures=$((failures + 1))
    fi
  done
  IFS="$old_ifs"
done

if [ "$failures" -ne 0 ]; then
  cat >&2 <<'EOF'

Every feat/fix commit must confirm ADR review with one trailer:
  ADR-Review: docs/adr/0001-example.md
or, only when no decision applies:
  ADR-Review: none (explain why no architectural decision is affected)
EOF
  exit 1
fi

echo "adr-review: every feat/fix commit declares a valid ADR review."
