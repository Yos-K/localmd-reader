package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.ViewerLanguage;

public final class WelcomeDocumentBuilderTest {
    public static void main(String[] args) {
        WelcomeDocumentBuilderTest test = new WelcomeDocumentBuilderTest();
        test.englishWelcomeDocument_hasPrimaryOpenActionThatCanBeHandledByTheApp();
        test.japaneseWelcomeDocument_hasPrimaryOpenActionThatCanBeHandledByTheApp();
        test.welcomeDocument_doesNotPresentOpenAsANonClickableCard();
    }

    public void englishWelcomeDocument_hasPrimaryOpenActionThatCanBeHandledByTheApp() {
        String html = WelcomeDocumentBuilder.build(ViewerLanguage.english()).value();

        assertContains(html, "href=\"" + WelcomeDocumentBuilder.OPEN_MARKDOWN_URL + "\"");
        assertContains(html, "Open Markdown file");
    }

    public void japaneseWelcomeDocument_hasPrimaryOpenActionThatCanBeHandledByTheApp() {
        String html = WelcomeDocumentBuilder.build(ViewerLanguage.japanese()).value();

        assertContains(html, "href=\"" + WelcomeDocumentBuilder.OPEN_MARKDOWN_URL + "\"");
        assertContains(html, "Markdownファイルを開く");
    }

    public void welcomeDocument_doesNotPresentOpenAsANonClickableCard() {
        String html = WelcomeDocumentBuilder.build(ViewerLanguage.english()).value();

        assertDoesNotContain(html, "<div class=\"welcome-card\"><strong>Open</strong>");
    }

    private static void assertContains(String actual, String expected) {
        if (!actual.contains(expected)) {
            throw new AssertionError("Expected to contain: " + expected + "\nActual: " + actual);
        }
    }

    private static void assertDoesNotContain(String actual, String unexpected) {
        if (actual.contains(unexpected)) {
            throw new AssertionError("Expected not to contain: " + unexpected + "\nActual: " + actual);
        }
    }
}
