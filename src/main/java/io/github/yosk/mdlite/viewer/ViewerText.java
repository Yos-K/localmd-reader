package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.ProFeatureDescriptor;
import io.github.yosk.mdlite.domain.ViewerFeature;

public abstract class ViewerText {
    public static ViewerText fromLanguage(ViewerLanguage language) {
        if (language != null && language.isJapanese()) {
            return new JapaneseViewerText();
        }
        return new EnglishViewerText();
    }

    public abstract String menuButton();
    public abstract String openMenuDescription();
    public abstract String openFile();
    public abstract String createFromClipboard();
    public abstract String saveAs();
    public abstract String exportAsHtml();
    public abstract String printOrSavePdf();
    public abstract String exportedHtml();
    public abstract String htmlExportFailed();
    public abstract String switchLanguage();
    public abstract String filesSection();
    public abstract String readingSection();
    public abstract String layoutSection();
    public abstract String infoSection();
    public abstract String settings();
    public abstract String tableOfContents();
    public abstract String noHeadings();
    public abstract String findInDocument();
    public abstract String searchHint();
    public abstract String findAction();
    public abstract String previousSearchResult();
    public abstract String nextSearchResult();
    public abstract String pinnedFiles();
    public abstract String pinCurrentFile();
    public abstract String unpinCurrentFile();
    public abstract String noPinnedFiles();
    public abstract String clearPinnedFiles();
    public abstract String currentFilePinned();
    public abstract String currentFileUnpinned();
    public abstract String pinnedFilesCleared();
    public abstract String appearanceSettings();
    public abstract String proFeatures();
    public abstract String proFeaturesIntro();
    public abstract ProFeatureDescriptor[] proFeatureCatalog();
    public abstract String clipboardDiagnostics();
    public abstract String privacy();
    public abstract String moveControlsToTop();
    public abstract String moveControlsToBottom();
    public abstract String gestures();
    public abstract String doubleTapPrefix();
    public abstract String circleGesturePrefix();
    public abstract String customGesturePrefix();
    public abstract String registerCustomGesture();
    public abstract String changeCustomGestureAction();
    public abstract String clearCustomGesture();
    public abstract String customGestureRegistrationComingSoon();
    public abstract String customGestureCleared();
    public abstract String drawCustomGestureInstruction();
    public abstract String customGestureTooSmall();
    public abstract String proOnly();
    public abstract String openMenu();
    public abstract String previousTabAction();
    public abstract String nextTabAction();
    public abstract String nextThemeAction();
    public abstract String moveControlsAction();
    public abstract String showSearchBarAction();
    public abstract String nextHeadingAction();
    public abstract String previousHeadingAction();
    public abstract String off();
    public abstract String recentFiles();
    public abstract String openMarkdownFile();
    public abstract String noRecentFiles();
    public abstract String noMarkdownFilesInFolder();
    public abstract String chooseAnotherFolder();
    public abstract String markdownLibrary();
    public abstract String libraryFilterHint();
    public abstract String noMatchingLibraryItems();
    public abstract String openMarkdownLibrary();
    public abstract String libraryFolderGuidanceTitle();
    public abstract String libraryFolderGuidanceMessage();
    public abstract String chooseLibraryFolder();
    public abstract String upOneFolder();
    public abstract String folderEntry(String displayName);
    public abstract String clearHistory();
    public abstract String recentFilesCleared();
    public abstract String noTextToCreate();
    public abstract String noClipboardText();
    public abstract String clipboardItemsToOpen();
    public abstract String openSelected();
    public abstract String cancel();
    public abstract String close();
    public abstract String noClipboardItemSelected();
    public abstract String noDocumentToSave();
    public abstract String createMarkdownFailed();
    public abstract String temporaryMarkdown();
    public abstract String selectedTextMarkdown();
    public abstract String savedMarkdown();
    public abstract String unsupportedFile();
    public abstract String fileTooLarge();
    public abstract String unreadableFile();
    public abstract String darkTheme();
    public abstract String lightTheme();
    public abstract String amoledTheme();
    public abstract String gradientTheme();
    public abstract String auroraTheme();
    public abstract String mistTheme();
    public abstract String duskTheme();
    public abstract String themeLabel(ViewerTheme theme);
    public abstract String welcomeTabTitle();
    public abstract String closeTabDescription(String title);
    public abstract String historyClipboardTitle(int index);
    public abstract String proStatus(boolean pro);
    public abstract String featureAvailable();
    public abstract String featureLocked();
    public abstract String purchaseProAction();
    public abstract String restorePurchaseAction();
    public abstract String purchaseMessage(String messageCode);
    public abstract String purchaseFlowUnavailable();
    public abstract String privacyMessage();

    private static final class EnglishViewerText extends ViewerText {
        @Override public String menuButton() { return "Menu"; }
        @Override public String openMenuDescription() { return "Open menu"; }
        @Override public String openFile() { return "Open file"; }
        @Override public String createFromClipboard() { return "Create from clipboard"; }
        @Override public String saveAs() { return "Save as..."; }
        @Override public String exportAsHtml() { return "Export as HTML"; }
        @Override public String printOrSavePdf() { return "Print / Save as PDF"; }
        @Override public String exportedHtml() { return "Exported HTML file."; }
        @Override public String htmlExportFailed() { return "The HTML file could not be exported."; }
        @Override public String switchLanguage() { return "日本語に切り替え"; }
        @Override public String filesSection() { return "Files"; }
        @Override public String readingSection() { return "Reading"; }
        @Override public String layoutSection() { return "Layout"; }
        @Override public String infoSection() { return "Info"; }
        @Override public String settings() { return "Settings"; }
        @Override public String tableOfContents() { return "Table of contents"; }
        @Override public String noHeadings() { return "This document has no headings."; }
        @Override public String findInDocument() { return "Find in document"; }
        @Override public String searchHint() { return "Search text"; }
        @Override public String findAction() { return "Find"; }
        @Override public String previousSearchResult() { return "Previous"; }
        @Override public String nextSearchResult() { return "Next"; }
        @Override public String pinnedFiles() { return "Pinned files"; }
        @Override public String pinCurrentFile() { return "Pin current file"; }
        @Override public String unpinCurrentFile() { return "Unpin current file"; }
        @Override public String noPinnedFiles() { return "No pinned files yet."; }
        @Override public String clearPinnedFiles() { return "Clear pinned files"; }
        @Override public String currentFilePinned() { return "Pinned current file."; }
        @Override public String currentFileUnpinned() { return "Unpinned current file."; }
        @Override public String pinnedFilesCleared() { return "Pinned files cleared."; }
        @Override public String appearanceSettings() { return "Appearance"; }
        @Override public String proFeatures() { return "Pro features"; }
        @Override public String proFeaturesIntro() { return "Free covers the core offline reader. Pro adds faster navigation and comfort tools for long files and linked project notes."; }
        @Override public ProFeatureDescriptor[] proFeatureCatalog() {
            return new ProFeatureDescriptor[] {
                new ProFeatureDescriptor(ViewerFeature.EXTRA_THEMES,
                        "More comfortable reading themes",
                        "Choose extra color styles when light and dark are not enough for your reading environment."),
                new ProFeatureDescriptor(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS,
                        "More gesture shortcuts",
                        "Assign circle, direction, and custom gestures to the actions you repeat most."),
                new ProFeatureDescriptor(ViewerFeature.TABLE_OF_CONTENTS,
                        "Faster long-document navigation",
                        "Open the table of contents and jump through headings without losing your place."),
                new ProFeatureDescriptor(ViewerFeature.HEADING_JUMP,
                        "Heading jump shortcuts",
                        "Move to the next or previous heading from gestures while reading."),
                new ProFeatureDescriptor(ViewerFeature.TABLE_READING_ENHANCEMENTS,
                        "Easier wide-table reading",
                        "Keep table headers and first columns visible while reading wide tables."),
                new ProFeatureDescriptor(ViewerFeature.EXTENDED_RECENT_FILES,
                        "More reading history",
                        "Keep more recent files available when you switch between many local documents."),
                new ProFeatureDescriptor(ViewerFeature.RELATIVE_LINKS,
                        "Linked project notes",
                        "Open safe relative Markdown links inside local project document sets."),
                new ProFeatureDescriptor(ViewerFeature.RELATIVE_IMAGES,
                        "Local images in project notes",
                        "Render safe relative image references inside local Markdown document sets."),
                new ProFeatureDescriptor(ViewerFeature.EXPORT_OPTIONS,
                        "Export and print",
                        "Save as HTML or use Android printing to save as PDF."),
                new ProFeatureDescriptor(ViewerFeature.PROJECT_LIBRARY,
                        "Navigate project folders",
                        "Move through nested folders and open related Markdown files without leaving the library.")
            };
        }
        @Override public String clipboardDiagnostics() { return "Clipboard diagnostics"; }
        @Override public String privacy() { return "Privacy"; }
        @Override public String moveControlsToTop() { return "Move controls to top"; }
        @Override public String moveControlsToBottom() { return "Move controls to bottom"; }
        @Override public String gestures() { return "Gestures"; }
        @Override public String doubleTapPrefix() { return "Double tap: "; }
        @Override public String circleGesturePrefix() { return "Circle gesture: "; }
        @Override public String customGesturePrefix() { return "Custom gesture: "; }
        @Override public String registerCustomGesture() { return "Register custom gesture"; }
        @Override public String changeCustomGestureAction() { return "Change custom gesture action"; }
        @Override public String clearCustomGesture() { return "Clear custom gesture"; }
        @Override public String customGestureRegistrationComingSoon() { return "Custom gesture drawing will be added next."; }
        @Override public String customGestureCleared() { return "Custom gesture cleared."; }
        @Override public String drawCustomGestureInstruction() { return "Draw one gesture"; }
        @Override public String customGestureTooSmall() { return "Draw a larger gesture."; }
        @Override public String proOnly() { return "Pro only"; }
        @Override public String openMenu() { return "Open menu"; }
        @Override public String previousTabAction() { return "Previous tab"; }
        @Override public String nextTabAction() { return "Next tab"; }
        @Override public String nextThemeAction() { return "Next theme"; }
        @Override public String moveControlsAction() { return "Move controls"; }
        @Override public String showSearchBarAction() { return "Show search"; }
        @Override public String nextHeadingAction() { return "Next heading"; }
        @Override public String previousHeadingAction() { return "Previous heading"; }
        @Override public String off() { return "Off"; }
        @Override public String recentFiles() { return "Recent files"; }
        @Override public String openMarkdownFile() { return "Open Markdown file"; }
        @Override public String noRecentFiles() { return "No recent files yet."; }
        @Override public String noMarkdownFilesInFolder() { return "This folder has no .md or .markdown files."; }
        @Override public String chooseAnotherFolder() { return "Choose another folder"; }
        @Override public String markdownLibrary() { return "Markdown library"; }
        @Override public String libraryFilterHint() { return "Filter by file or folder name"; }
        @Override public String noMatchingLibraryItems() { return "No matching files or folders"; }
        @Override public String openMarkdownLibrary() { return "Markdown library"; }
        @Override public String libraryFolderGuidanceTitle() { return "Choose a library folder"; }
        @Override public String libraryFolderGuidanceMessage() {
            return "Android does not allow apps to use the Download folder itself. "
                    + "Choose or create a subfolder inside it instead.";
        }
        @Override public String chooseLibraryFolder() { return "Choose folder"; }
        @Override public String upOneFolder() { return "Up one folder"; }
        @Override public String folderEntry(String displayName) { return displayName + "/"; }
        @Override public String clearHistory() { return "Clear history"; }
        @Override public String recentFilesCleared() { return "Recent files cleared."; }
        @Override public String noTextToCreate() { return "There is no selected text to create a Markdown file."; }
        @Override public String noClipboardText() { return "There is no clipboard text to create a Markdown file."; }
        @Override public String clipboardItemsToOpen() { return "Clipboard items to open"; }
        @Override public String openSelected() { return "Open selected"; }
        @Override public String cancel() { return "Cancel"; }
        @Override public String close() { return "Close"; }
        @Override public String noClipboardItemSelected() { return "Select clipboard items to open."; }
        @Override public String noDocumentToSave() { return "There is no Markdown document to save."; }
        @Override public String createMarkdownFailed() { return "The Markdown file could not be created."; }
        @Override public String temporaryMarkdown() { return "Opened as temporary Markdown. Use Save as... to keep it as a file."; }
        @Override public String selectedTextMarkdown() { return "Opened selected text as temporary Markdown. If formatting is missing, copy it first and use Create from clipboard."; }
        @Override public String savedMarkdown() { return "Saved Markdown file."; }
        @Override public String unsupportedFile() { return "Only .md and .markdown files are supported."; }
        @Override public String fileTooLarge() { return "Files larger than 10 MB cannot be opened."; }
        @Override public String unreadableFile() { return "The document could not be read. It may have been moved, deleted, or opened without lasting permission."; }
        @Override public String darkTheme() { return "Dark theme"; }
        @Override public String lightTheme() { return "Light theme"; }
        @Override public String amoledTheme() { return "AMOLED theme"; }
        @Override public String gradientTheme() { return "Gradient theme"; }
        @Override public String auroraTheme() { return "Aurora theme"; }
        @Override public String mistTheme() { return "Mist theme"; }
        @Override public String duskTheme() { return "Dusk theme"; }
        @Override public String themeLabel(ViewerTheme theme) {
            ViewerTheme safeTheme = theme == null ? ViewerTheme.light() : theme;
            if (safeTheme.isAurora()) {
                return auroraTheme();
            }
            if (safeTheme.isMist()) {
                return mistTheme();
            }
            if (safeTheme.isDusk()) {
                return duskTheme();
            }
            if (safeTheme.isGradient()) {
                return gradientTheme();
            }
            if (safeTheme.isAmoled()) {
                return amoledTheme();
            }
            return safeTheme.isDark() ? darkTheme() : lightTheme();
        }
        @Override public String welcomeTabTitle() { return "Welcome"; }
        @Override public String closeTabDescription(String title) { return "Close tab: " + title; }
        @Override public String historyClipboardTitle(int index) { return "History " + (index + 1); }
        @Override public String proStatus(boolean pro) { return "Current status: " + (pro ? "Pro active" : "Free"); }
        @Override public String featureAvailable() { return "Available"; }
        @Override public String featureLocked() { return "Locked"; }
        @Override public String purchaseProAction() { return "Unlock Pro"; }
        @Override public String restorePurchaseAction() { return "Restore purchase"; }
        @Override public String purchaseMessage(String messageCode) {
            if ("pro_active".equals(messageCode)) {
                return "Pro is active.";
            }
            if ("purchase_available".equals(messageCode)) {
                return "Unlock Pro when you want faster reading across long documents and linked Markdown sets.";
            }
            if ("purchase_pending".equals(messageCode)) {
                return "Purchase is pending.";
            }
            if ("purchase_in_progress".equals(messageCode)) {
                return "Opening purchase...";
            }
            return "Purchase is not available yet.";
        }
        @Override public String purchaseFlowUnavailable() { return "Purchase flow is not connected yet."; }
        @Override public String privacyMessage() {
            return "LocalMD Reader does not collect personal information.\n\n"
                    + "There are no ads, analytics SDKs, login, automatic crash reporting, or network permission.\n\n"
                    + "Selected Markdown files are rendered on your device and are not uploaded by the app.\n\n"
                    + "Recent file and tab restoration metadata stays in app-private storage and can be removed by clearing history or app data.";
        }
    }

    private static final class JapaneseViewerText extends ViewerText {
        @Override public String menuButton() { return "メニュー"; }
        @Override public String openMenuDescription() { return "メニューを開く"; }
        @Override public String openFile() { return "ファイルを開く"; }
        @Override public String createFromClipboard() { return "クリップボードから作成"; }
        @Override public String saveAs() { return "名前を付けて保存"; }
        @Override public String exportAsHtml() { return "HTMLとしてエクスポート"; }
        @Override public String printOrSavePdf() { return "印刷 / PDFとして保存"; }
        @Override public String exportedHtml() { return "HTMLファイルをエクスポートしました。"; }
        @Override public String htmlExportFailed() { return "HTMLファイルをエクスポートできませんでした。"; }
        @Override public String switchLanguage() { return "Switch to English"; }
        @Override public String filesSection() { return "ファイル"; }
        @Override public String readingSection() { return "表示"; }
        @Override public String layoutSection() { return "レイアウト"; }
        @Override public String infoSection() { return "情報"; }
        @Override public String settings() { return "設定"; }
        @Override public String tableOfContents() { return "目次"; }
        @Override public String noHeadings() { return "この文書には見出しがありません。"; }
        @Override public String findInDocument() { return "文書内検索"; }
        @Override public String searchHint() { return "検索する文字"; }
        @Override public String findAction() { return "検索"; }
        @Override public String previousSearchResult() { return "前へ"; }
        @Override public String nextSearchResult() { return "次へ"; }
        @Override public String pinnedFiles() { return "ピン留めしたファイル"; }
        @Override public String pinCurrentFile() { return "現在のファイルをピン留め"; }
        @Override public String unpinCurrentFile() { return "現在のファイルのピン留めを解除"; }
        @Override public String noPinnedFiles() { return "ピン留めしたファイルはまだありません。"; }
        @Override public String clearPinnedFiles() { return "ピン留めをクリア"; }
        @Override public String currentFilePinned() { return "現在のファイルをピン留めしました。"; }
        @Override public String currentFileUnpinned() { return "現在のファイルのピン留めを解除しました。"; }
        @Override public String pinnedFilesCleared() { return "ピン留めをクリアしました。"; }
        @Override public String appearanceSettings() { return "表示設定"; }
        @Override public String proFeatures() { return "Pro機能"; }
        @Override public String proFeaturesIntro() { return "Free版はオフライン閲覧の基本機能を備えています。Proでは長い文書や関連するプロジェクト文書を、より速く快適に読めます。"; }
        @Override public ProFeatureDescriptor[] proFeatureCatalog() {
            return new ProFeatureDescriptor[] {
                new ProFeatureDescriptor(ViewerFeature.EXTRA_THEMES,
                        "より快適な閲覧テーマ",
                        "ライトとダークだけでは読みづらい環境に合わせて、追加の配色を選べます。"),
                new ProFeatureDescriptor(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS,
                        "より多くのジェスチャーショートカット",
                        "円、方向、カスタムジェスチャーに、よく使う操作を割り当てられます。"),
                new ProFeatureDescriptor(ViewerFeature.TABLE_OF_CONTENTS,
                        "長い文書をすばやく移動",
                        "目次を開いて、読んでいた位置を失わずに見出しへ移動できます。"),
                new ProFeatureDescriptor(ViewerFeature.HEADING_JUMP,
                        "見出し移動ショートカット",
                        "閲覧中にジェスチャーで前後の見出しへ移動できます。"),
                new ProFeatureDescriptor(ViewerFeature.TABLE_READING_ENHANCEMENTS,
                        "横に広い表を読みやすく",
                        "横に広い表を読むとき、見出し行と先頭列を表示したままにできます。"),
                new ProFeatureDescriptor(ViewerFeature.EXTENDED_RECENT_FILES,
                        "より多くの閲覧履歴",
                        "多数のローカル文書を切り替えるとき、より多くの最近開いたファイルを保持します。"),
                new ProFeatureDescriptor(ViewerFeature.RELATIVE_LINKS,
                        "つながったプロジェクトノート",
                        "ローカルの文書セット内にある安全な相対Markdownリンクを開けます。"),
                new ProFeatureDescriptor(ViewerFeature.RELATIVE_IMAGES,
                        "プロジェクトノート内のローカル画像",
                        "ローカルのMarkdown文書セット内にある安全な相対画像を表示します。"),
                new ProFeatureDescriptor(ViewerFeature.EXPORT_OPTIONS,
                        "エクスポートと印刷",
                        "HTMLとして保存するか、Androidの印刷機能からPDFとして保存できます。"),
                new ProFeatureDescriptor(ViewerFeature.PROJECT_LIBRARY,
                        "プロジェクトフォルダーを移動",
                        "ライブラリを閉じずに、階層を移動して関連するMarkdownファイルを開けます。")
            };
        }
        @Override public String clipboardDiagnostics() { return "クリップボード診断"; }
        @Override public String privacy() { return "プライバシー"; }
        @Override public String moveControlsToTop() { return "操作バーを上に移動"; }
        @Override public String moveControlsToBottom() { return "操作バーを下に移動"; }
        @Override public String gestures() { return "ジェスチャー"; }
        @Override public String doubleTapPrefix() { return "ダブルタップ: "; }
        @Override public String circleGesturePrefix() { return "円ジェスチャー: "; }
        @Override public String customGesturePrefix() { return "カスタムジェスチャー: "; }
        @Override public String registerCustomGesture() { return "カスタムジェスチャーを登録"; }
        @Override public String changeCustomGestureAction() { return "カスタムジェスチャーの動作を変更"; }
        @Override public String clearCustomGesture() { return "カスタムジェスチャーを削除"; }
        @Override public String customGestureRegistrationComingSoon() { return "カスタムジェスチャーを描いて登録する画面は次に追加します。"; }
        @Override public String customGestureCleared() { return "カスタムジェスチャーを削除しました。"; }
        @Override public String drawCustomGestureInstruction() { return "ジェスチャーを1つ描いてください"; }
        @Override public String customGestureTooSmall() { return "もう少し大きく描いてください。"; }
        @Override public String proOnly() { return "Proで利用可能"; }
        @Override public String openMenu() { return "メニューを開く"; }
        @Override public String previousTabAction() { return "前のタブ"; }
        @Override public String nextTabAction() { return "次のタブ"; }
        @Override public String nextThemeAction() { return "テーマ切り替え"; }
        @Override public String moveControlsAction() { return "操作バー移動"; }
        @Override public String showSearchBarAction() { return "検索バーを表示"; }
        @Override public String nextHeadingAction() { return "次の見出し"; }
        @Override public String previousHeadingAction() { return "前の見出し"; }
        @Override public String off() { return "オフ"; }
        @Override public String recentFiles() { return "最近開いたファイル"; }
        @Override public String openMarkdownFile() { return "Markdownファイルを開く"; }
        @Override public String noRecentFiles() { return "最近開いたファイルはまだありません。"; }
        @Override public String noMarkdownFilesInFolder() { return "このフォルダには .md / .markdown ファイルがありません。"; }
        @Override public String chooseAnotherFolder() { return "別のフォルダーを選ぶ"; }
        @Override public String markdownLibrary() { return "Markdownライブラリ"; }
        @Override public String libraryFilterHint() { return "ファイル・フォルダー名で絞り込み"; }
        @Override public String noMatchingLibraryItems() { return "一致するファイルやフォルダーはありません"; }
        @Override public String openMarkdownLibrary() { return "Markdownライブラリ"; }
        @Override public String libraryFolderGuidanceTitle() { return "ライブラリフォルダーを選ぶ"; }
        @Override public String libraryFolderGuidanceMessage() {
            return "Androidの制限により、ダウンロードフォルダー自体は使用できません。"
                    + "その中のサブフォルダーを選ぶか、新しく作成してください。";
        }
        @Override public String chooseLibraryFolder() { return "フォルダーを選ぶ"; }
        @Override public String upOneFolder() { return "一つ上のフォルダーへ"; }
        @Override public String folderEntry(String displayName) { return displayName + "/"; }
        @Override public String clearHistory() { return "履歴をクリア"; }
        @Override public String recentFilesCleared() { return "最近開いたファイルをクリアしました。"; }
        @Override public String noTextToCreate() { return "Markdownファイルを作成できる選択テキストがありません。"; }
        @Override public String noClipboardText() { return "クリップボードにMarkdownファイルを作成できるテキストがありません。"; }
        @Override public String clipboardItemsToOpen() { return "開くクリップボード項目"; }
        @Override public String openSelected() { return "選択した項目を開く"; }
        @Override public String cancel() { return "キャンセル"; }
        @Override public String close() { return "閉じる"; }
        @Override public String noClipboardItemSelected() { return "開くクリップボード項目を選択してください。"; }
        @Override public String noDocumentToSave() { return "保存できるMarkdown文書がありません。"; }
        @Override public String createMarkdownFailed() { return "Markdownファイルを作成できませんでした。"; }
        @Override public String temporaryMarkdown() { return "一時的なMarkdownとして開きました。保存するには「名前を付けて保存」を使ってください。"; }
        @Override public String selectedTextMarkdown() { return "選択テキストを一時的なMarkdownとして開きました。書式が崩れる場合は、コピーしてから「クリップボードから作成」を使ってください。"; }
        @Override public String savedMarkdown() { return "Markdownファイルを保存しました。"; }
        @Override public String unsupportedFile() { return ".md と .markdown ファイルのみ対応しています。"; }
        @Override public String fileTooLarge() { return "10 MB を超えるファイルは開けません。"; }
        @Override public String unreadableFile() { return "文書を読み取れませんでした。ファイルが移動または削除されたか、継続的な読み取り権限がない可能性があります。"; }
        @Override public String darkTheme() { return "ダークテーマ"; }
        @Override public String lightTheme() { return "ライトテーマ"; }
        @Override public String amoledTheme() { return "AMOLEDテーマ"; }
        @Override public String gradientTheme() { return "グラデーションテーマ"; }
        @Override public String auroraTheme() { return "オーロラテーマ"; }
        @Override public String mistTheme() { return "ミストテーマ"; }
        @Override public String duskTheme() { return "夕暮れテーマ"; }
        @Override public String themeLabel(ViewerTheme theme) {
            ViewerTheme safeTheme = theme == null ? ViewerTheme.light() : theme;
            if (safeTheme.isAurora()) {
                return auroraTheme();
            }
            if (safeTheme.isMist()) {
                return mistTheme();
            }
            if (safeTheme.isDusk()) {
                return duskTheme();
            }
            if (safeTheme.isGradient()) {
                return gradientTheme();
            }
            if (safeTheme.isAmoled()) {
                return amoledTheme();
            }
            return safeTheme.isDark() ? darkTheme() : lightTheme();
        }
        @Override public String welcomeTabTitle() { return "ホーム"; }
        @Override public String closeTabDescription(String title) { return "タブを閉じる: " + title; }
        @Override public String historyClipboardTitle(int index) { return "履歴 " + (index + 1); }
        @Override public String proStatus(boolean pro) { return "現在の状態: " + (pro ? "Pro有効" : "Free"); }
        @Override public String featureAvailable() { return "利用可能"; }
        @Override public String featureLocked() { return "ロック中"; }
        @Override public String purchaseProAction() { return "Proを購入"; }
        @Override public String restorePurchaseAction() { return "購入を復元"; }
        @Override public String purchaseMessage(String messageCode) {
            if ("pro_active".equals(messageCode)) {
                return "Proが有効です。";
            }
            if ("purchase_available".equals(messageCode)) {
                return "長い文書や関連するMarkdown群をもっと速く読む必要が出てきたら、Proを利用できます。";
            }
            if ("purchase_pending".equals(messageCode)) {
                return "購入は保留中です。";
            }
            if ("purchase_in_progress".equals(messageCode)) {
                return "購入画面を開いています。";
            }
            return "購入はまだ利用できません。";
        }
        @Override public String purchaseFlowUnavailable() { return "購入導線はまだ準備中です。"; }
        @Override public String privacyMessage() {
            return "LocalMD Reader は個人情報を収集しません。\n\n"
                    + "広告、解析SDK、ログイン、自動クラッシュ送信、ネットワーク権限はありません。\n\n"
                    + "選択したMarkdownは端末上で表示され、アプリによってアップロードされません。\n\n"
                    + "最近開いたファイルとタブ復元の情報は端末内のアプリ専用領域に保存され、履歴クリアまたはアプリデータ削除で消去できます。";
        }
    }
}
