package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.file.FolderDocumentEntry;
import io.github.yosk.mdlite.file.FolderMarkdownDocuments;
import io.github.yosk.mdlite.file.PinnedDocuments;
import io.github.yosk.mdlite.file.RecentDocument;
import io.github.yosk.mdlite.file.RecentDocuments;
import io.github.yosk.mdlite.testing.TestAssertions;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public final class DocumentListDialogStateTest {
    @Test
    void selectingARecentDocumentRequestsOpeningThatDocument() {
        DocumentListDialogState state = DocumentListDialogState.recent(
                RecentDocuments.empty(5).recordOpened(document()));

        DocumentListCommand.OpenDocument command =
                (DocumentListCommand.OpenDocument) state.select(0);

        TestAssertions.assertEquals("content://guide", command.document().uri(),
                "selecting a recent item must retain the selected document identity");
    }

    @Test
    void selectingOutsideTheDisplayedListProducesNoCommand() {
        DocumentListDialogState state = DocumentListDialogState.pinned(
                PinnedDocuments.empty(5).pin(document()));

        TestAssertions.assertTrue(state.select(1) instanceof DocumentListCommand.None,
                "an invalid item index must be a total no-op");
    }

    @Test
    void selectingAPinnedDocumentRequestsOpeningThatDocument() {
        DocumentListDialogState state = DocumentListDialogState.pinned(
                PinnedDocuments.empty(5).pin(document()));

        TestAssertions.assertTrue(state.select(0) instanceof DocumentListCommand.OpenDocument,
                "selecting a pinned item must request opening the selected document");
    }

    @Test
    void selectingAFolderDocumentRequestsOpeningThatDocument() {
        DocumentListDialogState state = DocumentListDialogState.folder(folderDocuments());

        TestAssertions.assertTrue(state.select(0) instanceof DocumentListCommand.OpenDocument,
                "selecting a folder item must request opening the selected document");
    }

    @Test
    void recentDocumentsSecondaryActionClearsRecentHistory() {
        DocumentListDialogState state = DocumentListDialogState.recent(
                RecentDocuments.empty(5).recordOpened(document()));

        TestAssertions.assertTrue(state.secondaryAction() instanceof DocumentListCommand.ClearRecent,
                "the recent dialog secondary action must clear only recent history");
    }

    @Test
    void pinnedDocumentsSecondaryActionClearsPinnedFiles() {
        DocumentListDialogState state = DocumentListDialogState.pinned(
                PinnedDocuments.empty(5).pin(document()));

        TestAssertions.assertTrue(state.secondaryAction() instanceof DocumentListCommand.ClearPinned,
                "the pinned dialog secondary action must clear only pinned files");
    }

    @Test
    void folderDocumentsSecondaryActionChoosesAnotherFolder() {
        DocumentListDialogState state = DocumentListDialogState.folder(folderDocuments());

        TestAssertions.assertTrue(
                state.secondaryAction() instanceof DocumentListCommand.ChooseAnotherFolder,
                "the folder dialog secondary action must reopen folder selection");
    }

    @Test
    void closingAnOpenDocumentListProducesTheClosedState() {
        DocumentListDialogState state = DocumentListDialogState.recent(
                RecentDocuments.empty(5).recordOpened(document()));

        TestAssertions.assertTrue(state.close() instanceof DocumentListDialogState.Closed,
                "closing must remove the active document-list context");
    }

    @Test
    void closingPinnedDocumentsProducesTheClosedState() {
        DocumentListDialogState state = DocumentListDialogState.pinned(
                PinnedDocuments.empty(5).pin(document()));

        TestAssertions.assertTrue(state.close() instanceof DocumentListDialogState.Closed,
                "closing pinned documents must remove the active list context");
    }

    @Test
    void closingFolderDocumentsProducesTheClosedState() {
        DocumentListDialogState state = DocumentListDialogState.folder(folderDocuments());

        TestAssertions.assertTrue(state.close() instanceof DocumentListDialogState.Closed,
                "closing folder documents must remove the active list context");
    }

    @Test
    void selectingWhileClosedProducesNoCommand() {
        TestAssertions.assertTrue(
                DocumentListDialogState.closed().select(0) instanceof DocumentListCommand.None,
                "a delayed callback after closing must remain a total no-op");
    }

    private static RecentDocument document() {
        return RecentDocument.of("guide.md", "content://guide");
    }

    private static FolderMarkdownDocuments folderDocuments() {
        return FolderMarkdownDocuments.from(Collections.singletonList(
                FolderDocumentEntry.markdownFile("guide.md", "content://guide")));
    }
}
