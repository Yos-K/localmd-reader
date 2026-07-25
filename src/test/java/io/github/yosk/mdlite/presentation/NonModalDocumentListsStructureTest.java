package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public final class NonModalDocumentListsStructureTest {
    @Test
    void recentAndPinnedActionsToggleMenuPanelsWithoutClosingTheMenu() throws IOException {
        String actions = source("MainMenuActions.java") + source("PinnedDocumentMenuActions.java");

        TestAssertions.assertContains(actions, "activity.toggleRecentDocumentsPanel();",
                "recent files must expand inside the navigation menu");
        TestAssertions.assertContains(actions, "activity.togglePinnedDocumentsPanel();",
                "pinned files must expand inside the navigation menu");
        TestAssertions.assertNotContains(
                actions, "activity.showRecentDocuments();", "recent files must not open a modal document list");
        TestAssertions.assertNotContains(
                actions, "activity.showPinnedDocuments();", "pinned files must not open a modal document list");
    }

    @Test
    void readerScreenOwnsExpandableRecentAndPinnedPanels() throws IOException {
        String initializer = source("ReaderScreenInitializer.java").replaceAll("\\s+", " ");
        String panel = source("DocumentListMenuPanel.java");

        TestAssertions.assertContains(initializer, "new DocumentListMenuPanel(activity, false)",
                "the reader screen must compose the recent-files panel");
        TestAssertions.assertContains(initializer, "new DocumentListMenuPanel(activity, true)",
                "the reader screen must compose the pinned-files panel");
        TestAssertions.assertContains(
                panel, "void openDocument(", "opening a listed document must be an explicit panel command");
        TestAssertions.assertContains(
                panel, "void unpinDocument(", "unpinning one document must be an explicit panel command");
        TestAssertions.assertContains(
                panel, "void clearDocuments()", "clearing the displayed list must be an explicit panel command");
    }

    @Test
    void gestureSettingsToggleAnExpandableMenuPanel() throws IOException {
        String actions = source("MainMenuActions.java");
        String initializer = source("ReaderScreenInitializer.java");

        TestAssertions.assertContains(actions, "activity.toggleGestureShortcutsPanel();",
                "gesture settings must expand without leaving the navigation menu");
        TestAssertions.assertContains(initializer, "new GestureShortcutMenuPanel(activity)",
                "the reader screen must compose gesture settings as a menu panel");
        TestAssertions.assertNotContains(
                actions, "activity.showGestureShortcutsDialog();", "gesture settings must not start with a modal list");
        TestAssertions.assertContains(source("GestureShortcutDialogs.java"), "void selectShortcutAction(",
                "selecting a shortcut action must have an explicit command entry point");
    }

    @Test
    void gestureActionChoicesStayInsideTheExpandablePanel() throws IOException {
        String panel = source("GestureShortcutMenuPanel.java");
        String dialogs = source("GestureShortcutDialogs.java");

        TestAssertions.assertContains(
                panel, "void showActions(int targetIndex)", "a selected gesture must reveal actions inline");
        TestAssertions.assertContains(panel, "void showGestureList()", "inline action choices must return to the list");
        TestAssertions.assertNotContains(
                dialogs, "new AlertDialog.Builder(activity)", "gesture configuration must not use modal choices");
    }

    @Test
    void themeChoicesStayInsideTheSettingsPanel() throws IOException {
        String actions = source("MainMenuActions.java");
        String initializer = source("ReaderScreenInitializer.java");
        String panel = source("ThemeMenuPanel.java");

        TestAssertions.assertContains(actions, "activity.toggleThemePanel();",
                "the theme action must toggle inline choices without closing the menu");
        TestAssertions.assertContains(
                initializer, "new ThemeMenuPanel(activity)", "the settings panel must own theme choices");
        TestAssertions.assertContains(panel, "void selectTheme(", "theme selection must be an explicit panel command");
        TestAssertions.assertNotContains(
                actions, "activity.showThemeDialog();", "theme selection must not open a modal dialog");
    }

    @Test
    void appearanceChangesPreserveTheMenuAccessibilityAction() throws IOException {
        String activity = source("MainActivity.java");

        TestAssertions.assertContains(activity, "menuAccessibilityDescription()",
                "localized updates must derive the menu action from its visible state");
        TestAssertions.assertNotContains(activity,
                "menuButton.setContentDescription(viewerText.openMenuDescription());\n        appTitle",
                "localized updates must not reset an open menu to the open action");
    }

    @Test
    void everyExpandablePanelImmediatelyFollowsItsOwningAction() throws IOException {
        String initializer = source("ReaderScreenInitializer.java").replaceAll("\\s+", " ");

        TestAssertions.assertContains(initializer,
                "activity.pinnedFilesButton, activity.pinnedDocumentsPanel, activity.recentButton, "
                        + "activity.recentDocumentsPanel",
                "pinned and recent contents must remain attached to their own rows");
        TestAssertions.assertContains(initializer,
                "activity.tableOfContentsButton, activity.tableOfContentsPanel, activity.findInDocumentButton",
                "the table of contents must remain attached to its row");
        TestAssertions.assertContains(initializer,
                "activity.themeButton, activity.themePanel, activity.languageButton, "
                        + "activity.controlsPlacementButton, activity.gestureShortcutsButton, "
                        + "activity.gestureShortcutPanel",
                "theme and gesture contents must remain attached to their own rows");
    }

    private static String source(String fileName) throws IOException {
        String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
        return new String(Files.readAllBytes(
                                  Paths.get(projectRoot, "src/main/java/io/github/yosk/mdlite/presentation", fileName)),
                StandardCharsets.UTF_8);
    }
}
