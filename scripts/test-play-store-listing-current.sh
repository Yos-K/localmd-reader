#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
JA="$ROOT/play-store/listing/ja-JP/full-description.txt"
EN="$ROOT/play-store/listing/en-US/full-description.txt"

reject_obsolete_claims() {
  file="$1"
  if grep -E 'v0\.1\.0|V0\.1\.0|viewing rather than editing|編集ではなく閲覧' "$file" >/dev/null; then
    echo "Store listing contains an obsolete product claim: $file" >&2
    exit 1
  fi
}

require_current_capability() {
  file="$1"
  pattern="$2"
  label="$3"
  if ! grep -E "$pattern" "$file" >/dev/null; then
    echo "Store listing must describe $label: $file" >&2
    exit 1
  fi
}

reject_obsolete_claims "$JA"
reject_obsolete_claims "$EN"

require_current_capability "$JA" 'クリップボード' 'clipboard creation'
require_current_capability "$JA" '検索' 'document search'
require_current_capability "$JA" 'ピン留め' 'pinned documents'
require_current_capability "$JA" '広告なし|広告はありません' 'no-ads policy'
require_current_capability "$EN" '[Cc]lipboard' 'clipboard creation'
require_current_capability "$EN" '[Ss]earch' 'document search'
require_current_capability "$EN" '[Pp]in' 'pinned documents'
require_current_capability "$EN" '[Nn]o ads' 'no-ads policy'

echo "Play Store listing currency test passed"
