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
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.github.yosk.mdlite.domain.ControlsPlacement;
import io.github.yosk.mdlite.domain.FileSizePolicy;
import io.github.yosk.mdlite.domain.FileTypeDetector;
import io.github.yosk.mdlite.domain.FontSize;
import io.github.yosk.mdlite.domain.OpenDocumentTab;
import io.github.yosk.mdlite.domain.OpenDocumentTabs;
import io.github.yosk.mdlite.domain.RecentDocument;
import io.github.yosk.mdlite.domain.RecentDocuments;
import io.github.yosk.mdlite.domain.RestorableOpenTab;
import io.github.yosk.mdlite.domain.RestorableOpenTabs;
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
    private static final String OPEN_TABS_PREFS = "open_tabs";
    private static final String OPEN_TABS_ITEMS = "items";
    private static final String OPEN_TABS_ACTIVE_INDEX = "active_index";
    private static final String SETTINGS_PREFS = "viewer_settings";
    private static final String CONTROLS_PLACEMENT = "controls_placement";
    private static final String WELCOME_URI = "app://welcome";
    private static final int MENU_WIDTH_DP = 280;
    private static final int EDGE_SWIPE_DP = 24;
    private static final int MENU_SWIPE_MIN_DISTANCE_DP = 72;

    private final JavaSimpleMarkdownRenderer renderer = new JavaSimpleMarkdownRenderer();
    private final FileSizePolicy fileSizePolicy = new FileSizePolicy(MAX_FILE_SIZE_BYTES);

    private WebView webView;
    private TextView messageView;
    private Button menuButton;
    private Button openButton;
    private Button recentButton;
    private Button themeButton;
    private Button controlsPlacementButton;
    private SwipeMenuLayout menuPanel;
    private LinearLayout root;
    private LinearLayout controlsBar;
    private LinearLayout tabRow;
    private HorizontalScrollView tabScroller;
    private OpenDocumentTabs openTabs;
    private ControlsPlacement controlsPlacement;
    private ViewerTheme currentTheme = ViewerTheme.light();
    private FontSize currentFontSize = FontSize.defaultSize();
    private RecentDocuments displayedRecentDocuments = RecentDocuments.empty(MAX_RECENT_DOCUMENTS);
    private ScaleGestureDetector fontScaleGestureDetector;
    private float accumulatedPinchScale = 1f;
    private boolean trackingEdgeSwipe;
    private float edgeSwipeStartX;
    private float menuSwipeStartX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeSwipeFrameLayout appRoot = new EdgeSwipeFrameLayout(this);

        controlsPlacement = loadControlsPlacement();

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);

        menuButton = new Button(this);
        menuButton.setText("☰");
        menuButton.setContentDescription("Open menu");
        menuButton.setAllCaps(false);
        menuButton.setOnClickListener(this);
        topBar.addView(menuButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

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

        controlsPlacementButton = new Button(this);
        controlsPlacementButton.setAllCaps(false);
        controlsPlacementButton.setOnClickListener(this);
        updateControlsPlacementButton();

        menuPanel = new SwipeMenuLayout(this);
        menuPanel.setOrientation(LinearLayout.VERTICAL);
        menuPanel.setVisibility(View.GONE);
        menuPanel.setBackgroundColor((int) 0xfff8fbfa);
        menuPanel.setPadding(16, 24, 16, 16);
        menuPanel.setClickable(true);

        TextView menuTitle = new TextView(this);
        menuTitle.setText("Menu");
        menuTitle.setTextSize(18);
        menuTitle.setGravity(Gravity.CENTER_VERTICAL);
        menuTitle.setPadding(8, 0, 8, 16);
        menuPanel.addView(menuTitle, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuPanel.addView(openButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuPanel.addView(recentButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuPanel.addView(themeButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuPanel.addView(controlsPlacementButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        messageView = new TextView(this);
        messageView.setGravity(Gravity.CENTER_VERTICAL);
        messageView.setPadding(24, 12, 24, 12);

        tabRow = new LinearLayout(this);
        tabRow.setOrientation(LinearLayout.HORIZONTAL);

        tabScroller = new HorizontalScrollView(this);
        tabScroller.setHorizontalScrollBarEnabled(true);
        tabScroller.addView(tabRow, new HorizontalScrollView.LayoutParams(
                HorizontalScrollView.LayoutParams.WRAP_CONTENT,
                HorizontalScrollView.LayoutParams.WRAP_CONTENT));

        controlsBar = new LinearLayout(this);
        controlsBar.setOrientation(LinearLayout.VERTICAL);
        controlsBar.addView(topBar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        controlsBar.addView(tabScroller, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        webView = new WebView(this);
        configureWebView(webView);
        fontScaleGestureDetector = new ScaleGestureDetector(this, new FontScaleGestureListener(this));
        webView.setOnTouchListener(new FontScaleTouchListener(this));
        openTabs = restoreOpenTabsOrInitial();
        renderTabs();
        renderCurrentDocument();

        applyControlsPlacement();

        appRoot.addView(root, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        FrameLayout.LayoutParams menuParams = new FrameLayout.LayoutParams(
                dp(MENU_WIDTH_DP),
                FrameLayout.LayoutParams.MATCH_PARENT);
        menuParams.gravity = Gravity.START;
        appRoot.addView(menuPanel, menuParams);

        setContentView(appRoot);
        handleIncomingIntent(getIntent());
    }

    @Override
    public void onClick(View view) {
        if (view == menuButton) {
            toggleMenu();
        } else if (view == openButton) {
            closeMenu();
            openMarkdownPicker();
        } else if (view == recentButton) {
            closeMenu();
            showRecentDocuments();
        } else if (view == themeButton) {
            currentTheme = currentTheme.toggled();
            themeButton.setText(currentTheme.isDark() ? "Light theme" : "Dark theme");
            closeMenu();
            renderCurrentDocument();
        } else if (view == controlsPlacementButton) {
            controlsPlacement = controlsPlacement.toggled();
            saveControlsPlacement(controlsPlacement);
            updateControlsPlacementButton();
            applyControlsPlacement();
            closeMenu();
        } else if (view instanceof TabButton) {
            openTabs = openTabs.activate(((TabButton) view).tabIndex());
            renderTabs();
            renderCurrentDocument();
            saveOpenTabs();
        } else if (view instanceof CloseTabText) {
            openTabs = openTabs.close(((CloseTabText) view).tabIndex());
            renderTabs();
            renderCurrentDocument();
            saveOpenTabs();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            clearRecentDocuments();
            showMessage("Recent files cleared.");
            return;
        }
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
            saveOpenTabs();
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
            new AlertDialog.Builder(this)
                    .setTitle("Recent files")
                    .setMessage("No recent files yet.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        String[] labels = new String[displayedRecentDocuments.items().size()];
        for (int i = 0; i < displayedRecentDocuments.items().size(); i++) {
            labels[i] = displayedRecentDocuments.items().get(i).displayName();
        }

        new AlertDialog.Builder(this)
                .setTitle("Recent files")
                .setItems(labels, this)
                .setNegativeButton("Clear history", this)
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

    private ControlsPlacement loadControlsPlacement() {
        SharedPreferences prefs = getSharedPreferences(SETTINGS_PREFS, MODE_PRIVATE);
        return ControlsPlacement.fromStoredValue(prefs.getString(CONTROLS_PLACEMENT, ControlsPlacement.TOP_VALUE));
    }

    private void saveControlsPlacement(ControlsPlacement placement) {
        getSharedPreferences(SETTINGS_PREFS, MODE_PRIVATE)
                .edit()
                .putString(CONTROLS_PLACEMENT, placement.storedValue())
                .apply();
    }

    private void updateControlsPlacementButton() {
        if (controlsPlacement.isBottom()) {
            controlsPlacementButton.setText("Move controls to top");
        } else {
            controlsPlacementButton.setText("Move controls to bottom");
        }
    }

    private void applyControlsPlacement() {
        root.removeAllViews();
        if (controlsPlacement.isBottom()) {
            root.addView(messageView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            root.addView(webView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1));
            root.addView(controlsBar, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        } else {
            root.addView(controlsBar, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            root.addView(messageView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            root.addView(webView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1));
        }
    }

    private void toggleMenu() {
        if (isMenuOpen()) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    private void openMenu() {
        menuPanel.setVisibility(View.VISIBLE);
        menuButton.setContentDescription("Close menu");
    }

    private void closeMenu() {
        menuPanel.setVisibility(View.GONE);
        menuButton.setContentDescription("Open menu");
    }

    private boolean isMenuOpen() {
        return menuPanel.getVisibility() == View.VISIBLE;
    }

    private boolean handleEdgeSwipe(MotionEvent event) {
        if (isMenuOpen()) {
            if (event.getX() > dp(MENU_WIDTH_DP)) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    closeMenu();
                }
                return true;
            }
            trackingEdgeSwipe = false;
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            trackingEdgeSwipe = event.getX() <= dp(EDGE_SWIPE_DP);
            edgeSwipeStartX = event.getX();
            return trackingEdgeSwipe;
        }
        if (!trackingEdgeSwipe) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float distance = event.getX() - edgeSwipeStartX;
            trackingEdgeSwipe = false;
            if (distance >= dp(MENU_SWIPE_MIN_DISTANCE_DP)) {
                openMenu();
                return true;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            trackingEdgeSwipe = false;
        }
        return true;
    }

    private boolean handleMenuSwipe(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            menuSwipeStartX = event.getX();
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float distance = event.getX() - menuSwipeStartX;
            if (distance <= -dp(MENU_SWIPE_MIN_DISTANCE_DP)) {
                closeMenu();
                return true;
            }
        }
        return false;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private OpenDocumentTabs restoreOpenTabsOrInitial() {
        RestorableOpenTabs storedTabs = loadRestorableOpenTabs();
        if (storedTabs.isEmpty()) {
            return OpenDocumentTabs.withInitialTab(initialTab());
        }

        ArrayList<OpenDocumentTab> restoredTabs = new ArrayList<OpenDocumentTab>();
        int restoredActiveIndex = -1;
        List<RestorableOpenTab> items = storedTabs.tabs();
        for (int i = 0; i < items.size(); i++) {
            OpenDocumentTab tab = restoreOpenTab(items.get(i));
            if (tab != null) {
                if (i == storedTabs.activeIndex()) {
                    restoredActiveIndex = restoredTabs.size();
                }
                restoredTabs.add(tab);
            }
        }

        if (restoredTabs.isEmpty()) {
            return OpenDocumentTabs.withInitialTab(initialTab());
        }
        if (restoredActiveIndex < 0) {
            restoredActiveIndex = storedTabs.activeIndex();
            if (restoredActiveIndex >= restoredTabs.size()) {
                restoredActiveIndex = restoredTabs.size() - 1;
            }
        }
        return openTabsFrom(restoredTabs, restoredActiveIndex);
    }

    private OpenDocumentTab restoreOpenTab(RestorableOpenTab storedTab) {
        try {
            Uri uri = Uri.parse(storedTab.uri());
            FileInfo fileInfo = readFileInfo(uri);
            String displayName = fileInfo.displayName.length() == 0 ? storedTab.title() : fileInfo.displayName;
            if (!FileTypeDetector.isMarkdownDisplayName(displayName)) {
                return null;
            }
            if (!fileSizePolicy.isReadableSize(fileInfo.sizeBytes)) {
                return null;
            }

            String markdown = readText(uri, MAX_FILE_SIZE_BYTES);
            SafeHtml rendered = renderer.render(markdown);
            return OpenDocumentTab.of(displayName, uri.toString(), rendered);
        } catch (IllegalArgumentException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (SecurityException e) {
            return null;
        }
    }

    private static OpenDocumentTabs openTabsFrom(List<OpenDocumentTab> restoredTabs, int activeIndex) {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(restoredTabs.get(0));
        for (int i = 1; i < restoredTabs.size(); i++) {
            tabs = tabs.open(restoredTabs.get(i));
        }
        return tabs.activate(activeIndex);
    }

    private RestorableOpenTabs loadRestorableOpenTabs() {
        SharedPreferences prefs = getSharedPreferences(OPEN_TABS_PREFS, MODE_PRIVATE);
        String raw = prefs.getString(OPEN_TABS_ITEMS, "");
        ArrayList<RestorableOpenTab> items = new ArrayList<RestorableOpenTab>();
        if (raw != null && raw.length() > 0) {
            String[] lines = raw.split("\\n", -1);
            for (int i = 0; i < lines.length; i++) {
                RestorableOpenTab tab = decodeRestorableOpenTab(lines[i]);
                if (tab != null) {
                    items.add(tab);
                }
            }
        }
        return RestorableOpenTabs.from(items, prefs.getInt(OPEN_TABS_ACTIVE_INDEX, 0));
    }

    private void saveOpenTabs() {
        StringBuilder raw = new StringBuilder();
        int savedCount = 0;
        int savedActiveIndex = 0;
        for (int i = 0; i < openTabs.tabs().size(); i++) {
            OpenDocumentTab tab = openTabs.tabs().get(i);
            if (WELCOME_URI.equals(tab.uri())) {
                continue;
            }
            if (i == openTabs.activeIndex()) {
                savedActiveIndex = savedCount;
            }
            if (savedCount > 0) {
                raw.append('\n');
            }
            raw.append(encode(tab.title()))
                    .append('\t')
                    .append(encode(tab.uri()));
            savedCount++;
        }

        SharedPreferences.Editor editor = getSharedPreferences(OPEN_TABS_PREFS, MODE_PRIVATE).edit();
        if (savedCount == 0) {
            editor.clear().apply();
            return;
        }
        editor.putString(OPEN_TABS_ITEMS, raw.toString())
                .putInt(OPEN_TABS_ACTIVE_INDEX, savedActiveIndex)
                .apply();
    }

    private static RestorableOpenTab decodeRestorableOpenTab(String line) {
        if (line == null || line.length() == 0) {
            return null;
        }
        int separator = line.indexOf('\t');
        if (separator < 0) {
            return null;
        }
        try {
            return RestorableOpenTab.of(
                    decode(line.substring(0, separator)),
                    decode(line.substring(separator + 1)));
        } catch (IllegalArgumentException e) {
            return null;
        }
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

    private void clearRecentDocuments() {
        displayedRecentDocuments = displayedRecentDocuments.clear();
        getSharedPreferences(RECENT_PREFS, MODE_PRIVATE)
                .edit()
                .clear()
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
                WELCOME_URI,
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

    private boolean handleFontScaleTouch(MotionEvent event) {
        fontScaleGestureDetector.onTouchEvent(event);
        return event.getPointerCount() > 1 || fontScaleGestureDetector.isInProgress();
    }

    private void changeFontSizeByPinch(float scaleFactor) {
        accumulatedPinchScale *= scaleFactor;
        FontSize changed = currentFontSize.changedByPinchScale(accumulatedPinchScale);
        if (changed.sp() == currentFontSize.sp()) {
            return;
        }
        currentFontSize = changed;
        accumulatedPinchScale = 1f;
        renderCurrentDocument();
    }

    private void resetAccumulatedPinchScale() {
        accumulatedPinchScale = 1f;
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

    private static final class EdgeSwipeFrameLayout extends FrameLayout {
        private final MainActivity activity;

        private EdgeSwipeFrameLayout(MainActivity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            if (activity.handleEdgeSwipe(event)) {
                return true;
            }
            return super.dispatchTouchEvent(event);
        }
    }

    private static final class SwipeMenuLayout extends LinearLayout {
        private final MainActivity activity;

        private SwipeMenuLayout(MainActivity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            if (activity.handleMenuSwipe(event)) {
                return true;
            }
            return super.dispatchTouchEvent(event);
        }
    }

    private static final class FontScaleTouchListener implements View.OnTouchListener {
        private final MainActivity activity;

        private FontScaleTouchListener(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            return activity.handleFontScaleTouch(event);
        }
    }

    private static final class FontScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private final MainActivity activity;

        private FontScaleGestureListener(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            activity.resetAccumulatedPinchScale();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            activity.changeFontSizeByPinch(detector.getScaleFactor());
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            activity.resetAccumulatedPinchScale();
        }
    }

}
