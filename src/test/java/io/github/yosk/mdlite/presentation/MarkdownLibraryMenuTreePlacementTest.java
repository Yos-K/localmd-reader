package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public final class MarkdownLibraryMenuTreePlacementTest {
    @Test
    void markdownLibraryTreeAppearsImmediatelyAfterItsMenuAction() throws IOException {
        String source = sourceFile("MainActivity.java");

        TestAssertions.assertContains(source,
                "markdownLibraryButton, markdownLibraryMenuTree, createFromClipboardButton",
                "The persistent tree must appear directly below the library action");
    }

    @Test
    void projectLibraryResultsRenderIntoThePersistentMenuTree() throws IOException {
        String source = sourceFile("MainActivity.java");

        TestAssertions.assertContains(source, "markdownLibraryMenuTree.show(location, listing)",
                "Library results must render in the menu instead of a dialog");
    }

    @Test
    void openingADocumentKeepsTheTreeStateForTheNextMenuVisit() throws IOException {
        String source = sourceFile("MarkdownLibraryMenuTree.java");

        TestAssertions.assertContains(source, "activity.closeMenu();",
                "Opening a document must close only the menu and preserve the tree component");
    }

    @Test
    void markdownLibraryActionUsesTheSameExpandAffordanceAsTheTableOfContents() throws IOException {
        String source = sourceFile("MainActivity.java");

        TestAssertions.assertContains(source,
                "applyExpandChevron(markdownLibraryButton, markdownLibraryMenuTree.isExpanded())",
                "The library action must communicate whether its persistent tree is expanded");
    }

    private static String sourceFile(String fileName) throws IOException {
        String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
        return new String(Files.readAllBytes(Paths.get(projectRoot,
                "src/main/java/io/github/yosk/mdlite/presentation/" + fileName)),
                StandardCharsets.UTF_8);
    }
}
