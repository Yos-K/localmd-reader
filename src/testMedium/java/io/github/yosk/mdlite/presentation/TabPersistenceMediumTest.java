package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import io.github.yosk.mdlite.domain.RecentDocumentLimit;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.file.PinnedDocuments;
import io.github.yosk.mdlite.file.RecentDocuments;
import io.github.yosk.mdlite.file.RestorableOpenTabs;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.OpenDocumentTabs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

/**
 * Medium-tier test: TabPersistence survives a save -> restore round trip through
 * real Android SharedPreferences on the JVM (Robolectric), the last of the three
 * Robolectric targets in docs/harness/test-strategy.md (after reader-WebView
 * JS-off #67 and Intent open #74).
 *
 * This glue (open-tab restore, Recent and Pinned documents) is Base64-encoded into
 * SharedPreferences, so it cannot run on the pure-JVM unit runner and was only
 * covered by the flaky/manual large smoke. Driving the real TabPersistence over the
 * application Context pushes that "persist and come back" coverage into the fast
 * gradle `test` job, every PR.
 */
@RunWith(RobolectricTestRunner.class)
public class TabPersistenceMediumTest {

    private static final SafeHtml DOC = SafeHtml.fromTrustedRendererOutput("<p>body</p>");

    private TabPersistence persistence() {
        Context context = RuntimeEnvironment.getApplication();
        return new TabPersistence(context, RecentDocumentLimit.fromEntitlement(null));
    }

    private OpenDocumentTab welcome() {
        return OpenDocumentTab.welcome("Welcome", "app://welcome", DOC);
    }

    private OpenDocumentTab file(String title, String uri) {
        return OpenDocumentTab.fileDocument(title, uri, DOC);
    }

    @Test
    public void openTabsRoundTripKeepsFileTabsInOrderAndRestoresTheActiveOne() {
        // Welcome (non-file) leads; the second file document is active.
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(welcome())
                .open(file("a.md", "file:///a.md"))
                .open(file("b.md", "file:///b.md"));
        TabPersistence persistence = persistence();
        persistence.saveOpenTabs(tabs);

        RestorableOpenTabs restored = persistence.loadRestorableOpenTabs();

        assertEquals("only the file documents are persisted (the welcome tab is dropped)",
                2, restored.tabs().size());
        assertEquals("file order is preserved", "file:///a.md", restored.tabs().get(0).uri());
        assertEquals("file order is preserved", "file:///b.md", restored.tabs().get(1).uri());
        assertEquals("titles survive the Base64 round trip", "b.md", restored.tabs().get(1).title());
        assertEquals("the active index is remapped past the dropped welcome tab",
                "file:///b.md", restored.tabs().get(restored.activeIndex()).uri());
    }

    @Test
    public void savingTabsWithNoFileDocumentsClearsAnyPreviouslyPersistedTabs() {
        TabPersistence persistence = persistence();
        persistence.saveOpenTabs(OpenDocumentTabs.withInitialTab(welcome())
                .open(file("a.md", "file:///a.md")));

        // Closing back to a welcome-only state must wipe the stored tabs.
        persistence.saveOpenTabs(OpenDocumentTabs.withInitialTab(welcome()));

        assertTrue("welcome-only state restores as empty (nothing to reopen)",
                persistence.loadRestorableOpenTabs().isEmpty());
    }

    @Test
    public void recentDocumentsRoundTripMostRecentFirstWithoutDuplicates() {
        TabPersistence persistence = persistence();
        persistence.recordRecentDocument("a.md", "file:///a.md");
        persistence.recordRecentDocument("b.md", "file:///b.md");
        persistence.recordRecentDocument("a.md", "file:///a.md"); // reopen a

        RecentDocuments recent = persistence.loadRecentDocuments();

        assertEquals("reopening collapses the duplicate", 2, recent.items().size());
        assertEquals("the most recently opened document leads", "file:///a.md",
                recent.items().get(0).uri());
        assertEquals("the older document follows", "file:///b.md", recent.items().get(1).uri());
    }

    @Test
    public void pinnedDocumentsRoundTripMostRecentlyPinnedFirst() {
        TabPersistence persistence = persistence();
        persistence.pinDocument("a.md", "file:///a.md");
        persistence.pinDocument("b.md", "file:///b.md");

        PinnedDocuments pinned = persistence.loadPinnedDocuments();

        assertEquals(2, pinned.items().size());
        assertEquals("the most recently pinned document leads", "file:///b.md",
                pinned.items().get(0).uri());
        assertEquals("a.md", pinned.items().get(1).displayName());
    }

    @Test
    public void clearingPinnedDocumentsRemovesThemFromPersistence() {
        TabPersistence persistence = persistence();
        persistence.pinDocument("a.md", "file:///a.md");

        persistence.clearPinnedDocuments();

        assertTrue("cleared pins do not come back", persistence.loadPinnedDocuments().items().isEmpty());
    }
}
