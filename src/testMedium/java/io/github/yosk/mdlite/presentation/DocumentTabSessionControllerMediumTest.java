package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.file.RestorableOpenTabs;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.OpenDocumentTabs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public final class DocumentTabSessionControllerMediumTest {
    @Test
    public void activatingATabMakesTheRequestedDocumentActive() {
        MainActivity activity = activityWithTwoFiles();
        DocumentTabSessionController controller = new DocumentTabSessionController(activity);

        controller.activate(0);

        assertEquals("tab activation must update the authoritative open-tab session",
                "file:///first.md", activity.openTabs.activeTab().uri());
    }

    @Test
    public void activatingATabPersistsTheRequestedDocumentAsActive() {
        MainActivity activity = activityWithTwoFiles();
        DocumentTabSessionController controller = new DocumentTabSessionController(activity);

        controller.activate(0);
        RestorableOpenTabs restored = activity.tabPersistence.loadRestorableOpenTabs();

        assertEquals("a completed tab activation must persist the active document",
                "file:///first.md", restored.tabs().get(restored.activeIndex()).uri());
    }

    @Test
    public void closingATabRendersOnlyTheRemainingTab() {
        MainActivity activity = activityWithTwoFiles();
        DocumentTabSessionController controller = new DocumentTabSessionController(activity);

        controller.close(1);

        assertEquals("a completed close must refresh the visible tab strip",
                1, activity.tabRow.getChildCount());
    }

    @Test
    public void closingTheOnlyDocumentRestoresTheWelcomeSession() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        activity.openTabs = OpenDocumentTabs.withInitialTab(file("Only", "file:///only.md"));
        DocumentTabSessionController controller = new DocumentTabSessionController(activity);

        controller.close(0);

        assertEquals("closing the final document must leave an always-valid welcome session",
                MainActivity.WELCOME_URI, activity.openTabs.activeTab().uri());
    }

    private static MainActivity activityWithTwoFiles() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        activity.openTabs = OpenDocumentTabs.withInitialTab(file("First", "file:///first.md"))
                .open(file("Second", "file:///second.md"));
        activity.renderTabs();
        return activity;
    }

    private static OpenDocumentTab file(String title, String uri) {
        return OpenDocumentTab.fileDocument(
                title,
                uri,
                SafeHtml.fromTrustedRendererOutput("<p>document</p>"));
    }
}
