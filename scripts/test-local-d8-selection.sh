#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
FAKE_HOME="/tmp/localmd-d8-policy-home"
EXPECTED="$FAKE_HOME/AndroidDev/sdk/build-tools/35.0.0/d8"

resolved_default="$(env -u D8 HOME="$FAKE_HOME" sh -c '. "$1/env.project.sh"; printf "%s" "$D8"' sh "$ROOT")"
[ "$resolved_default" = "$EXPECTED" ] || {
  echo "local-d8-selection: expected $EXPECTED, got $resolved_default" >&2
  exit 1
}

resolved_override="$(D8=/custom/d8 HOME="$FAKE_HOME" sh -c '. "$1/env.project.sh"; printf "%s" "$D8"' sh "$ROOT")"
[ "$resolved_override" = "/custom/d8" ] || {
  echo "local-d8-selection: explicit D8 override was discarded" >&2
  exit 1
}

echo "local-d8-selection: modern project D8 default and explicit override are preserved"
