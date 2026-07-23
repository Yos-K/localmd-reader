package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.testing.TestAssertions;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public final class SavedDocumentPlacementTest {
    @Test
    void savingAClipboardDraftReplacesItsTemporaryTab() {
        OpenDocumentTab draft = OpenDocumentTab.clipboardDraft(
                "Draft.md", "draft://clipboard/1", html("draft"));
        OpenDocumentTabs placed = SavedDocumentPlacement.from(draft).place(
                OpenDocumentTabs.withInitialTab(file("Other.md", "content://other")).open(draft),
                file("Saved.md", "content://saved"));

        TestAssertions.assertEquals("content://other,content://saved",
                placed.tabs().stream().map(OpenDocumentTab::uri).collect(Collectors.joining(",")),
                "saving a draft must replace its temporary identity instead of adding a third tab");
    }

    @Test
    void savingARegularFileOpensTheSavedDestinationNormally() {
        OpenDocumentTab source = file("Source.md", "content://source");
        OpenDocumentTabs placed = SavedDocumentPlacement.from(source).place(
                OpenDocumentTabs.withInitialTab(source), file("Copy.md", "content://copy"));

        TestAssertions.assertEquals("content://copy", placed.activeTab().uri(),
                "saving a file copy must open the destination without treating the source as temporary");
    }

    @Test
    void savingToAnAlreadyOpenDestinationRemovesTheDraftWithoutDuplicatingTheFile() {
        OpenDocumentTab draft = OpenDocumentTab.selectedTextDraft(
                "Selection.md", "draft://selection/1", html("draft"));
        OpenDocumentTabs placed = SavedDocumentPlacement.from(draft).place(
                OpenDocumentTabs.withInitialTab(file("Saved.md", "content://saved")).open(draft),
                file("Saved.md", "content://saved"));

        TestAssertions.assertEquals(1, placed.tabs().size(),
                "saving onto an open destination must remove the draft and reuse the file tab");
    }

    private static OpenDocumentTab.FileDocumentTab file(String title, String uri) {
        return (OpenDocumentTab.FileDocumentTab) OpenDocumentTab.fileDocument(title, uri, html(title));
    }

    private static SafeHtml html(String value) {
        return SafeHtml.fromTrustedRendererOutput(value);
    }
}
