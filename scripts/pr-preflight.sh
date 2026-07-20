#!/data/data/com.termux/files/usr/bin/sh
# Local mirror of the CI `fitness` job, plus the SDK-free guards of `test`.
#
# Runs the same Android-build-free acceptance guards that every pull request
# must pass, then prints a concise pass/fail summary. Run this before opening
# a PR so a failing acceptance criterion is caught locally instead of in CI.
#
# This is the local proxy for the machine-enforced acceptance gate documented
# in AGENTS.md ("変更の受け入れ基準"). It intentionally excludes the unit-test
# and Gradle build jobs, which require the Android SDK and run in CI's `test`
# and `gradle-build` jobs — except the test-smell scan, a pure grep from the
# `test` job that once failed only in CI because this mirror lacked it
# (see PR #78's review history).
#
# Scope: this is the *pull request* preflight (every PR's fitness gate). The
# separate scripts/release-preflight.sh is the *release* preflight, run before
# building or uploading a release artifact (version, release notes, package id).
#
# Usage:
#   sh scripts/pr-preflight.sh ["<conventional-pr-title>"]
#
# The optional title argument enables the Conventional Commits title check
# (the same check CI runs against the PR title). Omit it to skip that check.
set -u

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

TITLE="${1:-}"
failures=0
skipped=0
results=""

record() {
  # record <status: PASS|FAIL|SKIP> <label>
  results="${results}  $1  $2
"
  if [ "$1" = "FAIL" ]; then
    failures=$((failures + 1))
  elif [ "$1" = "SKIP" ]; then
    skipped=$((skipped + 1))
  fi
}

section() {
  printf '\n=== %s ===\n' "$1"
}

# 1. Conventional Commits title (matches CI: PR title validation)
LABEL="Conventional Commits title"
if [ -n "$TITLE" ]; then
  section "$LABEL"
  if printf '%s\n' "$TITLE" | sh scripts/check-conventional-title.sh; then
    record PASS "$LABEL"
  else
    record FAIL "$LABEL"
  fi
else
  record SKIP "$LABEL (no title argument)"
fi

# 2. Per-file line count limit (matches CI: check-file-sizes.sh)
LABEL="Per-file 300-line limit"
section "$LABEL"
if sh scripts/check-file-sizes.sh; then
  record PASS "$LABEL"
else
  record FAIL "$LABEL"
fi

# 3. Harness hard constraints (matches CI: check-hard-constraints.sh)
LABEL="Hard constraints (INTERNET / reader WebView JS)"
section "$LABEL"
if sh scripts/check-hard-constraints.sh; then
  record PASS "$LABEL"
else
  record FAIL "$LABEL"
fi

# 4. Committed secrets scan (matches CI: check-no-committed-secrets.sh)
LABEL="No committed secrets"
section "$LABEL"
if sh scripts/check-no-committed-secrets.sh; then
  record PASS "$LABEL"
else
  record FAIL "$LABEL"
fi

# 5. Documentation currency (matches CI: check-docs-currency.sh).
# Unified glossary + rule-doc engine. Diff-based: compares against origin/main;
# skips (passes) if base is absent.
LABEL="Documentation currency (glossary + rule docs)"
section "$LABEL"
if sh scripts/check-docs-currency.sh; then
  record PASS "$LABEL"
else
  record FAIL "$LABEL"
fi

# 6. ADR review declaration.
# Every feature addition and behavior fix must prove that applicable architecture
# decisions were reviewed before the commit is accepted.
LABEL="ADR review declaration"
section "$LABEL"
if sh scripts/test-adr-commit-msg-hook.sh \
    && sh scripts/test-adr-review-guard.sh \
    && sh scripts/check-adr-review.sh; then
  record PASS "$LABEL"
else
  record FAIL "$LABEL"
fi

# 7. Test smells (matches CI: check-test-smells.sh in the `test` job).
# A pure grep, so it belongs in this local mirror even though CI runs it in
# `test`: in PR #78 a smelly test passed this preflight and failed only in CI.
LABEL="Test smells"
section "$LABEL"
if sh scripts/check-test-smells.sh; then
  record PASS "$LABEL"
else
  record FAIL "$LABEL"
fi

# 8. Unit test dependency downloads.
# CI once accepted truncated Maven jar downloads until javac failed with
# "zip END header not found" after a merge push. Keep the retry and jar
# validation contract visible in the fast local gate.
LABEL="Unit test dependency download guard"
section "$LABEL"
if sh scripts/check-unit-test-dependency-downloads.sh; then
  record PASS "$LABEL"
else
  record FAIL "$LABEL"
fi

# 9. Artifact storage policy.
# GitHub Actions artifacts must have bounded retention to avoid exhausting the
# repository storage allowance.
LABEL="Artifact storage policy"
section "$LABEL"
if sh scripts/check-artifact-storage-policy.sh; then
  record PASS "$LABEL"
else
  record FAIL "$LABEL"
fi

# 10. Interaction storming gaps.
# This catches incomplete user flows such as dialogs without a close command or
# library views that cannot recover by selecting another folder.
LABEL="Interaction storming flow completeness"
section "$LABEL"
if sh scripts/check-interaction-storming.sh; then
  record PASS "$LABEL"
else
  record FAIL "$LABEL"
fi

# 11. Interaction model checking.
# The flow table says a command exists; this graph check proves the command has a
# modeled transition and every modeled state can reach a stable reader state.
LABEL="Interaction model reachability"
section "$LABEL"
if sh scripts/check-interaction-model.sh; then
  record PASS "$LABEL"
else
  record FAIL "$LABEL"
fi

# 12. Modeled commands must be linked to implementation and behavior tests.
LABEL="Interaction command traceability"
section "$LABEL"
if sh scripts/test-interaction-command-traceability.sh \
    && sh scripts/check-interaction-command-traceability.sh; then
  record PASS "$LABEL"
else
  record FAIL "$LABEL"
fi

# 13. Interactive Android surfaces must be linked to the state model.
LABEL="Interaction surface registration"
section "$LABEL"
if sh scripts/check-interaction-surface-registration.sh; then
  record PASS "$LABEL"
else
  record FAIL "$LABEL"
fi

# 14. Public source and private release operations must not share defaults.
LABEL="Public/private repository boundary"
section "$LABEL"
if sh scripts/test-release-repository-boundary.sh; then
  record PASS "$LABEL"
else
  record FAIL "$LABEL"
fi

printf '\n==================== preflight summary ====================\n'
printf '%s' "$results"
printf '===========================================================\n'

if [ "$failures" -eq 0 ]; then
  if [ "$skipped" -gt 0 ]; then
    printf 'Executed fitness checks passed, but %d check(s) were skipped. Provide a PR title before opening a PR.\n' "$skipped"
    exit 0
  fi
  printf 'All fitness checks passed. Safe to open a PR.\n'
  exit 0
fi

printf '%d check(s) failed. Fix before opening a PR (CI would reject this).\n' "$failures"
exit 1
