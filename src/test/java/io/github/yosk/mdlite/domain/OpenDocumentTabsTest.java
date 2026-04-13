package io.github.yosk.mdlite.domain;

public final class OpenDocumentTabsTest {
    public static void main(String[] args) {
        OpenDocumentTabsTest test = new OpenDocumentTabsTest();
        test.initialTabsExposeTheInitialDocumentAsActiveTab();
        test.openAddsANewDocumentAsTheActiveTab();
        test.openExistingUriReplacesThatTabAndActivatesItWithoutDuplicate();
        test.activateSwitchesTheActiveTabByIndex();
        test.activatePreviousSwitchesToThePreviousTab();
        test.activatePreviousKeepsTheFirstTabActiveAtTheStartBoundary();
        test.activateNextSwitchesToTheNextTab();
        test.activateNextKeepsTheLastTabActiveAtTheEndBoundary();
    }

    public void initialTabsExposeTheInitialDocumentAsActiveTab() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"));

        assertEquals("Welcome", tabs.activeTab().title(), "initial document must be the active tab");
        assertEquals(1, tabs.tabs().size(), "initial tabs must contain exactly one tab");
    }

    public void openAddsANewDocumentAsTheActiveTab() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"))
                .open(tab("First", "content://first", "first"));

        assertEquals(2, tabs.tabs().size(), "opening a new URI must add a new tab");
        assertEquals("First", tabs.activeTab().title(), "newly opened tab must become active");
    }

    public void openExistingUriReplacesThatTabAndActivatesItWithoutDuplicate() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"))
                .open(tab("First", "content://first", "old"))
                .open(tab("Second", "content://second", "second"))
                .open(tab("First Updated", "content://first", "new"));

        assertEquals(3, tabs.tabs().size(), "opening an existing URI must not create a duplicate tab");
        assertEquals(1, tabs.activeIndex(), "reopened existing tab must become active at its original position");
        assertEquals("First Updated", tabs.activeTab().title(), "existing tab must use latest title");
        assertEquals("new", tabs.activeTab().document().value(), "existing tab must use latest rendered document");
    }

    public void activateSwitchesTheActiveTabByIndex() {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"))
                .open(tab("First", "content://first", "first"))
                .activate(0);

        assertEquals("Welcome", tabs.activeTab().title(), "activating a tab index must switch the active document");
    }

    public void activatePreviousSwitchesToThePreviousTab() {
        OpenDocumentTabs tabs = threeTabs().activate(2).activatePrevious();

        assertEquals(1, tabs.activeIndex(), "previous tab gesture must move one tab left");
        assertEquals("First", tabs.activeTab().title(), "previous tab gesture must activate the previous document");
    }

    public void activatePreviousKeepsTheFirstTabActiveAtTheStartBoundary() {
        OpenDocumentTabs tabs = threeTabs().activate(0).activatePrevious();

        assertEquals(0, tabs.activeIndex(), "previous tab gesture at the first tab must keep the first tab active");
    }

    public void activateNextSwitchesToTheNextTab() {
        OpenDocumentTabs tabs = threeTabs().activate(0).activateNext();

        assertEquals(1, tabs.activeIndex(), "next tab gesture must move one tab right");
        assertEquals("First", tabs.activeTab().title(), "next tab gesture must activate the next document");
    }

    public void activateNextKeepsTheLastTabActiveAtTheEndBoundary() {
        OpenDocumentTabs tabs = threeTabs().activate(2).activateNext();

        assertEquals(2, tabs.activeIndex(), "next tab gesture at the last tab must keep the last tab active");
    }

    private static OpenDocumentTabs threeTabs() {
        return OpenDocumentTabs.withInitialTab(tab("Welcome", "app://welcome", "welcome"))
                .open(tab("First", "content://first", "first"))
                .open(tab("Second", "content://second", "second"));
    }

    private static OpenDocumentTab tab(String title, String uri, String document) {
        return OpenDocumentTab.of(title, uri, SafeHtml.fromTrustedRendererOutput(document));
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + "\nExpected: " + expected + "\nActual: " + actual);
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + "\nExpected: " + expected + "\nActual: " + actual);
        }
    }
}
