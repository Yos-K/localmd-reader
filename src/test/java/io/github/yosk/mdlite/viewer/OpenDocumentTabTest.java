package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.testing.TestAssertions;

public final class OpenDocumentTabTest {
    public static void main(String[] args) {
        OpenDocumentTabTest test = new OpenDocumentTabTest();
        test.welcomeTabHasNoStatusMessage();
        test.fileDocumentTabHasNoStatusMessage();
        test.clipboardDraftTabHasTemporaryMarkdownStatusMessage();
        test.selectedTextDraftTabHasSelectedTextStatusMessage();
        test.clipboardDraftTabIsADraftDocumentTabType();
        test.selectedTextDraftTabIsADraftDocumentTabType();
        test.fileDocumentTabIsNotADraftDocumentTabType();
    }

    public void welcomeTabHasNoStatusMessage() {
        OpenDocumentTab tab = OpenDocumentTab.welcome("Welcome", "app://welcome", document());

        TestAssertions.assertEquals("", tab.statusMessage().localized(englishText()), "welcome tab must not carry a status message");
    }

    public void fileDocumentTabHasNoStatusMessage() {
        OpenDocumentTab tab = OpenDocumentTab.fileDocument("README.md", "content://readme", document());

        TestAssertions.assertEquals("", tab.statusMessage().localized(englishText()), "file document tab must not carry a status message");
    }

    public void clipboardDraftTabHasTemporaryMarkdownStatusMessage() {
        OpenDocumentTab tab = OpenDocumentTab.clipboardDraft("Clipboard.md", "draft://clipboard", document());

        TestAssertions.assertEquals(
                englishText().temporaryMarkdown(),
                tab.statusMessage().localized(englishText()),
                "clipboard draft tab must explain that the document is temporary");
    }

    public void selectedTextDraftTabHasSelectedTextStatusMessage() {
        OpenDocumentTab tab = OpenDocumentTab.selectedTextDraft("Selected text.md", "draft://selected", document());

        TestAssertions.assertEquals(
                englishText().selectedTextMarkdown(),
                tab.statusMessage().localized(englishText()),
                "selected text draft tab must explain the selected text formatting limitation");
    }

    public void clipboardDraftTabIsADraftDocumentTabType() {
        OpenDocumentTab tab = OpenDocumentTab.clipboardDraft("Clipboard.md", "draft://clipboard", document());

        TestAssertions.assertTrue(
                tab instanceof OpenDocumentTab.DraftDocumentTab,
                "clipboard draft must be represented by the draft tab type");
    }

    public void selectedTextDraftTabIsADraftDocumentTabType() {
        OpenDocumentTab tab = OpenDocumentTab.selectedTextDraft("Selected text.md", "draft://selected", document());

        TestAssertions.assertTrue(
                tab instanceof OpenDocumentTab.DraftDocumentTab,
                "selected text draft must be represented by the draft tab type");
    }

    public void fileDocumentTabIsNotADraftDocumentTabType() {
        OpenDocumentTab tab = OpenDocumentTab.fileDocument("README.md", "content://readme", document());

        TestAssertions.assertFalse(
                tab instanceof OpenDocumentTab.DraftDocumentTab,
                "file document tab must not be represented by the draft tab type");
    }

    private static SafeHtml document() {
        return SafeHtml.fromTrustedRendererOutput("<p>Markdown</p>");
    }

    private static ViewerText englishText() {
        return ViewerText.fromLanguage(ViewerLanguage.english());
    }
}
