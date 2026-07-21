package io.github.yosk.mdlite.presentation;

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
        activity.documentTabSession.activate(tabIndex);
        complete();
    }

    void activatePrevious() {
        activity.documentTabSession.activatePrevious();
        complete();
    }

    void activateNext() {
        activity.documentTabSession.activateNext();
        complete();
    }

    void close(int tabIndex) {
        DocumentTabCloseResult result = activity.documentTabSession.closeOrFallback(tabIndex, activity.initialTab());
        activity.documentRenderingCoordinator.close(result);
        complete();
    }

    private void complete() {
        activity.clearMessage();
        activity.updateLocalizedText();
        activity.renderTabs();
        activity.renderCurrentDocument();
        activity.saveOpenTabs();
    }
}
