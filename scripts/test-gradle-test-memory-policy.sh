#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
APP_GRADLE="$ROOT/app/build.gradle"
CI_WORKFLOW="$ROOT/.github/workflows/ci.yml"

require_line() {
  pattern="$1"
  file="$2"
  message="$3"
  if ! grep -Eq "$pattern" "$file"; then
    echo "gradle-test-memory-policy: $message" >&2
    exit 1
  fi
}

require_line 'maxHeapSize[[:space:]]*=[[:space:]]*"1536m"' "$APP_GRADLE" \
  'test JVM heap must accommodate Robolectric without consuming the whole runner'
require_line 'maxParallelForks[[:space:]]*=[[:space:]]*1' "$APP_GRADLE" \
  'Android JVM tests must run in one worker at a time'
require_line 'forkEvery[[:space:]]*=[[:space:]]*20' "$APP_GRADLE" \
  'test workers must restart before accumulated Robolectric state exhausts memory'
require_line 'name:[[:space:]]*Run Free Gradle unit tests' "$CI_WORKFLOW" \
  'Free tests must have an isolated Gradle invocation'
require_line 'name:[[:space:]]*Run Pro Preview Gradle unit tests' "$CI_WORKFLOW" \
  'Pro Preview tests must have an isolated Gradle invocation'

echo "gradle-test-memory-policy: passed"
