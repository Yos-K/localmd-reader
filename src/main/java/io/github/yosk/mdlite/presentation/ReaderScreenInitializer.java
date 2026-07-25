package io.github.yosk.mdlite.presentation;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

final class ReaderScreenInitializer {
    private ReaderScreenInitializer() {}

    static void initialize(MainActivity activity) {
        activity.root = new LinearLayout(activity);
        activity.root.setOrientation(LinearLayout.VERTICAL);
        initTopBar(activity);
        initMenuButtons(activity);
        initMenuPanel(activity);
        initMessageAndTabs(activity);
        initWebView(activity);
    }

    private static void initTopBar(MainActivity activity) {
        activity.topBar = new LinearLayout(activity);
        activity.topBar.setOrientation(LinearLayout.HORIZONTAL);
        activity.topBar.setGravity(Gravity.CENTER_VERTICAL);
        activity.topBar.setPadding(activity.dp(14), activity.dp(10), activity.dp(14), activity.dp(10));
        activity.topBar.setBackgroundColor(activity.backgroundColor());

        activity.menuButton = new Button(activity);
        activity.menuButton.setText("Menu");
        activity.menuButton.setContentDescription("Open menu");
        activity.menuButton.setAllCaps(false);
        activity.menuButton.setOnClickListener(activity);
        activity.styleToolbarButton(activity.menuButton);
        activity.topBar.addView(activity.menuButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        activity.appTitle = new TextView(activity);
        activity.appTitle.setText("LocalMD Reader");
        activity.appTitle.setTextColor(activity.textColor());
        activity.appTitle.setTextSize(17);
        activity.appTitle.setTypeface(Typeface.DEFAULT_BOLD);
        activity.appTitle.setGravity(Gravity.CENTER_VERTICAL);
        activity.appTitle.setSingleLine(true);
        activity.appTitle.setEllipsize(TextUtils.TruncateAt.END);
        activity.appTitle.setPadding(activity.dp(14), 0, 0, 0);
        activity.topBar.addView(
                activity.appTitle, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
    }

    private static void initMenuButtons(MainActivity activity) {
        activity.openButton = activity.menuActionButton(MainMenuActions.openFile());
        activity.markdownLibraryButton = activity.menuActionButton(MainMenuActions.markdownLibrary());
        activity.createFromClipboardButton = activity.menuActionButton(MainMenuActions.createFromClipboard());
        activity.saveAsButton = activity.menuActionButton(MainMenuActions.saveAs());
        activity.exportAsHtmlButton = activity.menuActionButton(MainMenuActions.exportAsHtml());
        activity.printOrSavePdfButton = activity.menuActionButton(PrintDocumentMenuActions.printOrSavePdf());
        activity.pinCurrentFileButton = activity.menuActionButton(MainMenuActions.pinCurrentFile());
        activity.unpinCurrentFileButton = activity.menuActionButton(MainMenuActions.unpinCurrentFile());
        activity.pinnedFilesButton = activity.menuActionButton(MainMenuActions.pinnedFiles());
        activity.recentButton = activity.menuActionButton(MainMenuActions.recentFiles());
        activity.settingsButton = activity.menuActionButton(MainMenuActions.settings());
        activity.tableOfContentsButton = activity.menuActionButton(MainMenuActions.tableOfContents());
        activity.findInDocumentButton = activity.menuActionButton(MainMenuActions.findInDocument());
        activity.themeButton = activity.menuActionButton(MainMenuActions.theme());
        activity.languageButton = activity.menuActionButton(MainMenuActions.language());
        activity.controlsPlacementButton = activity.menuActionButton(MainMenuActions.controlsPlacement());
        activity.gestureShortcutsButton = activity.menuActionButton(MainMenuActions.gestureShortcuts());
        activity.proFeaturesButton = activity.menuActionButton(MainMenuActions.proFeatures());
        activity.clipboardDiagnosticsButton = activity.menuActionButton(MainMenuActions.clipboardDiagnostics());
        activity.privacyButton = activity.menuActionButton(MainMenuActions.privacy());
        activity.menuActionButtons = new MainMenuActionButton[] {activity.openButton, activity.markdownLibraryButton,
                activity.createFromClipboardButton, activity.saveAsButton, activity.exportAsHtmlButton,
                activity.printOrSavePdfButton, activity.pinCurrentFileButton, activity.unpinCurrentFileButton,
                activity.pinnedFilesButton, activity.recentButton, activity.tableOfContentsButton,
                activity.findInDocumentButton, activity.settingsButton, activity.themeButton, activity.languageButton,
                activity.controlsPlacementButton, activity.gestureShortcutsButton, activity.proFeaturesButton,
                activity.clipboardDiagnosticsButton, activity.privacyButton};
    }

    private static void initMenuPanel(MainActivity activity) {
        activity.menuScrollContainer = new SwipeMenuScrollView(activity);
        activity.menuScrollContainer.setVisibility(View.GONE);
        activity.menuScrollContainer.setBackgroundColor(activity.backgroundColor());
        activity.menuScrollContainer.setClickable(true);

        activity.menuPanel = new SwipeMenuLayout(activity);
        activity.menuPanel.setOrientation(LinearLayout.VERTICAL);
        activity.menuPanel.setVisibility(View.VISIBLE);
        activity.menuPanel.setBackgroundColor(activity.backgroundColor());
        activity.menuPanel.setPadding(activity.dp(18), activity.dp(28), activity.dp(18), activity.dp(18));
        activity.menuPanel.setClickable(true);
        activity.menuScrollContainer.addView(activity.menuPanel,
                new ScrollView.LayoutParams(
                        ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));

        activity.menuTitle = new TextView(activity);
        activity.menuTitle.setTextColor(activity.textColor());
        activity.menuTitle.setTextSize(22);
        activity.menuTitle.setTypeface(Typeface.DEFAULT_BOLD);
        activity.menuTitle.setGravity(Gravity.CENTER_VERTICAL);
        activity.menuTitle.setPadding(activity.dp(8), 0, activity.dp(8), activity.dp(18));
        activity.menuPanel.addView(activity.menuTitle, MainActivity.wrapParams());
        activity.filesSection = activity.menuSection("");
        activity.markdownLibraryMenuTree = new MarkdownLibraryMenuTree(activity);
        addMenuCard(activity, activity.menuPanel, activity.filesSection, activity.openButton,
                activity.markdownLibraryButton, activity.markdownLibraryMenuTree,
                activity.createFromClipboardButton,
                activity.saveAsButton, activity.exportAsHtmlButton, activity.printOrSavePdfButton,
                activity.pinCurrentFileButton, activity.unpinCurrentFileButton, activity.pinnedFilesButton,
                activity.recentButton);
        activity.pinnedDocumentsPanel = new DocumentListMenuPanel(activity, true);
        activity.menuPanel.addView(activity.pinnedDocumentsPanel, MainActivity.wrapParams());
        activity.pinnedDocumentsMenuSection =
                new ExpandableMenuSection(activity.pinnedDocumentsPanel, activity.pinnedDocumentsPanel);
        activity.recentDocumentsPanel = new DocumentListMenuPanel(activity, false);
        activity.menuPanel.addView(activity.recentDocumentsPanel, MainActivity.wrapParams());
        activity.recentDocumentsMenuSection =
                new ExpandableMenuSection(activity.recentDocumentsPanel, activity.recentDocumentsPanel);
        activity.readingSection = activity.menuSection("");
        addMenuCard(activity, activity.menuPanel, activity.readingSection, activity.tableOfContentsButton,
                activity.findInDocumentButton);
        activity.tableOfContentsPanel = new TableOfContentsMenuPanel(activity);
        activity.menuPanel.addView(activity.tableOfContentsPanel, MainActivity.wrapParams());
        activity.tableOfContentsMenuSection =
                new ExpandableMenuSection(activity.tableOfContentsPanel, activity.tableOfContentsPanel);
        addMenuCard(activity, activity.menuPanel, null, activity.settingsButton);

        activity.settingsPanel = new LinearLayout(activity);
        activity.settingsPanel.setOrientation(LinearLayout.VERTICAL);
        activity.settingsPanel.setVisibility(View.GONE);
        activity.settingsPanel.setPadding(activity.dp(10), 0, 0, 0);
        activity.menuPanel.addView(activity.settingsPanel, MainActivity.wrapParams());
        activity.settingsMenuSection = ExpandableMenuSection.staticContent(activity.settingsPanel);
        activity.layoutSection = activity.menuSection("");
        addMenuCard(activity, activity.settingsPanel, activity.layoutSection, activity.themeButton,
                activity.languageButton, activity.controlsPlacementButton, activity.gestureShortcutsButton);
        activity.themePanel = new ThemeMenuPanel(activity);
        activity.settingsPanel.addView(activity.themePanel, MainActivity.wrapParams());
        activity.themeMenuSection = new ExpandableMenuSection(activity.themePanel, activity.themePanel);
        activity.gestureShortcutPanel = new GestureShortcutMenuPanel(activity);
        activity.settingsPanel.addView(activity.gestureShortcutPanel, MainActivity.wrapParams());
        activity.gestureShortcutMenuSection =
                new ExpandableMenuSection(activity.gestureShortcutPanel, activity.gestureShortcutPanel);
        activity.infoSection = activity.menuSection("");
        addMenuCard(activity, activity.settingsPanel, activity.infoSection, activity.proFeaturesButton,
                activity.clipboardDiagnosticsButton, activity.privacyButton);
    }

    private static void addMenuCard(MainActivity activity, LinearLayout parent, TextView sectionLabel, View... items) {
        if (sectionLabel != null) {
            parent.addView(sectionLabel, MainActivity.wrapParams());
        }
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        addMenuItems(card, items);
        activity.menuCards.add(card);
        activity.styleMenuCard(card);
        LinearLayout.LayoutParams params = MainActivity.wrapParams();
        params.bottomMargin = activity.dp(10);
        parent.addView(card, params);
    }

    private static void initMessageAndTabs(MainActivity activity) {
        activity.messageView = new TextView(activity);
        activity.messageView.setGravity(Gravity.CENTER_VERTICAL);
        activity.messageView.setTextColor(activity.textColor());
        activity.messageView.setBackgroundColor(activity.messageColor());
        activity.messageView.setPadding(activity.dp(24), activity.dp(12), activity.dp(24), activity.dp(12));

        activity.tabRow = new LinearLayout(activity);
        activity.tabRow.setOrientation(LinearLayout.HORIZONTAL);
        activity.tabRow.setPadding(activity.dp(12), activity.dp(8), activity.dp(12), activity.dp(8));

        activity.tabScroller = new HorizontalScrollView(activity);
        activity.tabScroller.setHorizontalScrollBarEnabled(true);
        activity.tabScroller.setBackgroundColor(activity.backgroundColor());
        activity.tabScroller.addView(activity.tabRow,
                new HorizontalScrollView.LayoutParams(HorizontalScrollView.LayoutParams.WRAP_CONTENT,
                        HorizontalScrollView.LayoutParams.WRAP_CONTENT));

        activity.controlsBar = new LinearLayout(activity);
        activity.controlsBar.setOrientation(LinearLayout.VERTICAL);
        activity.controlsBar.addView(activity.topBar, MainActivity.wrapParams());
        activity.controlsBar.addView(activity.tabScroller, MainActivity.wrapParams());
        activity.controlsBar.addView(activity.documentSearchBar, MainActivity.wrapParams());
    }

    private static void initWebView(MainActivity activity) {
        activity.webView = new WebView(activity);
        WebSettings settings = activity.webView.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setDomStorageEnabled(false);
        settings.setDatabaseEnabled(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        activity.webView.setWebViewClient(new AppLinkClient(activity));
        if (activity.documentRenderingProfile.mermaidRendering().isEnabled()) {
            activity.mermaidRenderEngine = new MermaidJsRenderEngine(activity, activity);
        }
    }

    private static void addMenuItems(LinearLayout parent, View... views) {
        LinearLayout.LayoutParams params = MainActivity.wrapParams();
        for (int index = 0; index < views.length; index++) {
            parent.addView(views[index], params);
        }
    }
}
