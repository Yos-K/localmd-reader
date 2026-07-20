#!/data/data/com.termux/files/usr/bin/sh
# Release preflight: run the source-level release checks and print one concise
# pass/fail summary before building or uploading a release artifact.
#
# Source-level only (no APK or Android SDK needed). The APK-level checks
# (scripts/check-release-basics.sh) still run later in the release workflow
# against the built APK.
set -u

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"
. "$ROOT/scripts/version-env.sh"

status=0
results=""

record() {
  if [ "$2" -eq 0 ]; then
    results="$results
  PASS  $1"
  else
    results="$results
  FAIL  $1"
    status=1
  fi
}

run_check() {
  name="$1"
  shift
  if "$@" >/dev/null 2>&1; then
    record "$name" 0
  else
    record "$name" 1
  fi
}

# Only the free-play package is ever uploaded to Play, so the upload-critical
# invariant is that the free package id is identical in both release-path
# sources: src/main/AndroidManifest.xml (the script path's source of truth, used
# by build-release-aab.sh) and app/build.gradle (the Gradle path).
check_upload_package_id() {
  grep -q 'package="io.github.yosk.mdlite"' src/main/AndroidManifest.xml \
    && grep -q 'applicationId "io.github.yosk.mdlite"' app/build.gradle
}

# Gradle product-flavor suffixes (pro preview = .pro, debug = .debug). NOTE: the
# default script release path does NOT apply these suffixes; build-release-aab.sh
# builds io.github.yosk.mdlite for every channel. Pro preview is never uploaded
# (the free-only guard blocks it), so the only package that reaches Play is the
# free package verified above.
check_gradle_flavor_ids() {
  grep -q 'applicationIdSuffix ".pro"' app/build.gradle \
    && grep -q 'applicationIdSuffix ".debug"' app/build.gradle
}

# Play upload must stay free-only: the workflow keeps the guard that fails an
# upload of any non free-play channel.
check_free_only_upload() {
  grep -q "inputs.upload_to_play && inputs.channel != 'free-play'" \
    .github/workflows/play-release.yml
}

run_check "version consistency" sh scripts/version-check.sh
run_check "version name not already released" sh scripts/check-release-version-name.sh
run_check "release notes present and current" sh scripts/check-release-notes.sh
run_check "hard constraints (no INTERNET, reader WebView JS off)" sh scripts/check-hard-constraints.sh
run_check "no committed secrets or keystores" sh scripts/check-no-committed-secrets.sh
run_check "third-party notices present" sh scripts/check-third-party-notices.sh
run_check "free upload package id io.github.yosk.mdlite (manifest and gradle agree)" check_upload_package_id
run_check "gradle flavor package suffixes (pro=.pro, debug=.debug)" check_gradle_flavor_ids
run_check "Play upload free-only guard present" check_free_only_upload

echo "Release preflight for v$VERSION_NAME ($VERSION_CODE):$results"
if [ "$status" -ne 0 ]; then
  echo "Release preflight FAILED" >&2
else
  echo "Release preflight passed"
fi
exit "$status"
