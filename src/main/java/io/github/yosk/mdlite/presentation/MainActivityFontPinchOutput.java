package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.viewer.FontPinchController;

final class MainActivityFontPinchOutput implements FontPinchController.Output {
    private final MainActivity activity;

    MainActivityFontPinchOutput(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void applyTextZoom(int percent) {
        activity.webView.getSettings().setTextZoom(percent);
    }

    @Override
    public int currentScrollY() {
        return activity.webView.getScrollY();
    }

    @Override
    public int currentViewportHeight() {
        return activity.webView.getHeight();
    }

    @Override
    public void requestDocumentRender(int scrollYAfterRender) {
        activity.renderCurrentDocument();
    }

    @Override
    public void restoreScrollPosition(int scrollY) {
        activity.webView.post(new RestoreScrollPosition(activity.webView, scrollY, 6));
    }
}
