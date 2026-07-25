package io.github.yosk.mdlite.presentation;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import io.github.yosk.mdlite.infrastructure.ReaderLinkNavigation;

final class AppLinkClient extends WebViewClient implements ReaderLinkNavigation.Handler {
    private final MainActivity activity;

    AppLinkClient(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return handleUrlLoading(url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Uri uri = request == null ? null : request.getUrl();
        return handleUrlLoading(uri == null ? null : uri.toString());
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return activity.openActiveRelativeImage(url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        Uri uri = request == null ? null : request.getUrl();
        return activity.openActiveRelativeImage(uri == null ? null : uri.toString());
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        activity.restorePendingScrollAfterPageLoad();
    }

    @Override
    public void openMarkdownPicker() {
        activity.openMarkdownPicker();
    }

    @Override
    public void openRelativeMarkdown(String requestUrl) {
        activity.openActiveRelativeMarkdownLink(requestUrl);
    }

    @Override
    public void openExternalUrl(String url) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private boolean handleUrlLoading(String url) {
        ReaderLinkNavigation.from(url).perform(this);
        return true;
    }
}
