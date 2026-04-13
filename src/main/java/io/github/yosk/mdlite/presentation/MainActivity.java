package io.github.yosk.mdlite.presentation;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public final class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        configureWebView(webView);
        webView.loadDataWithBaseURL(null, initialHtml(), "text/html", "UTF-8", null);
        setContentView(webView);
    }

    private static void configureWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setDomStorageEnabled(false);
        settings.setDatabaseEnabled(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
    }

    private static String initialHtml() {
        return "<!doctype html>"
                + "<html><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                + "<style>"
                + "body{font-family:sans-serif;margin:24px;line-height:1.55;color:#172121;background:#f8fbfa;}"
                + "h1{font-size:24px;margin:0 0 12px;}"
                + "p{font-size:16px;margin:0 0 12px;}"
                + "code{background:#e6eeee;padding:2px 4px;border-radius:4px;}"
                + "</style></head><body>"
                + "<h1>MdLite Reader</h1>"
                + "<p>Lightweight Markdown viewing starts here.</p>"
                + "<p>No ads. No tracking. No network permission.</p>"
                + "<p><code>v0.1.0</code> foundation build</p>"
                + "</body></html>";
    }
}
