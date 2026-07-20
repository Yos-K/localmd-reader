#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"

if [ -f "$ROOT/src/main/assets/mermaid.min.js" ]; then
  test -f "$ROOT/THIRD_PARTY_NOTICES.md"
  test -f "$ROOT/docs/third-party/mermaid-11.15.0-MIT.txt"
  grep -q "Mermaid" "$ROOT/THIRD_PARTY_NOTICES.md"
  grep -q "11.15.0" "$ROOT/THIRD_PARTY_NOTICES.md"
  grep -q "MIT" "$ROOT/THIRD_PARTY_NOTICES.md"
  grep -q "src/main/assets/mermaid.min.js" "$ROOT/THIRD_PARTY_NOTICES.md"
  grep -q "The MIT License" "$ROOT/docs/third-party/mermaid-11.15.0-MIT.txt"
fi

if grep -R -n "unpkg\\|jsdelivr\\|cdnjs" "$ROOT/src/main/java" "$ROOT/src/main/res"; then
  echo "CDN references are not allowed in app code." >&2
  exit 1
fi

echo "Third-party notice checks passed"
