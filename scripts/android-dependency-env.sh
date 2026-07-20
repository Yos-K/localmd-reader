#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="${ROOT:-$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)}"
ANDROID_DEPS_DIR="${MDLITE_ANDROID_DEPS_DIR:-$ROOT/.android-deps}"
ANDROID_DEPENDENCY_CLASSPATH=""
ANDROID_DEPENDENCY_D8_INPUTS=""

if [ "${MDLITE_INCLUDE_ANDROID_DEPS:-false}" = "true" ] && [ -d "$ANDROID_DEPS_DIR/classes" ]; then
  for jar in "$ANDROID_DEPS_DIR"/classes/*.jar; do
    if [ -f "$jar" ]; then
      if [ -z "$ANDROID_DEPENDENCY_CLASSPATH" ]; then
        ANDROID_DEPENDENCY_CLASSPATH="$jar"
      else
        ANDROID_DEPENDENCY_CLASSPATH="$ANDROID_DEPENDENCY_CLASSPATH:$jar"
      fi
      ANDROID_DEPENDENCY_D8_INPUTS="$ANDROID_DEPENDENCY_D8_INPUTS $jar"
    fi
  done
fi

export ANDROID_DEPS_DIR ANDROID_DEPENDENCY_CLASSPATH ANDROID_DEPENDENCY_D8_INPUTS
