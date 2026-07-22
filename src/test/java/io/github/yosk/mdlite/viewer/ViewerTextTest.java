package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ViewerTextTest {

    @Test
    void englishViewerTextProvidesEnglishTemporaryMarkdownMessage() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.english());

        TestAssertions.assertEquals(
                "Opened as temporary Markdown. Use Save as... to keep it as a file.",
                text.temporaryMarkdown(),
                "English viewer text must provide the English temporary Markdown message");
    }

    @Test
    void japaneseViewerTextProvidesJapaneseTemporaryMarkdownMessage() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals(
                "一時的なMarkdownとして開きました。保存するには「名前を付けて保存」を使ってください。",
                text.temporaryMarkdown(),
                "Japanese viewer text must provide the Japanese temporary Markdown message");
    }

    @Test
    void viewerTextFromLanguageUsesLanguageSpecificTextSet() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals(
                "メニュー",
                text.menuButton(),
                "Viewer text must be selected as one language-specific set");
    }

    @Test
    void languageSwitchActionDescribesTargetLanguage() {
        ViewerText englishText = ViewerText.fromLanguage(ViewerLanguage.english());
        ViewerText japaneseText = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals(
                "日本語に切り替え",
                englishText.switchLanguage(),
                "English UI must describe switching to Japanese");
        TestAssertions.assertEquals(
                "Switch to English",
                japaneseText.switchLanguage(),
                "Japanese UI must describe switching to English");
    }

    @Test
    void themeLabelComesFromViewerTextLanguageSet() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals(
                "オーロラテーマ",
                text.themeLabel(ViewerTheme.aurora()),
                "Theme label must be selected from the active viewer text set");
    }

    @Test
    void clipboardActionLabelStaysShortForMenuReadability() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.english());

        TestAssertions.assertEquals(
                "Create from clipboard",
                text.createFromClipboard(),
                "Clipboard action label must stay short for menu readability");
    }

    @Test
    void viewerLanguagesProvideHtmlExportActions() {
        ViewerText englishText = ViewerText.fromLanguage(ViewerLanguage.english());
        ViewerText japaneseText = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals(
                "Export as HTML",
                englishText.exportAsHtml(),
                "English menu must name the HTML export action");
        TestAssertions.assertEquals(
                "HTMLとしてエクスポート",
                japaneseText.exportAsHtml(),
                "Japanese menu must name the HTML export action");
        TestAssertions.assertEquals(
                "Exported HTML file.",
                englishText.exportedHtml(),
                "English completion message must confirm HTML export");
        TestAssertions.assertEquals(
                "HTMLファイルをエクスポートしました。",
                japaneseText.exportedHtml(),
                "Japanese completion message must confirm HTML export");
        TestAssertions.assertEquals(
                "The HTML file could not be exported.",
                englishText.htmlExportFailed(),
                "English failure message must identify HTML export");
        TestAssertions.assertEquals(
                "HTMLファイルをエクスポートできませんでした。",
                japaneseText.htmlExportFailed(),
                "Japanese failure message must identify HTML export");
    }

    @Test
    void viewerLanguagesProvidePrintAndPdfActions() {
        ViewerText englishText = ViewerText.fromLanguage(ViewerLanguage.english());
        ViewerText japaneseText = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals(
                "Print / Save as PDF",
                englishText.printOrSavePdf(),
                "English menu must describe both standard print outcomes");
        TestAssertions.assertEquals(
                "印刷 / PDFとして保存",
                japaneseText.printOrSavePdf(),
                "Japanese menu must describe both standard print outcomes");
    }

    @Test
    void englishViewerTextProvidesDocumentSearchLabels() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.english());

        TestAssertions.assertEquals("Find in document", text.findInDocument(), "English menu must expose document search");
        TestAssertions.assertEquals("Search text", text.searchHint(), "English search dialog must describe the input");
        TestAssertions.assertEquals("Find", text.findAction(), "English search dialog must provide a find action");
        TestAssertions.assertEquals("Previous", text.previousSearchResult(), "English search dialog must provide previous result action");
        TestAssertions.assertEquals("Next", text.nextSearchResult(), "English search dialog must provide next result action");
    }

    @Test
    void englishViewerTextProvidesPinnedDocumentLabels() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.english());

        TestAssertions.assertEquals("Pinned files", text.pinnedFiles(), "English menu must expose pinned files");
        TestAssertions.assertEquals("Pin current file", text.pinCurrentFile(), "English menu must expose pinning the current file");
        TestAssertions.assertEquals("Unpin current file", text.unpinCurrentFile(), "English menu must expose unpinning the current file");
        TestAssertions.assertEquals("No pinned files yet.", text.noPinnedFiles(), "English empty pinned files message");
        TestAssertions.assertEquals("Clear pinned files", text.clearPinnedFiles(), "English clear pinned files action");
        TestAssertions.assertEquals("Pinned current file.", text.currentFilePinned(), "English pin completion message");
        TestAssertions.assertEquals("Unpinned current file.", text.currentFileUnpinned(), "English unpin completion message");
        TestAssertions.assertEquals("Pinned files cleared.", text.pinnedFilesCleared(), "English clear completion message");
    }

    @Test
    void japaneseViewerTextProvidesPinnedDocumentLabels() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals("ピン留めしたファイル", text.pinnedFiles(), "Japanese menu must expose pinned files");
        TestAssertions.assertEquals("現在のファイルをピン留め", text.pinCurrentFile(), "Japanese menu must expose pinning the current file");
        TestAssertions.assertEquals("現在のファイルのピン留めを解除", text.unpinCurrentFile(), "Japanese menu must expose unpinning the current file");
        TestAssertions.assertEquals("ピン留めしたファイルはまだありません。", text.noPinnedFiles(), "Japanese empty pinned files message");
        TestAssertions.assertEquals("ピン留めをクリア", text.clearPinnedFiles(), "Japanese clear pinned files action");
        TestAssertions.assertEquals("現在のファイルをピン留めしました。", text.currentFilePinned(), "Japanese pin completion message");
        TestAssertions.assertEquals("現在のファイルのピン留めを解除しました。", text.currentFileUnpinned(), "Japanese unpin completion message");
        TestAssertions.assertEquals("ピン留めをクリアしました。", text.pinnedFilesCleared(), "Japanese clear completion message");
    }

    @Test
    void viewerTextProvidesCloseActionForReadOnlyDialogs() {
        ViewerText englishText = ViewerText.fromLanguage(ViewerLanguage.english());
        ViewerText japaneseText = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals("Close", englishText.close(), "English read-only dialogs must expose a close action");
        TestAssertions.assertEquals("閉じる", japaneseText.close(), "Japanese read-only dialogs must expose a close action");
    }

    @Test
    void viewerTextProvidesActionToChooseAnotherFolderFromTheMarkdownLibrary() {
        ViewerText englishText = ViewerText.fromLanguage(ViewerLanguage.english());
        ViewerText japaneseText = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals(
                "Choose another folder",
                englishText.chooseAnotherFolder(),
                "English folder document list must let users pick a different folder");
        TestAssertions.assertEquals(
                "別のフォルダーを選ぶ",
                japaneseText.chooseAnotherFolder(),
                "Japanese folder document list must let users pick a different folder");
    }

    @Test
    void japaneseViewerTextProvidesDocumentSearchLabels() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals("文書内検索", text.findInDocument(), "Japanese menu must expose document search");
        TestAssertions.assertEquals("検索する文字", text.searchHint(), "Japanese search dialog must describe the input");
        TestAssertions.assertEquals("検索", text.findAction(), "Japanese search dialog must provide a find action");
        TestAssertions.assertEquals("前へ", text.previousSearchResult(), "Japanese search dialog must provide previous result action");
        TestAssertions.assertEquals("次へ", text.nextSearchResult(), "Japanese search dialog must provide next result action");
    }

    @Test
    void selectedTextMessageExplainsClipboardFallback() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals(
                "選択テキストを一時的なMarkdownとして開きました。書式が崩れる場合は、コピーしてから「クリップボードから作成」を使ってください。",
                text.selectedTextMarkdown(),
                "Selected text message must explain the clipboard fallback when formatting is missing");
    }

}
