#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"

sh "$ROOT/scripts/version-check.sh"
sh "$ROOT/scripts/test-no-os-metadata.sh"
sh "$ROOT/scripts/test-smoke-render-contract.sh"
sh "$ROOT/scripts/test-interaction-storming-guard.sh"
sh "$ROOT/scripts/test-interaction-model-check.sh"
sh "$ROOT/scripts/test-interaction-surface-registration.sh"
sh "$ROOT/scripts/test-artifact-storage-policy.sh"
sh "$ROOT/scripts/test-gradle-test-memory-policy.sh"
sh "$ROOT/scripts/test-domain-model-solver-reporting.sh"
sh "$ROOT/scripts/run-unit-tests.sh"
sh "$ROOT/scripts/check-test-smells.sh"
sh "$ROOT/scripts/check-third-party-notices.sh"
sh "$ROOT/build.sh"
sh "$ROOT/scripts/check-release-basics.sh"

echo "Tests passed"
