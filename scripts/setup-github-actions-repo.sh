#!/data/data/com.termux/files/usr/bin/sh
set -eu

REPO="${1:-Yos-K/localmd-reader-release}"

gh api --method PUT "repos/$REPO/environments/release-build" --input /dev/null > /dev/null
gh api --method PUT "repos/$REPO/environments/play-console" --input /dev/null > /dev/null

echo "Configured GitHub Actions environments for $REPO:"
echo "- release-build"
echo "- play-console"
echo
echo "Register environment secrets with:"
echo "  gh secret set MDLITE_RELEASE_KEYSTORE_BASE64 --env release-build --repo $REPO"
echo "  gh secret set MDLITE_RELEASE_KEY_ALIAS --env release-build --repo $REPO"
echo "  gh secret set MDLITE_RELEASE_STORE_PASS --env release-build --repo $REPO"
echo "  gh secret set MDLITE_RELEASE_KEY_PASS --env release-build --repo $REPO"
echo "  gh secret set MDLITE_RELEASE_KEYSTORE_BASE64 --env play-console --repo $REPO"
echo "  gh secret set MDLITE_RELEASE_KEY_ALIAS --env play-console --repo $REPO"
echo "  gh secret set MDLITE_RELEASE_STORE_PASS --env play-console --repo $REPO"
echo "  gh secret set MDLITE_RELEASE_KEY_PASS --env play-console --repo $REPO"
echo
echo "Register OIDC environment variables with:"
echo "  gh variable set GCP_WORKLOAD_IDENTITY_PROVIDER --env play-console --repo $REPO"
echo "  gh variable set GCP_SERVICE_ACCOUNT --env play-console --repo $REPO"
