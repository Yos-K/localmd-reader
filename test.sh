#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"

sh "$ROOT/scripts/version-check.sh"
sh "$ROOT/scripts/test-version-bump.sh"
sh "$ROOT/scripts/test-release-notes-version-guard.sh"
sh "$ROOT/scripts/test-no-os-metadata.sh"
sh "$ROOT/scripts/test-smoke-render-contract.sh"
sh "$ROOT/scripts/test-interaction-storming-guard.sh"
sh "$ROOT/scripts/test-interaction-model-check.sh"
sh "$ROOT/scripts/test-interaction-surface-registration.sh"
sh "$ROOT/scripts/test-artifact-storage-policy.sh"
sh "$ROOT/scripts/test-gradle-test-memory-policy.sh"
sh "$ROOT/scripts/test-target-api-policy.sh"
sh "$ROOT/scripts/test-test-lab-evidence-extractor.sh"
sh "$ROOT/scripts/test-test-lab-workflow-contract.sh"
sh "$ROOT/scripts/test-domain-model-solver-reporting.sh"
sh "$ROOT/scripts/test-play-open-test-promotion-workflow.sh"
sh "$ROOT/scripts/test-play-free-billing-build-policy.sh"
python3 "$ROOT/scripts/test_play_upload_cli.py"
sh "$ROOT/scripts/run-unit-tests.sh"
sh "$ROOT/scripts/check-test-smells.sh"
sh "$ROOT/scripts/check-third-party-notices.sh"
sh "$ROOT/build.sh"
sh "$ROOT/scripts/check-release-basics.sh"

echo "Tests passed"
