#!/data/data/com.termux/files/usr/bin/sh
# Require every choice-bearing Android dialog to name a modeled interaction
# surface. Informational dialogs with only an acknowledgement button are out of
# scope because they do not hold a user decision or an incomplete workflow.
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"

SOURCE_ROOT="${1:-src/main/java/io/github/yosk/mdlite/presentation}"
SURFACES="${2:-docs/harness/interaction-model-surfaces.psv}"

[ -d "$SOURCE_ROOT" ] || {
  echo "interaction-surface-registration: missing source directory: $SOURCE_ROOT" >&2
  exit 1
}
[ -f "$SURFACES" ] || {
  echo "interaction-surface-registration: missing surface registry: $SURFACES" >&2
  exit 1
}

JAVA_FILES="$(find "$SOURCE_ROOT" -type f -name '*.java' -print)"
[ -n "$JAVA_FILES" ] || {
  echo "interaction-surface-registration: no Java sources under $SOURCE_ROOT" >&2
  exit 1
}

awk -F'|' '
FILENAME == ARGV[1] {
  if ($1 != "" && substr($1, 1, 1) != "#" && $1 != "surface_id") registered[$1] = 1
  next
}
FNR == 1 {
  previous = ""
}
/\.set(Items|MultiChoiceItems|View)[[:space:]]*\(/ {
  marker = previous
  if (marker !~ /^[[:space:]]*\/\/[[:space:]]*interaction-surface:[[:space:]]*/) {
    failures++
    print "interaction-surface-registration: FAIL: " FILENAME ":" FNR ": choice surface has no interaction-surface marker" > "/dev/stderr"
    next
  }
  sub(/^.*interaction-surface:[[:space:]]*/, "", marker)
  sub(/[[:space:]]*$/, "", marker)
  if (!(marker in registered)) {
    failures++
    print "interaction-surface-registration: FAIL: " FILENAME ":" FNR ": unknown surface id: " marker > "/dev/stderr"
  }
}
{
  previous = $0
}
END {
  if (failures > 0) exit 1
}
' "$SURFACES" $JAVA_FILES

echo "interaction-surface-registration: all choice surfaces are registered."
