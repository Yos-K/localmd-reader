package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.DocumentUri;
import io.github.yosk.mdlite.domain.SafeHtml;

public final class OpenDocumentTabSession {
    private OpenDocumentTabs tabs;

    public OpenDocumentTabSession(OpenDocumentTab initialTab) {
        this(OpenDocumentTabs.withInitialTab(initialTab));
    }

    public OpenDocumentTabSession(OpenDocumentTabs tabs) {
        if (tabs == null) {
            throw new IllegalArgumentException("open document tab session requires tabs");
        }
        this.tabs = tabs;
    }

    public OpenDocumentTabs tabs() {
        return tabs;
    }

    public void reset(OpenDocumentTabs tabs) {
        if (tabs == null) {
            throw new IllegalArgumentException("tab session reset requires tabs");
        }
        this.tabs = tabs;
    }

    public void open(OpenDocumentTab tab) {
        tabs = tabs.open(tab);
    }

    public void activate(int index) {
        tabs = tabs.activate(index);
    }

    public void activatePrevious() {
        tabs = tabs.activatePrevious();
    }

    public void activateNext() {
        tabs = tabs.activateNext();
    }

    public DocumentTabCloseResult closeOrFallback(int index, OpenDocumentTab fallbackTab) {
        DocumentTabCloseResult result = tabs.closeOrFallback(index, fallbackTab);
        tabs = result.tabs();
        return result;
    }

    public void replaceRenderedDocument(
            DocumentUri documentUri,
            SafeHtml document,
            ReplacementHandler handler) {
        if (documentUri == null || document == null || handler == null) {
            throw new IllegalArgumentException("tab replacement requires a URI, document, and handler");
        }
        OpenDocumentTabs replaced = tabs.replaceRenderedDocument(documentUri.value(), document);
        if (replaced == tabs) {
            handler.unchanged();
            return;
        }
        tabs = replaced;
        handler.replaced(tabs);
    }

    public interface ReplacementHandler {
        void replaced(OpenDocumentTabs tabs);
        void unchanged();
    }
}
