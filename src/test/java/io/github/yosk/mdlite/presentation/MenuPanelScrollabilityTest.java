package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public final class MenuPanelScrollabilityTest {
    @Test
    void menuPanelIsHostedByAScrollContainerSoEveryExpandedSectionRemainsReachable()
            throws IOException {
        String source = mainActivitySource();

        TestAssertions.assertContains(source, "menuScrollContainer.addView(menuPanel",
                "The complete menu must be inside its vertical scroll container");
    }

    @Test
    void menuTransitionMovesTheScrollContainerThatOwnsTheVisiblePanel() throws IOException {
        String source = mainActivitySource();

        TestAssertions.assertContains(source,
                "new MenuTransitions(menuScrollContainer, menuScrim)",
                "Menu animation must target the scroll container");
    }

    private static String mainActivitySource() throws IOException {
        String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
        return new String(Files.readAllBytes(Paths.get(projectRoot,
                "src/main/java/io/github/yosk/mdlite/presentation/MainActivity.java")),
                StandardCharsets.UTF_8);
    }
}
