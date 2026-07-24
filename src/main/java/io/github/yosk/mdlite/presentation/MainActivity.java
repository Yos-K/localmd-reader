package io.github.yosk.mdlite.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import io.github.yosk.mdlite.R;
import io.github.yosk.mdlite.domain.CompositeEntitlementSource;
import io.github.yosk.mdlite.domain.DocumentRenderingProfile;
import io.github.yosk.mdlite.domain.DocumentUri;
import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.FeatureEntitlements;
import io.github.yosk.mdlite.domain.HeadingScrollPosition;
import io.github.yosk.mdlite.domain.MarkdownHeading;
import io.github.yosk.mdlite.domain.MarkdownHeadings;
import io.github.yosk.mdlite.domain.MermaidRenderJob;
import io.github.yosk.mdlite.domain.ProPurchaseFlow;
import io.github.yosk.mdlite.domain.ProPurchaseUiState;
import io.github.yosk.mdlite.domain.RecentDocumentLimit;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.domain.TableOfContentsItem;
import io.github.yosk.mdlite.domain.TableReadingMode;
import io.github.yosk.mdlite.domain.UnavailableProPurchaseFlow;
import io.github.yosk.mdlite.domain.ViewerFeature;
import io.github.yosk.mdlite.file.FileInfo;
import io.github.yosk.mdlite.file.FileSizePolicy;
import io.github.yosk.mdlite.file.LocalRelativeImageResource;
import io.github.yosk.mdlite.file.LocalRelativeMarkdownLink;
import io.github.yosk.mdlite.file.MarkdownFileOpenResult;
import io.github.yosk.mdlite.file.RestorableOpenTab;
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
import io.github.yosk.mdlite.viewer.DocumentNavigationController;
import io.github.yosk.mdlite.viewer.DocumentRenderingCoordinator;
import io.github.yosk.mdlite.viewer.FontSize;
import io.github.yosk.mdlite.viewer.GestureShortcutBindings;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.OpenDocumentTabSession;
import io.github.yosk.mdlite.viewer.OpenDocumentTabs;
import io.github.yosk.mdlite.viewer.ViewerLanguage;
import io.github.yosk.mdlite.viewer.ViewerText;
import io.github.yosk.mdlite.viewer.ViewerTheme;
import io.github.yosk.mdlite.viewer.TabPinningDecision;
import io.github.yosk.mdlite.viewer.PinnedDocumentController;
import io.github.yosk.mdlite.viewer.SavedDocumentPlacement;
import io.github.yosk.mdlite.model.RestoredOpenDocumentTab;
import io.github.yosk.mdlite.model.RestoredOpenDocumentTabs;
import io.github.yosk.mdlite.file.RecentDocument;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MainActivity extends Activity implements View.OnClickListener, View.OnLongClickListener,
        View.OnApplyWindowInsetsListener,
        MermaidJsRenderEngine.Listener, CustomGestureDrawingView.Listener,
        DocumentNavigationController.Host, PinnedDocumentController.Host {

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
    DocumentRenderingProfile documentRenderingProfile = DocumentRenderingProfile.fromEntitlement(null);
    ProPurchaseUiState proPurchaseUiState = ProPurchaseUiState.unavailable();
    ProPurchaseFlow proPurchaseFlow = UnavailableProPurchaseFlow.instance();
    ProPurchaseCacheStore purchaseCacheStore;
    ProPurchaseStatusRefresh proPurchaseStatusRefresh;
    ViewerSettingsStore settingsStore;
    ClipboardHistoryStore clipboardHistoryStore;
    WebView webView;
    MermaidJsRenderEngine mermaidRenderEngine;
    OpenDocumentTabSession documentTabSession;
    ControlsPlacement controlsPlacement;
    ViewerLanguage currentLanguage = ViewerLanguage.english();
    ViewerTheme currentTheme = ViewerTheme.light();
    ViewerPalette viewerPalette = ViewerPalette.from(ViewerTheme.light());
    GestureShortcutBindings gestureShortcutBindings = GestureShortcutBindings.empty();
    ViewerText viewerText = ViewerText.fromLanguage(ViewerLanguage.english());
    FontSize currentFontSize = FontSize.defaultSize();
    FontSize renderedFontSize = FontSize.defaultSize();
    String pendingSaveMarkdown = "";
    SavedDocumentPlacement pendingSavePlacement = SavedDocumentPlacement.openNormally();
    String pendingExportHtml = "";
    final Map<String, String> draftMarkdownByUri = new HashMap<String, String>();
    DocumentRenderingCoordinator documentRenderingCoordinator;
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
    DocumentSearchBar documentSearchBar;
    DocumentTabSessionController documentTabSessionController;
    PinnedDocumentController pinnedDocumentController;
    private DocumentTabBar documentTabBar;
    private ReaderAppearance readerAppearance;
    private DocumentNavigationController documentNavigationController;

    TextView messageView;
    Button menuButton;
    MainMenuActionButton openButton;
    MainMenuActionButton markdownLibraryButton;
    MainMenuActionButton createFromClipboardButton;
    MainMenuActionButton saveAsButton;
    MainMenuActionButton exportAsHtmlButton;
    MainMenuActionButton printOrSavePdfButton;
    MainMenuActionButton pinCurrentFileButton;
    MainMenuActionButton unpinCurrentFileButton;
    MainMenuActionButton pinnedFilesButton;
    MainMenuActionButton recentButton;
    MainMenuActionButton settingsButton;
    MainMenuActionButton tableOfContentsButton;
    MainMenuActionButton findInDocumentButton;
    MainMenuActionButton themeButton;
    MainMenuActionButton languageButton;
    MainMenuActionButton controlsPlacementButton;
    MainMenuActionButton gestureShortcutsButton;
    MainMenuActionButton proFeaturesButton;
    MainMenuActionButton clipboardDiagnosticsButton;
    MainMenuActionButton privacyButton;
    MainMenuActionButton[] menuActionButtons;
    LinearLayout settingsPanel;
    ExpandableMenuSection settingsMenuSection;
    TableOfContentsMenuPanel tableOfContentsPanel;
    ExpandableMenuSection tableOfContentsMenuSection;
    MarkdownLibraryMenuTree markdownLibraryMenuTree;
    SwipeMenuScrollView menuScrollContainer;
    SwipeMenuLayout menuPanel;
    View menuScrim;
    private MenuTransitions menuTransitions;
    final List<LinearLayout> menuCards = new ArrayList<LinearLayout>();
    LinearLayout root;
    LinearLayout topBar;
    LinearLayout controlsBar;
    LinearLayout tabRow;
    HorizontalScrollView tabScroller;
    private int systemTopInsetPx;
    private int systemBottomInsetPx;
    TextView appTitle;
    TextView menuTitle;
    TextView filesSection;
    TextView readingSection;
    TextView layoutSection;
    TextView infoSection;
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
        documentRenderingProfile = DocumentRenderingProfile.fromEntitlement(featureEntitlement);
        reclampCurrentThemeForEntitlement();
    }

    private void reclampCurrentThemeForEntitlement() {
        ViewerTheme clampedTheme = currentTheme.clampedForEntitlement(featureEntitlement);
        if (clampedTheme.storedValue().equals(currentTheme.storedValue())) {
            return;
        }
        currentTheme = clampedTheme;
        viewerPalette = ViewerPalette.from(currentTheme);
        if (root == null || documentTabSession == null || webView == null) {
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
        pinnedDocumentController = new PinnedDocumentController(tabPersistence, this);
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

        readerAppearance = new ReaderAppearance(this);
        ReaderScreenInitializer.initialize(this);
        documentNavigationController = new DocumentNavigationController(this);
        documentTabBar = new DocumentTabBar(this, tabScroller, tabRow);
        documentRenderingCoordinator = new DocumentRenderingCoordinator(
                new MainActivityDocumentRenderingOutput(this));

        fontScaleGestureDetector = new ScaleGestureDetector(this, new FontScaleGestureListener(this));
        shortcutGestureDetector = new GestureDetector(this, new ShortcutGestureListener(this));
        webView.setOnTouchListener(new ViewerTouchListener(this));
        updateLocalizedText();
        documentTabSession = new OpenDocumentTabSession(restoreOpenTabsOrInitial());
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

    void styleMenuCard(LinearLayout card) {
        readerAppearance.styleMenuCard(card);
    }

    static LinearLayout.LayoutParams wrapParams() {
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
    public void onMermaidRendered(MermaidRenderJob job, SafeHtml svg) {
        documentRenderingCoordinator.complete(job, svg);
    }

    @Override
    public void onMermaidRenderFailed(MermaidRenderJob job, String reason) {
        SafeHtml errorHtml = MermaidRenderErrorHtml.from(job.block(), reason);
        documentRenderingCoordinator.complete(job, errorHtml);
    }

    void switchLanguage() {
        currentLanguage = currentLanguage.toggled();
        viewerText = ViewerText.fromLanguage(currentLanguage);
        settingsStore.saveViewerLanguage(currentLanguage);
        updateLocalizedText();
        if (WELCOME_URI.equals(openTabs().activeTab().uri())) {
            documentTabSession.reset(OpenDocumentTabs.withInitialTab(initialTab()));
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
        documentTabBar.render(openTabs());
    }

    @Override
    public boolean onLongClick(View view) {
        if (!(view instanceof TabButton)) {
            return false;
        }
        int index = ((TabButton) view).tabIndex();
        if (index < 0 || index >= openTabs().tabs().size()) {
            return false;
        }
        return pinnedDocumentController.decision(openTabs().tabs().get(index))
                .perform(pinnedDocumentController);
    }

    void renderCurrentDocument() {
        renderCurrentDocument(null);
    }

    void renderCurrentDocument(String anchorId) {
        documentNavigationController.resetForDocument();
        String historyUrl = anchorId == null ? null : "https://localmd.local/#" + anchorId;
        webView.loadDataWithBaseURL("https://localmd.local/",
                HtmlPageBuilder.buildPage(
                        openTabs().activeTab().document(),
                        currentTheme,
                        currentFontSize,
                        TableReadingMode.fromEntitlement(featureEntitlement)),
                "text/html", "UTF-8", historyUrl);
        renderedFontSize = currentFontSize;
        webView.getSettings().setTextZoom(100);
    }

    WebResourceResponse openActiveRelativeImage(String requestUrl) {
        if (!documentRenderingProfile.relativeImageRendering().isEnabled()) {
            return null;
        }
        if (documentTabSession == null || !(openTabs().activeTab() instanceof OpenDocumentTab.FileDocumentTab)) {
            return null;
        }
        LocalRelativeImageResource resource =
                LocalRelativeImageResource.resolve(
                        openTabs().activeTab().uri(),
                        requestUrl,
                        allowedRelativeImageRoot(openTabs().activeTab().uri()));
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
        if (!documentRenderingProfile.relativeLinkRendering().isEnabled()) {
            return false;
        }
        if (documentTabSession == null || !(openTabs().activeTab() instanceof OpenDocumentTab.FileDocumentTab)) {
            return false;
        }
        LocalRelativeMarkdownLink link = LocalRelativeMarkdownLink.resolve(
                openTabs().activeTab().uri(),
                requestUrl,
                allowedRelativeDocumentRoot(openTabs().activeTab().uri()));
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
        documentNavigationController.showSearchBar();
    }

    void showFindInDocumentBar() {
        documentNavigationController.showSearchBar();
    }

    void searchTextInDocument(DocumentSearchQuery query) {
        documentNavigationController.search(query);
    }

    boolean hasActiveDocumentSearch() {
        return documentNavigationController.hasActiveSearch();
    }

    String currentSearchQueryText() {
        return documentNavigationController.searchQueryText();
    }

    void findNextSearchResult() {
        documentNavigationController.nextSearchResult();
    }

    void findPreviousSearchResult() {
        documentNavigationController.previousSearchResult();
    }

    void clearWebViewSearch() {
        documentNavigationController.clearSearchMatches();
    }

    MarkdownHeadings activeMarkdownHeadings() {
        return documentNavigationController.activeHeadings();
    }

    @Override
    public MarkdownHeadings activeHeadings() {
        if (documentTabSession == null) {
            return MarkdownHeadings.fromMarkdown("");
        }
        String markdown = documentRenderingCoordinator.markdownFor(openTabs().activeTab().documentUri());
        return MarkdownHeadings.fromMarkdown(markdown);
    }

    void jumpToHeading(MarkdownHeading heading) {
        documentNavigationController.jumpTo(heading);
    }

    void jumpToNextHeading() {
        documentNavigationController.jumpToNextHeading();
    }

    void jumpToPreviousHeading() {
        documentNavigationController.jumpToPreviousHeading();
    }

    @Override
    public void showSearchBar() {
        documentSearchBar.showBar();
    }

    @Override
    public void findAll(String query) {
        webView.findAllAsync(query);
    }

    @Override
    public void findNext(boolean forward) {
        webView.findNext(forward);
    }

    @Override
    public void clearSearchMatches() {
        webView.clearMatches();
    }

    @Override
    public void synchronizeSearchBar() {
        documentSearchBar.syncFromSession();
    }

    @Override
    public HeadingScrollPosition headingScrollPosition() {
        return HeadingScrollPosition.fromWebViewMetrics(
                webView.getScrollY(),
                webView.getContentHeight(),
                webView.getHeight(),
                webView.getScale());
    }

    @Override
    public void openHeading(MarkdownHeading heading) {
        renderCurrentDocument(heading.anchorId());
    }

    SafeHtml renderMarkdownForUri(String documentUri, String markdown) {
        return documentRenderingCoordinator.open(
                DocumentUri.from(documentUri),
                markdown,
                documentRenderingProfile);
    }

    void saveOpenTabs() {
        tabPersistence.saveOpenTabs(openTabs());
    }

    OpenDocumentTabs openTabs() {
        return documentTabSession.tabs();
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
        documentPrintLauncher.print(webView, openTabs().activeTab().title());
    }

    boolean documentOutputAvailable() {
        return documentTabSession != null
                && openTabs().activeTab() instanceof OpenDocumentTab.UserDocumentTab
                && featureEntitlement.allows(ViewerFeature.EXPORT_OPTIONS);
    }

    void showRecentDocuments() {
        documentListDialogs.showRecentDocuments();
    }

    void showProjectLibrary(io.github.yosk.mdlite.file.MarkdownLibraryLocation location,
            io.github.yosk.mdlite.file.MarkdownLibraryListing listing) {
        markdownLibraryMenuTree.show(location, listing);
        refreshMenuActionButtons();
        openMenu();
    }

    boolean activeTabIsDraft() {
        return documentTabSession != null && openTabs().activeTab() instanceof OpenDocumentTab.DraftDocumentTab;
    }

    boolean activeTabIsFile() {
        return documentTabSession != null && openTabs().activeTab() instanceof OpenDocumentTab.FileDocumentTab;
    }

    @Override
    public boolean pinnedDocumentsAvailable() {
        return featureEntitlement.allows(ViewerFeature.EXTENDED_RECENT_FILES);
    }

    boolean activeFileIsPinned() {
        return activeTabIsFile() && pinnedDocumentController.isPinned(openTabs().activeTab());
    }

    void pinCurrentDocument() {
        pinnedDocumentController.pinCurrent(openTabs().activeTab());
    }

    void unpinCurrentDocument() {
        pinnedDocumentController.unpinCurrent(openTabs().activeTab());
    }

    void clearPinnedDocuments() {
        pinnedDocumentController.clear();
    }

    void unpinPinnedDocument(RecentDocument document) {
        pinnedDocumentController.unpin(document.uri());
    }

    @Override
    public void refreshPinnedDocuments(String message) {
        renderTabs();
        refreshMenuActionButtons();
        showMessage(message);
    }

    void showPinnedDocuments() {
        documentListDialogs.showPinnedDocuments();
    }

    String recentFilesTitle() {
        return viewerText.recentFiles();
    }

    @Override
    public ViewerText viewerText() {
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
        return readerAppearance.themedIcon(resId, color);
    }

    void applyExpandChevron(TextView button, boolean expanded) {
        readerAppearance.applyExpandChevron(button, expanded);
    }

    /**
     * Rounded button background with ripple touch feedback (#75). The ripple
     * wraps the unchanged rounded surface and is masked to the same rounded
     * shape; its color derives from the theme primary at low alpha.
     */
    Drawable makeRoundedBackground(int fillColor, int strokeColor, int radiusDp) {
        return readerAppearance.roundedBackground(fillColor, strokeColor, radiusDp);
    }

    /**
     * Borderless tonal surface (#77): list-like buttons inside panels separate
     * from the page by fill contrast alone, without the 1px border noise.
     */
    Drawable makeTonalBackground(int fillColor, int radiusDp) {
        return readerAppearance.tonalBackground(fillColor, radiusDp);
    }

    /** Non-interactive tonal container (section cards must not ripple). */
    GradientDrawable makePlainTonalBackground(int fillColor, int radiusDp) {
        return readerAppearance.plainTonalBackground(fillColor, radiusDp);
    }

    /** Transparent row inside a card: ripple feedback without its own surface. */
    Drawable makeRowRippleBackground() {
        return readerAppearance.rowRippleBackground();
    }

    /** Plain variant for non-interactive surfaces (text fields must not ripple). */
    GradientDrawable makePlainRoundedBackground(int fillColor, int strokeColor, int radiusDp) {
        return readerAppearance.plainRoundedBackground(fillColor, strokeColor, radiusDp);
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
    int messageColor() { return viewerPalette.message; }

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
        readerAppearance.applyCurrentTheme();
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
        if (!documentRenderingProfile.mermaidRendering().isEnabled()) { return; }
        documentRenderingCoordinator.resetForTheme(documentRenderingProfile);
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
        applyExpandChevron(markdownLibraryButton, markdownLibraryMenuTree.isExpanded());
    }

    private void updateLocalizedMessage() {
        if (MESSAGE_SAVED_MARKDOWN.equals(currentMessage)) {
            showMessage(viewerText.savedMarkdown());
            return;
        }
        if (documentTabSession != null) {
            showMessage(openTabs().activeTab().statusMessage().localized(viewerText));
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

    MainMenuActionButton menuActionButton(MainMenuAction action) {
        MainMenuActionButton button = new MainMenuActionButton(this, action);
        styleMenuButton(button);
        return button;
    }

    TextView menuSection(String label) {
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

    void styleToolbarButton(TextView view) {
        readerAppearance.styleToolbarButton(view);
    }

    void styleCompactButton(TextView view) {
        readerAppearance.styleCompactButton(view);
    }

    private void styleMenuButton(TextView view) {
        readerAppearance.styleMenuButton(view);
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
        return RestoredOpenDocumentTabs.restore(
                tabPersistence.loadRestorableOpenTabs(),
                initialTab(),
                new RestoredOpenDocumentTabs.Loader() {
                    @Override
                    public RestoredOpenDocumentTab load(RestorableOpenTab storedTab) {
                        return restoreOpenTab(storedTab);
                    }
                });
    }

    private RestoredOpenDocumentTab restoreOpenTab(RestorableOpenTab storedTab) {
        try {
            Uri uri = Uri.parse(storedTab.uri());
            FileInfo fileInfo = documentOpener.readFileInfo(uri);
            String displayName = fileInfo.displayName.length() == 0 ? storedTab.title() : fileInfo.displayName;
            MarkdownFileOpenResult openResult = MarkdownFileOpenResult.from(displayName, fileInfo.sizeBytes, fileSizePolicy);
            if (!(openResult instanceof MarkdownFileOpenResult.ReadableMarkdownFile)) {
                return RestoredOpenDocumentTab.unavailable();
            }
            MarkdownFileOpenResult.ReadableMarkdownFile readableFile = (MarkdownFileOpenResult.ReadableMarkdownFile) openResult;
            String markdown = documentOpener.readText(uri, MAX_FILE_SIZE_BYTES);
            String documentUri = uri.toString();
            SafeHtml rendered = renderMarkdownForUri(documentUri, markdown);
            return RestoredOpenDocumentTab.available(
                    OpenDocumentTab.fileDocument(readableFile.displayName(), documentUri, rendered));
        } catch (IllegalArgumentException e) { return RestoredOpenDocumentTab.unavailable(); }
        catch (IOException e) { return RestoredOpenDocumentTab.unavailable(); }
        catch (SecurityException e) { return RestoredOpenDocumentTab.unavailable(); }
    }

    void restorePendingScrollAfterPageLoad() {
        if (pendingScrollRestoreY < 0) { return; }
        int scrollY = pendingScrollRestoreY;
        pendingScrollRestoreY = -1;
        webView.post(new RestoreScrollPosition(webView, scrollY, 6));
    }

}
