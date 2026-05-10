#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"

"$ROOT/scripts/run-unit-tests.sh"
"$ROOT/scripts/check-test-smells.sh"
"$ROOT/build.sh"
"$ROOT/scripts/check-release-basics.sh"

echo "Tests passed"
