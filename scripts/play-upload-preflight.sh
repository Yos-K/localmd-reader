#!/data/data/com.termux/files/usr/bin/sh
# Play upload preflight.
#
# Validates that the GitHub environment variables and secrets required for a
# Google Play upload are present, BEFORE the workflow spends minutes building a
# signed AAB and then fails at the authentication or upload step.
#
# It reads each value from the environment (the workflow maps GitHub Actions
# `vars.*` and `secrets.*` into env). It only checks presence — it NEVER prints
# any value — and exits non-zero listing every missing name and where to set it.
#
# Usage: sh scripts/play-upload-preflight.sh
set -u

missing=0

# require <ENV_NAME> <kind: variable|secret> <github environment> <purpose>
require() {
  name="$1"
  kind="$2"
  envname="$3"
  purpose="$4"
  eval "value=\${$name:-}"
  if [ -z "$value" ]; then
    echo "  MISSING  $name ($kind in the '$envname' environment) — $purpose" >&2
    missing=$((missing + 1))
  else
    echo "  ok       $name"
  fi
}

echo "Play upload preflight: checking required GitHub variables and secrets..."

# Workload Identity Federation: keyless authentication to Google Cloud / Play.
require GCP_WORKLOAD_IDENTITY_PROVIDER variable play-console "Workload Identity Provider resource name for keyless auth"
require GCP_SERVICE_ACCOUNT            variable play-console "service account email used to upload to Play"

# Release signing: the AAB that gets uploaded must be signed first.
require MDLITE_RELEASE_KEYSTORE_BASE64 secret play-console "base64-encoded release keystore used to sign the AAB"
require MDLITE_RELEASE_KEY_ALIAS       secret play-console "release key alias"
require MDLITE_RELEASE_STORE_PASS      secret play-console "release keystore password"
require MDLITE_RELEASE_KEY_PASS        secret play-console "release key password"

if [ "$missing" -eq 0 ]; then
  echo "Play upload preflight: all required variables and secrets are set."
  exit 0
fi

cat >&2 <<'MSG'

Play upload preflight FAILED: the run would otherwise fail later at
authentication or upload. Set the missing values on the 'play-console' GitHub
Environment (names above), for example:

  gh variable set GCP_WORKLOAD_IDENTITY_PROVIDER --env play-console --repo Yos-K/localmd-reader-release
  gh variable set GCP_SERVICE_ACCOUNT            --env play-console --repo Yos-K/localmd-reader-release
  gh secret   set MDLITE_RELEASE_KEYSTORE_BASE64 --env play-console --repo Yos-K/localmd-reader-release
  gh secret   set MDLITE_RELEASE_KEY_ALIAS       --env play-console --repo Yos-K/localmd-reader-release
  gh secret   set MDLITE_RELEASE_STORE_PASS      --env play-console --repo Yos-K/localmd-reader-release
  gh secret   set MDLITE_RELEASE_KEY_PASS        --env play-console --repo Yos-K/localmd-reader-release

See docs/harness/github-actions-cicd.md ("Required GitHub Secrets And Variables").
MSG
exit 1
