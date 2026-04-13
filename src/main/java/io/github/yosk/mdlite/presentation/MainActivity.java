package io.github.yosk.mdlite.presentation;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.github.yosk.mdlite.domain.FileSizePolicy;
import io.github.yosk.mdlite.domain.FileTypeDetector;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.infrastructure.JavaSimpleMarkdownRenderer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class MainActivity extends Activity implements View.OnClickListener {
    private static final int REQUEST_OPEN_DOCUMENT = 1001;
    private static final long MAX_FILE_SIZE_BYTES = 2L * 1024L * 1024L;

    private final JavaSimpleMarkdownRenderer renderer = new JavaSimpleMarkdownRenderer();
    private final FileSizePolicy fileSizePolicy = new FileSizePolicy(MAX_FILE_SIZE_BYTES);

    private WebView webView;
    private TextView messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        Button openButton = new Button(this);
        openButton.setText("Open Markdown file");
        openButton.setAllCaps(false);
        openButton.setOnClickListener(this);

        messageView = new TextView(this);
        messageView.setGravity(Gravity.CENTER_VERTICAL);
        messageView.setPadding(24, 12, 24, 12);

        webView = new WebView(this);
        configureWebView(webView);
        webView.loadDataWithBaseURL(null, pageHtml(initialDocument()), "text/html", "UTF-8", null);

        root.addView(openButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(messageView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(webView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1));

        setContentView(root);
        handleIncomingIntent(getIntent());
    }

    @Override
    public void onClick(View view) {
        openMarkdownPicker();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_DOCUMENT && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                openUri(uri);
            }
        }
    }

    private void openMarkdownPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_OPEN_DOCUMENT);
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        Uri uri = intent.getData();
        if (Intent.ACTION_VIEW.equals(action) && uri != null) {
            openUri(uri);
        }
    }

    private void openUri(Uri uri) {
        FileInfo fileInfo = readFileInfo(uri);
        if (!FileTypeDetector.isMarkdownDisplayName(fileInfo.displayName)) {
            showMessage("This file cannot be opened.");
            return;
        }
        if (!fileSizePolicy.isReadableSize(fileInfo.sizeBytes)) {
            showMessage("The file is too large.");
            return;
        }

        try {
            String markdown = readText(uri, MAX_FILE_SIZE_BYTES);
            SafeHtml rendered = renderer.render(markdown);
            webView.loadDataWithBaseURL(null, pageHtml(rendered), "text/html", "UTF-8", null);
            showMessage("");
        } catch (IOException e) {
            showMessage("The document could not be displayed.");
        }
    }

    private FileInfo readFileInfo(Uri uri) {
        String displayName = "";
        long size = -1;

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    displayName = cursor.getString(nameIndex);
                }
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                    size = cursor.getLong(sizeIndex);
                }
            } finally {
                cursor.close();
            }
        }

        if (displayName == null || displayName.length() == 0) {
            String lastSegment = uri.getLastPathSegment();
            displayName = lastSegment == null ? "" : lastSegment;
        }

        return new FileInfo(displayName, size);
    }

    private String readText(Uri uri, long maxBytes) throws IOException {
        InputStream input = getContentResolver().openInputStream(uri);
        if (input == null) {
            throw new IOException("document input stream unavailable");
        }
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            long total = 0;
            int read;
            while ((read = input.read(buffer)) != -1) {
                total += read;
                if (total > maxBytes) {
                    throw new IOException("document too large");
                }
                output.write(buffer, 0, read);
            }
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        } finally {
            input.close();
        }
    }

    private void showMessage(String message) {
        messageView.setText(message);
        messageView.setVisibility(message.length() == 0 ? View.GONE : View.VISIBLE);
    }

    private static void configureWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setDomStorageEnabled(false);
        settings.setDatabaseEnabled(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
    }

    private static SafeHtml initialDocument() {
        String markdown = "# MdLite Reader\n\n"
                + "Lightweight Markdown viewing starts here.\n\n"
                + "No ads. No tracking. No network permission.\n\n"
                + "Use `<script>` as text, not as HTML.";
        return new JavaSimpleMarkdownRenderer().render(markdown);
    }

    private static String pageHtml(SafeHtml body) {
        return "<!doctype html>"
                + "<html><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                + "<style>"
                + "body{font-family:sans-serif;margin:24px;line-height:1.55;color:#172121;background:#f8fbfa;}"
                + "h1{font-size:24px;margin:0 0 12px;}"
                + "p{font-size:16px;margin:0 0 12px;}"
                + "code{background:#e6eeee;padding:2px 4px;border-radius:4px;}"
                + "</style></head><body>"
                + body.value()
                + "</body></html>";
    }

    private static final class FileInfo {
        private final String displayName;
        private final long sizeBytes;

        private FileInfo(String displayName, long sizeBytes) {
            this.displayName = displayName;
            this.sizeBytes = sizeBytes;
        }
    }
}
