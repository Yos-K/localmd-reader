package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class ViewerTextTest {
    public static void main(String[] args) {
        ViewerTextTest test = new ViewerTextTest();
        test.englishViewerTextProvidesEnglishTemporaryMarkdownMessage();
        test.japaneseViewerTextProvidesJapaneseTemporaryMarkdownMessage();
        test.viewerTextFromLanguageUsesLanguageSpecificTextSet();
        test.languageSwitchActionDescribesTargetLanguage();
        test.themeLabelComesFromViewerTextLanguageSet();
        test.clipboardActionLabelSetsFormattingExpectation();
        test.selectedTextMessageExplainsClipboardFallback();
    }

    public void englishViewerTextProvidesEnglishTemporaryMarkdownMessage() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.english());

        TestAssertions.assertEquals(
                "Opened as temporary Markdown. Use Save as... to keep it as a file.",
                text.temporaryMarkdown(),
                "English viewer text must provide the English temporary Markdown message");
    }

    public void japaneseViewerTextProvidesJapaneseTemporaryMarkdownMessage() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals(
                "一時的なMarkdownとして開きました。保存するには「名前を付けて保存」を使ってください。",
                text.temporaryMarkdown(),
                "Japanese viewer text must provide the Japanese temporary Markdown message");
    }

    public void viewerTextFromLanguageUsesLanguageSpecificTextSet() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals(
                "☰ メニュー",
                text.menuButton(),
                "Viewer text must be selected as one language-specific set");
    }

    public void languageSwitchActionDescribesTargetLanguage() {
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

    public void themeLabelComesFromViewerTextLanguageSet() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals(
                "オーロラテーマ",
                text.themeLabel(ViewerTheme.aurora()),
                "Theme label must be selected from the active viewer text set");
    }

    public void clipboardActionLabelSetsFormattingExpectation() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.english());

        TestAssertions.assertEquals(
                "Create from clipboard (keeps formatting)",
                text.createFromClipboard(),
                "Clipboard action label must set the formatting expectation");
    }

    public void selectedTextMessageExplainsClipboardFallback() {
        ViewerText text = ViewerText.fromLanguage(ViewerLanguage.japanese());

        TestAssertions.assertEquals(
                "選択テキストを一時的なMarkdownとして開きました。書式が崩れる場合は、コピーしてから「クリップボードから作成」を使ってください。",
                text.selectedTextMarkdown(),
                "Selected text message must explain the clipboard fallback when formatting is missing");
    }
}
