package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.viewer.ViewerLanguage;
import io.github.yosk.mdlite.testing.TestAssertions;

public final class WelcomeDocumentBuilderTest {
    public static void main(String[] args) {
        WelcomeDocumentBuilderTest test = new WelcomeDocumentBuilderTest();
        test.englishWelcomeDocument_hasPrimaryOpenActionThatCanBeHandledByTheApp();
        test.japaneseWelcomeDocument_hasPrimaryOpenActionThatCanBeHandledByTheApp();
        test.welcomeDocument_doesNotPresentOpenAsANonClickableCard();
    }

    public void englishWelcomeDocument_hasPrimaryOpenActionThatCanBeHandledByTheApp() {
        String html = WelcomeDocumentBuilder.build(ViewerLanguage.english()).value();

        TestAssertions.assertContains(html, "href=\"" + WelcomeDocumentBuilder.OPEN_MARKDOWN_URL + "\"", "English welcome document must link the primary open action");
        TestAssertions.assertContains(html, "Open Markdown file", "English welcome document must label the primary open action");
    }

    public void japaneseWelcomeDocument_hasPrimaryOpenActionThatCanBeHandledByTheApp() {
        String html = WelcomeDocumentBuilder.build(ViewerLanguage.japanese()).value();

        TestAssertions.assertContains(html, "href=\"" + WelcomeDocumentBuilder.OPEN_MARKDOWN_URL + "\"", "Japanese welcome document must link the primary open action");
        TestAssertions.assertContains(html, "Markdownファイルを開く", "Japanese welcome document must label the primary open action");
    }

    public void welcomeDocument_doesNotPresentOpenAsANonClickableCard() {
        String html = WelcomeDocumentBuilder.build(ViewerLanguage.english()).value();

        TestAssertions.assertNotContains(html, "<div class=\"welcome-card\"><strong>Open</strong>", "Welcome document must not present Open as a non-clickable card");
    }
}
