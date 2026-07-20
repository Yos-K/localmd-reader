package io.github.yosk.mdlite.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import io.github.yosk.mdlite.domain.CodeHighlighting;
import io.github.yosk.mdlite.R;
import io.github.yosk.mdlite.domain.CodeHighlightingPolicy;
import io.github.yosk.mdlite.domain.CompositeEntitlementSource;
import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.FeatureEntitlements;
import io.github.yosk.mdlite.domain.FolderBrowsingAction;
import io.github.yosk.mdlite.domain.FolderBrowsingMode;
import io.github.yosk.mdlite.domain.HeadingNavigation;
import io.github.yosk.mdlite.domain.HeadingScrollPosition;
import io.github.yosk.mdlite.domain.MarkdownHeading;
import io.github.yosk.mdlite.domain.MarkdownHeadings;
import io.github.yosk.mdlite.domain.MermaidDiagramBlock;
import io.github.yosk.mdlite.domain.MermaidDiagramBlocks;
import io.github.yosk.mdlite.domain.MermaidRendering;
import io.github.yosk.mdlite.domain.MermaidRenderingPolicy;
import io.github.yosk.mdlite.domain.ProPurchaseFlow;
import io.github.yosk.mdlite.domain.ProPurchaseUiState;
import io.github.yosk.mdlite.domain.RecentDocumentLimit;
import io.github.yosk.mdlite.domain.RelativeImageRendering;
import io.github.yosk.mdlite.domain.RelativeImageRenderingPolicy;
import io.github.yosk.mdlite.domain.RelativeLinkRendering;
import io.github.yosk.mdlite.domain.RelativeLinkRenderingPolicy;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.domain.TableOfContentsItem;
import io.github.yosk.mdlite.domain.TableOfContentsItems;
import io.github.yosk.mdlite.domain.TableReadingMode;
import io.github.yosk.mdlite.domain.UnavailableProPurchaseFlow;
import io.github.yosk.mdlite.domain.ViewerFeature;
import io.github.yosk.mdlite.file.FileInfo;
import io.github.yosk.mdlite.file.FileSizePolicy;
import io.github.yosk.mdlite.file.LocalRelativeImageResource;
import io.github.yosk.mdlite.file.LocalRelativeMarkdownLink;
import io.github.yosk.mdlite.file.MarkdownFileOpenResult;
import io.github.yosk.mdlite.file.RestorableOpenTab;
import io.github.yosk.mdlite.file.RestorableOpenTabs;
import io.github.yosk.mdlite.infrastructure.BuildEntitlementSource;
import io.github.yosk.mdlite.infrastructure.BuildProPurchaseStatusRefresh;
import io.github.yosk.mdlite.infrastructure.CachedProPurchaseEntitlementSource;
import io.github.yosk.mdlite.infrastructure.HtmlPageBuilder;
import io.github.yosk.mdlite.infrastructure.JavaSimpleMarkdownRenderer;
import io.github.yosk.mdlite.infrastructure.ProPurchaseCacheStore;
import io.github.yosk.mdlite.infrastructure.ProPurchaseStatusRefresh;
import io.github.yosk.mdlite.infrastructure.WelcomeDocumentBuilder;
import io.github.yosk.mdlite.viewer.ControlsPlacement;
import io.github.yosk.mdlite.viewer.DocumentSearchQuery;
import io.github.yosk.mdlite.viewer.DocumentSearchSession;
import io.github.yosk.mdlite.viewer.FontSize;
import io.github.yosk.mdlite.viewer.GestureShortcutBindings;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.OpenDocumentTabs;
import io.github.yosk.mdlite.viewer.ViewerLanguage;
import io.github.yosk.mdlite.viewer.ViewerText;
import io.github.yosk.mdlite.viewer.ViewerTheme;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MainActivity extends Activity implements View.OnClickListener,
        View.OnApplyWindowInsetsListener,
        MermaidJsRenderEngine.Listener, CustomGestureDrawingView.Listener,
        HeadingNavigation.Handler {

    static final int REQUEST_OPEN_DOCUMENT = 1001;
    static final int REQUEST_SAVE_DOCUMENT = 1002;
    static final int REQUEST_OPEN_FOLDER = 1003;
    static final int REQUEST_EXPORT_HTML = 1004;
    static final String ACTION_OPEN_TEXT = "io.github.yosk.mdlite.action.OPEN_TEXT";
    static final String ACTION_OPEN_TEXTS = "io.github.yosk.mdlite.action.OPEN_TEXTS";
    static final String EXTRA_MARKDOWN_TITLE = "io.github.yosk.mdlite.extra.MARKDOWN_TITLE";
    static final String EXTRA_MARKDOWN_TITLES = "io.github.yosk.mdlite.extra.MARKDOWN_TITLES";
    static final String EXTRA_MARKDOWN_SOURCE = "io.github.yosk.mdlite.extra.MARKDOWN_SOURCE";
    static final String EXTRA_MARKDOWN_SOURCES = "io.github.yosk.mdlite.extra.MARKDOWN_SOURCES";
    static final String EXTRA_MARKDOWN_TEXT = "io.github.yosk.mdlite.extra.MARKDOWN_TEXT";
    static final String EXTRA_MARKDOWN_TEXTS_BASE64 = "io.github.yosk.mdlite.extra.MARKDOWN_TEXTS_BASE64";
    static final long MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L;
    static final String WELCOME_URI = "app://welcome";
    static final String DRAFT_URI_PREFIX = "draft://";
    private static final String MESSAGE_NONE = "";
    private static final String MESSAGE_SAVED_MARKDOWN = "saved_markdown";
    private static final int MENU_WIDTH_DP = 280;
    private static final int EDGE_SWIPE_DP = 24;
    /** Corner radius far beyond half of any button height renders as a pill. */
    static final int PILL_RADIUS_DP = 999;
    private static final int MENU_SWIPE_MIN_DISTANCE_DP = 72;

    final JavaSimpleMarkdownRenderer renderer = new JavaSimpleMarkdownRenderer();
    final FileSizePolicy fileSizePolicy = new FileSizePolicy(MAX_FILE_SIZE_BYTES);
    FeatureEntitlement featureEntitlement;
    CodeHighlighting codeHighlighting;
    MermaidRendering mermaidRendering;
    RelativeLinkRendering relativeLinkRendering;
    RelativeImageRendering relativeImageRendering;
    ProPurchaseUiState proPurchaseUiState = ProPurchaseUiState.unavailable();
    ProPurchaseFlow proPurchaseFlow = UnavailableProPurchaseFlow.instance();
    ProPurchaseCacheStore purchaseCacheStore;
    ProPurchaseStatusRefresh proPurchaseStatusRefresh;
    ViewerSettingsStore settingsStore;
    ClipboardHistoryStore clipboardHistoryStore;
    WebView webView;
    MermaidJsRenderEngine mermaidRenderEngine;
    OpenDocumentTabs openTabs;
    ControlsPlacement controlsPlacement;
    ViewerLanguage currentLanguage = ViewerLanguage.english();
    ViewerTheme currentTheme = ViewerTheme.light();
    ViewerPalette viewerPalette = ViewerPalette.from(ViewerTheme.light());
    GestureShortcutBindings gestureShortcutBindings = GestureShortcutBindings.empty();
    DocumentSearchSession documentSearchSession = DocumentSearchSession.empty();
    ViewerText viewerText = ViewerText.fromLanguage(ViewerLanguage.english());
    FontSize currentFontSize = FontSize.defaultSize();
    FontSize renderedFontSize = FontSize.defaultSize();
    String pendingSaveMarkdown = "";
    String pendingExportHtml = "";
    final Map<String, String> draftMarkdownByUri = new HashMap<String, String>();
    final Map<String, String> markdownSourceByUri = new HashMap<String, String>();
    final Map<String, MermaidDiagramBlocks> mermaidBlocksByUri = new HashMap<String, MermaidDiagramBlocks>();
    final Map<String, Map<Integer, SafeHtml>> mermaidSvgByUri = new HashMap<String, Map<Integer, SafeHtml>>();
    final Set<String> pendingMermaidRenderJobs = new HashSet<String>();
    CircleGestureTrace circleGestureTrace;
    ScaleGestureDetector fontScaleGestureDetector;
    GestureDetector shortcutGestureDetector;
    EdgeSwipeFrameLayout appRoot;
    CustomGestureDrawingView customGestureDrawingView;
    io.github.yosk.mdlite.viewer.CustomGestureShape pendingCustomGestureShape;
    Button purchaseProButton;
    Button restorePurchaseButton;

    TabPersistence tabPersistence;
    DocumentOpener documentOpener;
    ClipboardDocumentCreator clipboardDocumentCreator;
    private DocumentSaver documentSaver;
    private DocumentListDialogController documentListDialogs;
    private HtmlDocumentExporter htmlDocumentExporter;
    DocumentPrintLauncher documentPrintLauncher;
    private SettingsDialogs settingsDialogs;
    private GestureShortcutDialogs gestureShortcutDialogs;
    private GestureShortcutHandler gestureShortcutHandler;
    private DocumentSearchBar documentSearchBar;
    DocumentTabSessionController documentTabSessionController;

    private TextView messageView;
    Button menuButton;
    private MainMenuActionButton openButton;
    private MainMenuActionButton openFolderButton;
    private MainMenuActionButton createFromClipboardButton;
    private MainMenuActionButton saveAsButton;
    private MainMenuActionButton exportAsHtmlButton;
    private MainMenuActionButton printOrSavePdfButton;
    private MainMenuActionButton pinCurrentFileButton;
    private MainMenuActionButton unpinCurrentFileButton;
    private MainMenuActionButton pinnedFilesButton;
    private MainMenuActionButton recentButton;
    private MainMenuActionButton settingsButton;
    private MainMenuActionButton tableOfContentsButton;
    private MainMenuActionButton findInDocumentButton;
    private MainMenuActionButton themeButton;
    private MainMenuActionButton languageButton;
    private MainMenuActionButton controlsPlacementButton;
    private MainMenuActionButton gestureShortcutsButton;
    private MainMenuActionButton proFeaturesButton;
    MainMenuActionButton clipboardDiagnosticsButton;
    private MainMenuActionButton privacyButton;
    private MainMenuActionButton[] menuActionButtons;
    private LinearLayout settingsPanel;
    private ExpandableMenuSection settingsMenuSection;
    private TableOfContentsMenuPanel tableOfContentsPanel;
    private ExpandableMenuSection tableOfContentsMenuSection;
    private MarkdownLibraryMenuTree markdownLibraryMenuTree;
    SwipeMenuScrollView menuScrollContainer;
    SwipeMenuLayout menuPanel;
    View menuScrim;
    private MenuTransitions menuTransitions;
    private final List<LinearLayout> menuCards = new ArrayList<LinearLayout>();
    private LinearLayout root;
    private LinearLayout topBar;
    private LinearLayout controlsBar;
    LinearLayout tabRow;
    private HorizontalScrollView tabScroller;
    private int systemTopInsetPx;
    private int systemBottomInsetPx;
    private TextView appTitle;
    private TextView menuTitle;
    private TextView filesSection;
    private TextView readingSection;
    private TextView layoutSection;
    private TextView infoSection;
    private String currentMessage = MESSAGE_NONE;
    private float accumulatedPinchScale = 1f;
    private float temporaryPinchScale = 1f;
    private int pendingScrollRestoreY = -1;
    private FontSize pinchStartFontSize = FontSize.defaultSize();
    private boolean trackingEdgeSwipe;
    private float edgeSwipeStartX;
    private float menuSwipeStartX;

    private FeatureEntitlement loadFeatureEntitlement(ProPurchaseCacheStore purchaseCacheStore) {
        return FeatureEntitlements.current(CompositeEntitlementSource.anyPro(
                BuildEntitlementSource.current(),
                new CachedProPurchaseEntitlementSource(purchaseCacheStore)));
    }

    void reloadFeatureEntitlement() {
        featureEntitlement = loadFeatureEntitlement(purchaseCacheStore);
        codeHighlighting = CodeHighlightingPolicy.fromEntitlement(featureEntitlement);
        mermaidRendering = MermaidRenderingPolicy.fromEntitlement(featureEntitlement);
        relativeLinkRendering = RelativeLinkRenderingPolicy.fromEntitlement(featureEntitlement);
        relativeImageRendering = RelativeImageRenderingPolicy.fromEntitlement(featureEntitlement);
        reclampCurrentThemeForEntitlement();
    }

    private void reclampCurrentThemeForEntitlement() {
        ViewerTheme clampedTheme = currentTheme.clampedForEntitlement(featureEntitlement);
        if (clampedTheme.storedValue().equals(currentTheme.storedValue())) {
            return;
        }
        currentTheme = clampedTheme;
        viewerPalette = ViewerPalette.from(currentTheme);
        if (root == null || openTabs == null || webView == null) {
            return;
        }
        updateLocalizedText();
        applyNativeTheme();
        rerenderMermaidDiagramsForCurrentTheme();
        renderTabs();
        renderCurrentDocument();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        circleGestureTrace = new CircleGestureTrace(getResources().getDisplayMetrics().density);
        purchaseCacheStore = new ProPurchaseCacheStore(new SharedPreferencesProPurchaseCacheStorage(this));
        proPurchaseStatusRefresh = BuildProPurchaseStatusRefresh.current(
                purchaseCacheStore,
                AndroidProPurchaseStatusProviderFactory.current(this));
        proPurchaseFlow = AndroidProPurchaseFlowFactory.current(
                this,
                proPurchaseStatusRefresh,
                new PurchaseStatusUiCallback(this));
        proPurchaseStatusRefresh.refreshAt(System.currentTimeMillis(), new PurchaseStatusUiCallback(this));
        reloadFeatureEntitlement();

        tabPersistence = new TabPersistence(this, RecentDocumentLimit.fromEntitlement(featureEntitlement));
        documentOpener = new DocumentOpener(this);
        documentSaver = new DocumentSaver(this);
        documentListDialogs = new DocumentListDialogController(this);
        htmlDocumentExporter = new HtmlDocumentExporter(this);
        documentPrintLauncher = new AndroidDocumentPrintLauncher(this);
        clipboardDocumentCreator = new ClipboardDocumentCreator(this);
        settingsDialogs = new SettingsDialogs(this);
        gestureShortcutDialogs = new GestureShortcutDialogs(this);
        gestureShortcutHandler = new GestureShortcutHandler(this);
        documentSearchBar = new DocumentSearchBar(this);

        appRoot = new EdgeSwipeFrameLayout(this);
        settingsStore = new ViewerSettingsStore(this, featureEntitlement);
        clipboardHistoryStore = new ClipboardHistoryStore(this);
        controlsPlacement = settingsStore.loadControlsPlacement();
        currentLanguage = settingsStore.loadViewerLanguage();
        viewerText = ViewerText.fromLanguage(currentLanguage);
        currentTheme = settingsStore.loadViewerTheme();
        viewerPalette = ViewerPalette.from(currentTheme);
        gestureShortcutBindings = settingsStore.loadGestureShortcutBindings();

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        initTopBar();
        initMenuButtons();
        initMenuPanel();
        initMessageAndTabs();
        initWebView();

        fontScaleGestureDetector = new ScaleGestureDetector(this, new FontScaleGestureListener(this));
        shortcutGestureDetector = new GestureDetector(this, new ShortcutGestureListener(this));
        webView.setOnTouchListener(new ViewerTouchListener(this));
        updateLocalizedText();
        openTabs = restoreOpenTabsOrInitial();
        documentTabSessionController = new DocumentTabSessionController(this);
        applyNativeTheme();
        renderTabs();
        renderCurrentDocument();
        applyControlsPlacement();

        appRoot.setOnApplyWindowInsetsListener(this);
        appRoot.addView(root, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        menuScrim = new View(this);
        menuScrim.setBackgroundColor(0x66000000);
        menuScrim.setAlpha(0f);
        menuScrim.setVisibility(View.GONE);
        menuScrim.setOnClickListener(this);
        appRoot.addView(menuScrim, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        FrameLayout.LayoutParams menuParams = new FrameLayout.LayoutParams(
                dp(MENU_WIDTH_DP), FrameLayout.LayoutParams.MATCH_PARENT);
        menuParams.gravity = Gravity.START;
        appRoot.addView(menuScrollContainer, menuParams);
        menuTransitions = new MenuTransitions(menuScrollContainer, menuScrim);
        setContentView(appRoot);
        if (savedInstanceState == null) {
            documentOpener.handleIncomingIntent(getIntent());
        }
    }

    private void initTopBar() {
        topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(dp(14), dp(10), dp(14), dp(10));
        topBar.setBackgroundColor(backgroundColor());

        menuButton = new Button(this);
        menuButton.setText("Menu");
        menuButton.setContentDescription("Open menu");
        menuButton.setAllCaps(false);
        menuButton.setOnClickListener(this);
        styleToolbarButton(menuButton);
        topBar.addView(menuButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        appTitle = new TextView(this);
        appTitle.setText("LocalMD Reader");
        appTitle.setTextColor(textColor());
        appTitle.setTextSize(17);
        appTitle.setTypeface(Typeface.DEFAULT_BOLD);
        appTitle.setGravity(Gravity.CENTER_VERTICAL);
        appTitle.setSingleLine(true);
        appTitle.setEllipsize(TextUtils.TruncateAt.END);
        appTitle.setPadding(dp(14), 0, 0, 0);
        topBar.addView(appTitle, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
    }

    private void initMenuButtons() {
        openButton = menuActionButton(MainMenuActions.openFile());
        openFolderButton = menuActionButton(MainMenuActions.openFolder());
        createFromClipboardButton = menuActionButton(MainMenuActions.createFromClipboard());
        saveAsButton = menuActionButton(MainMenuActions.saveAs());
        exportAsHtmlButton = menuActionButton(MainMenuActions.exportAsHtml());
        printOrSavePdfButton = menuActionButton(PrintDocumentMenuActions.printOrSavePdf());
        pinCurrentFileButton = menuActionButton(MainMenuActions.pinCurrentFile());
        unpinCurrentFileButton = menuActionButton(MainMenuActions.unpinCurrentFile());
        pinnedFilesButton = menuActionButton(MainMenuActions.pinnedFiles());
        recentButton = menuActionButton(MainMenuActions.recentFiles());
        settingsButton = menuActionButton(MainMenuActions.settings());
        tableOfContentsButton = menuActionButton(MainMenuActions.tableOfContents());
        findInDocumentButton = menuActionButton(MainMenuActions.findInDocument());
        themeButton = menuActionButton(MainMenuActions.theme());
        languageButton = menuActionButton(MainMenuActions.language());
        controlsPlacementButton = menuActionButton(MainMenuActions.controlsPlacement());
        gestureShortcutsButton = menuActionButton(MainMenuActions.gestureShortcuts());
        proFeaturesButton = menuActionButton(MainMenuActions.proFeatures());
        clipboardDiagnosticsButton = menuActionButton(MainMenuActions.clipboardDiagnostics());
        privacyButton = menuActionButton(MainMenuActions.privacy());
        menuActionButtons = new MainMenuActionButton[] {
            openButton, openFolderButton, createFromClipboardButton, saveAsButton, exportAsHtmlButton,
            printOrSavePdfButton, pinCurrentFileButton,
            unpinCurrentFileButton, pinnedFilesButton, recentButton,
            tableOfContentsButton, findInDocumentButton, settingsButton, themeButton, languageButton,
            controlsPlacementButton, gestureShortcutsButton, proFeaturesButton,
            clipboardDiagnosticsButton, privacyButton
        };
    }

    private void initMenuPanel() {
        menuScrollContainer = new SwipeMenuScrollView(this);
        menuScrollContainer.setVisibility(View.GONE);
        menuScrollContainer.setBackgroundColor(backgroundColor());
        menuScrollContainer.setClickable(true);

        menuPanel = new SwipeMenuLayout(this);
        menuPanel.setOrientation(LinearLayout.VERTICAL);
        menuPanel.setVisibility(View.VISIBLE);
        menuPanel.setBackgroundColor(backgroundColor());
        menuPanel.setPadding(dp(18), dp(28), dp(18), dp(18));
        menuPanel.setClickable(true);
        menuScrollContainer.addView(menuPanel, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));

        menuTitle = new TextView(this);
        menuTitle.setTextColor(textColor());
        menuTitle.setTextSize(22);
        menuTitle.setTypeface(Typeface.DEFAULT_BOLD);
        menuTitle.setGravity(Gravity.CENTER_VERTICAL);
        menuTitle.setPadding(dp(8), 0, dp(8), dp(18));
        menuPanel.addView(menuTitle, wrapParams());
        filesSection = menuSection("");
        markdownLibraryMenuTree = new MarkdownLibraryMenuTree(this);
        addMenuCard(menuPanel, filesSection, openButton,
                openFolderButton, markdownLibraryMenuTree, createFromClipboardButton, saveAsButton,
                exportAsHtmlButton, printOrSavePdfButton, pinCurrentFileButton, unpinCurrentFileButton,
                pinnedFilesButton, recentButton);
        readingSection = menuSection("");
        addMenuCard(menuPanel, readingSection, tableOfContentsButton, findInDocumentButton);
        tableOfContentsPanel = new TableOfContentsMenuPanel(this);
        menuPanel.addView(tableOfContentsPanel, wrapParams());
        tableOfContentsMenuSection = new ExpandableMenuSection(
                tableOfContentsPanel, tableOfContentsPanel);
        addMenuCard(menuPanel, null, settingsButton);

        settingsPanel = new LinearLayout(this);
        settingsPanel.setOrientation(LinearLayout.VERTICAL);
        settingsPanel.setVisibility(View.GONE);
        settingsPanel.setPadding(dp(10), 0, 0, 0);
        menuPanel.addView(settingsPanel, wrapParams());
        settingsMenuSection = ExpandableMenuSection.staticContent(settingsPanel);
        layoutSection = menuSection("");
        addMenuCard(settingsPanel, layoutSection, themeButton, languageButton,
                controlsPlacementButton, gestureShortcutsButton);
        infoSection = menuSection("");
        addMenuCard(settingsPanel, infoSection, proFeaturesButton, clipboardDiagnosticsButton, privacyButton);
    }

    /**
     * Grouped-list pattern (#77): one rounded tonal card per menu section, rows
     * inside stay flat with their own ripple. Cards reduce the rounded-corner
     * count from one-per-button to one-per-section and avoid the scalloped
     * seams of adjacent rounded buttons.
     */
    private void addMenuCard(LinearLayout parent, TextView sectionLabel, View... items) {
        if (sectionLabel != null) {
            parent.addView(sectionLabel, wrapParams());
        }
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        addMenuItems(card, items);
        menuCards.add(card);
        styleMenuCard(card);
        LinearLayout.LayoutParams params = wrapParams();
        params.bottomMargin = dp(10);
        parent.addView(card, params);
    }

    private void styleMenuCard(LinearLayout card) {
        card.setBackground(makePlainTonalBackground(surfaceAltColor(), 12));
        card.setClipToOutline(true);
    }

    private void initMessageAndTabs() {
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
        controlsBar.addView(topBar, wrapParams());
        controlsBar.addView(tabScroller, wrapParams());
        controlsBar.addView(documentSearchBar, wrapParams());
    }

    private void initWebView() {
        webView = new WebView(this);
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
        if (mermaidRendering.isEnabled()) {
            mermaidRenderEngine = new MermaidJsRenderEngine(this, this);
        }
    }

    private static void addMenuItems(LinearLayout parent, View... views) {
        LinearLayout.LayoutParams params = wrapParams();
        for (View v : views) {
            parent.addView(v, params);
        }
    }

    private static LinearLayout.LayoutParams wrapParams() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View view) {
        if (view == menuButton) {
            updateLocalizedText();
            toggleMenu();
        } else if (view == menuScrim) {
            closeMenu();
        } else if (view instanceof MainMenuActionButton) {
            ((MainMenuActionButton) view).perform(this);
        } else if (view instanceof TableOfContentsItemButton) {
            jumpToHeading(((TableOfContentsItemButton) view).heading());
            closeMenu();
        } else if (view instanceof TabButton) {
            documentTabSessionController.activate(((TabButton) view).tabIndex());
        } else if (view instanceof CloseTabText) {
            documentTabSessionController.close(((CloseTabText) view).tabIndex());
        } else if (view == purchaseProButton) {
            settingsDialogs.startProPurchase();
        } else if (view == restorePurchaseButton) {
            settingsDialogs.restoreProPurchase();
        }
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
        documentOpener.handleIncomingIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_DOCUMENT && resultCode == RESULT_OK && data != null) {
            documentOpener.openSelectedDocuments(data);
            return;
        }
        if (requestCode == REQUEST_OPEN_FOLDER && resultCode == RESULT_OK && data != null) {
            documentOpener.openSelectedFolder(data);
            return;
        }
        if (requestCode == REQUEST_SAVE_DOCUMENT && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                documentSaver.writePendingMarkdown(uri);
            }
            return;
        }
        if (requestCode == REQUEST_EXPORT_HTML && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                htmlDocumentExporter.writePendingHtml(uri);
            }
        }
    }

    @Override
    public void onCustomGestureDrawn(float[] xs, float[] ys) {
        gestureShortcutDialogs.onCustomGestureDrawn(xs, ys);
    }

    @Override
    public void onMermaidRendered(String documentUri, int diagramIndex, SafeHtml svg) {
        pendingMermaidRenderJobs.remove(mermaidJobKey(documentUri, diagramIndex));
        Map<Integer, SafeHtml> rendered = mermaidSvgByUri.get(documentUri);
        if (rendered == null) {
            rendered = new HashMap<Integer, SafeHtml>();
            mermaidSvgByUri.put(documentUri, rendered);
        }
        rendered.put(Integer.valueOf(diagramIndex), svg);
        refreshRenderedTab(documentUri);
    }

    @Override
    public void onMermaidRenderFailed(String documentUri, int diagramIndex, String reason) {
        pendingMermaidRenderJobs.remove(mermaidJobKey(documentUri, diagramIndex));
        Map<Integer, SafeHtml> rendered = mermaidSvgByUri.get(documentUri);
        if (rendered == null) {
            rendered = new HashMap<Integer, SafeHtml>();
            mermaidSvgByUri.put(documentUri, rendered);
        }
        MermaidDiagramBlocks blocks = mermaidBlocksByUri.get(documentUri);
        SafeHtml errorHtml;
        if (blocks == null || diagramIndex < 0 || diagramIndex >= blocks.items().length) {
            errorHtml = SafeHtml.fromTrustedRendererOutput(
                    "<div class=\"mermaid-error\"><strong>Unable to render this Mermaid diagram.</strong></div>");
        } else {
            errorHtml = MermaidRenderErrorHtml.from(blocks.items()[diagramIndex], reason);
        }
        rendered.put(Integer.valueOf(diagramIndex), errorHtml);
        refreshRenderedTab(documentUri);
    }

    void switchLanguage() {
        currentLanguage = currentLanguage.toggled();
        viewerText = ViewerText.fromLanguage(currentLanguage);
        settingsStore.saveViewerLanguage(currentLanguage);
        updateLocalizedText();
        if (WELCOME_URI.equals(openTabs.activeTab().uri())) {
            openTabs = OpenDocumentTabs.withInitialTab(initialTab());
            renderTabs();
            renderCurrentDocument();
        }
    }

    void toggleControlsPlacement() {
        controlsPlacement = controlsPlacement.toggled();
        settingsStore.saveControlsPlacement(controlsPlacement);
        updateLocalizedText();
        applyControlsPlacement();
    }

    void applySelectedTheme(ViewerTheme theme) {
        currentTheme = theme;
        settingsStore.saveViewerTheme(currentTheme);
        viewerPalette = ViewerPalette.from(currentTheme);
        updateLocalizedText();
        applyNativeTheme();
        rerenderMermaidDiagramsForCurrentTheme();
        renderTabs();
        renderCurrentDocument();
    }

    void updateLocalizedText() {
        menuButton.setText(viewerText.menuButton());
        menuButton.setContentDescription(viewerText.openMenuDescription());
        appTitle.setText("LocalMD Reader");
        refreshMenuActionButtons();
        menuTitle.setText("LocalMD Reader");
        filesSection.setText(viewerText.filesSection());
        readingSection.setText(viewerText.settings());
        layoutSection.setText(viewerText.appearanceSettings());
        infoSection.setText(viewerText.infoSection());
        updateLocalizedMessage();
    }

    void renderTabs() {
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
            button.setTextColor(i == openTabs.activeIndex() ? onPrimaryColor() : textColor());
            button.setPadding(dp(16), dp(8), dp(16), dp(8));
            // Pill tabs (#77): the active tab is a filled primary pill, inactive
            // tabs are borderless tonal pills, so selection reads from fill
            // contrast instead of a 1px border.
            button.setBackground(makeTonalBackground(
                    i == openTabs.activeIndex() ? primaryColor() : surfaceAltColor(),
                    PILL_RADIUS_DP));
            tabGroup.addView(button, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            if (!WELCOME_URI.equals(tab.uri())) {
                CloseTabText closeText = new CloseTabText(this, i);
                closeText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        themedIcon(R.drawable.ic_close_20, mutedColor()), null, null, null);
                closeText.setGravity(Gravity.CENTER);
                closeText.setPadding(dp(6), 0, dp(14), 0);
                closeText.setContentDescription(viewerText.closeTabDescription(tab.title()));
                closeText.setOnClickListener(this);
                tabGroup.addView(closeText, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            }
            tabRow.addView(tabGroup, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        tabRow.post(new CloseTabTouchTargets(tabRow, dp(48)));
        tabScroller.post(new ScrollToActiveTab(tabScroller, tabRow, openTabs.activeIndex()));
    }

    void renderCurrentDocument() {
        renderCurrentDocument(null);
    }

    void renderCurrentDocument(String anchorId) {
        documentSearchSession = documentSearchSession.clear();
        if (documentSearchBar != null) {
            documentSearchBar.syncFromSession();
        }
        String historyUrl = anchorId == null ? null : "https://localmd.local/#" + anchorId;
        webView.loadDataWithBaseURL("https://localmd.local/",
                HtmlPageBuilder.buildPage(
                        openTabs.activeTab().document(),
                        currentTheme,
                        currentFontSize,
                        TableReadingMode.fromEntitlement(featureEntitlement)),
                "text/html", "UTF-8", historyUrl);
        renderedFontSize = currentFontSize;
        webView.getSettings().setTextZoom(100);
    }

    WebResourceResponse openActiveRelativeImage(String requestUrl) {
        if (relativeImageRendering == null || !relativeImageRendering.isEnabled()) {
            return null;
        }
        if (openTabs == null || !(openTabs.activeTab() instanceof OpenDocumentTab.FileDocumentTab)) {
            return null;
        }
        LocalRelativeImageResource resource =
                LocalRelativeImageResource.resolve(
                        openTabs.activeTab().uri(),
                        requestUrl,
                        allowedRelativeImageRoot(openTabs.activeTab().uri()));
        if (!resource.isAvailable()) {
            return null;
        }
        try {
            return new WebResourceResponse(
                    resource.mimeType(),
                    null,
                    new FileInputStream(resource.filePath()));
        } catch (IOException e) {
            return null;
        }
    }

    boolean openActiveRelativeMarkdownLink(String requestUrl) {
        if (relativeLinkRendering == null || !relativeLinkRendering.isEnabled()) {
            return false;
        }
        if (openTabs == null || !(openTabs.activeTab() instanceof OpenDocumentTab.FileDocumentTab)) {
            return false;
        }
        LocalRelativeMarkdownLink link = LocalRelativeMarkdownLink.resolve(
                openTabs.activeTab().uri(),
                requestUrl,
                allowedRelativeDocumentRoot(openTabs.activeTab().uri()));
        if (!link.isAvailable()) {
            return false;
        }
        documentOpener.openUri(Uri.fromFile(new File(link.filePath())), true, link.targetAnchorId());
        return true;
    }

    private String allowedRelativeImageRoot(String markdownDocumentUri) {
        return allowedRelativeDocumentRoot(markdownDocumentUri);
    }

    private String allowedRelativeDocumentRoot(String markdownDocumentUri) {
        try {
            URI document = URI.create(markdownDocumentUri);
            if (!"file".equals(document.getScheme())) {
                return "";
            }
            File documentFile = new File(document.getPath() == null ? "" : document.getPath());
            File documentDirectory = documentFile.getParentFile();
            File documentSetRoot = documentDirectory == null ? null : documentDirectory.getParentFile();
            return documentSetRoot == null ? "" : documentSetRoot.getAbsolutePath();
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    boolean tableOfContentsAvailable() {
        return featureEntitlement.allows(ViewerFeature.TABLE_OF_CONTENTS);
    }

    void showFindInDocumentDialog() {
        documentSearchBar.showBar();
    }

    void showFindInDocumentBar() {
        documentSearchBar.showBar();
    }

    void findTextInDocument(String query) {
        webView.findAllAsync(query);
    }

    void searchTextInDocument(DocumentSearchQuery query) {
        documentSearchSession = documentSearchSession.search(query);
        if (documentSearchSession.hasActiveQuery()) {
            findTextInDocument(documentSearchSession.queryText());
        }
    }

    boolean hasActiveDocumentSearch() {
        return documentSearchSession.hasActiveQuery();
    }

    String currentSearchQueryText() {
        return documentSearchSession.queryText();
    }

    void findNextSearchResult() {
        if (documentSearchSession.hasActiveQuery()) {
            webView.findNext(true);
        }
    }

    void findPreviousSearchResult() {
        if (documentSearchSession.hasActiveQuery()) {
            webView.findNext(false);
        }
    }

    void clearWebViewSearch() {
        webView.clearMatches();
    }

    MarkdownHeadings activeMarkdownHeadings() {
        if (openTabs == null) {
            return MarkdownHeadings.fromMarkdown("");
        }
        String markdown = markdownSourceByUri.get(openTabs.activeTab().uri());
        return MarkdownHeadings.fromMarkdown(markdown);
    }

    void jumpToHeading(MarkdownHeading heading) {
        HeadingNavigation.selected(
                TableOfContentsItems.from(activeMarkdownHeadings()),
                heading).handle(this);
    }

    void jumpToNextHeading() {
        activeHeadingNavigation().next().handle(this);
    }

    void jumpToPreviousHeading() {
        activeHeadingNavigation().previous().handle(this);
    }

    private HeadingNavigation activeHeadingNavigation() {
        return HeadingNavigation.from(
                TableOfContentsItems.from(activeMarkdownHeadings()),
                HeadingScrollPosition.fromWebViewMetrics(
                        webView.getScrollY(),
                        webView.getContentHeight(),
                        webView.getHeight(),
                        webView.getScale()));
    }

    @Override
    public void unavailable() {
    }

    @Override
    public void destination(MarkdownHeading heading) {
        renderCurrentDocument(heading.anchorId());
    }

    SafeHtml renderMarkdownForUri(String documentUri, String markdown) {
        markdownSourceByUri.put(documentUri, markdown == null ? "" : markdown);
        MermaidDiagramBlocks blocks = MermaidDiagramBlocks.fromMarkdown(markdown);
        mermaidBlocksByUri.put(documentUri, blocks);
        enqueueMermaidRenderJobs(documentUri, blocks);
        return renderer.render(
                markdown,
                codeHighlighting,
                mermaidRendering,
                relativeLinkRendering,
                relativeImageRendering,
                mermaidSvgByUri.get(documentUri));
    }

    void saveOpenTabs() {
        tabPersistence.saveOpenTabs(openTabs);
    }

    void clearMessage() {
        currentMessage = MESSAGE_NONE;
        updateLocalizedMessage();
    }

    void showSavedMarkdownMessage() {
        currentMessage = MESSAGE_SAVED_MARKDOWN;
        showMessage(viewerText.savedMarkdown());
    }

    void showFileOpenError(String message) {
        new AlertDialog.Builder(this)
                .setTitle(viewerText.openMarkdownFile())
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    void showInfoDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    void openMenu() {
        tableOfContentsMenuSection.refreshExpandedContent();
        menuTransitions.open();
        menuButton.setContentDescription("Close menu");
    }

    void closeMenu() {
        menuTransitions.close();
        menuButton.setContentDescription("Open menu");
    }

    // interaction-surface: settings-collapsed-row
    // interaction-surface: settings-inline-section
    // interaction-command: expand_settings_section
    // interaction-command: collapse_settings_section
    void toggleSettingsPanel() {
        settingsMenuSection.toggle();
        refreshMenuActionButtons();
    }

    // interaction-surface: table-of-contents-collapsed-row
    // interaction-surface: table-of-contents-inline-section
    // interaction-command: expand_table_of_contents
    // interaction-command: collapse_table_of_contents
    void toggleTableOfContentsPanel() {
        tableOfContentsMenuSection.toggle();
        refreshMenuActionButtons();
    }

    void toggleMarkdownLibraryTree() {
        if (markdownLibraryMenuTree.toggleLoadedTree()) {
            refreshMenuActionButtons();
            return;
        }
        openMarkdownLibrary();
    }

    void showGestureShortcutsDialog() {
        gestureShortcutDialogs.showGestureShortcutsDialog();
    }

    void showThemeDialog() {
        settingsDialogs.showThemeDialog();
    }

    void showPrivacyPolicyDialog() {
        settingsDialogs.showPrivacyPolicyDialog();
    }

    void showProFeaturesDialog() {
        settingsDialogs.showProFeaturesDialog();
    }

    void showClipboardDiagnosticsDialog() {
        settingsDialogs.showClipboardDiagnosticsDialog();
    }

    void openMarkdownPicker() {
        documentOpener.openMarkdownPicker();
    }

    void openMarkdownLibrary() {
        documentOpener.openMarkdownLibrary();
    }

    void chooseAnotherFolder() {
        documentOpener.chooseAnotherFolder();
    }

    void createMarkdownFromClipboard() {
        clipboardDocumentCreator.createMarkdownFromClipboard();
    }

    void saveActiveMarkdownAs() {
        documentSaver.saveActiveMarkdownAs();
    }

    void exportActiveDocumentAsHtml() {
        if (!documentOutputAvailable()) {
            return;
        }
        htmlDocumentExporter.exportActiveDocument();
    }

    void printActiveDocument() {
        if (!documentOutputAvailable()) {
            return;
        }
        documentPrintLauncher.print(webView, openTabs.activeTab().title());
    }

    boolean documentOutputAvailable() {
        return openTabs != null
                && openTabs.activeTab() instanceof OpenDocumentTab.UserDocumentTab
                && featureEntitlement.allows(ViewerFeature.EXPORT_OPTIONS);
    }

    void showRecentDocuments() {
        documentListDialogs.showRecentDocuments();
    }

    void showFolderDocuments(io.github.yosk.mdlite.file.FolderMarkdownDocuments documents) {
        documentListDialogs.showFolderDocuments(documents);
    }

    void showProjectLibrary(io.github.yosk.mdlite.file.MarkdownLibraryLocation location,
            io.github.yosk.mdlite.file.MarkdownLibraryListing listing) {
        markdownLibraryMenuTree.show(location, listing);
        refreshMenuActionButtons();
        openMenu();
    }

    boolean activeTabIsDraft() {
        return openTabs != null && openTabs.activeTab() instanceof OpenDocumentTab.DraftDocumentTab;
    }

    boolean activeTabIsFile() {
        return openTabs != null && openTabs.activeTab() instanceof OpenDocumentTab.FileDocumentTab;
    }

    boolean pinnedDocumentsAvailable() {
        return featureEntitlement.allows(ViewerFeature.EXTENDED_RECENT_FILES);
    }

    boolean activeFileIsPinned() {
        return activeTabIsFile() && tabPersistence.isPinnedDocument(openTabs.activeTab().uri());
    }

    void pinCurrentDocument() {
        OpenDocumentTab tab = openTabs.activeTab();
        tabPersistence.pinDocument(tab.title(), tab.uri());
        showInfoDialog(viewerText.pinnedFiles(), viewerText.currentFilePinned());
    }

    void unpinCurrentDocument() {
        OpenDocumentTab tab = openTabs.activeTab();
        tabPersistence.unpinDocument(tab.uri());
        showInfoDialog(viewerText.pinnedFiles(), viewerText.currentFileUnpinned());
    }

    void showPinnedDocuments() {
        documentListDialogs.showPinnedDocuments();
    }

    String recentFilesTitle() {
        return viewerText.recentFiles();
    }

    ViewerText viewerText() {
        return viewerText;
    }

    ViewerTheme currentTheme() {
        return currentTheme;
    }

    ControlsPlacement controlsPlacement() {
        return controlsPlacement;
    }

    boolean customGestureShortcutsAvailable() {
        return featureEntitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS);
    }

    boolean handleViewerTouch(MotionEvent event) {
        return gestureShortcutHandler.handleViewerTouch(event);
    }

    boolean handleDoubleTapShortcut() {
        return gestureShortcutHandler.handleDoubleTapShortcut();
    }

    boolean handleEdgeSwipe(MotionEvent event) {
        if (isMenuOpen()) {
            if (event.getX() > dp(MENU_WIDTH_DP)) {
                if (event.getAction() == MotionEvent.ACTION_UP) { closeMenu(); }
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
        if (!trackingEdgeSwipe) { return false; }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float distance = event.getX() - edgeSwipeStartX;
            trackingEdgeSwipe = false;
            if (distance >= dp(MENU_SWIPE_MIN_DISTANCE_DP)) { openMenu(); return true; }
        }
        if (event.getAction() == MotionEvent.ACTION_CANCEL) { trackingEdgeSwipe = false; }
        return true;
    }

    boolean handleMenuSwipe(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            menuSwipeStartX = event.getX();
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() - menuSwipeStartX <= -dp(MENU_SWIPE_MIN_DISTANCE_DP)) {
                closeMenu();
                return true;
            }
        }
        return false;
    }

    int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    /** Vector icons replace the old text glyphs (#73); tinted per theme. */
    Drawable themedIcon(int resId, int color) {
        Drawable icon = getDrawable(resId).mutate();
        icon.setTint(color);
        return icon;
    }

    void applyExpandChevron(TextView button, boolean expanded) {
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                themedIcon(expanded ? R.drawable.ic_expand_less_18 : R.drawable.ic_expand_more_18,
                        textColor()), null);
        button.setCompoundDrawablePadding(dp(6));
    }

    /**
     * Rounded button background with ripple touch feedback (#75). The ripple
     * wraps the unchanged rounded surface and is masked to the same rounded
     * shape; its color derives from the theme primary at low alpha.
     */
    Drawable makeRoundedBackground(int fillColor, int strokeColor, int radiusDp) {
        return withRipple(makePlainRoundedBackground(fillColor, strokeColor, radiusDp), radiusDp);
    }

    /**
     * Borderless tonal surface (#77): list-like buttons inside panels separate
     * from the page by fill contrast alone, without the 1px border noise.
     */
    Drawable makeTonalBackground(int fillColor, int radiusDp) {
        return withRipple(makePlainTonalBackground(fillColor, radiusDp), radiusDp);
    }

    /** Non-interactive tonal container (section cards must not ripple). */
    GradientDrawable makePlainTonalBackground(int fillColor, int radiusDp) {
        GradientDrawable surface = new GradientDrawable();
        surface.setColor(fillColor);
        surface.setCornerRadius(dp(radiusDp));
        return surface;
    }

    /** Transparent row inside a card: ripple feedback without its own surface. */
    Drawable makeRowRippleBackground() {
        GradientDrawable mask = new GradientDrawable();
        mask.setColor(0xffffffff);
        return new RippleDrawable(ColorStateList.valueOf(rippleColor()), null, mask);
    }

    /** Plain variant for non-interactive surfaces (text fields must not ripple). */
    GradientDrawable makePlainRoundedBackground(int fillColor, int strokeColor, int radiusDp) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(fillColor);
        background.setCornerRadius(dp(radiusDp));
        background.setStroke(1, strokeColor);
        return background;
    }

    private Drawable withRipple(GradientDrawable content, int radiusDp) {
        GradientDrawable mask = new GradientDrawable();
        mask.setColor(0xffffffff);
        mask.setCornerRadius(dp(radiusDp));
        return new RippleDrawable(ColorStateList.valueOf(rippleColor()), content, mask);
    }

    int rippleColor() {
        return (primaryColor() & 0x00ffffff) | 0x33000000;
    }

    int backgroundColor() { return viewerPalette.background; }
    int surfaceColor() { return viewerPalette.surface; }
    int surfaceAltColor() { return viewerPalette.surfaceAlt; }
    int textColor() { return viewerPalette.text; }
    int mutedColor() { return viewerPalette.muted; }
    int primaryColor() { return viewerPalette.primary; }
    int primaryStrongColor() { return viewerPalette.primaryStrong; }
    int onPrimaryColor() { return viewerPalette.onPrimary; }
    int borderColor() { return viewerPalette.border; }
    private int messageColor() { return viewerPalette.message; }

    void changeFontSizeByPinch(float scaleFactor) {
        if (!FontSize.canApplyPinchScale(scaleFactor)) {
            return;
        }
        accumulatedPinchScale *= scaleFactor;
        temporaryPinchScale = clampedTemporaryPinchScale(temporaryPinchScale * scaleFactor);
        FontSize changed = pinchStartFontSize.changedByPinchScale(accumulatedPinchScale);
        if (changed.sp() != currentFontSize.sp()) { currentFontSize = changed; }
        int zoomPercent = Math.round((pinchStartFontSize.sp() * temporaryPinchScale * 100f) / renderedFontSize.sp());
        webView.getSettings().setTextZoom(zoomPercent);
    }

    void beginFontSizePinch() {
        accumulatedPinchScale = 1f;
        temporaryPinchScale = 1f;
        pinchStartFontSize = currentFontSize;
    }

    void finishFontSizePinch() {
        accumulatedPinchScale = 1f;
        temporaryPinchScale = 1f;
        if (currentFontSize.sp() != renderedFontSize.sp()) {
            pendingScrollRestoreY = restoredScrollYForFontChange(renderedFontSize, currentFontSize);
            renderCurrentDocument();
        } else {
            webView.getSettings().setTextZoom(100);
        }
    }

    void applyNativeTheme() {
        SystemBarsTheme.apply(getWindow(), viewerPalette);
        root.setBackgroundColor(backgroundColor());
        topBar.setBackgroundColor(backgroundColor());
        tabScroller.setBackgroundColor(backgroundColor());
        menuScrollContainer.setBackgroundColor(backgroundColor());
        menuPanel.setBackgroundColor(backgroundColor());
        appTitle.setTextColor(textColor());
        menuTitle.setTextColor(textColor());
        messageView.setTextColor(textColor());
        messageView.setBackgroundColor(messageColor());
        styleToolbarButton(menuButton);
        for (int i = 0; i < menuActionButtons.length; i++) { styleMenuButton(menuActionButtons[i]); }
        for (int i = 0; i < menuCards.size(); i++) { styleMenuCard(menuCards.get(i)); }
        markdownLibraryMenuTree.refreshStyle();
        tableOfContentsPanel.refreshStyle();
        documentSearchBar.refreshStyle();
        int sc = primaryStrongColor();
        filesSection.setTextColor(sc);
        readingSection.setTextColor(sc);
        layoutSection.setTextColor(sc);
        infoSection.setTextColor(sc);
    }

    void applyControlsPlacement() {
        root.removeAllViews();
        applyControlsBarInsets();
        if (controlsPlacement.isBottom()) {
            root.addView(messageView, wrapParams());
            root.addView(webView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            root.addView(controlsBar, wrapParams());
        } else {
            root.addView(controlsBar, wrapParams());
            root.addView(messageView, wrapParams());
            root.addView(webView, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        }
    }

    void rerenderMermaidDiagramsForCurrentTheme() {
        if (!mermaidRendering.isEnabled()) { return; }
        mermaidSvgByUri.clear();
        pendingMermaidRenderJobs.clear();
        List<String> documentUris = new ArrayList<String>(mermaidBlocksByUri.keySet());
        for (int i = 0; i < documentUris.size(); i++) {
            String documentUri = documentUris.get(i);
            MermaidDiagramBlocks blocks = mermaidBlocksByUri.get(documentUri);
            if (blocks != null) {
                enqueueMermaidRenderJobs(documentUri, blocks);
                refreshRenderedTab(documentUri);
            }
        }
    }

    OpenDocumentTab initialTab() {
        return OpenDocumentTab.welcome(viewerText.welcomeTabTitle(), WELCOME_URI,
                WelcomeDocumentBuilder.build(currentLanguage));
    }

    void refreshMenuActionButtons() {
        for (int i = 0; i < menuActionButtons.length; i++) { menuActionButtons[i].refresh(this); }
        settingsButton.setText(viewerText.settings());
        tableOfContentsButton.setText(viewerText.tableOfContents());
        settingsMenuSection.refreshChevron(this, settingsButton);
        tableOfContentsMenuSection.refreshChevron(this, tableOfContentsButton);
        refreshMarkdownLibraryChevron();
        documentSearchBar.refreshText();
    }

    private void refreshMarkdownLibraryChevron() {
        FolderBrowsingAction action = FolderBrowsingMode.from(featureEntitlement).action();
        if (action.hasExpandableMenuTree()) {
            applyExpandChevron(openFolderButton, markdownLibraryMenuTree.isExpanded());
            return;
        }
        openFolderButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
    }

    private void updateLocalizedMessage() {
        if (MESSAGE_SAVED_MARKDOWN.equals(currentMessage)) {
            showMessage(viewerText.savedMarkdown());
            return;
        }
        if (openTabs != null) {
            showMessage(openTabs.activeTab().statusMessage().localized(viewerText));
            return;
        }
        showMessage("");
    }

    private void showMessage(String message) {
        messageView.setText(message);
        messageView.setVisibility(message.length() == 0 ? View.GONE : View.VISIBLE);
    }

    private void toggleMenu() {
        if (isMenuOpen()) { closeMenu(); } else { openMenu(); }
    }

    private boolean isMenuOpen() {
        return menuTransitions.isOpenTargeted();
    }

    private MainMenuActionButton menuActionButton(MainMenuAction action) {
        MainMenuActionButton button = new MainMenuActionButton(this, action);
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

    // Bold discipline (#77): bold is reserved for the app titles, the active
    // tab and section labels, so buttons and rows below use the regular face.

    private void styleToolbarButton(TextView view) {
        view.setTextColor(primaryStrongColor());
        view.setTextSize(15);
        view.setTypeface(Typeface.DEFAULT);
        view.setPadding(dp(16), dp(9), dp(16), dp(9));
        view.setBackground(makeRoundedBackground(surfaceAltColor(), borderColor(), 8));
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                themedIcon(R.drawable.ic_menu_20, primaryStrongColor()), null, null, null);
        view.setCompoundDrawablePadding(dp(8));
    }

    void styleCompactButton(TextView view) {
        view.setTextColor(primaryStrongColor());
        view.setTextSize(14);
        view.setTypeface(Typeface.DEFAULT);
        view.setMinWidth(0);
        view.setMinHeight(0);
        view.setMinimumWidth(0);
        view.setMinimumHeight(0);
        view.setPadding(dp(8), dp(4), dp(8), dp(4));
        view.setBackground(makeRoundedBackground(surfaceAltColor(), borderColor(), 8));
    }

    private void styleMenuButton(TextView view) {
        view.setTextColor(textColor());
        view.setTextSize(16);
        view.setTypeface(Typeface.DEFAULT);
        view.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        view.setPadding(dp(16), dp(12), dp(16), dp(12));
        // Rows live inside a section card (tonal surfaceAlt), so they stay
        // transparent themselves and only contribute ripple feedback.
        view.setBackground(makeRowRippleBackground());
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

    private float clampedTemporaryPinchScale(float scale) {
        float minScale = FontSize.MIN_SP / (float) pinchStartFontSize.sp();
        float maxScale = FontSize.MAX_SP / (float) pinchStartFontSize.sp();
        return Math.max(minScale, Math.min(maxScale, scale));
    }

    private int restoredScrollYForFontChange(FontSize previousFontSize, FontSize nextFontSize) {
        FontSize safePrevious = previousFontSize == null ? FontSize.defaultSize() : previousFontSize;
        FontSize safeNext = nextFontSize == null ? FontSize.defaultSize() : nextFontSize;
        float scale = safeNext.sp() / (float) safePrevious.sp();
        float viewportCenter = webView.getScrollY() + (webView.getHeight() / 2f);
        return Math.max(0, Math.round((viewportCenter * scale) - (webView.getHeight() / 2f)));
    }

    private OpenDocumentTabs restoreOpenTabsOrInitial() {
        RestorableOpenTabs storedTabs = tabPersistence.loadRestorableOpenTabs();
        if (storedTabs.isEmpty()) { return OpenDocumentTabs.withInitialTab(initialTab()); }
        ArrayList<OpenDocumentTab> restoredTabs = new ArrayList<OpenDocumentTab>();
        int restoredActiveIndex = -1;
        List<RestorableOpenTab> items = storedTabs.tabs();
        for (int i = 0; i < items.size(); i++) {
            OpenDocumentTab tab = restoreOpenTab(items.get(i));
            if (tab != null) {
                if (i == storedTabs.activeIndex()) { restoredActiveIndex = restoredTabs.size(); }
                restoredTabs.add(tab);
            }
        }
        if (restoredTabs.isEmpty()) { return OpenDocumentTabs.withInitialTab(initialTab()); }
        if (restoredActiveIndex < 0) {
            restoredActiveIndex = storedTabs.activeIndex();
            if (restoredActiveIndex >= restoredTabs.size()) { restoredActiveIndex = restoredTabs.size() - 1; }
        }
        return openTabsFrom(restoredTabs, restoredActiveIndex);
    }

    private OpenDocumentTab restoreOpenTab(RestorableOpenTab storedTab) {
        try {
            Uri uri = Uri.parse(storedTab.uri());
            FileInfo fileInfo = documentOpener.readFileInfo(uri);
            String displayName = fileInfo.displayName.length() == 0 ? storedTab.title() : fileInfo.displayName;
            MarkdownFileOpenResult openResult = MarkdownFileOpenResult.from(displayName, fileInfo.sizeBytes, fileSizePolicy);
            if (!(openResult instanceof MarkdownFileOpenResult.ReadableMarkdownFile)) { return null; }
            MarkdownFileOpenResult.ReadableMarkdownFile readableFile = (MarkdownFileOpenResult.ReadableMarkdownFile) openResult;
            String markdown = documentOpener.readText(uri, MAX_FILE_SIZE_BYTES);
            String documentUri = uri.toString();
            SafeHtml rendered = renderMarkdownForUri(documentUri, markdown);
            return OpenDocumentTab.fileDocument(readableFile.displayName(), documentUri, rendered);
        } catch (IllegalArgumentException e) { return null; }
        catch (IOException e) { return null; }
        catch (SecurityException e) { return null; }
    }

    private static OpenDocumentTabs openTabsFrom(List<OpenDocumentTab> restoredTabs, int activeIndex) {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(restoredTabs.get(0));
        for (int i = 1; i < restoredTabs.size(); i++) { tabs = tabs.open(restoredTabs.get(i)); }
        return tabs.activate(activeIndex);
    }

    private void enqueueMermaidRenderJobs(String documentUri, MermaidDiagramBlocks blocks) {
        if (!mermaidRendering.isEnabled() || mermaidRenderEngine == null || blocks.isEmpty()) { return; }
        MermaidDiagramBlock[] items = blocks.items();
        for (int i = 0; i < items.length; i++) {
            if (mermaidDiagramNeedsRendering(documentUri, i)) {
                pendingMermaidRenderJobs.add(mermaidJobKey(documentUri, i));
                mermaidRenderEngine.enqueue(documentUri, i, items[i], MermaidDiagramTheme.from(currentTheme));
            }
        }
    }

    private boolean mermaidDiagramNeedsRendering(String documentUri, int diagramIndex) {
        Map<Integer, SafeHtml> rendered = mermaidSvgByUri.get(documentUri);
        return (rendered == null || !rendered.containsKey(Integer.valueOf(diagramIndex)))
                && !pendingMermaidRenderJobs.contains(mermaidJobKey(documentUri, diagramIndex));
    }

    private static String mermaidJobKey(String documentUri, int diagramIndex) {
        return documentUri + "#" + diagramIndex;
    }

    private void refreshRenderedTab(String documentUri) {
        String markdown = markdownSourceByUri.get(documentUri);
        if (markdown == null) { return; }
        OpenDocumentTab tab = tabByUri(documentUri);
        if (tab == null) { return; }
        SafeHtml rendered = renderer.render(
                markdown,
                codeHighlighting,
                mermaidRendering,
                relativeLinkRendering,
                relativeImageRendering,
                mermaidSvgByUri.get(documentUri));
        openTabs = replaceTabDocument(documentUri, tabWithDocument(tab, rendered));
        renderTabs();
        if (openTabs.activeTab().uri().equals(documentUri)) { renderCurrentDocument(); }
    }

    private OpenDocumentTabs replaceTabDocument(String documentUri, OpenDocumentTab replacement) {
        ArrayList<OpenDocumentTab> tabs = new ArrayList<OpenDocumentTab>(openTabs.tabs());
        int activeIndex = openTabs.activeIndex();
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).uri().equals(documentUri)) {
                tabs.set(i, replacement);
                return openTabsFrom(tabs, activeIndex);
            }
        }
        return openTabs;
    }

    private OpenDocumentTab tabByUri(String documentUri) {
        List<OpenDocumentTab> tabs = openTabs.tabs();
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).uri().equals(documentUri)) { return tabs.get(i); }
        }
        return null;
    }

    private static OpenDocumentTab tabWithDocument(OpenDocumentTab tab, SafeHtml rendered) {
        if (tab instanceof OpenDocumentTab.ClipboardDraftTab) {
            return OpenDocumentTab.clipboardDraft(tab.title(), tab.uri(), rendered);
        }
        if (tab instanceof OpenDocumentTab.SelectedTextDraftTab) {
            return OpenDocumentTab.selectedTextDraft(tab.title(), tab.uri(), rendered);
        }
        if (tab instanceof OpenDocumentTab.WelcomeTab) {
            return OpenDocumentTab.welcome(tab.title(), tab.uri(), rendered);
        }
        return OpenDocumentTab.fileDocument(tab.title(), tab.uri(), rendered);
    }

    private void restorePendingScrollAfterPageLoad() {
        if (pendingScrollRestoreY < 0) { return; }
        int scrollY = pendingScrollRestoreY;
        pendingScrollRestoreY = -1;
        webView.post(new RestoreScrollPosition(webView, scrollY, 6));
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

        private boolean handleUrlLoading(WebView view, String url) {
            if (WelcomeDocumentBuilder.OPEN_MARKDOWN_URL.equals(url)) {
                activity.openMarkdownPicker();
                return true;
            }
            if (url == null) { return true; }
            if (activity.openActiveRelativeMarkdownLink(url)) {
                return true;
            }
            String lower = url.toLowerCase();
            if (lower.startsWith("https://localmd.local/")) {
                return true;
            }
            if (lower.startsWith("https://") || lower.startsWith("http://")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
            }
            return true;
        }
    }
}
