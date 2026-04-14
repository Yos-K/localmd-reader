package io.github.yosk.mdlite.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
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
import io.github.yosk.mdlite.domain.FontSize;
import io.github.yosk.mdlite.domain.MarkdownFileOpenResult;
import io.github.yosk.mdlite.domain.OpenDocumentTab;
import io.github.yosk.mdlite.domain.OpenDocumentTabs;
import io.github.yosk.mdlite.domain.RecentDocument;
import io.github.yosk.mdlite.domain.RecentDocuments;
import io.github.yosk.mdlite.domain.RestorableOpenTab;
import io.github.yosk.mdlite.domain.RestorableOpenTabs;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.domain.ViewerLanguage;
import io.github.yosk.mdlite.domain.ViewerTheme;
import io.github.yosk.mdlite.infrastructure.HtmlPageBuilder;
import io.github.yosk.mdlite.infrastructure.JavaSimpleMarkdownRenderer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class MainActivity extends Activity implements View.OnClickListener, DialogInterface.OnClickListener {
    private static final int REQUEST_OPEN_DOCUMENT = 1001;
    private static final String ACTION_OPEN_TEXT = "io.github.yosk.mdlite.action.OPEN_TEXT";
    private static final String ACTION_OPEN_TEXTS = "io.github.yosk.mdlite.action.OPEN_TEXTS";
    private static final String EXTRA_MARKDOWN_TITLE = "io.github.yosk.mdlite.extra.MARKDOWN_TITLE";
    private static final String EXTRA_MARKDOWN_TITLES = "io.github.yosk.mdlite.extra.MARKDOWN_TITLES";
    private static final String EXTRA_MARKDOWN_SOURCE = "io.github.yosk.mdlite.extra.MARKDOWN_SOURCE";
    private static final String EXTRA_MARKDOWN_SOURCES = "io.github.yosk.mdlite.extra.MARKDOWN_SOURCES";
    private static final String EXTRA_MARKDOWN_TEXT = "io.github.yosk.mdlite.extra.MARKDOWN_TEXT";
    private static final String EXTRA_MARKDOWN_TEXTS_BASE64 = "io.github.yosk.mdlite.extra.MARKDOWN_TEXTS_BASE64";
    private static final long MAX_FILE_SIZE_BYTES = 2L * 1024L * 1024L;
    private static final int MAX_RECENT_DOCUMENTS = 5;
    private static final String RECENT_PREFS = "recent_documents";
    private static final String RECENT_ITEMS = "items";
    private static final String OPEN_TABS_PREFS = "open_tabs";
    private static final String OPEN_TABS_ITEMS = "items";
    private static final String OPEN_TABS_ACTIVE_INDEX = "active_index";
    private static final String SETTINGS_PREFS = "viewer_settings";
    private static final String CONTROLS_PLACEMENT = "controls_placement";
    private static final String VIEWER_LANGUAGE = "viewer_language";
    private static final String WELCOME_URI = "app://welcome";
    private static final int MENU_WIDTH_DP = 280;
    private static final int EDGE_SWIPE_DP = 24;
    private static final int MENU_SWIPE_MIN_DISTANCE_DP = 72;
    private static final int LIGHT_BACKGROUND = 0xfff8fbfa;
    private static final int LIGHT_SURFACE = 0xffffffff;
    private static final int LIGHT_SURFACE_ALT = 0xffeef5f3;
    private static final int LIGHT_TEXT = 0xff172121;
    private static final int LIGHT_MUTED = 0xff566664;
    private static final int LIGHT_PRIMARY = 0xff006d77;
    private static final int LIGHT_PRIMARY_DARK = 0xff0f3d3e;
    private static final int LIGHT_BORDER = 0xffc9d8d5;
    private static final int LIGHT_MESSAGE = 0xffe6eeee;
    private static final int DARK_BACKGROUND = 0xff101414;
    private static final int DARK_SURFACE = 0xff1b2423;
    private static final int DARK_SURFACE_ALT = 0xff25302f;
    private static final int DARK_TEXT = 0xffedf5f2;
    private static final int DARK_MUTED = 0xffa7bbb7;
    private static final int DARK_PRIMARY = 0xff2a9d8f;
    private static final int DARK_PRIMARY_DARK = 0xff7ccbe0;
    private static final int DARK_BORDER = 0xff3c4b49;
    private static final int DARK_MESSAGE = 0xff25302f;

    private final JavaSimpleMarkdownRenderer renderer = new JavaSimpleMarkdownRenderer();
    private final FileSizePolicy fileSizePolicy = new FileSizePolicy(MAX_FILE_SIZE_BYTES);

    private WebView webView;
    private TextView messageView;
    private Button menuButton;
    private Button openButton;
    private Button recentButton;
    private Button themeButton;
    private Button languageButton;
    private Button controlsPlacementButton;
    private Button privacyButton;
    private SwipeMenuLayout menuPanel;
    private LinearLayout root;
    private LinearLayout topBar;
    private LinearLayout controlsBar;
    private LinearLayout tabRow;
    private HorizontalScrollView tabScroller;
    private TextView appTitle;
    private TextView menuTitle;
    private TextView filesSection;
    private TextView readingSection;
    private TextView layoutSection;
    private TextView infoSection;
    private OpenDocumentTabs openTabs;
    private ControlsPlacement controlsPlacement;
    private ViewerLanguage currentLanguage = ViewerLanguage.english();
    private ViewerTheme currentTheme = ViewerTheme.light();
    private FontSize currentFontSize = FontSize.defaultSize();
    private FontSize renderedFontSize = FontSize.defaultSize();
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
        currentLanguage = loadViewerLanguage();

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(dp(12), dp(8), dp(12), dp(8));
        topBar.setBackgroundColor(backgroundColor());

        menuButton = new Button(this);
        menuButton.setText("☰ Menu");
        menuButton.setContentDescription("Open menu");
        menuButton.setAllCaps(false);
        menuButton.setOnClickListener(this);
        styleToolbarButton(menuButton);
        topBar.addView(menuButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        appTitle = new TextView(this);
        appTitle.setText("MdLite Reader");
        appTitle.setTextColor(textColor());
        appTitle.setTextSize(17);
        appTitle.setTypeface(Typeface.DEFAULT_BOLD);
        appTitle.setGravity(Gravity.CENTER_VERTICAL);
        appTitle.setSingleLine(true);
        appTitle.setEllipsize(TextUtils.TruncateAt.END);
        topBar.addView(appTitle, new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));

        openButton = new Button(this);
        openButton.setAllCaps(false);
        openButton.setOnClickListener(this);
        styleMenuButton(openButton);

        recentButton = new Button(this);
        recentButton.setAllCaps(false);
        recentButton.setOnClickListener(this);
        styleMenuButton(recentButton);

        themeButton = new Button(this);
        themeButton.setAllCaps(false);
        themeButton.setOnClickListener(this);
        styleMenuButton(themeButton);

        languageButton = new Button(this);
        languageButton.setAllCaps(false);
        languageButton.setOnClickListener(this);
        styleMenuButton(languageButton);

        controlsPlacementButton = new Button(this);
        controlsPlacementButton.setAllCaps(false);
        controlsPlacementButton.setOnClickListener(this);
        styleMenuButton(controlsPlacementButton);

        privacyButton = new Button(this);
        privacyButton.setAllCaps(false);
        privacyButton.setOnClickListener(this);
        styleMenuButton(privacyButton);

        menuPanel = new SwipeMenuLayout(this);
        menuPanel.setOrientation(LinearLayout.VERTICAL);
        menuPanel.setVisibility(View.GONE);
        menuPanel.setBackgroundColor(backgroundColor());
        menuPanel.setPadding(dp(18), dp(28), dp(18), dp(18));
        menuPanel.setClickable(true);

        menuTitle = new TextView(this);
        menuTitle.setTextColor(textColor());
        menuTitle.setTextSize(22);
        menuTitle.setTypeface(Typeface.DEFAULT_BOLD);
        menuTitle.setGravity(Gravity.CENTER_VERTICAL);
        menuTitle.setPadding(dp(8), 0, dp(8), dp(18));
        menuPanel.addView(menuTitle, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        filesSection = menuSection("");
        menuPanel.addView(filesSection, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuPanel.addView(openButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuPanel.addView(recentButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        readingSection = menuSection("");
        menuPanel.addView(readingSection, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuPanel.addView(themeButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuPanel.addView(languageButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layoutSection = menuSection("");
        menuPanel.addView(layoutSection, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuPanel.addView(controlsPlacementButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        infoSection = menuSection("");
        menuPanel.addView(infoSection, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuPanel.addView(privacyButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        messageView = new TextView(this);
        messageView.setGravity(Gravity.CENTER_VERTICAL);
        messageView.setTextColor(textColor());
        messageView.setBackgroundColor(messageColor());
        messageView.setPadding(dp(24), dp(12), dp(24), dp(12));

        tabRow = new LinearLayout(this);
        tabRow.setOrientation(LinearLayout.HORIZONTAL);
        tabRow.setPadding(dp(12), dp(8), dp(12), dp(8));

        tabScroller = new HorizontalScrollView(this);
        tabScroller.setHorizontalScrollBarEnabled(true);
        tabScroller.setBackgroundColor(backgroundColor());
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
        updateLocalizedText();
        openTabs = restoreOpenTabsOrInitial();
        applyNativeTheme();
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
            updateLocalizedText();
            applyNativeTheme();
            closeMenu();
            renderTabs();
            renderCurrentDocument();
        } else if (view == languageButton) {
            currentLanguage = currentLanguage.toggled();
            saveViewerLanguage(currentLanguage);
            updateLocalizedText();
            if (WELCOME_URI.equals(openTabs.activeTab().uri())) {
                openTabs = OpenDocumentTabs.withInitialTab(initialTab());
                renderTabs();
                renderCurrentDocument();
            }
            closeMenu();
        } else if (view == controlsPlacementButton) {
            controlsPlacement = controlsPlacement.toggled();
            saveControlsPlacement(controlsPlacement);
            updateLocalizedText();
            applyControlsPlacement();
            closeMenu();
        } else if (view == privacyButton) {
            closeMenu();
            showPrivacyPolicyDialog();
        } else if (view instanceof TabButton) {
            openTabs = openTabs.activate(((TabButton) view).tabIndex());
            renderTabs();
            renderCurrentDocument();
            saveOpenTabs();
        } else if (view instanceof CloseTabText) {
            openTabs = openTabs.closeOrFallback(((CloseTabText) view).tabIndex(), initialTab());
            renderTabs();
            renderCurrentDocument();
            saveOpenTabs();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            clearRecentDocuments();
            showInfoDialog(recentFilesTitle(), recentFilesClearedMessage());
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
            return;
        }
        if (Intent.ACTION_SEND.equals(action)) {
            Uri sharedUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (sharedUri != null) {
                persistReadPermission(intent, sharedUri);
                openUri(sharedUri, true);
            }
            return;
        }
        if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            ArrayList<Uri> sharedUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            if (sharedUris != null) {
                openUris(sharedUris, true, intent);
            }
            return;
        }
        if (ACTION_OPEN_TEXT.equals(action)) {
            openMarkdownText(
                    intent.getStringExtra(EXTRA_MARKDOWN_TITLE),
                    intent.getStringExtra(EXTRA_MARKDOWN_SOURCE),
                    intent.getStringExtra(EXTRA_MARKDOWN_TEXT));
            return;
        }
        if (ACTION_OPEN_TEXTS.equals(action)) {
            openMarkdownTexts(
                    intent.getStringArrayExtra(EXTRA_MARKDOWN_TITLES),
                    intent.getStringArrayExtra(EXTRA_MARKDOWN_SOURCES),
                    intent.getStringArrayExtra(EXTRA_MARKDOWN_TEXTS_BASE64));
        }
    }

    private void openUris(List<Uri> uris, boolean remember, Intent permissionIntent) {
        for (Uri uri : uris) {
            if (uri != null) {
                persistReadPermission(permissionIntent, uri);
                openUri(uri, remember);
            }
        }
    }

    private void openUri(Uri uri, boolean remember) {
        FileInfo fileInfo = readFileInfo(uri);
        MarkdownFileOpenResult openResult = MarkdownFileOpenResult.from(fileInfo.displayName, fileInfo.sizeBytes, fileSizePolicy);
        if (openResult instanceof MarkdownFileOpenResult.UnsupportedMarkdownFile) {
            showFileOpenError(unsupportedFileMessage());
            return;
        }
        if (openResult instanceof MarkdownFileOpenResult.OversizedMarkdownFile) {
            showFileOpenError(fileTooLargeMessage());
            return;
        }

        MarkdownFileOpenResult.ReadableMarkdownFile readableFile = (MarkdownFileOpenResult.ReadableMarkdownFile) openResult;
        try {
            String markdown = readText(uri, MAX_FILE_SIZE_BYTES);
            SafeHtml rendered = renderer.render(markdown);
            openTabs = openTabs.open(OpenDocumentTab.of(readableFile.displayName(), uri.toString(), rendered));
            renderTabs();
            renderCurrentDocument();
            saveOpenTabs();
            if (remember) {
                recordRecentDocument(readableFile.displayName(), uri);
            }
            showMessage("");
        } catch (IOException e) {
            showFileOpenError(unreadableFileMessage());
        }
    }

    private void openMarkdownText(String title, String source, String markdown) {
        String displayName = title == null || title.length() == 0 ? "Termux.md" : title;
        String sourceId = source == null || source.length() == 0 ? displayName : source;
        String text = markdown == null ? "" : markdown;
        long sizeBytes = text.getBytes(StandardCharsets.UTF_8).length;
        MarkdownFileOpenResult openResult = MarkdownFileOpenResult.from(displayName, sizeBytes, fileSizePolicy);
        if (openResult instanceof MarkdownFileOpenResult.UnsupportedMarkdownFile) {
            showFileOpenError(unsupportedFileMessage());
            return;
        }
        if (openResult instanceof MarkdownFileOpenResult.OversizedMarkdownFile) {
            showFileOpenError(fileTooLargeMessage());
            return;
        }

        MarkdownFileOpenResult.ReadableMarkdownFile readableFile = (MarkdownFileOpenResult.ReadableMarkdownFile) openResult;
        SafeHtml rendered = renderer.render(text);
        String uri = "termux://open/" + Uri.encode(sourceId);
        openTabs = openTabs.open(OpenDocumentTab.of(readableFile.displayName(), uri, rendered));
        renderTabs();
        renderCurrentDocument();
        saveOpenTabs();
        showMessage("");
    }

    private void openMarkdownTexts(String[] titles, String[] sources, String[] textsBase64) {
        if (titles == null || sources == null || textsBase64 == null) {
            return;
        }
        int count = Math.min(titles.length, Math.min(sources.length, textsBase64.length));
        for (int i = 0; i < count; i++) {
            openMarkdownText(titles[i], sources[i], decodeBase64Text(textsBase64[i]));
        }
    }

    private String decodeBase64Text(String encoded) {
        if (encoded == null) {
            return "";
        }
        byte[] bytes = Base64.decode(encoded, Base64.DEFAULT);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private void showRecentDocuments() {
        displayedRecentDocuments = loadRecentDocuments();
        if (displayedRecentDocuments.items().isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(recentFilesTitle())
                    .setMessage(noRecentFilesMessage())
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        String[] labels = new String[displayedRecentDocuments.items().size()];
        for (int i = 0; i < displayedRecentDocuments.items().size(); i++) {
            labels[i] = displayedRecentDocuments.items().get(i).displayName();
        }

        new AlertDialog.Builder(this)
                .setTitle(recentFilesTitle())
                .setItems(labels, this)
                .setNegativeButton(clearHistoryLabel(), this)
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
        if ("file".equals(uri.getScheme())) {
            File file = new File(uri.getPath() == null ? "" : uri.getPath());
            return new FileInfo(file.getName(), file.length());
        }

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
        InputStream input = openInputStream(uri);
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

    private InputStream openInputStream(Uri uri) throws IOException {
        if ("file".equals(uri.getScheme())) {
            return new FileInputStream(new File(uri.getPath() == null ? "" : uri.getPath()));
        }
        return getContentResolver().openInputStream(uri);
    }

    private void showMessage(String message) {
        messageView.setText(message);
        messageView.setVisibility(message.length() == 0 ? View.GONE : View.VISIBLE);
    }

    private void showFileOpenError(String message) {
        new AlertDialog.Builder(this)
                .setTitle(openMarkdownTitle())
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showInfoDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showPrivacyPolicyDialog() {
        showInfoDialog(privacyTitle(), privacyMessage());
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

    private ViewerLanguage loadViewerLanguage() {
        SharedPreferences prefs = getSharedPreferences(SETTINGS_PREFS, MODE_PRIVATE);
        return ViewerLanguage.fromStoredValue(prefs.getString(VIEWER_LANGUAGE, ViewerLanguage.ENGLISH_VALUE));
    }

    private void saveViewerLanguage(ViewerLanguage language) {
        getSharedPreferences(SETTINGS_PREFS, MODE_PRIVATE)
                .edit()
                .putString(VIEWER_LANGUAGE, language.storedValue())
                .apply();
    }

    private void updateLocalizedText() {
        menuButton.setText(currentLanguage.isJapanese() ? "☰ メニュー" : "☰ Menu");
        menuButton.setContentDescription(currentLanguage.isJapanese() ? "メニューを開く" : "Open menu");
        appTitle.setText("MdLite Reader");
        openButton.setText(currentLanguage.isJapanese() ? "ファイルを開く" : "Open file");
        recentButton.setText(recentFilesTitle());
        themeButton.setText(currentTheme.isDark() ? lightThemeLabel() : darkThemeLabel());
        languageButton.setText(currentLanguage.isJapanese() ? "Switch to English" : "日本語に切り替え");
        menuTitle.setText("MdLite Reader");
        filesSection.setText(currentLanguage.isJapanese() ? "ファイル" : "Files");
        readingSection.setText(currentLanguage.isJapanese() ? "表示" : "Reading");
        layoutSection.setText(currentLanguage.isJapanese() ? "レイアウト" : "Layout");
        infoSection.setText(currentLanguage.isJapanese() ? "情報" : "Info");
        privacyButton.setText(currentLanguage.isJapanese() ? "プライバシー" : "Privacy");
        if (controlsPlacement.isBottom()) {
            controlsPlacementButton.setText(currentLanguage.isJapanese() ? "操作バーを上に移動" : "Move controls to top");
        } else {
            controlsPlacementButton.setText(currentLanguage.isJapanese() ? "操作バーを下に移動" : "Move controls to bottom");
        }
    }

    private String recentFilesTitle() {
        return currentLanguage.isJapanese() ? "最近開いたファイル" : "Recent files";
    }

    private String openMarkdownTitle() {
        return currentLanguage.isJapanese() ? "Markdownファイルを開く" : "Open Markdown file";
    }

    private String noRecentFilesMessage() {
        return currentLanguage.isJapanese() ? "最近開いたファイルはまだありません。" : "No recent files yet.";
    }

    private String clearHistoryLabel() {
        return currentLanguage.isJapanese() ? "履歴をクリア" : "Clear history";
    }

    private String recentFilesClearedMessage() {
        return currentLanguage.isJapanese() ? "最近開いたファイルをクリアしました。" : "Recent files cleared.";
    }

    private String privacyTitle() {
        return currentLanguage.isJapanese() ? "プライバシー" : "Privacy";
    }

    private String privacyMessage() {
        if (currentLanguage.isJapanese()) {
            return "MdLite Reader は個人情報を収集しません。\n\n"
                    + "広告、解析SDK、ログイン、自動クラッシュ送信、ネットワーク権限はありません。\n\n"
                    + "選択したMarkdownは端末上で表示され、アプリによってアップロードされません。\n\n"
                    + "最近開いたファイルとタブ復元の情報は端末内のアプリ専用領域に保存され、履歴クリアまたはアプリデータ削除で消去できます。";
        }
        return "MdLite Reader does not collect personal information.\n\n"
                + "There are no ads, analytics SDKs, login, automatic crash reporting, or network permission.\n\n"
                + "Selected Markdown files are rendered on your device and are not uploaded by the app.\n\n"
                + "Recent file and tab restoration metadata stays in app-private storage and can be removed by clearing history or app data.";
    }

    private String unsupportedFileMessage() {
        return currentLanguage.isJapanese()
                ? ".md と .markdown ファイルのみ対応しています。"
                : "Only .md and .markdown files are supported.";
    }

    private String fileTooLargeMessage() {
        return currentLanguage.isJapanese()
                ? "2 MB を超えるファイルは開けません。"
                : "Files larger than 2 MB cannot be opened.";
    }

    private String unreadableFileMessage() {
        return currentLanguage.isJapanese()
                ? "文書を読み取れませんでした。ファイルが移動または削除されたか、継続的な読み取り権限がない可能性があります。"
                : "The document could not be read. It may have been moved, deleted, or opened without lasting permission.";
    }

    private String darkThemeLabel() {
        return currentLanguage.isJapanese() ? "ダークテーマ" : "Dark theme";
    }

    private String lightThemeLabel() {
        return currentLanguage.isJapanese() ? "ライトテーマ" : "Light theme";
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

    private TextView menuSection(String label) {
        TextView section = new TextView(this);
        section.setText(label);
        section.setTextColor(primaryStrongColor());
        section.setTextSize(12);
        section.setTypeface(Typeface.DEFAULT_BOLD);
        section.setPadding(dp(4), dp(18), dp(4), dp(6));
        return section;
    }

    private void styleToolbarButton(TextView view) {
        view.setTextColor(primaryStrongColor());
        view.setTextSize(15);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setPadding(dp(14), dp(8), dp(14), dp(8));
        view.setBackground(makeRoundedBackground(surfaceAltColor(), borderColor(), 8));
    }

    private void styleMenuButton(TextView view) {
        view.setTextColor(textColor());
        view.setTextSize(16);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        view.setPadding(dp(16), dp(12), dp(16), dp(12));
        view.setBackground(makeRoundedBackground(surfaceColor(), borderColor(), 8));
    }

    private void applyNativeTheme() {
        root.setBackgroundColor(backgroundColor());
        topBar.setBackgroundColor(backgroundColor());
        tabScroller.setBackgroundColor(backgroundColor());
        menuPanel.setBackgroundColor(backgroundColor());
        appTitle.setTextColor(textColor());
        menuTitle.setTextColor(textColor());
        messageView.setTextColor(textColor());
        messageView.setBackgroundColor(messageColor());
        styleToolbarButton(menuButton);
        styleMenuButton(openButton);
        styleMenuButton(recentButton);
        styleMenuButton(themeButton);
        styleMenuButton(languageButton);
        styleMenuButton(controlsPlacementButton);
        styleMenuButton(privacyButton);
        applyMenuSectionTheme();
    }

    private void applyMenuSectionTheme() {
        int sectionColor = primaryStrongColor();
        filesSection.setTextColor(sectionColor);
        readingSection.setTextColor(sectionColor);
        layoutSection.setTextColor(sectionColor);
        infoSection.setTextColor(sectionColor);
    }

    private GradientDrawable makeRoundedBackground(int fillColor, int strokeColor, int radiusDp) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(fillColor);
        background.setCornerRadius(dp(radiusDp));
        background.setStroke(1, strokeColor);
        return background;
    }

    private int backgroundColor() {
        return currentTheme.isDark() ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }

    private int surfaceColor() {
        return currentTheme.isDark() ? DARK_SURFACE : LIGHT_SURFACE;
    }

    private int surfaceAltColor() {
        return currentTheme.isDark() ? DARK_SURFACE_ALT : LIGHT_SURFACE_ALT;
    }

    private int textColor() {
        return currentTheme.isDark() ? DARK_TEXT : LIGHT_TEXT;
    }

    private int mutedColor() {
        return currentTheme.isDark() ? DARK_MUTED : LIGHT_MUTED;
    }

    private int primaryColor() {
        return currentTheme.isDark() ? DARK_PRIMARY : LIGHT_PRIMARY;
    }

    private int primaryStrongColor() {
        return currentTheme.isDark() ? DARK_PRIMARY_DARK : LIGHT_PRIMARY_DARK;
    }

    private int borderColor() {
        return currentTheme.isDark() ? DARK_BORDER : LIGHT_BORDER;
    }

    private int messageColor() {
        return currentTheme.isDark() ? DARK_MESSAGE : LIGHT_MESSAGE;
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
            MarkdownFileOpenResult openResult = MarkdownFileOpenResult.from(displayName, fileInfo.sizeBytes, fileSizePolicy);
            if (!(openResult instanceof MarkdownFileOpenResult.ReadableMarkdownFile)) {
                return null;
            }

            MarkdownFileOpenResult.ReadableMarkdownFile readableFile = (MarkdownFileOpenResult.ReadableMarkdownFile) openResult;
            String markdown = readText(uri, MAX_FILE_SIZE_BYTES);
            SafeHtml rendered = renderer.render(markdown);
            return OpenDocumentTab.of(readableFile.displayName(), uri.toString(), rendered);
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

    private OpenDocumentTab initialTab() {
        return OpenDocumentTab.of(
                currentLanguage.isJapanese() ? "ホーム" : "Welcome",
                WELCOME_URI,
                SafeHtml.fromTrustedRendererOutput(welcomeHtml()));
    }

    private String welcomeHtml() {
        if (currentLanguage.isJapanese()) {
            return "<section class=\"welcome\">"
                    + "<p class=\"welcome-kicker\">ローカルMarkdownビューア</p>"
                    + "<h1>MdLite Reader</h1>"
                    + "<p class=\"welcome-lead\">広告、トラッキング、ログイン、ネットワークアクセスなしでMarkdownファイルを読みます。</p>"
                    + "<div class=\"welcome-grid\">"
                    + "<div class=\"welcome-card\"><strong>開く</strong><span>メニューから .md または .markdown ファイルを選びます。</span></div>"
                    + "<div class=\"welcome-card\"><strong>戻る</strong><span>最近開いたファイルには、この端末で開いた直近5件が残ります。</span></div>"
                    + "<div class=\"welcome-card\"><strong>読む</strong><span>ピンチで文字サイズを変えられます。複数ファイルはタブで開きます。</span></div>"
                    + "</div>"
                    + "<p class=\"welcome-note\">生HTMLはテキストとして表示します。HTTP / HTTPSリンクはアプリ外で開きます。</p>"
                    + "</section>";
        }
        return "<section class=\"welcome\">"
                + "<p class=\"welcome-kicker\">Local Markdown reader</p>"
                + "<h1>MdLite Reader</h1>"
                + "<p class=\"welcome-lead\">Open a Markdown file and read it without ads, tracking, login, or network access.</p>"
                + "<div class=\"welcome-grid\">"
                + "<div class=\"welcome-card\"><strong>Open</strong><span>Use Menu to choose a .md or .markdown file.</span></div>"
                + "<div class=\"welcome-card\"><strong>Return</strong><span>Recent files keeps the last 5 documents on this device.</span></div>"
                + "<div class=\"welcome-card\"><strong>Read</strong><span>Pinch to adjust text size. Open more files to create tabs.</span></div>"
                + "</div>"
                + "<p class=\"welcome-note\">Raw HTML is shown as text. HTTP and HTTPS links open outside the app.</p>"
                + "</section>";
    }

    private void renderCurrentDocument() {
        webView.loadDataWithBaseURL(null, HtmlPageBuilder.buildPage(openTabs.activeTab().document(), currentTheme, currentFontSize), "text/html", "UTF-8", null);
        renderedFontSize = currentFontSize;
        webView.getSettings().setTextZoom(100);
    }

    private void renderTabs() {
        tabRow.removeAllViews();
        for (int i = 0; i < openTabs.tabs().size(); i++) {
            OpenDocumentTab tab = openTabs.tabs().get(i);
            LinearLayout tabGroup = new LinearLayout(this);
            tabGroup.setOrientation(LinearLayout.HORIZONTAL);
            tabGroup.setGravity(Gravity.CENTER_VERTICAL);
            tabGroup.setPadding(0, 0, dp(6), 0);

            TabButton button = new TabButton(this, i);
            button.setText(tab.title());
            button.setAllCaps(false);
            button.setOnClickListener(this);
            button.setClickable(i != openTabs.activeIndex());
            button.setSingleLine(true);
            button.setEllipsize(TextUtils.TruncateAt.END);
            button.setMaxWidth(dp(220));
            button.setTextSize(14);
            button.setTypeface(i == openTabs.activeIndex() ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            button.setTextColor(i == openTabs.activeIndex() ? 0xffffffff : textColor());
            button.setPadding(dp(14), dp(8), dp(14), dp(8));
            button.setBackground(makeRoundedBackground(
                    i == openTabs.activeIndex() ? primaryColor() : surfaceColor(),
                    i == openTabs.activeIndex() ? primaryColor() : borderColor(),
                    8));
            tabGroup.addView(button, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            if (canCloseTab(tab)) {
                CloseTabText closeText = new CloseTabText(this, i);
                closeText.setText("×");
                closeText.setTextSize(20);
                closeText.setTextColor(mutedColor());
                closeText.setGravity(Gravity.CENTER);
                closeText.setPadding(dp(6), 0, dp(14), 0);
                closeText.setContentDescription(currentLanguage.isJapanese()
                        ? "タブを閉じる: " + tab.title()
                        : "Close tab: " + tab.title());
                closeText.setOnClickListener(this);
                tabGroup.addView(closeText, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
            }

            tabRow.addView(tabGroup, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private static boolean canCloseTab(OpenDocumentTab tab) {
        return !WELCOME_URI.equals(tab.uri());
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
        applyTextZoomWithoutReload();
    }

    private void applyTextZoomWithoutReload() {
        int zoomPercent = Math.round((currentFontSize.sp() * 100f) / renderedFontSize.sp());
        webView.getSettings().setTextZoom(zoomPercent);
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
