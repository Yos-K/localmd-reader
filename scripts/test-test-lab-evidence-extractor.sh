#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
ACTUAL="${TMPDIR:-/tmp}/localmd-test-lab-evidence-uris.txt"

python3 "$ROOT/scripts/extract-test-lab-evidence-uris.py" \
  "$ROOT/scripts/test-fixtures/test-lab-matrix.json" > "$ACTUAL"

EXPECTED='gs://test-results/matrix/step-1.png
gs://test-results/matrix/step-2.PNG'

[ "$(cat "$ACTUAL")" = "$EXPECTED" ] || {
  echo "test-test-lab-evidence-extractor: screenshot URIs must be unique, stable, and limited to Google Cloud Storage" >&2
  exit 1
}

echo "Test Lab evidence extractor test passed"
