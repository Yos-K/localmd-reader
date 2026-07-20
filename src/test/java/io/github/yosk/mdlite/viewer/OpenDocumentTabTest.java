package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class OpenDocumentTabTest {

    @Test
    void welcomeTabHasNoStatusMessage() {
        OpenDocumentTab tab = OpenDocumentTab.welcome("Welcome", "app://welcome", document());

        TestAssertions.assertEquals("", tab.statusMessage().localized(englishText()), "welcome tab must not carry a status message");
    }

    @Test
    void fileDocumentTabHasNoStatusMessage() {
        OpenDocumentTab tab = OpenDocumentTab.fileDocument("README.md", "content://readme", document());

        TestAssertions.assertEquals("", tab.statusMessage().localized(englishText()), "file document tab must not carry a status message");
    }

    @Test
    void clipboardDraftTabHasTemporaryMarkdownStatusMessage() {
        OpenDocumentTab tab = OpenDocumentTab.clipboardDraft("Clipboard.md", "draft://clipboard", document());

        TestAssertions.assertEquals(
                englishText().temporaryMarkdown(),
                tab.statusMessage().localized(englishText()),
                "clipboard draft tab must explain that the document is temporary");
    }

    @Test
    void selectedTextDraftTabHasSelectedTextStatusMessage() {
        OpenDocumentTab tab = OpenDocumentTab.selectedTextDraft("Selected text.md", "draft://selected", document());

        TestAssertions.assertEquals(
                englishText().selectedTextMarkdown(),
                tab.statusMessage().localized(englishText()),
                "selected text draft tab must explain the selected text formatting limitation");
    }

    @Test
    void clipboardDraftTabIsADraftDocumentTabType() {
        OpenDocumentTab tab = OpenDocumentTab.clipboardDraft("Clipboard.md", "draft://clipboard", document());

        TestAssertions.assertTrue(
                tab instanceof OpenDocumentTab.DraftDocumentTab,
                "clipboard draft must be represented by the draft tab type");
    }

    @Test
    void selectedTextDraftTabIsADraftDocumentTabType() {
        OpenDocumentTab tab = OpenDocumentTab.selectedTextDraft("Selected text.md", "draft://selected", document());

        TestAssertions.assertTrue(
                tab instanceof OpenDocumentTab.DraftDocumentTab,
                "selected text draft must be represented by the draft tab type");
    }

    @Test
    void fileDocumentTabIsNotADraftDocumentTabType() {
        OpenDocumentTab tab = OpenDocumentTab.fileDocument("README.md", "content://readme", document());

        TestAssertions.assertFalse(
                tab instanceof OpenDocumentTab.DraftDocumentTab,
                "file document tab must not be represented by the draft tab type");
    }

    @Test
    void fileDocumentTabIsAUserDocumentTabType() {
        OpenDocumentTab tab = OpenDocumentTab.fileDocument("README.md", "content://readme", document());

        TestAssertions.assertTrue(
                tab instanceof OpenDocumentTab.UserDocumentTab,
                "saved file must be represented by the user document tab type");
    }

    @Test
    void clipboardDraftTabIsAUserDocumentTabType() {
        OpenDocumentTab tab = OpenDocumentTab.clipboardDraft("Clipboard.md", "draft://clipboard", document());

        TestAssertions.assertTrue(
                tab instanceof OpenDocumentTab.UserDocumentTab,
                "temporary Markdown must be represented by the user document tab type");
    }

    @Test
    void welcomeTabIsNotAUserDocumentTabType() {
        OpenDocumentTab tab = OpenDocumentTab.welcome("Welcome", "app://welcome", document());

        TestAssertions.assertFalse(
                tab instanceof OpenDocumentTab.UserDocumentTab,
                "generated welcome content must stay outside the user document tab type");
    }

    @Test
    void welcomeTabCanReplaceItsRenderedDocument() {
        OpenDocumentTab tab = OpenDocumentTab.welcome("Welcome", "app://welcome", document())
                .withDocument(updatedDocument());

        TestAssertions.assertEquals(
                "<p>Updated</p>",
                tab.document().value(),
                "welcome tab must expose the replacement rendered document");
    }

    @Test
    void fileTabCanReplaceItsRenderedDocument() {
        OpenDocumentTab tab = OpenDocumentTab.fileDocument("README.md", "content://readme", document())
                .withDocument(updatedDocument());

        TestAssertions.assertEquals(
                "<p>Updated</p>",
                tab.document().value(),
                "file tab must expose the replacement rendered document");
    }

    @Test
    void clipboardDraftCanReplaceItsRenderedDocument() {
        OpenDocumentTab tab = OpenDocumentTab.clipboardDraft("Clipboard.md", "draft://clipboard", document())
                .withDocument(updatedDocument());

        TestAssertions.assertEquals(
                "<p>Updated</p>",
                tab.document().value(),
                "clipboard draft must expose the replacement rendered document");
    }

    @Test
    void selectedTextDraftCanReplaceItsRenderedDocument() {
        OpenDocumentTab tab = OpenDocumentTab.selectedTextDraft("Selected text.md", "draft://selected", document())
                .withDocument(updatedDocument());

        TestAssertions.assertEquals(
                "<p>Updated</p>",
                tab.document().value(),
                "selected text draft must expose the replacement rendered document");
    }

    private static SafeHtml document() {
        return SafeHtml.fromTrustedRendererOutput("<p>Markdown</p>");
    }

    private static SafeHtml updatedDocument() {
        return SafeHtml.fromTrustedRendererOutput("<p>Updated</p>");
    }

    private static ViewerText englishText() {
        return ViewerText.fromLanguage(ViewerLanguage.english());
    }
}
