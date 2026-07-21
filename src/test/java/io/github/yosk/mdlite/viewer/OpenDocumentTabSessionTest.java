package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.DocumentUri;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class OpenDocumentTabSessionTest {
    @Test
    void openingDocumentMakesItActiveInTheOwnedTabState() {
        OpenDocumentTabSession session = new OpenDocumentTabSession(tab("Welcome", "app://welcome"));

        session.open(tab("Guide", "content://guide"));

        TestAssertions.assertEquals("Guide", session.tabs().activeTab().title(),
                "session opening must update its owned tab state");
    }

    @Test
    void closingDocumentUpdatesOwnedTabsAndReturnsTheSameCloseOutcome() {
        OpenDocumentTabSession session = new OpenDocumentTabSession(tab("Welcome", "app://welcome"));
        session.open(tab("Guide", "content://guide"));

        DocumentTabCloseResult result = session.closeOrFallback(1, tab("Welcome", "app://welcome"));

        TestAssertions.assertSame(session.tabs(), result.tabs(),
                "close result and owned tab state must refer to the same completed transition");
    }

    @Test
    void replacingKnownDocumentDispatchesItsUpdatedTabState() {
        OpenDocumentTabSession session = new OpenDocumentTabSession(tab("Guide", "content://guide"));
        ReplacementCount count = new ReplacementCount();

        session.replaceRenderedDocument(
                DocumentUri.from("content://guide"),
                SafeHtml.fromTrustedRendererOutput("<h1>Updated</h1>"),
                count);

        TestAssertions.assertEquals(1, count.value,
                "known document replacement must dispatch exactly one completed update");
    }

    @Test
    void replacingUnknownDocumentDispatchesNoTabUpdate() {
        OpenDocumentTabSession session = new OpenDocumentTabSession(tab("Guide", "content://guide"));
        ReplacementCount count = new ReplacementCount();

        session.replaceRenderedDocument(
                DocumentUri.from("content://missing"),
                SafeHtml.fromTrustedRendererOutput("unused"),
                count);

        TestAssertions.assertEquals(0, count.value,
                "unknown document replacement must not dispatch a tab update");
    }

    private static OpenDocumentTab tab(String title, String uri) {
        return OpenDocumentTab.fileDocument(
                title,
                uri,
                SafeHtml.fromTrustedRendererOutput("<p>document</p>"));
    }

    private static final class ReplacementCount implements OpenDocumentTabSession.ReplacementHandler {
        private int value;

        @Override public void replaced(OpenDocumentTabs tabs) { value++; }
        @Override public void unchanged() { }
    }
}
