#!/data/data/com.termux/files/usr/bin/sh
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
BUILD="$ROOT/build/unit-tests"
MAIN_CLASSES="$BUILD/main"
TEST_CLASSES="$BUILD/test"

rm -rf "$BUILD"
mkdir -p "$MAIN_CLASSES" "$TEST_CLASSES"
mkdir -p "$BUILD/generated/io/github/yosk/mdlite/infrastructure"
cat > "$BUILD/generated/io/github/yosk/mdlite/infrastructure/BuildConfig.java" <<EOF
package io.github.yosk.mdlite.infrastructure;

public final class BuildConfig {
    public static final boolean PRO_FEATURES_ENABLED = false;

    private BuildConfig() {
    }
}
EOF

find "$ROOT/src/main/java" -name "*.java" ! -path "*/presentation/*" > "$BUILD/main-sources.txt"
find "$BUILD/generated" -name "*.java" >> "$BUILD/main-sources.txt"
find "$ROOT/src/test/java" -name "*.java" > "$BUILD/test-sources.txt"

if [ -s "$BUILD/main-sources.txt" ]; then
  javac -source 8 -target 8 -d "$MAIN_CLASSES" @"$BUILD/main-sources.txt"
fi
javac -source 8 -target 8 -cp "$MAIN_CLASSES" -d "$TEST_CLASSES" @"$BUILD/test-sources.txt"

java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.infrastructure.JavaSimpleMarkdownRendererTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.infrastructure.HtmlPageBuilderTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.infrastructure.BuildEntitlementSourceTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.FileTypeDetectorTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.FileSizePolicyTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.MarkdownFileOpenResultTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.FontSizeTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.RecentDocumentsTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.OpenDocumentTabsTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.RestorableOpenTabsTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.ControlsPlacementTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.ViewerLanguageTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.ViewerThemeTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.GestureShortcutActionTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.CircleGesturePathTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.FeatureEntitlementTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.FeatureEntitlementsTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.StaticEntitlementSourceTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.ProFeatureCatalogTest
java -cp "$MAIN_CLASSES:$TEST_CLASSES" io.github.yosk.mdlite.domain.ProFeaturePresentationTest
echo "Unit tests passed"
