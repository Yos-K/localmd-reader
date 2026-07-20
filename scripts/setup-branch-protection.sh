#!/data/data/com.termux/files/usr/bin/sh
# Apply the reproducible branch protection policy for the default branch.
#
# Why: protection was configured by hand, so the required checks and rules were
# not reproducible or reviewable. This script encodes the intended policy so it
# can be re-applied and audited.
#
# Policy:
#   - required status checks (strict / up-to-date): fitness, test, gradle-build, mutation
#     (mutation runs on every PR but only does the heavy analysis when the logic layers
#     change, so it always reports a status and is safe to require)
#   - enforce for admins (no bypass)
#   - require a pull request before merging (0 approvals, so solo development is
#     not blocked on self-approval, but direct pushes to the branch are refused)
#   - require conversation resolution before merge
#   - no force pushes, no branch deletion
#
# Usage: sh scripts/setup-branch-protection.sh [owner/repo] [branch]
set -eu

REPO="${1:-Yos-K/localmd-reader}"
BRANCH="${2:-main}"

gh api --method PUT "repos/$REPO/branches/$BRANCH/protection" --input - > /dev/null <<'JSON'
{
  "required_status_checks": {
    "strict": true,
    "contexts": ["fitness", "test", "gradle-build", "mutation"]
  },
  "enforce_admins": true,
  "required_pull_request_reviews": {
    "required_approving_review_count": 0,
    "dismiss_stale_reviews": false,
    "require_code_owner_reviews": false
  },
  "restrictions": null,
  "required_conversation_resolution": true,
  "required_linear_history": false,
  "allow_force_pushes": false,
  "allow_deletions": false,
  "block_creations": false
}
JSON

echo "Applied branch protection to $REPO@$BRANCH:"
echo "- required status checks (strict): fitness, test, gradle-build, mutation"
echo "- enforce for admins: true"
echo "- require conversation resolution: true"
echo "- force pushes / branch deletion: disabled"
echo "- pull request required before merge (0 approvals; direct pushes refused)"
