package io.github.yosk.mdlite.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.github.yosk.mdlite.domain.FileSizePolicy;
import io.github.yosk.mdlite.domain.FileTypeDetector;
import io.github.yosk.mdlite.domain.FontSize;
import io.github.yosk.mdlite.domain.OpenDocumentTab;
import io.github.yosk.mdlite.domain.OpenDocumentTabs;
import io.github.yosk.mdlite.domain.RecentDocument;
import io.github.yosk.mdlite.domain.RecentDocuments;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.domain.ViewerTheme;
import io.github.yosk.mdlite.infrastructure.HtmlPageBuilder;
import io.github.yosk.mdlite.infrastructure.JavaSimpleMarkdownRenderer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class MainActivity extends Activity implements View.OnClickListener, DialogInterface.OnClickListener {
    private static final int REQUEST_OPEN_DOCUMENT = 1001;
    private static final long MAX_FILE_SIZE_BYTES = 2L * 1024L * 1024L;
    private static final int MAX_RECENT_DOCUMENTS = 5;
    private static final String RECENT_PREFS = "recent_documents";
    private static final String RECENT_ITEMS = "items";

    private final JavaSimpleMarkdownRenderer renderer = new JavaSimpleMarkdownRenderer();
    private final FileSizePolicy fileSizePolicy = new FileSizePolicy(MAX_FILE_SIZE_BYTES);

    private WebView webView;
    private TextView messageView;
    private Button openButton;
    private Button recentButton;
    private Button themeButton;
    private Button smallerTextButton;
    private Button largerTextButton;
    private LinearLayout tabRow;
    private OpenDocumentTabs openTabs;
    private ViewerTheme currentTheme = ViewerTheme.light();
    private FontSize currentFontSize = FontSize.defaultSize();
    private RecentDocuments displayedRecentDocuments = RecentDocuments.empty(MAX_RECENT_DOCUMENTS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        openButton = new Button(this);
        openButton.setText("Open Markdown file");
        openButton.setAllCaps(false);
        openButton.setOnClickListener(this);

        recentButton = new Button(this);
        recentButton.setText("Recent files");
        recentButton.setAllCaps(false);
        recentButton.setOnClickListener(this);

        themeButton = new Button(this);
        themeButton.setText("Dark theme");
        themeButton.setAllCaps(false);
        themeButton.setOnClickListener(this);

        LinearLayout textSizeRow = new LinearLayout(this);
        textSizeRow.setOrientation(LinearLayout.HORIZONTAL);

        smallerTextButton = new Button(this);
        smallerTextButton.setText("A");
        smallerTextButton.setTextSize(14);
        smallerTextButton.setContentDescription("Decrease font size");
        smallerTextButton.setAllCaps(false);
        smallerTextButton.setOnClickListener(this);

        largerTextButton = new Button(this);
        largerTextButton.setText("A");
        largerTextButton.setTextSize(22);
        largerTextButton.setContentDescription("Increase font size");
        largerTextButton.setAllCaps(false);
        largerTextButton.setOnClickListener(this);

        textSizeRow.addView(smallerTextButton, new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        textSizeRow.addView(largerTextButton, new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));

        messageView = new TextView(this);
        messageView.setGravity(Gravity.CENTER_VERTICAL);
        messageView.setPadding(24, 12, 24, 12);

        tabRow = new LinearLayout(this);
        tabRow.setOrientation(LinearLayout.HORIZONTAL);

        HorizontalScrollView tabScroller = new HorizontalScrollView(this);
        tabScroller.setHorizontalScrollBarEnabled(true);
        tabScroller.addView(tabRow, new HorizontalScrollView.LayoutParams(
                HorizontalScrollView.LayoutParams.WRAP_CONTENT,
                HorizontalScrollView.LayoutParams.WRAP_CONTENT));

        webView = new WebView(this);
        configureWebView(webView);
        openTabs = OpenDocumentTabs.withInitialTab(initialTab());
        renderTabs();
        renderCurrentDocument();

        root.addView(openButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(recentButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(themeButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(textSizeRow, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(messageView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(tabScroller, new LinearLayout.LayoutParams(
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
        if (view == openButton) {
            openMarkdownPicker();
        } else if (view == recentButton) {
            showRecentDocuments();
        } else if (view == themeButton) {
            currentTheme = currentTheme.toggled();
            themeButton.setText(currentTheme.isDark() ? "Light theme" : "Dark theme");
            renderCurrentDocument();
        } else if (view == smallerTextButton) {
            currentFontSize = currentFontSize.decreased();
            renderCurrentDocument();
        } else if (view == largerTextButton) {
            currentFontSize = currentFontSize.increased();
            renderCurrentDocument();
        } else if (view instanceof TabButton) {
            openTabs = openTabs.activate(((TabButton) view).tabIndex());
            renderTabs();
            renderCurrentDocument();
        } else if (view instanceof CloseTabText) {
            openTabs = openTabs.close(((CloseTabText) view).tabIndex());
            renderTabs();
            renderCurrentDocument();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which < 0 || which >= displayedRecentDocuments.items().size()) {
            return;
        }
        RecentDocument selected = displayedRecentDocuments.items().get(which);
        openUri(Uri.parse(selected.uri()), true);
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
                persistReadPermission(data, uri);
                openUri(uri, true);
            }
        }
    }

    private void openMarkdownPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
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
            persistReadPermission(intent, uri);
            openUri(uri, true);
        }
    }

    private void openUri(Uri uri, boolean remember) {
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
            openTabs = openTabs.open(OpenDocumentTab.of(fileInfo.displayName, uri.toString(), rendered));
            renderTabs();
            renderCurrentDocument();
            if (remember) {
                recordRecentDocument(fileInfo.displayName, uri);
            }
            showMessage("");
        } catch (IOException e) {
            showMessage("The document could not be displayed.");
        }
    }

    private void showRecentDocuments() {
        displayedRecentDocuments = loadRecentDocuments();
        if (displayedRecentDocuments.items().isEmpty()) {
            showMessage("No recent files yet.");
            return;
        }

        String[] labels = new String[displayedRecentDocuments.items().size()];
        for (int i = 0; i < displayedRecentDocuments.items().size(); i++) {
            labels[i] = displayedRecentDocuments.items().get(i).displayName();
        }

        new AlertDialog.Builder(this)
                .setTitle("Recent files")
                .setItems(labels, this)
                .show();
    }

    private void persistReadPermission(Intent data, Uri uri) {
        int flags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
        if (flags == 0) {
            return;
        }
        try {
            getContentResolver().takePersistableUriPermission(uri, flags);
        } catch (SecurityException e) {
            // Some providers grant temporary access only. The file can still be opened now.
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

    private void recordRecentDocument(String displayName, Uri uri) {
        RecentDocuments documents = loadRecentDocuments()
                .recordOpened(RecentDocument.of(displayName, uri.toString()));
        saveRecentDocuments(documents);
    }

    private RecentDocuments loadRecentDocuments() {
        SharedPreferences prefs = getSharedPreferences(RECENT_PREFS, MODE_PRIVATE);
        String raw = prefs.getString(RECENT_ITEMS, "");
        ArrayList<RecentDocument> items = new ArrayList<RecentDocument>();
        if (raw != null && raw.length() > 0) {
            String[] lines = raw.split("\\n", -1);
            for (int i = 0; i < lines.length; i++) {
                RecentDocument document = decodeRecentDocument(lines[i]);
                if (document != null) {
                    items.add(document);
                }
            }
        }
        return RecentDocuments.from(MAX_RECENT_DOCUMENTS, items);
    }

    private void saveRecentDocuments(RecentDocuments documents) {
        StringBuilder raw = new StringBuilder();
        List<RecentDocument> items = documents.items();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                raw.append('\n');
            }
            raw.append(encode(items.get(i).displayName()))
                    .append('\t')
                    .append(encode(items.get(i).uri()));
        }
        getSharedPreferences(RECENT_PREFS, MODE_PRIVATE)
                .edit()
                .putString(RECENT_ITEMS, raw.toString())
                .apply();
    }

    private static RecentDocument decodeRecentDocument(String line) {
        if (line == null || line.length() == 0) {
            return null;
        }
        int separator = line.indexOf('\t');
        if (separator < 0) {
            return null;
        }
        try {
            return RecentDocument.of(
                    decode(line.substring(0, separator)),
                    decode(line.substring(separator + 1)));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String encode(String value) {
        return Base64.encodeToString(value.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP | Base64.URL_SAFE);
    }

    private static String decode(String value) {
        return new String(Base64.decode(value, Base64.NO_WRAP | Base64.URL_SAFE), StandardCharsets.UTF_8);
    }

    private static void configureWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setDomStorageEnabled(false);
        settings.setDatabaseEnabled(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        webView.setWebViewClient(new ExternalHttpLinkClient());
    }

    private static OpenDocumentTab initialTab() {
        String markdown = "# MdLite Reader\n\n"
                + "Lightweight Markdown viewing starts here.\n\n"
                + "No ads. No tracking. No network permission.\n\n"
                + "Use `<script>` as text, not as HTML.";
        return OpenDocumentTab.of(
                "Welcome",
                "app://welcome",
                new JavaSimpleMarkdownRenderer().render(markdown));
    }

    private void renderCurrentDocument() {
        webView.loadDataWithBaseURL(null, HtmlPageBuilder.buildPage(openTabs.activeTab().document(), currentTheme, currentFontSize), "text/html", "UTF-8", null);
    }

    private void renderTabs() {
        tabRow.removeAllViews();
        for (int i = 0; i < openTabs.tabs().size(); i++) {
            OpenDocumentTab tab = openTabs.tabs().get(i);
            LinearLayout tabGroup = new LinearLayout(this);
            tabGroup.setOrientation(LinearLayout.HORIZONTAL);

            TabButton button = new TabButton(this, i);
            button.setText(tab.title());
            button.setAllCaps(false);
            button.setOnClickListener(this);
            button.setEnabled(i != openTabs.activeIndex());
            tabGroup.addView(button, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            CloseTabText closeText = new CloseTabText(this, i);
            closeText.setText("×");
            closeText.setTextSize(20);
            closeText.setGravity(Gravity.CENTER);
            closeText.setPadding(12, 0, 16, 0);
            closeText.setContentDescription("Close " + tab.title());
            closeText.setOnClickListener(this);
            tabGroup.addView(closeText, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));

            tabRow.addView(tabGroup, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private static final class FileInfo {
        private final String displayName;
        private final long sizeBytes;

        private FileInfo(String displayName, long sizeBytes) {
            this.displayName = displayName;
            this.sizeBytes = sizeBytes;
        }
    }

    private static final class ExternalHttpLinkClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!isExternalHttpUrl(url)) {
                return true;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        }

        private static boolean isExternalHttpUrl(String url) {
            if (url == null) {
                return false;
            }
            String lower = url.toLowerCase();
            return lower.startsWith("https://") || lower.startsWith("http://");
        }
    }

    private static final class TabButton extends Button {
        private final int tabIndex;

        private TabButton(Activity activity, int tabIndex) {
            super(activity);
            this.tabIndex = tabIndex;
        }

        private int tabIndex() {
            return tabIndex;
        }
    }

    private static final class CloseTabText extends TextView {
        private final int tabIndex;

        private CloseTabText(Activity activity, int tabIndex) {
            super(activity);
            this.tabIndex = tabIndex;
        }

        private int tabIndex() {
            return tabIndex;
        }
    }

}
