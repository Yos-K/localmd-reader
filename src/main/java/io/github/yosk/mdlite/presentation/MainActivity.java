package io.github.yosk.mdlite.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.webkit.WebSettings;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.github.yosk.mdlite.domain.CodeHighlighting;
import io.github.yosk.mdlite.domain.CodeHighlightingPolicy;
import io.github.yosk.mdlite.viewer.CircleGesturePath;
import io.github.yosk.mdlite.viewer.ControlsPlacement;
import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.FeatureEntitlements;
import io.github.yosk.mdlite.file.FileSizePolicy;
import io.github.yosk.mdlite.viewer.FontSize;
import io.github.yosk.mdlite.viewer.GestureShortcutAction;
import io.github.yosk.mdlite.domain.MarkdownDraftFileName;
import io.github.yosk.mdlite.file.MarkdownFileOpenResult;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.OpenDocumentTabs;
import io.github.yosk.mdlite.domain.ProFeatureCatalog;
import io.github.yosk.mdlite.domain.ProFeaturePresentation;
import io.github.yosk.mdlite.domain.ProFeaturePresentationItem;
import io.github.yosk.mdlite.file.RecentDocument;
import io.github.yosk.mdlite.file.RecentDocuments;
import io.github.yosk.mdlite.file.RestorableOpenTab;
import io.github.yosk.mdlite.file.RestorableOpenTabs;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.viewer.ViewerLanguage;
import io.github.yosk.mdlite.domain.ViewerFeature;
import io.github.yosk.mdlite.viewer.ViewerText;
import io.github.yosk.mdlite.viewer.ViewerTheme;
import io.github.yosk.mdlite.infrastructure.HtmlPageBuilder;
import io.github.yosk.mdlite.infrastructure.BuildEntitlementSource;
import io.github.yosk.mdlite.infrastructure.JavaSimpleMarkdownRenderer;
import io.github.yosk.mdlite.infrastructure.WelcomeDocumentBuilder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MainActivity extends Activity implements View.OnClickListener, DialogInterface.OnClickListener, View.OnApplyWindowInsetsListener {
    private static final int REQUEST_OPEN_DOCUMENT = 1001;
    private static final int REQUEST_SAVE_DOCUMENT = 1002;
    private static final String ACTION_OPEN_TEXT = "io.github.yosk.mdlite.action.OPEN_TEXT";
    private static final String ACTION_OPEN_TEXTS = "io.github.yosk.mdlite.action.OPEN_TEXTS";
    private static final String EXTRA_MARKDOWN_TITLE = "io.github.yosk.mdlite.extra.MARKDOWN_TITLE";
    private static final String EXTRA_MARKDOWN_TITLES = "io.github.yosk.mdlite.extra.MARKDOWN_TITLES";
    private static final String EXTRA_MARKDOWN_SOURCE = "io.github.yosk.mdlite.extra.MARKDOWN_SOURCE";
    private static final String EXTRA_MARKDOWN_SOURCES = "io.github.yosk.mdlite.extra.MARKDOWN_SOURCES";
    private static final String EXTRA_MARKDOWN_TEXT = "io.github.yosk.mdlite.extra.MARKDOWN_TEXT";
    private static final String EXTRA_MARKDOWN_TEXTS_BASE64 = "io.github.yosk.mdlite.extra.MARKDOWN_TEXTS_BASE64";
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L;
    private static final int MAX_RECENT_DOCUMENTS = 5;
    private static final String RECENT_PREFS = "recent_documents";
    private static final String RECENT_ITEMS = "items";
    private static final String CLIPBOARD_HISTORY_PREFS = "clipboard_history";
    private static final String CLIPBOARD_HISTORY_ITEMS = "items";
    private static final int MAX_CLIPBOARD_HISTORY = 10;
    private static final String OPEN_TABS_PREFS = "open_tabs";
    private static final String OPEN_TABS_ITEMS = "items";
    private static final String OPEN_TABS_ACTIVE_INDEX = "active_index";
    private static final String WELCOME_URI = "app://welcome";
    private static final String DRAFT_URI_PREFIX = "draft://";
    private static final String MESSAGE_NONE = "";
    private static final String MESSAGE_SAVED_MARKDOWN = "saved_markdown";
    private static final int MENU_WIDTH_DP = 280;
    private static final int EDGE_SWIPE_DP = 24;
    private static final int MENU_SWIPE_MIN_DISTANCE_DP = 72;

    private final JavaSimpleMarkdownRenderer renderer = new JavaSimpleMarkdownRenderer();
    private final FileSizePolicy fileSizePolicy = new FileSizePolicy(MAX_FILE_SIZE_BYTES);
    private final FeatureEntitlement featureEntitlement = FeatureEntitlements.current(BuildEntitlementSource.current());
    private final CodeHighlighting codeHighlighting = CodeHighlightingPolicy.fromEntitlement(featureEntitlement);

    private ViewerSettingsStore settingsStore;
    private WebView webView;
    private TextView messageView;
    private Button menuButton;
    private MenuActionButton openButton;
    private MenuActionButton createFromClipboardButton;
    private MenuActionButton saveAsButton;
    private MenuActionButton recentButton;
    private MenuActionButton themeButton;
    private MenuActionButton languageButton;
    private MenuActionButton controlsPlacementButton;
    private MenuActionButton gestureShortcutsButton;
    private MenuActionButton proFeaturesButton;
    private MenuActionButton clipboardDiagnosticsButton;
    private MenuActionButton privacyButton;
    private MenuActionButton[] menuActionButtons;
    private SwipeMenuLayout menuPanel;
    private LinearLayout root;
    private LinearLayout topBar;
    private LinearLayout controlsBar;
    private LinearLayout tabRow;
    private HorizontalScrollView tabScroller;
    private int systemTopInsetPx;
    private int systemBottomInsetPx;
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
    private ViewerPalette viewerPalette = ViewerPalette.from(ViewerTheme.light());
    private GestureShortcutAction doubleTapShortcut = GestureShortcutAction.off();
    private GestureShortcutAction circleGestureShortcut = GestureShortcutAction.off();
    private ViewerText viewerText = ViewerText.fromLanguage(ViewerLanguage.english());
    private FontSize currentFontSize = FontSize.defaultSize();
    private FontSize renderedFontSize = FontSize.defaultSize();
    private String pendingSaveMarkdown = "";
    private String currentMessage = MESSAGE_NONE;
    private final Map<String, String> draftMarkdownByUri = new HashMap<String, String>();
    private RecentDocuments displayedRecentDocuments = RecentDocuments.empty(MAX_RECENT_DOCUMENTS);
    private ScaleGestureDetector fontScaleGestureDetector;
    private GestureDetector shortcutGestureDetector;
    private float accumulatedPinchScale = 1f;
    private final List<Float> circleGestureXs = new ArrayList<>();
    private final List<Float> circleGestureYs = new ArrayList<>();
    private boolean consumingCircleGesture;
    private boolean trackingEdgeSwipe;
    private float edgeSwipeStartX;
    private float menuSwipeStartX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeSwipeFrameLayout appRoot = new EdgeSwipeFrameLayout(this);

        settingsStore = new ViewerSettingsStore(this, featureEntitlement);
        controlsPlacement = settingsStore.loadControlsPlacement();
        currentLanguage = settingsStore.loadViewerLanguage();
        viewerText = ViewerText.fromLanguage(currentLanguage);
        currentTheme = settingsStore.loadViewerTheme();
        viewerPalette = ViewerPalette.from(currentTheme);
        doubleTapShortcut = settingsStore.loadDoubleTapShortcut();
        circleGestureShortcut = settingsStore.loadCircleGestureShortcut();

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(dp(14), dp(10), dp(14), dp(10));
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
        appTitle.setText("LocalMD Reader");
        appTitle.setTextColor(textColor());
        appTitle.setTextSize(17);
        appTitle.setTypeface(Typeface.DEFAULT_BOLD);
        appTitle.setGravity(Gravity.CENTER_VERTICAL);
        appTitle.setSingleLine(true);
        appTitle.setEllipsize(TextUtils.TruncateAt.END);
        appTitle.setPadding(dp(14), 0, 0, 0);
        topBar.addView(appTitle, new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));

        openButton = menuActionButton(new OpenFileMenuAction());
        createFromClipboardButton = menuActionButton(new CreateFromClipboardMenuAction());
        saveAsButton = menuActionButton(new SaveAsMenuAction());
        recentButton = menuActionButton(new RecentFilesMenuAction());
        themeButton = menuActionButton(new ThemeMenuAction());
        languageButton = menuActionButton(new LanguageMenuAction());
        controlsPlacementButton = menuActionButton(new ControlsPlacementMenuAction());
        gestureShortcutsButton = menuActionButton(new GestureShortcutsMenuAction());
        proFeaturesButton = menuActionButton(new ProFeaturesMenuAction());
        clipboardDiagnosticsButton = menuActionButton(new ClipboardDiagnosticsMenuAction());
        privacyButton = menuActionButton(new PrivacyMenuAction());
        menuActionButtons = new MenuActionButton[] {
            openButton,
            createFromClipboardButton,
            saveAsButton,
            recentButton,
            themeButton,
            languageButton,
            controlsPlacementButton,
            gestureShortcutsButton,
            proFeaturesButton,
            clipboardDiagnosticsButton,
            privacyButton
        };

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
        menuPanel.addView(createFromClipboardButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuPanel.addView(saveAsButton, new LinearLayout.LayoutParams(
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
        menuPanel.addView(gestureShortcutsButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        infoSection = menuSection("");
        menuPanel.addView(infoSection, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuPanel.addView(proFeaturesButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuPanel.addView(clipboardDiagnosticsButton, new LinearLayout.LayoutParams(
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
        shortcutGestureDetector = new GestureDetector(this, new ShortcutGestureListener(this));
        webView.setOnTouchListener(new ViewerTouchListener(this));
        updateLocalizedText();
        openTabs = restoreOpenTabsOrInitial();
        applyNativeTheme();
        renderTabs();
        renderCurrentDocument();

        applyControlsPlacement();

        appRoot.setOnApplyWindowInsetsListener(this);
        appRoot.addView(root, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        FrameLayout.LayoutParams menuParams = new FrameLayout.LayoutParams(
                dp(MENU_WIDTH_DP),
                FrameLayout.LayoutParams.MATCH_PARENT);
        menuParams.gravity = Gravity.START;
        appRoot.addView(menuPanel, menuParams);

        setContentView(appRoot);
        if (savedInstanceState == null) {
            handleIncomingIntent(getIntent());
        }
    }

    @Override
    public void onClick(View view) {
        if (view == menuButton) {
            updateLocalizedText();
            toggleMenu();
        } else if (view instanceof MenuActionButton) {
            ((MenuActionButton) view).perform(this);
        } else if (view instanceof TabButton) {
            openTabs = openTabs.activate(((TabButton) view).tabIndex());
            clearMessage();
            updateLocalizedText();
            renderTabs();
            renderCurrentDocument();
            saveOpenTabs();
        } else if (view instanceof CloseTabText) {
            openTabs = openTabs.closeOrFallback(((CloseTabText) view).tabIndex(), initialTab());
            clearMessage();
            updateLocalizedText();
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
    public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
        systemTopInsetPx = insets.getSystemWindowInsetTop();
        systemBottomInsetPx = insets.getSystemWindowInsetBottom();
        applyControlsBarInsets();
        return insets;
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
            openSelectedDocuments(data);
            return;
        }
        if (requestCode == REQUEST_SAVE_DOCUMENT && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                writePendingMarkdown(uri);
            }
        }
    }

    private void openMarkdownPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_OPEN_DOCUMENT);
    }

    private void openSelectedDocuments(Intent data) {
        ClipData clipData = data.getClipData();
        if (clipData != null && clipData.getItemCount() > 0) {
            openClipDataUris(clipData, true, data);
            return;
        }
        Uri uri = data.getData();
        if (uri != null) {
            persistReadPermission(data, uri);
            openUri(uri, true);
        }
    }

    private void openClipDataUris(ClipData clipData, boolean remember, Intent permissionIntent) {
        for (int i = 0; i < clipData.getItemCount(); i++) {
            Uri uri = clipData.getItemAt(i).getUri();
            if (uri != null) {
                persistReadPermission(permissionIntent, uri);
                openUri(uri, remember);
            }
        }
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        setIntent(new Intent());
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
                return;
            }
            ClipData clipData = intent.getClipData();
            if (clipData != null) {
                openClipDataUris(clipData, true, intent);
            }
            return;
        }
        if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            ArrayList<Uri> sharedUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            if (sharedUris != null) {
                openUris(sharedUris, true, intent);
                return;
            }
            ClipData clipData = intent.getClipData();
            if (clipData != null) {
                openClipDataUris(clipData, true, intent);
            }
            return;
        }
        if (Intent.ACTION_PROCESS_TEXT.equals(action)) {
            createMarkdownFromSelectedText(intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT));
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

    private void createMarkdownFromSelectedText(CharSequence selectedText) {
        if (selectedText == null || selectedText.length() == 0) {
            showFileOpenError(noTextToCreateMessage());
            return;
        }
        openSelectedTextMarkdown("Selected text", AndroidStyledTextMarkdown.from(selectedText));
    }

    private void createMarkdownFromClipboard() {
        ClipData clip = clipboardClip();
        List<ClipboardMarkdownItem> items = clipboardMarkdownItems(clip);
        if (items.isEmpty()) {
            showFileOpenError(noClipboardTextMessage());
            return;
        }
        if (items.size() == 1) {
            openClipboardMarkdownItem(items.get(0));
            return;
        }
        showClipboardItemPicker(items);
    }

    private List<ClipboardMarkdownItem> clipboardMarkdownItems(ClipData clip) {
        ArrayList<ClipboardMarkdownItem> items = new ArrayList<ClipboardMarkdownItem>();
        appendCurrentClipboardItems(items, clip);
        appendClipboardHistoryItems(items);
        return items;
    }

    private void appendCurrentClipboardItems(List<ClipboardMarkdownItem> items, ClipData clip) {
        if (clip == null) {
            return;
        }
        for (int i = 0; i < clip.getItemCount(); i++) {
            CharSequence text = clip.getItemAt(i).coerceToStyledText(this);
            ClipboardMarkdownItem item = clipboardMarkdownItem(currentClipboardTitle(i), text);
            if (item != null) {
                items.add(item);
            }
        }
    }

    private void appendClipboardHistoryItems(List<ClipboardMarkdownItem> items) {
        List<String> history = loadClipboardHistory();
        for (int i = 0; i < history.size(); i++) {
            ClipboardMarkdownItem item = clipboardMarkdownItem(historyClipboardTitle(i), history.get(i));
            if (item != null && !containsClipboardMarkdown(items, item.markdown())) {
                items.add(item);
            }
        }
    }

    private ClipboardMarkdownItem clipboardMarkdownItem(String title, CharSequence text) {
        if (text == null || text.length() == 0) {
            return null;
        }
        String markdown = AndroidStyledTextMarkdown.from(text);
        if (markdown.length() == 0) {
            return null;
        }
        return new ClipboardMarkdownItem(title, markdown);
    }

    private boolean containsClipboardMarkdown(List<ClipboardMarkdownItem> items, String markdown) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).markdown().equals(markdown)) {
                return true;
            }
        }
        return false;
    }

    private String currentClipboardTitle(int index) {
        return index == 0 ? "Clipboard" : "Clipboard " + (index + 1);
    }

    private String historyClipboardTitle(int index) {
        return viewerText.historyClipboardTitle(index);
    }

    private void showClipboardItemPicker(List<ClipboardMarkdownItem> items) {
        boolean[] selected = new boolean[items.size()];
        new AlertDialog.Builder(this)
                .setTitle(clipboardItemsTitle())
                .setMultiChoiceItems(clipboardItemLabels(items), selected, new ClipboardItemCheckedListener(selected))
                .setPositiveButton(openSelectedClipboardItemsLabel(), new ClipboardItemsOpenListener(this, items, selected))
                .setNegativeButton(cancelLabel(), null)
                .show();
    }

    private String[] clipboardItemLabels(List<ClipboardMarkdownItem> items) {
        String[] labels = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            labels[i] = clipboardItemLabel(items.get(i));
        }
        return labels;
    }

    private String clipboardItemLabel(ClipboardMarkdownItem item) {
        String preview = item.markdown().replace('\n', ' ').trim();
        String clipped = preview.length() > 48 ? preview.substring(0, 48) + "..." : preview;
        return item.title() + ": " + clipped;
    }

    private void openSelectedClipboardItems(List<ClipboardMarkdownItem> items, boolean[] selected) {
        boolean opened = false;
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                openClipboardMarkdownItem(items.get(i));
                opened = true;
            }
        }
        if (!opened) {
            showFileOpenError(noClipboardItemSelectedMessage());
        }
    }

    private void openClipboardMarkdownItem(ClipboardMarkdownItem item) {
        recordClipboardHistory(item.markdown());
        openClipboardDraftMarkdown(item.title(), item.markdown());
    }

    private ClipData clipboardClip() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null || !clipboard.hasPrimaryClip()) {
            return null;
        }
        ClipData clip = clipboard.getPrimaryClip();
        if (clip == null || clip.getItemCount() == 0) {
            return null;
        }
        return clip;
    }

    private void openClipboardDraftMarkdown(String title, String markdown) {
        DraftMarkdownDocument draft = draftMarkdownDocument(title, markdown);
        if (draft == null) {
            return;
        }
        draftMarkdownByUri.put(draft.uri(), draft.markdown());
        openTabs = openTabs.open(OpenDocumentTab.clipboardDraft(draft.displayName(), draft.uri(), draft.rendered()));
        updateLocalizedText();
        renderTabs();
        renderCurrentDocument();
    }

    private void openSelectedTextMarkdown(String title, String markdown) {
        DraftMarkdownDocument draft = draftMarkdownDocument(title, markdown);
        if (draft == null) {
            return;
        }
        draftMarkdownByUri.put(draft.uri(), draft.markdown());
        openTabs = openTabs.open(OpenDocumentTab.selectedTextDraft(draft.displayName(), draft.uri(), draft.rendered()));
        updateLocalizedText();
        renderTabs();
        renderCurrentDocument();
    }

    private DraftMarkdownDocument draftMarkdownDocument(String title, String markdown) {
        String text = markdown == null ? "" : markdown;
        long sizeBytes = text.getBytes(StandardCharsets.UTF_8).length;
        MarkdownFileOpenResult openResult = MarkdownFileOpenResult.from(title + ".md", sizeBytes, fileSizePolicy);
        if (openResult instanceof MarkdownFileOpenResult.OversizedMarkdownFile) {
            showFileOpenError(fileTooLargeMessage());
            return null;
        }

        SafeHtml rendered = renderer.render(text, codeHighlighting);
        String displayName = nextDraftDisplayName(title);
        String draftUri = DRAFT_URI_PREFIX + Uri.encode(displayName);
        return new DraftMarkdownDocument(displayName, draftUri, text, rendered);
    }

    private String nextDraftDisplayName(String title) {
        int sequence = 1;
        String displayName = MarkdownDraftFileName.fromTitle(title, sequence).value();
        while (draftMarkdownByUri.containsKey(DRAFT_URI_PREFIX + Uri.encode(displayName))) {
            sequence++;
            displayName = MarkdownDraftFileName.fromTitle(title, sequence).value();
        }
        return displayName;
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
            SafeHtml rendered = renderer.render(markdown, codeHighlighting);
            openTabs = openTabs.open(OpenDocumentTab.fileDocument(readableFile.displayName(), uri.toString(), rendered));
            updateLocalizedText();
            renderTabs();
            renderCurrentDocument();
            saveOpenTabs();
            if (remember) {
                recordRecentDocument(readableFile.displayName(), uri);
            }
            clearMessage();
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
        SafeHtml rendered = renderer.render(text, codeHighlighting);
        String uri = "termux://open/" + Uri.encode(sourceId);
        openTabs = openTabs.open(OpenDocumentTab.fileDocument(readableFile.displayName(), uri, rendered));
        updateLocalizedText();
        renderTabs();
        renderCurrentDocument();
        saveOpenTabs();
        clearMessage();
    }

    private void saveActiveMarkdownAs() {
        OpenDocumentTab tab = openTabs.activeTab();
        if (WELCOME_URI.equals(tab.uri())) {
            showFileOpenError(noDocumentToSaveMessage());
            return;
        }
        String markdown = markdownForSave(tab);
        if (markdown.length() == 0) {
            showFileOpenError(noDocumentToSaveMessage());
            return;
        }
        pendingSaveMarkdown = markdown;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/markdown");
        intent.putExtra(Intent.EXTRA_TITLE, tab.title());
        startActivityForResult(intent, REQUEST_SAVE_DOCUMENT);
    }

    private String markdownForSave(OpenDocumentTab tab) {
        if (tab instanceof OpenDocumentTab.DraftDocumentTab) {
            return draftMarkdownFrom(tab);
        }
        try {
            return readText(Uri.parse(tab.uri()), MAX_FILE_SIZE_BYTES);
        } catch (IOException e) {
            return "";
        } catch (SecurityException e) {
            return "";
        }
    }

    private String draftMarkdownFrom(OpenDocumentTab tab) {
        String markdown = draftMarkdownByUri.get(tab.uri());
        return markdown == null ? "" : markdown;
    }

    private void writePendingMarkdown(Uri uri) {
        try {
            OutputStream output = getContentResolver().openOutputStream(uri);
            if (output == null) {
                showFileOpenError(createMarkdownFailedMessage());
                return;
            }
            try {
                output.write(pendingSaveMarkdown.getBytes(StandardCharsets.UTF_8));
            } finally {
                output.close();
            }
            pendingSaveMarkdown = "";
            openUri(uri, true);
            showSavedMarkdownMessage();
        } catch (IOException e) {
            showFileOpenError(createMarkdownFailedMessage());
        }
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

    private void showSavedMarkdownMessage() {
        currentMessage = MESSAGE_SAVED_MARKDOWN;
        showMessage(savedMarkdownMessage());
    }

    private void clearMessage() {
        currentMessage = MESSAGE_NONE;
        updateLocalizedMessage();
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

    private void showProFeaturesDialog() {
        showInfoDialog(proFeaturesTitle(), proFeaturesMessage());
    }

    private void showGestureShortcutsDialog() {
        new AlertDialog.Builder(this)
                .setTitle(viewerText.gestures())
                .setItems(gestureShortcutLabels(), new GestureShortcutClickListener(this))
                .setNegativeButton("OK", null)
                .show();
    }

    private void showThemeDialog() {
        new AlertDialog.Builder(this)
                .setTitle(viewerText.themeLabel(currentTheme))
                .setItems(themeLabels(), new ThemeClickListener(this, availableThemes()))
                .setNegativeButton("OK", null)
                .show();
    }

    private ViewerTheme[] availableThemes() {
        return ViewerTheme.availableThemes(featureEntitlement);
    }

    private String[] themeLabels() {
        ViewerTheme[] themes = availableThemes();
        String[] labels = new String[themes.length];
        for (int i = 0; i < themes.length; i++) {
            labels[i] = viewerText.themeLabel(themes[i]);
        }
        return labels;
    }

    private String[] gestureShortcutLabels() {
        return new String[] {
            shortcutLabel(viewerText.doubleTapPrefix(), doubleTapShortcut),
            shortcutLabel(viewerText.circleGesturePrefix(), circleGestureShortcut)
        };
    }

    private void selectGestureTarget(int index) {
        if (index == 0) {
            showDoubleTapShortcutDialog();
            return;
        }
        if (index == 1) {
            showCircleGestureShortcutDialog();
        }
    }

    private void showDoubleTapShortcutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(viewerText.doubleTapPrefix())
                .setItems(gestureActionLabels(), new DoubleTapShortcutClickListener(this, availableGestureActions()))
                .setNegativeButton("OK", null)
                .show();
    }

    private void showCircleGestureShortcutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(viewerText.circleGesturePrefix())
                .setItems(gestureActionLabels(), new CircleGestureShortcutClickListener(this, availableGestureActions()))
                .setNegativeButton("OK", null)
                .show();
    }

    private GestureShortcutAction[] availableGestureActions() {
        return GestureShortcutAction.availableActions(featureEntitlement);
    }

    private String[] gestureActionLabels() {
        GestureShortcutAction[] actions = availableGestureActions();
        String[] labels = new String[actions.length];
        for (int i = 0; i < actions.length; i++) {
            labels[i] = actionLabel(actions[i]);
        }
        return labels;
    }

    private void showClipboardDiagnosticsDialog() {
        showInfoDialog(clipboardDiagnosticsTitle(), AndroidTextDiagnostics.describe(this, clipboardClip()));
    }

    private void updateLocalizedText() {
        menuButton.setText(viewerText.menuButton());
        menuButton.setContentDescription(viewerText.openMenuDescription());
        appTitle.setText("LocalMD Reader");
        refreshMenuActionButtons();
        menuTitle.setText("LocalMD Reader");
        filesSection.setText(viewerText.filesSection());
        readingSection.setText(viewerText.readingSection());
        layoutSection.setText(viewerText.layoutSection());
        infoSection.setText(viewerText.infoSection());
        updateLocalizedMessage();
    }

    private void refreshMenuActionButtons() {
        for (int i = 0; i < menuActionButtons.length; i++) {
            menuActionButtons[i].refresh(this);
        }
    }

    private void updateLocalizedMessage() {
        if (MESSAGE_SAVED_MARKDOWN.equals(currentMessage)) {
            showMessage(savedMarkdownMessage());
            return;
        }
        if (openTabs != null) {
            showMessage(openTabs.activeTab().statusMessage().localized(viewerText));
            return;
        }
        showMessage("");
    }

    private String shortcutLabel(String prefix, GestureShortcutAction action) {
        if (!featureEntitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS)) {
            return prefix + viewerText.proOnly();
        }
        return prefix + actionLabel(action);
    }

    private String actionLabel(GestureShortcutAction action) {
        if (action.isOpenFile()) {
            return viewerText.openFile();
        }
        if (action.isOpenMenu()) {
            return viewerText.openMenu();
        }
        if (action.isNextTheme()) {
            return viewerText.nextThemeAction();
        }
        if (action.isMoveControls()) {
            return viewerText.moveControlsAction();
        }
        return viewerText.off();
    }

    private void applySelectedTheme(ViewerTheme theme) {
        currentTheme = theme;
        settingsStore.saveViewerTheme(currentTheme);
        viewerPalette = ViewerPalette.from(currentTheme);
        updateLocalizedText();
        applyNativeTheme();
        renderTabs();
        renderCurrentDocument();
    }

    private void applyDoubleTapShortcut(GestureShortcutAction action) {
        doubleTapShortcut = action;
        settingsStore.saveDoubleTapShortcut(doubleTapShortcut);
        updateLocalizedText();
    }

    private void applyCircleGestureShortcut(GestureShortcutAction action) {
        circleGestureShortcut = action;
        settingsStore.saveCircleGestureShortcut(circleGestureShortcut);
        updateLocalizedText();
    }

    private boolean activeTabIsDraft() {
        return openTabs != null && openTabs.activeTab() instanceof OpenDocumentTab.DraftDocumentTab;
    }

    private String recentFilesTitle() {
        return viewerText.recentFiles();
    }

    private String openMarkdownTitle() {
        return viewerText.openMarkdownFile();
    }

    private String noRecentFilesMessage() {
        return viewerText.noRecentFiles();
    }

    private String clearHistoryLabel() {
        return viewerText.clearHistory();
    }

    private String recentFilesClearedMessage() {
        return viewerText.recentFilesCleared();
    }

    private String noTextToCreateMessage() {
        return viewerText.noTextToCreate();
    }

    private String noClipboardTextMessage() {
        return viewerText.noClipboardText();
    }

    private String clipboardItemsTitle() {
        return viewerText.clipboardItemsToOpen();
    }

    private String openSelectedClipboardItemsLabel() {
        return viewerText.openSelected();
    }

    private String cancelLabel() {
        return viewerText.cancel();
    }

    private String noClipboardItemSelectedMessage() {
        return viewerText.noClipboardItemSelected();
    }

    private String noDocumentToSaveMessage() {
        return viewerText.noDocumentToSave();
    }

    private String createMarkdownFailedMessage() {
        return viewerText.createMarkdownFailed();
    }

    private String savedMarkdownMessage() {
        return viewerText.savedMarkdown();
    }

    private String privacyTitle() {
        return viewerText.privacy();
    }

    private String proFeaturesTitle() {
        return viewerText.proFeatures();
    }

    private String clipboardDiagnosticsTitle() {
        return viewerText.clipboardDiagnostics();
    }

    private String proFeaturesMessage() {
        ProFeaturePresentationItem[] items = ProFeaturePresentation.from(
                featureEntitlement,
                ProFeatureCatalog.initialFeatures());
        StringBuilder message = new StringBuilder();
        message.append(viewerText.proStatus(featureEntitlement.isPro()));
        for (int i = 0; i < items.length; i++) {
            ProFeaturePresentationItem item = items[i];
            message.append("\n\n")
                    .append(item.isAvailable() ? "[Available] " : "[Locked] ")
                    .append(item.title())
                    .append("\n")
                    .append(item.isAvailable() ? viewerText.featureAvailable() : viewerText.featureLocked())
                    .append(" - ")
                    .append(item.description());
        }
        if (!featureEntitlement.isPro()) {
            message.append("\n\n")
                    .append(viewerText.purchaseFlowUnavailable());
        }
        return message.toString();
    }

    private String privacyMessage() {
        return viewerText.privacyMessage();
    }

    private String unsupportedFileMessage() {
        return viewerText.unsupportedFile();
    }

    private String fileTooLargeMessage() {
        return viewerText.fileTooLarge();
    }

    private String unreadableFileMessage() {
        return viewerText.unreadableFile();
    }

    private void applyControlsPlacement() {
        root.removeAllViews();
        applyControlsBarInsets();
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

    private void applyControlsBarInsets() {
        if (controlsPlacement.isBottom()) {
            root.setPadding(0, systemTopInsetPx, 0, 0);
            controlsBar.setPadding(0, 0, 0, systemBottomInsetPx);
        } else {
            root.setPadding(0, 0, 0, 0);
            controlsBar.setPadding(0, systemTopInsetPx, 0, 0);
        }
        menuPanel.setPadding(dp(18), systemTopInsetPx + dp(28), dp(18), dp(18));
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

    private MenuActionButton menuActionButton(MenuAction action) {
        MenuActionButton button = new MenuActionButton(this, action);
        styleMenuButton(button);
        return button;
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
        view.setPadding(dp(16), dp(9), dp(16), dp(9));
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
        applyMenuButtonTheme();
        applyMenuSectionTheme();
    }

    private void applyMenuButtonTheme() {
        for (int i = 0; i < menuActionButtons.length; i++) {
            styleMenuButton(menuActionButtons[i]);
        }
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
        return viewerPalette.background;
    }

    private int surfaceColor() {
        return viewerPalette.surface;
    }

    private int surfaceAltColor() {
        return viewerPalette.surfaceAlt;
    }

    private int textColor() {
        return viewerPalette.text;
    }

    private int mutedColor() {
        return viewerPalette.muted;
    }

    private int primaryColor() {
        return viewerPalette.primary;
    }

    private int primaryStrongColor() {
        return viewerPalette.primaryStrong;
    }

    private int borderColor() {
        return viewerPalette.border;
    }

    private int messageColor() {
        return viewerPalette.message;
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
            SafeHtml rendered = renderer.render(markdown, codeHighlighting);
            return OpenDocumentTab.fileDocument(readableFile.displayName(), uri.toString(), rendered);
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
            if (!(tab instanceof OpenDocumentTab.FileDocumentTab)) {
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

    private List<String> loadClipboardHistory() {
        SharedPreferences prefs = getSharedPreferences(CLIPBOARD_HISTORY_PREFS, MODE_PRIVATE);
        String raw = prefs.getString(CLIPBOARD_HISTORY_ITEMS, "");
        ArrayList<String> items = new ArrayList<String>();
        if (raw != null && raw.length() > 0) {
            String[] lines = raw.split("\\n", -1);
            for (int i = 0; i < lines.length; i++) {
                String markdown = decodeClipboardHistoryItem(lines[i]);
                if (markdown != null) {
                    items.add(markdown);
                }
            }
        }
        return items;
    }

    private void recordClipboardHistory(String markdown) {
        if (markdown == null || markdown.length() == 0) {
            return;
        }
        ArrayList<String> items = new ArrayList<String>();
        items.add(markdown);
        List<String> existing = loadClipboardHistory();
        for (int i = 0; i < existing.size(); i++) {
            String item = existing.get(i);
            if (!markdown.equals(item) && items.size() < MAX_CLIPBOARD_HISTORY) {
                items.add(item);
            }
        }
        saveClipboardHistory(items);
    }

    private void saveClipboardHistory(List<String> items) {
        StringBuilder raw = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                raw.append('\n');
            }
            raw.append(encode(items.get(i)));
        }
        getSharedPreferences(CLIPBOARD_HISTORY_PREFS, MODE_PRIVATE)
                .edit()
                .putString(CLIPBOARD_HISTORY_ITEMS, raw.toString())
                .apply();
    }

    private static String decodeClipboardHistoryItem(String line) {
        if (line == null || line.length() == 0) {
            return null;
        }
        try {
            return decode(line);
        } catch (IllegalArgumentException e) {
            return null;
        }
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

    private void configureWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setDomStorageEnabled(false);
        settings.setDatabaseEnabled(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        webView.setWebViewClient(new AppLinkClient(this));
    }

    private OpenDocumentTab initialTab() {
        return OpenDocumentTab.welcome(
                viewerText.welcomeTabTitle(),
                WELCOME_URI,
                WelcomeDocumentBuilder.build(currentLanguage));
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
                closeText.setContentDescription(viewerText.closeTabDescription(tab.title()));
                closeText.setOnClickListener(this);
                tabGroup.addView(closeText, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
            }

            tabRow.addView(tabGroup, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        scrollActiveTabIntoView();
    }

    private void scrollActiveTabIntoView() {
        tabScroller.post(new ScrollToActiveTab(tabScroller, tabRow, openTabs.activeIndex()));
    }

    private static boolean canCloseTab(OpenDocumentTab tab) {
        return !WELCOME_URI.equals(tab.uri());
    }

    private boolean handleViewerTouch(MotionEvent event) {
        fontScaleGestureDetector.onTouchEvent(event);
        shortcutGestureDetector.onTouchEvent(event);
        boolean circleHandled = handleCircleGestureTouch(event);
        return circleHandled || event.getPointerCount() > 1 || fontScaleGestureDetector.isInProgress();
    }

    private boolean handleDoubleTapShortcut() {
        return executeShortcutAction(doubleTapShortcut);
    }

    private boolean handleCircleGestureTouch(MotionEvent event) {
        if (!featureEntitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS)) {
            return false;
        }
        if (circleGestureShortcut.isOff() || event.getPointerCount() > 1) {
            resetCircleGesturePath();
            return false;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            resetCircleGesturePath();
            appendCircleGesturePoint(event);
            return false;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            appendCircleGesturePoint(event);
            consumingCircleGesture = consumingCircleGesture || circleGestureLooksIntentional();
            return consumingCircleGesture;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            appendCircleGesturePoint(event);
            boolean handled = CircleGesturePath.fromPoints(circleGestureXs(), circleGestureYs()).isCircleLike()
                    && executeShortcutAction(circleGestureShortcut);
            boolean consumed = consumingCircleGesture || handled;
            resetCircleGesturePath();
            return consumed;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            resetCircleGesturePath();
        }
        return false;
    }

    private boolean executeShortcutAction(GestureShortcutAction action) {
        if (!featureEntitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS)) {
            return false;
        }
        if (action.isOpenFile()) {
            openMarkdownPicker();
            return true;
        }
        if (action.isOpenMenu()) {
            openMenu();
            return true;
        }
        if (action.isNextTheme()) {
            currentTheme = currentTheme.next(featureEntitlement);
            settingsStore.saveViewerTheme(currentTheme);
            viewerPalette = ViewerPalette.from(currentTheme);
            updateLocalizedText();
            applyNativeTheme();
            renderTabs();
            renderCurrentDocument();
            return true;
        }
        if (action.isMoveControls()) {
            controlsPlacement = controlsPlacement.toggled();
            settingsStore.saveControlsPlacement(controlsPlacement);
            updateLocalizedText();
            applyControlsPlacement();
            return true;
        }
        return false;
    }

    private void appendCircleGesturePoint(MotionEvent event) {
        circleGestureXs.add(Float.valueOf(event.getX()));
        circleGestureYs.add(Float.valueOf(event.getY()));
    }

    private void resetCircleGesturePath() {
        circleGestureXs.clear();
        circleGestureYs.clear();
        consumingCircleGesture = false;
    }

    private boolean circleGestureLooksIntentional() {
        if (circleGestureXs.size() < 5) {
            return false;
        }
        float minX = circleGestureXs.get(0).floatValue();
        float maxX = minX;
        float minY = circleGestureYs.get(0).floatValue();
        float maxY = minY;
        for (int i = 1; i < circleGestureXs.size(); i++) {
            minX = Math.min(minX, circleGestureXs.get(i).floatValue());
            maxX = Math.max(maxX, circleGestureXs.get(i).floatValue());
            minY = Math.min(minY, circleGestureYs.get(i).floatValue());
            maxY = Math.max(maxY, circleGestureYs.get(i).floatValue());
        }
        float width = maxX - minX;
        float height = maxY - minY;
        if (width < dp(40) || height < dp(40)) {
            return false;
        }
        float aspectRatio = Math.max(width, height) / Math.max(1f, Math.min(width, height));
        return aspectRatio <= 1.8f;
    }

    private float[] circleGestureXs() {
        return toFloatArray(circleGestureXs);
    }

    private float[] circleGestureYs() {
        return toFloatArray(circleGestureYs);
    }

    private static float[] toFloatArray(List<Float> values) {
        float[] result = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i).floatValue();
        }
        return result;
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

    private abstract static class MenuAction {
        abstract String label(MainActivity activity);

        abstract void perform(MainActivity activity);

        boolean visible(MainActivity activity) {
            return true;
        }
    }

    private static final class OpenFileMenuAction extends MenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText.openFile();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.openMarkdownPicker();
        }
    }

    private static final class CreateFromClipboardMenuAction extends MenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText.createFromClipboard();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.createMarkdownFromClipboard();
        }
    }

    private static final class SaveAsMenuAction extends MenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText.saveAs();
        }

        @Override
        boolean visible(MainActivity activity) {
            return activity.activeTabIsDraft();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.saveActiveMarkdownAs();
        }
    }

    private static final class RecentFilesMenuAction extends MenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.recentFilesTitle();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showRecentDocuments();
        }
    }

    private static final class ThemeMenuAction extends MenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText.themeLabel(activity.currentTheme);
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showThemeDialog();
        }
    }

    private static final class LanguageMenuAction extends MenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText.switchLanguage();
        }

        @Override
        void perform(MainActivity activity) {
            activity.currentLanguage = activity.currentLanguage.toggled();
            activity.viewerText = ViewerText.fromLanguage(activity.currentLanguage);
            activity.settingsStore.saveViewerLanguage(activity.currentLanguage);
            activity.updateLocalizedText();
            if (WELCOME_URI.equals(activity.openTabs.activeTab().uri())) {
                activity.openTabs = OpenDocumentTabs.withInitialTab(activity.initialTab());
                activity.renderTabs();
                activity.renderCurrentDocument();
            }
            activity.closeMenu();
        }
    }

    private static final class ControlsPlacementMenuAction extends MenuAction {
        @Override
        String label(MainActivity activity) {
            if (activity.controlsPlacement.isBottom()) {
                return activity.viewerText.moveControlsToTop();
            }
            return activity.viewerText.moveControlsToBottom();
        }

        @Override
        void perform(MainActivity activity) {
            activity.controlsPlacement = activity.controlsPlacement.toggled();
            activity.settingsStore.saveControlsPlacement(activity.controlsPlacement);
            activity.updateLocalizedText();
            activity.applyControlsPlacement();
            activity.closeMenu();
        }
    }

    private static final class GestureShortcutsMenuAction extends MenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText.gestures();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showGestureShortcutsDialog();
        }
    }

    private static final class ProFeaturesMenuAction extends MenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText.proFeatures();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showProFeaturesDialog();
        }
    }

    private static final class ClipboardDiagnosticsMenuAction extends MenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText.clipboardDiagnostics();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showClipboardDiagnosticsDialog();
        }
    }

    private static final class PrivacyMenuAction extends MenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText.privacy();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showPrivacyPolicyDialog();
        }
    }

    private static final class MenuActionButton extends Button {
        private final MenuAction action;

        private MenuActionButton(MainActivity activity, MenuAction action) {
            super(activity);
            this.action = action;
            setAllCaps(false);
            setOnClickListener(activity);
        }

        private void refresh(MainActivity activity) {
            setText(action.label(activity));
            setVisibility(action.visible(activity) ? View.VISIBLE : View.GONE);
        }

        private void perform(MainActivity activity) {
            action.perform(activity);
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

    private static final class AppLinkClient extends WebViewClient {
        private final MainActivity activity;

        private AppLinkClient(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return handleUrlLoading(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request == null ? null : request.getUrl();
            return handleUrlLoading(view, uri == null ? null : uri.toString());
        }

        private boolean handleUrlLoading(WebView view, String url) {
            if (WelcomeDocumentBuilder.OPEN_MARKDOWN_URL.equals(url)) {
                activity.openMarkdownPicker();
                return true;
            }
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

    private static final class ScrollToActiveTab implements Runnable {
        private final HorizontalScrollView tabScroller;
        private final LinearLayout tabRow;
        private final int activeIndex;

        private ScrollToActiveTab(HorizontalScrollView tabScroller, LinearLayout tabRow, int activeIndex) {
            this.tabScroller = tabScroller;
            this.tabRow = tabRow;
            this.activeIndex = activeIndex;
        }

        @Override
        public void run() {
            if (activeIndex < 0 || activeIndex >= tabRow.getChildCount()) {
                return;
            }
            View activeTab = tabRow.getChildAt(activeIndex);
            int targetLeft = activeTab.getLeft() - tabScroller.getPaddingLeft();
            int targetRight = activeTab.getRight() - tabScroller.getWidth() + tabScroller.getPaddingRight();
            if (targetLeft < tabScroller.getScrollX()) {
                tabScroller.smoothScrollTo(targetLeft, 0);
            } else if (targetRight > tabScroller.getScrollX()) {
                tabScroller.smoothScrollTo(targetRight, 0);
            }
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

    private static final class GestureShortcutClickListener implements DialogInterface.OnClickListener {
        private final MainActivity activity;

        private GestureShortcutClickListener(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            activity.selectGestureTarget(which);
        }
    }

    private static final class ThemeClickListener implements DialogInterface.OnClickListener {
        private final MainActivity activity;
        private final ViewerTheme[] themes;

        private ThemeClickListener(MainActivity activity, ViewerTheme[] themes) {
            this.activity = activity;
            this.themes = themes;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            activity.applySelectedTheme(themes[which]);
        }
    }

    private static final class DoubleTapShortcutClickListener implements DialogInterface.OnClickListener {
        private final MainActivity activity;
        private final GestureShortcutAction[] actions;

        private DoubleTapShortcutClickListener(MainActivity activity, GestureShortcutAction[] actions) {
            this.activity = activity;
            this.actions = actions;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            activity.applyDoubleTapShortcut(actions[which]);
        }
    }

    private static final class CircleGestureShortcutClickListener implements DialogInterface.OnClickListener {
        private final MainActivity activity;
        private final GestureShortcutAction[] actions;

        private CircleGestureShortcutClickListener(MainActivity activity, GestureShortcutAction[] actions) {
            this.activity = activity;
            this.actions = actions;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            activity.applyCircleGestureShortcut(actions[which]);
        }
    }

    private static final class ClipboardItemCheckedListener implements DialogInterface.OnMultiChoiceClickListener {
        private final boolean[] selected;

        private ClipboardItemCheckedListener(boolean[] selected) {
            this.selected = selected;
        }

        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            selected[which] = isChecked;
        }
    }

    private static final class ClipboardItemsOpenListener implements DialogInterface.OnClickListener {
        private final MainActivity activity;
        private final List<ClipboardMarkdownItem> items;
        private final boolean[] selected;

        private ClipboardItemsOpenListener(MainActivity activity, List<ClipboardMarkdownItem> items, boolean[] selected) {
            this.activity = activity;
            this.items = items;
            this.selected = selected;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            activity.openSelectedClipboardItems(items, selected);
        }
    }

    private static final class DraftMarkdownDocument {
        private final String displayName;
        private final String uri;
        private final String markdown;
        private final SafeHtml rendered;

        private DraftMarkdownDocument(String displayName, String uri, String markdown, SafeHtml rendered) {
            this.displayName = displayName;
            this.uri = uri;
            this.markdown = markdown;
            this.rendered = rendered;
        }

        private String displayName() {
            return displayName;
        }

        private String uri() {
            return uri;
        }

        private String markdown() {
            return markdown;
        }

        private SafeHtml rendered() {
            return rendered;
        }
    }

    private static final class ClipboardMarkdownItem {
        private final String title;
        private final String markdown;

        private ClipboardMarkdownItem(String title, String markdown) {
            this.title = title;
            this.markdown = markdown;
        }

        private String title() {
            return title;
        }

        private String markdown() {
            return markdown;
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

    private static final class ViewerTouchListener implements View.OnTouchListener {
        private final MainActivity activity;

        private ViewerTouchListener(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            return activity.handleViewerTouch(event);
        }
    }

    private static final class ShortcutGestureListener extends GestureDetector.SimpleOnGestureListener {
        private final MainActivity activity;

        private ShortcutGestureListener(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            return activity.handleDoubleTapShortcut();
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
