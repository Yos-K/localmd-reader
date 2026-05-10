package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class OpenDocumentTabsTest {
    public static void main(String[] args) {
        OpenDocumentTabsTest test = new OpenDocumentTabsTest();
        test.initialTabsExposeTheInitialDocumentAsActiveTab();
        test.openAddsANewDocumentAsTheActiveTab();
        test.openExistingUriReplacesThatTabAndActivatesItWithoutDuplicate();
        test.activateSwitchesTheActiveTabByIndex();
        test.closeInactiveTabKeepsCurrentActiveDocument();
        test.closeActiveMiddleTabActivatesTheNextDocument();
        test.closeActiveLastTabActivatesThePreviousDocument();
        test.closeOnlyTabKeepsTheLastDocumentOpen();
        test.closeOrFallbackReplacesTheOnlyOpenDocumentWithTheFallbackTab();
    }

    public void initialTabsExposeTheInitialDocumentAsActiveTab() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"));

        TestAssertions.assertEquals("Welcome", tabs.activeTab().title(), "initial document must be the active tab");
        TestAssertions.assertEquals(1, tabs.tabs().size(), "initial tabs must contain exactly one tab");
    }

    public void openAddsANewDocumentAsTheActiveTab() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"))
                .open(tab("First", "content://first", "first"));

        TestAssertions.assertEquals(2, tabs.tabs().size(), "opening a new URI must add a new tab");
        TestAssertions.assertEquals("First", tabs.activeTab().title(), "newly opened tab must become active");
    }

    public void openExistingUriReplacesThatTabAndActivatesItWithoutDuplicate() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"))
                .open(tab("First", "content://first", "old"))
                .open(tab("Second", "content://second", "second"))
                .open(tab("First Updated", "content://first", "new"));

        TestAssertions.assertEquals(3, tabs.tabs().size(), "opening an existing URI must not create a duplicate tab");
        TestAssertions.assertEquals(1, tabs.activeIndex(), "reopened existing tab must become active at its original position");
        TestAssertions.assertEquals("First Updated", tabs.activeTab().title(), "existing tab must use latest title");
        TestAssertions.assertEquals("new", tabs.activeTab().document().value(), "existing tab must use latest rendered document");
    }

    public void activateSwitchesTheActiveTabByIndex() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"))
                .open(tab("First", "content://first", "first"))
                .activate(0);

        TestAssertions.assertEquals("Welcome", tabs.activeTab().title(), "activating a tab index must switch the active document");
    }

    public void closeInactiveTabKeepsCurrentActiveDocument() {
        OpenDocumentTabs tabs = threeTabs().activate(2).close(1);

        TestAssertions.assertEquals(2, tabs.tabs().size(), "closing an inactive tab must remove one tab");
        TestAssertions.assertEquals("Second", tabs.activeTab().title(), "closing an inactive tab before the active tab must keep the same active document");
    }

    public void closeActiveMiddleTabActivatesTheNextDocument() {
        OpenDocumentTabs tabs = threeTabs().activate(1).close(1);

        TestAssertions.assertEquals(2, tabs.tabs().size(), "closing the active middle tab must remove one tab");
        TestAssertions.assertEquals("Second", tabs.activeTab().title(), "closing the active middle tab must activate the next document");
    }

    public void closeActiveLastTabActivatesThePreviousDocument() {
        OpenDocumentTabs tabs = threeTabs().activate(2).close(2);

        TestAssertions.assertEquals(2, tabs.tabs().size(), "closing the active last tab must remove one tab");
        TestAssertions.assertEquals("First", tabs.activeTab().title(), "closing the active last tab must activate the previous document");
    }

    public void closeOnlyTabKeepsTheLastDocumentOpen() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome")).close(0);

        TestAssertions.assertEquals(1, tabs.tabs().size(), "closing the only tab must keep one document open");
        TestAssertions.assertEquals("Welcome", tabs.activeTab().title(), "closing the only tab must keep the same document active");
    }

    public void closeOrFallbackReplacesTheOnlyOpenDocumentWithTheFallbackTab() {
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
        return OpenDocumentTab.of(title, uri, SafeHtml.fromTrustedRendererOutput(document));
    }

}
