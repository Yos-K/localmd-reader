package io.github.yosk.mdlite.presentation;

import android.webkit.WebView;

final class RestoreScrollPosition implements Runnable {
    private final WebView webView;
    private final int scrollY;
    private final int remainingAttempts;

    RestoreScrollPosition(WebView webView, int scrollY, int remainingAttempts) {
        this.webView = webView;
        this.scrollY = scrollY;
        this.remainingAttempts = remainingAttempts;
    }

    @Override
    public void run() {
        int scrollRange = Math.max(0, scaledContentHeight(webView) - webView.getHeight());
        webView.scrollTo(webView.getScrollX(), Math.min(scrollY, scrollRange));
        if (remainingAttempts > 1) {
            webView.postDelayed(new RestoreScrollPosition(webView, scrollY, remainingAttempts - 1), 80);
        }
    }

    private static int scaledContentHeight(WebView webView) {
        return Math.round(webView.getContentHeight() * webView.getScale());
    }
}
