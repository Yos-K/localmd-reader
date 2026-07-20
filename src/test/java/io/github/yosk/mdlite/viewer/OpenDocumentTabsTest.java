package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class OpenDocumentTabsTest {

    @Test
    void initialTabsExposeTheInitialDocumentAsActiveTab() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"));

        TestAssertions.assertEquals("Welcome", tabs.activeTab().title(), "initial document must be the active tab");
        TestAssertions.assertEquals(1, tabs.tabs().size(), "initial tabs must contain exactly one tab");
    }

    @Test
    void openAddsANewDocumentAsTheActiveTab() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"))
                .open(tab("First", "content://first", "first"));

        TestAssertions.assertEquals(2, tabs.tabs().size(), "opening a new URI must add a new tab");
        TestAssertions.assertEquals("First", tabs.activeTab().title(), "newly opened tab must become active");
    }

    @Test
    void openExistingUriReplacesThatTabAndActivatesItWithoutDuplicate() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"))
                .open(tab("First", "content://first", "old"))
                .open(tab("Second", "content://second", "second"))
                .open(tab("First Updated", "content://first", "new"));

        TestAssertions.assertEquals(3, tabs.tabs().size(), "opening an existing URI must not create a duplicate tab");
        TestAssertions.assertEquals(1, tabs.activeIndex(), "reopened existing tab must become active at its original position");
        TestAssertions.assertEquals("First Updated", tabs.activeTab().title(), "existing tab must use latest title");
        TestAssertions.assertEquals("new", tabs.activeTab().document().value(), "existing tab must use latest rendered document");
    }

    @Test
    void activateSwitchesTheActiveTabByIndex() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"))
                .open(tab("First", "content://first", "first"))
                .activate(0);

        TestAssertions.assertEquals("Welcome", tabs.activeTab().title(), "activating a tab index must switch the active document");
    }

    @Test
    void activatePreviousSwitchesToThePreviousTab() {
        OpenDocumentTabs tabs = threeTabs().activate(2).activatePrevious();

        TestAssertions.assertEquals("First", tabs.activeTab().title(), "activating previous must switch to the tab on the left");
    }

    @Test
    void activatePreviousWrapsFromFirstToLastTab() {
        OpenDocumentTabs tabs = threeTabs().activate(0).activatePrevious();

        TestAssertions.assertEquals("Second", tabs.activeTab().title(), "activating previous from the first tab must wrap to the last tab");
    }

    @Test
    void activateNextSwitchesToTheNextTab() {
        OpenDocumentTabs tabs = threeTabs().activate(1).activateNext();

        TestAssertions.assertEquals("Second", tabs.activeTab().title(), "activating next must switch to the tab on the right");
    }

    @Test
    void activateNextWrapsFromLastToFirstTab() {
        OpenDocumentTabs tabs = threeTabs().activate(2).activateNext();

        TestAssertions.assertEquals("Welcome", tabs.activeTab().title(), "activating next from the last tab must wrap to the first tab");
    }

    @Test
    void replaceRenderedDocumentKeepsTheCurrentlyActiveTab() {
        OpenDocumentTabs tabs = threeTabs().activate(2)
                .replaceRenderedDocument(
                        "content://first",
                        SafeHtml.fromTrustedRendererOutput("updated"));

        TestAssertions.assertEquals("Second", tabs.activeTab().title(), "re-rendering an inactive tab must preserve the active tab");
        TestAssertions.assertEquals("updated", tabs.tabs().get(1).document().value(), "re-rendering must replace the targeted tab document");
    }

    @Test
    void replaceRenderedDocumentForUnknownUriKeepsTheTabSessionUnchanged() {
        OpenDocumentTabs tabs = threeTabs().activate(1);

        OpenDocumentTabs unchanged = tabs.replaceRenderedDocument(
                "content://missing",
                SafeHtml.fromTrustedRendererOutput("unused"));

        TestAssertions.assertSame(tabs, unchanged, "an unknown URI must keep the existing tab session");
    }

    @Test
    void closeInactiveTabKeepsCurrentActiveDocument() {
        OpenDocumentTabs tabs = threeTabs().activate(2).close(1);

        TestAssertions.assertEquals(2, tabs.tabs().size(), "closing an inactive tab must remove one tab");
        TestAssertions.assertEquals("Second", tabs.activeTab().title(), "closing an inactive tab before the active tab must keep the same active document");
    }

    @Test
    void closeActiveMiddleTabActivatesTheNextDocument() {
        OpenDocumentTabs tabs = threeTabs().activate(1).close(1);

        TestAssertions.assertEquals(2, tabs.tabs().size(), "closing the active middle tab must remove one tab");
        TestAssertions.assertEquals("Second", tabs.activeTab().title(), "closing the active middle tab must activate the next document");
    }

    @Test
    void closeActiveLastTabActivatesThePreviousDocument() {
        OpenDocumentTabs tabs = threeTabs().activate(2).close(2);

        TestAssertions.assertEquals(2, tabs.tabs().size(), "closing the active last tab must remove one tab");
        TestAssertions.assertEquals("First", tabs.activeTab().title(), "closing the active last tab must activate the previous document");
    }

    @Test
    void closeOnlyTabKeepsTheLastDocumentOpen() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome")).close(0);

        TestAssertions.assertEquals(1, tabs.tabs().size(), "closing the only tab must keep one document open");
        TestAssertions.assertEquals("Welcome", tabs.activeTab().title(), "closing the only tab must keep the same document active");
    }

    @Test
    void closeOrFallbackReplacesTheOnlyOpenDocumentWithTheFallbackTab() {
        OpenDocumentTabs tabs = OpenDocumentTabs
                .withInitialTab(tab("First", "content://first", "first"))
                .closeOrFallback(0, tab("Welcome", "app://welcome", "welcome"));

        TestAssertions.assertEquals(1, tabs.tabs().size(), "closing the only document with fallback must keep one tab open");
        TestAssertions.assertEquals("Welcome", tabs.activeTab().title(), "closing the only document with fallback must show the fallback tab");
        TestAssertions.assertEquals("app://welcome", tabs.activeTab().uri(), "fallback tab must become the only active tab");
    }

    private static OpenDocumentTabs threeTabs() {
        return OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"))
                .open(tab("First", "content://first", "first"))
                .open(tab("Second", "content://second", "second"));
    }

    private static OpenDocumentTab tab(String title, String uri, String document) {
        return OpenDocumentTab.fileDocument(title, uri, SafeHtml.fromTrustedRendererOutput(document));
    }

}
