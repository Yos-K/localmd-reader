package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.viewer.ViewerLanguage;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class WelcomeDocumentBuilderTest {

    @Test
    void englishWelcomeDocument_hasPrimaryOpenActionThatCanBeHandledByTheApp() {
        String html = WelcomeDocumentBuilder.build(ViewerLanguage.english()).value();

        TestAssertions.assertContains(html, "href=\"" + WelcomeDocumentBuilder.OPEN_MARKDOWN_URL + "\"", "English welcome document must link the primary open action");
        TestAssertions.assertContains(html, "Open Markdown file", "English welcome document must label the primary open action");
    }

    @Test
    void japaneseWelcomeDocument_hasPrimaryOpenActionThatCanBeHandledByTheApp() {
        String html = WelcomeDocumentBuilder.build(ViewerLanguage.japanese()).value();

        TestAssertions.assertContains(html, "href=\"" + WelcomeDocumentBuilder.OPEN_MARKDOWN_URL + "\"", "Japanese welcome document must link the primary open action");
        TestAssertions.assertContains(html, "Markdownファイルを開く", "Japanese welcome document must label the primary open action");
    }

    @Test
    void welcomeDocument_doesNotPresentOpenAsANonClickableCard() {
        String html = WelcomeDocumentBuilder.build(ViewerLanguage.english()).value();

        TestAssertions.assertNotContains(html, "<div class=\"welcome-card\"><strong>Open</strong>", "Welcome document must not present Open as a non-clickable card");
    }

    @Test
    void englishWelcomeDocument_labelsRecentFilesCardByItsFeature() {
        String html = WelcomeDocumentBuilder.build(ViewerLanguage.english()).value();

        TestAssertions.assertContains(html, "<div class=\"welcome-card\"><strong>Recent files</strong>", "English welcome card must name the recent files feature");
        TestAssertions.assertNotContains(html, "<div class=\"welcome-card\"><strong>Return</strong>", "English welcome card must not describe recent files as Return");
    }

    @Test
    void japaneseWelcomeDocument_labelsRecentFilesCardByItsFeature() {
        String html = WelcomeDocumentBuilder.build(ViewerLanguage.japanese()).value();

        TestAssertions.assertContains(html, "<div class=\"welcome-card\"><strong>最近開いたファイル</strong>", "Japanese welcome card must name the recent files feature");
        TestAssertions.assertNotContains(html, "<div class=\"welcome-card\"><strong>戻る</strong>", "Japanese welcome card must not describe recent files as 戻る");
    }
}
