package io.github.yosk.mdlite.domain;

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
    public abstract String switchLanguage();
    public abstract String filesSection();
    public abstract String readingSection();
    public abstract String layoutSection();
    public abstract String infoSection();
    public abstract String proFeatures();
    public abstract String clipboardDiagnostics();
    public abstract String privacy();
    public abstract String moveControlsToTop();
    public abstract String moveControlsToBottom();
    public abstract String gestures();
    public abstract String doubleTapPrefix();
    public abstract String circleGesturePrefix();
    public abstract String proOnly();
    public abstract String openMenu();
    public abstract String nextThemeAction();
    public abstract String moveControlsAction();
    public abstract String off();
    public abstract String recentFiles();
    public abstract String openMarkdownFile();
    public abstract String noRecentFiles();
    public abstract String clearHistory();
    public abstract String recentFilesCleared();
    public abstract String noTextToCreate();
    public abstract String noClipboardText();
    public abstract String clipboardItemsToOpen();
    public abstract String openSelected();
    public abstract String cancel();
    public abstract String noClipboardItemSelected();
    public abstract String noDocumentToSave();
    public abstract String createMarkdownFailed();
    public abstract String temporaryMarkdown();
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
    public abstract String purchaseFlowUnavailable();
    public abstract String privacyMessage();

    private static final class EnglishViewerText extends ViewerText {
        @Override public String menuButton() { return "☰ Menu"; }
        @Override public String openMenuDescription() { return "Open menu"; }
        @Override public String openFile() { return "Open file"; }
        @Override public String createFromClipboard() { return "Create from clipboard"; }
        @Override public String saveAs() { return "Save as..."; }
        @Override public String switchLanguage() { return "日本語に切り替え"; }
        @Override public String filesSection() { return "Files"; }
        @Override public String readingSection() { return "Reading"; }
        @Override public String layoutSection() { return "Layout"; }
        @Override public String infoSection() { return "Info"; }
        @Override public String proFeatures() { return "Pro features"; }
        @Override public String clipboardDiagnostics() { return "Clipboard diagnostics"; }
        @Override public String privacy() { return "Privacy"; }
        @Override public String moveControlsToTop() { return "Move controls to top"; }
        @Override public String moveControlsToBottom() { return "Move controls to bottom"; }
        @Override public String gestures() { return "Gestures"; }
        @Override public String doubleTapPrefix() { return "Double tap: "; }
        @Override public String circleGesturePrefix() { return "Circle gesture: "; }
        @Override public String proOnly() { return "Pro only"; }
        @Override public String openMenu() { return "Open menu"; }
        @Override public String nextThemeAction() { return "Next theme"; }
        @Override public String moveControlsAction() { return "Move controls"; }
        @Override public String off() { return "Off"; }
        @Override public String recentFiles() { return "Recent files"; }
        @Override public String openMarkdownFile() { return "Open Markdown file"; }
        @Override public String noRecentFiles() { return "No recent files yet."; }
        @Override public String clearHistory() { return "Clear history"; }
        @Override public String recentFilesCleared() { return "Recent files cleared."; }
        @Override public String noTextToCreate() { return "There is no selected text to create a Markdown file."; }
        @Override public String noClipboardText() { return "There is no clipboard text to create a Markdown file."; }
        @Override public String clipboardItemsToOpen() { return "Clipboard items to open"; }
        @Override public String openSelected() { return "Open selected"; }
        @Override public String cancel() { return "Cancel"; }
        @Override public String noClipboardItemSelected() { return "Select clipboard items to open."; }
        @Override public String noDocumentToSave() { return "There is no Markdown document to save."; }
        @Override public String createMarkdownFailed() { return "The Markdown file could not be created."; }
        @Override public String temporaryMarkdown() { return "Opened as temporary Markdown. Use Save as... to keep it as a file."; }
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
        @Override public String purchaseFlowUnavailable() { return "Purchase flow is not connected yet."; }
        @Override public String privacyMessage() {
            return "LocalMD Reader does not collect personal information.\n\n"
                    + "There are no ads, analytics SDKs, login, automatic crash reporting, or network permission.\n\n"
                    + "Selected Markdown files are rendered on your device and are not uploaded by the app.\n\n"
                    + "Recent file and tab restoration metadata stays in app-private storage and can be removed by clearing history or app data.";
        }
    }

    private static final class JapaneseViewerText extends ViewerText {
        @Override public String menuButton() { return "☰ メニュー"; }
        @Override public String openMenuDescription() { return "メニューを開く"; }
        @Override public String openFile() { return "ファイルを開く"; }
        @Override public String createFromClipboard() { return "クリップボードから作成"; }
        @Override public String saveAs() { return "名前を付けて保存"; }
        @Override public String switchLanguage() { return "Switch to English"; }
        @Override public String filesSection() { return "ファイル"; }
        @Override public String readingSection() { return "表示"; }
        @Override public String layoutSection() { return "レイアウト"; }
        @Override public String infoSection() { return "情報"; }
        @Override public String proFeatures() { return "Pro機能"; }
        @Override public String clipboardDiagnostics() { return "クリップボード診断"; }
        @Override public String privacy() { return "プライバシー"; }
        @Override public String moveControlsToTop() { return "操作バーを上に移動"; }
        @Override public String moveControlsToBottom() { return "操作バーを下に移動"; }
        @Override public String gestures() { return "ジェスチャー"; }
        @Override public String doubleTapPrefix() { return "ダブルタップ: "; }
        @Override public String circleGesturePrefix() { return "円ジェスチャー: "; }
        @Override public String proOnly() { return "Proで利用可能"; }
        @Override public String openMenu() { return "メニューを開く"; }
        @Override public String nextThemeAction() { return "テーマ切り替え"; }
        @Override public String moveControlsAction() { return "操作バー移動"; }
        @Override public String off() { return "オフ"; }
        @Override public String recentFiles() { return "最近開いたファイル"; }
        @Override public String openMarkdownFile() { return "Markdownファイルを開く"; }
        @Override public String noRecentFiles() { return "最近開いたファイルはまだありません。"; }
        @Override public String clearHistory() { return "履歴をクリア"; }
        @Override public String recentFilesCleared() { return "最近開いたファイルをクリアしました。"; }
        @Override public String noTextToCreate() { return "Markdownファイルを作成できる選択テキストがありません。"; }
        @Override public String noClipboardText() { return "クリップボードにMarkdownファイルを作成できるテキストがありません。"; }
        @Override public String clipboardItemsToOpen() { return "開くクリップボード項目"; }
        @Override public String openSelected() { return "選択した項目を開く"; }
        @Override public String cancel() { return "キャンセル"; }
        @Override public String noClipboardItemSelected() { return "開くクリップボード項目を選択してください。"; }
        @Override public String noDocumentToSave() { return "保存できるMarkdown文書がありません。"; }
        @Override public String createMarkdownFailed() { return "Markdownファイルを作成できませんでした。"; }
        @Override public String temporaryMarkdown() { return "一時的なMarkdownとして開きました。保存するには「名前を付けて保存」を使ってください。"; }
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
        @Override public String purchaseFlowUnavailable() { return "購入導線はまだ準備中です。"; }
        @Override public String privacyMessage() {
            return "LocalMD Reader は個人情報を収集しません。\n\n"
                    + "広告、解析SDK、ログイン、自動クラッシュ送信、ネットワーク権限はありません。\n\n"
                    + "選択したMarkdownは端末上で表示され、アプリによってアップロードされません。\n\n"
                    + "最近開いたファイルとタブ復元の情報は端末内のアプリ専用領域に保存され、履歴クリアまたはアプリデータ削除で消去できます。";
        }
    }
}
