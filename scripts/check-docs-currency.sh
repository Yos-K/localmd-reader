#!/data/data/com.termux/files/usr/bin/sh
# Documentation currency guard (unified glossary + rule-doc engine).
#
# Fails when a pull request changes rule-bearing code/config without updating the
# documents that teach those rules to humans and agents, unless a commit declares
# the impact with a trailer. This is the single engine that replaced the two
# former guards (check-glossary-currency.sh + check-rule-doc-currency.sh): same
# triggers, same required docs, same legacy trailers — one CI step, one mental model.
#
# Why one engine: the two guards were the same shape (diff a trigger set against a
# required-doc set, with a trailer escape hatch). Keeping them separate doubled the
# CI steps, the preflight checks, and — worst — the trailer regimes a contributor
# had to remember (Glossary-Impact AND Rule-Docs-Impact). One engine + one trailer
# (Docs-Impact) removes that load without weakening any gate.
#
# Three lanes (each: trigger -> required doc -> exempt):
#   glossary : domain/model/viewer/file *.java  -> docs/domain/domain-glossary*.md
#   harness  : workflows + harness-rule scripts  -> AGENTS / CONTRIBUTING / README / docs/harness / arch-policy
#   policy   : ViewerFeature/FeatureEntitlement(s)/ProFeatureCatalog -> AGENTS / free-pro-policy / glossary
#
# Trailer (any commit in BASE..HEAD), case-insensitive at line start:
#   Docs-Impact:      <- unified, going forward. Present => exempts ALL triggered lanes.
#   Glossary-Impact:  <- legacy, still honored. Exempts the glossary lane only.
#   Rule-Docs-Impact: <- legacy, still honored. Exempts the harness and policy lanes.
#
# Usage: sh scripts/check-docs-currency.sh [base-ref]   (base defaults to origin/main)
# Skips (exit 0) when the base cannot be resolved, so it never blocks spuriously.
set -u

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

BASE="${1:-${DOCS_CURRENCY_CHECK_BASE:-origin/main}}"

if ! git rev-parse --verify --quiet "$BASE" >/dev/null 2>&1; then
  echo "docs-currency: base '$BASE' not resolvable; skipping." >&2
  exit 0
fi

CHANGED="$(git diff --name-only "$BASE...HEAD")"
COMMITS="$(git log "$BASE..HEAD" --format='%B')"

# match <regex> : true if any changed path matches
match() { printf '%s\n' "$CHANGED" | grep -qE "$1"; }
# trailer <regex> : true if any commit message has the trailer at a line start
trailer() { printf '%s\n' "$COMMITS" | grep -qiE "^$1"; }

# Unified declaration exempts every triggered lane (the going-forward single trailer).
UNIFIED=0
trailer 'Docs-Impact:' && UNIFIED=1

MISSING=""
RULE_BEARING=""

# --- glossary lane ---
G_TRIGGER='^src/main/java/io/github/yosk/mdlite/(domain|model|viewer|file)/.*\.java$'
G_DOC='^docs/domain/domain-glossary.*\.md$'
if match "$G_TRIGGER"; then
  RULE_BEARING="${RULE_BEARING}$(printf '%s\n' "$CHANGED" | grep -E "$G_TRIGGER")
"
  if match "$G_DOC" || [ "$UNIFIED" -eq 1 ] || trailer 'Glossary-Impact:'; then :; else
    MISSING="${MISSING} glossary"
  fi
fi

# --- harness lane ---
H_TRIGGER='^(\.github/workflows/.*\.ya?ml|scripts/(check-.*|.*preflight.*|setup-branch-protection|setup-github-actions-repo|run-mutation-tests|test-balance-report|open-pr|start-work|emulator-smoke)\.sh)$'
H_DOC='^(AGENTS\.md|CONTRIBUTING\.md|scripts/README\.md|docs/harness/.*\.md|docs/product/architecture-package-policy.*\.md)$'
if match "$H_TRIGGER"; then
  RULE_BEARING="${RULE_BEARING}$(printf '%s\n' "$CHANGED" | grep -E "$H_TRIGGER")
"
  if match "$H_DOC" || [ "$UNIFIED" -eq 1 ] || trailer 'Rule-Docs-Impact:'; then :; else
    MISSING="${MISSING} harness"
  fi
fi

# --- policy lane ---
P_TRIGGER='^src/main/java/io/github/yosk/mdlite/domain/(ViewerFeature|FeatureEntitlement|FeatureEntitlements|ProFeatureCatalog)\.java$'
P_DOC='^(AGENTS\.md|docs/product/free-pro-feature-policy.*\.md|docs/domain/domain-glossary.*\.md)$'
if match "$P_TRIGGER"; then
  RULE_BEARING="${RULE_BEARING}$(printf '%s\n' "$CHANGED" | grep -E "$P_TRIGGER")
"
  if match "$P_DOC" || [ "$UNIFIED" -eq 1 ] || trailer 'Rule-Docs-Impact:'; then :; else
    MISSING="${MISSING} policy"
  fi
fi

if [ -z "$MISSING" ]; then
  echo "docs-currency: rule-bearing changes have current documentation (or a declared impact); ok."
  exit 0
fi

echo "docs-currency: FAIL —missing doc updates for:$MISSING" >&2
echo "Rule-bearing files changed without updating the matching documentation:" >&2
printf '%s' "$RULE_BEARING" | sed '/^$/d; s/^/  - /' >&2
cat >&2 <<'MSG'

Update the documentation for the lane that changed:
  - glossary -> docs/domain/domain-glossary*.md  (new domain type / L1 invariant / L2 rule / L3 contract)
  - harness  -> AGENTS.md, docs/harness/*.md, scripts/README.md, architecture-package-policy.md
  - policy   -> AGENTS.md, docs/product/free-pro-feature-policy*.md, domain-glossary*.md

Or, if the change alters no rule (pure refactor, comment, rename, infra), declare it
with ONE commit trailer that exempts all touched doc lanes:

  Docs-Impact: none (pure refactor, no rule or workflow behavior change)

(Legacy Glossary-Impact: / Rule-Docs-Impact: trailers are still honored per lane.)
MSG
exit 1
