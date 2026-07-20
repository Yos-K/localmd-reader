#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
BUILD="$ROOT/build/unit-tests"
LIB="$BUILD/lib"
MAIN_CLASSES="$BUILD/main"
TEST_CLASSES="$BUILD/test"
JUNIT_VERSION="1.11.4"
JUNIT_JAR="$LIB/junit-platform-console-standalone-$JUNIT_VERSION.jar"
ARCHUNIT_VERSION="1.3.0"
ARCHUNIT_JAR="$LIB/archunit-$ARCHUNIT_VERSION.jar"
SLF4J_VERSION="2.0.13"
SLF4J_API_JAR="$LIB/slf4j-api-$SLF4J_VERSION.jar"
SLF4J_NOP_JAR="$LIB/slf4j-nop-$SLF4J_VERSION.jar"
JQWIK_VERSION="1.9.0"
JQWIK_API_JAR="$LIB/jqwik-api-$JQWIK_VERSION.jar"
JQWIK_ENGINE_JAR="$LIB/jqwik-engine-$JQWIK_VERSION.jar"

rm -rf "$BUILD"
mkdir -p "$MAIN_CLASSES" "$TEST_CLASSES" "$LIB"
mkdir -p "$BUILD/generated/io/github/yosk/mdlite/infrastructure"
cat > "$BUILD/generated/io/github/yosk/mdlite/infrastructure/BuildConfig.java" <<EOF
package io.github.yosk.mdlite.infrastructure;

public final class BuildConfig {
    public static final boolean PRO_FEATURES_ENABLED = false;
    public static final boolean PLAY_BILLING_ENABLED = false;

    private BuildConfig() {
    }
}
EOF

download_if_missing() {
  target_path="$1"
  url="$2"
  if [ ! -f "$target_path" ]; then
    echo "Downloading $(basename "$target_path")..."
    tmp_path="$target_path.tmp"
    rm -f "$tmp_path"
    curl --fail --location --silent --show-error --retry 3 --retry-delay 1 "$url" -o "$tmp_path"
    if ! jar tf "$tmp_path" >/dev/null; then
      rm -f "$tmp_path"
      echo "Downloaded dependency is not a valid jar: $(basename "$target_path")" >&2
      exit 1
    fi
    mv "$tmp_path" "$target_path"
  fi
  if ! jar tf "$target_path" >/dev/null; then
    rm -f "$target_path"
    echo "Cached dependency is not a valid jar: $(basename "$target_path")" >&2
    exit 1
  fi
}

download_if_missing "$JUNIT_JAR" \
  "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$JUNIT_VERSION/junit-platform-console-standalone-$JUNIT_VERSION.jar"
download_if_missing "$ARCHUNIT_JAR" \
  "https://repo1.maven.org/maven2/com/tngtech/archunit/archunit/$ARCHUNIT_VERSION/archunit-$ARCHUNIT_VERSION.jar"
download_if_missing "$SLF4J_API_JAR" \
  "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/$SLF4J_VERSION/slf4j-api-$SLF4J_VERSION.jar"
download_if_missing "$SLF4J_NOP_JAR" \
  "https://repo1.maven.org/maven2/org/slf4j/slf4j-nop/$SLF4J_VERSION/slf4j-nop-$SLF4J_VERSION.jar"
download_if_missing "$JQWIK_API_JAR" \
  "https://repo1.maven.org/maven2/net/jqwik/jqwik-api/$JQWIK_VERSION/jqwik-api-$JQWIK_VERSION.jar"
download_if_missing "$JQWIK_ENGINE_JAR" \
  "https://repo1.maven.org/maven2/net/jqwik/jqwik-engine/$JQWIK_VERSION/jqwik-engine-$JQWIK_VERSION.jar"

LIB_CLASSPATH="$JUNIT_JAR:$ARCHUNIT_JAR:$SLF4J_API_JAR:$SLF4J_NOP_JAR:$JQWIK_API_JAR:$JQWIK_ENGINE_JAR"

find "$ROOT/src/main/java" -name "*.java" ! -path "*/presentation/*" > "$BUILD/main-sources.txt"
find "$ROOT/src/main/java/io/github/yosk/mdlite/presentation" -name "MermaidRenderErrorHtml.java" >> "$BUILD/main-sources.txt"
find "$BUILD/generated" -name "*.java" >> "$BUILD/main-sources.txt"
find "$ROOT/src/test/java" -name "*.java" > "$BUILD/test-sources.txt"

if [ -s "$BUILD/main-sources.txt" ]; then
  javac -source 8 -target 8 -d "$MAIN_CLASSES" @"$BUILD/main-sources.txt"
fi
javac -source 8 -target 8 -cp "$MAIN_CLASSES:$LIB_CLASSPATH" -d "$TEST_CLASSES" @"$BUILD/test-sources.txt"

java -Xmx1024m -jar "$JUNIT_JAR" \
  --class-path "$MAIN_CLASSES:$TEST_CLASSES:$LIB_CLASSPATH" \
  --scan-class-path "$TEST_CLASSES" \
  --include-classname "^.*(Test|Tests|Property|Properties)$" \
  --fail-if-no-tests

echo "Unit tests passed"
