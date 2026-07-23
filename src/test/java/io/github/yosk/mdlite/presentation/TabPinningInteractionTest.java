package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public final class TabPinningInteractionTest {
    @Test
    void everyTabRegistersTheLongPressPinningInteraction() throws IOException {
        TestAssertions.assertContains(mainActivitySource(), "button.setOnLongClickListener(this)",
                "tab rendering must keep the discoverable long-press pinning interaction");
    }

    @Test
    void pinnedTabsRenderAVisiblePinMark() throws IOException {
        TestAssertions.assertContains(mainActivitySource(), "R.drawable.ic_push_pin_18",
                "pinned tabs must communicate bookmark state without opening the menu");
    }

    @Test
    void pinMutationsRefreshEveryVisibleRepresentation() throws IOException {
        TestAssertions.assertContains(mainActivitySource(),
                "renderTabs();\n        refreshMenuActionButtons();\n        showMessage(message);",
                "pin mutations must refresh tab marks and menu actions together");
    }

    @Test
    void closingATabDoesNotDeleteItsPersistentBookmark() throws IOException {
        TestAssertions.assertNotContains(
                sourceFile("DocumentTabSessionController.java"), "unpinDocument",
                "closing an open tab must not silently remove its pinned-file bookmark");
    }

    private static String mainActivitySource() throws IOException {
        return sourceFile("MainActivity.java");
    }

    private static String sourceFile(String fileName) throws IOException {
        String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
        return new String(Files.readAllBytes(Paths.get(projectRoot,
                "src/main/java/io/github/yosk/mdlite/presentation/" + fileName)),
                StandardCharsets.UTF_8);
    }
}
