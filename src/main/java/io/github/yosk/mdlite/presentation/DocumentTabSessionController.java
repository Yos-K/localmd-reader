package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.viewer.OpenDocumentTabs;
import io.github.yosk.mdlite.viewer.DocumentTabCloseResult;

final class DocumentTabSessionController {
    private final MainActivity activity;

    DocumentTabSessionController(MainActivity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("document tab session requires an activity");
        }
        this.activity = activity;
    }

    void activate(int tabIndex) {
        complete(activity.openTabs.activate(tabIndex));
    }

    void activatePrevious() {
        complete(activity.openTabs.activatePrevious());
    }

    void activateNext() {
        complete(activity.openTabs.activateNext());
    }

    void close(int tabIndex) {
        DocumentTabCloseResult result = activity.openTabs.closeOrFallback(tabIndex, activity.initialTab());
        activity.documentRenderingCoordinator.close(result);
        complete(result.tabs());
    }

    private void complete(OpenDocumentTabs tabs) {
        activity.openTabs = tabs;
        activity.clearMessage();
        activity.updateLocalizedText();
        activity.renderTabs();
        activity.renderCurrentDocument();
        activity.saveOpenTabs();
    }
}
