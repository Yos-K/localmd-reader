#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
SCRIPT="$ROOT/scripts/run-unit-tests.sh"

require_pattern() {
  pattern="$1"
  message="$2"
  if ! grep -Eq -- "$pattern" "$SCRIPT"; then
    printf '%s\n' "$message" >&2
    exit 1
  fi
}

require_pattern 'curl .*--fail' \
  "run-unit-tests.sh must fail dependency downloads on HTTP errors."
require_pattern 'curl .*--retry [1-9]' \
  "run-unit-tests.sh must retry transient dependency downloads."
require_pattern 'jar tf "\$target_path"' \
  "run-unit-tests.sh must validate downloaded jars before compiling tests."

printf '%s\n' "Unit test dependency download guard passed"
