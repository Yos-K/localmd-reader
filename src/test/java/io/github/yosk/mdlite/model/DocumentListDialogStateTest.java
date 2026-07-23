package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.file.PinnedDocuments;
import io.github.yosk.mdlite.file.RecentDocument;
import io.github.yosk.mdlite.file.RecentDocuments;
import io.github.yosk.mdlite.testing.TestAssertions;
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
    void selectingAPinnedDocumentRequestsItsAvailableActions() {
        DocumentListDialogState state = DocumentListDialogState.pinned(
                PinnedDocuments.empty(5).pin(document()));

        TestAssertions.assertTrue(state.select(0) instanceof DocumentListCommand.ChoosePinnedDocumentAction,
                "selecting a pinned item must offer opening and individual unpinning");
    }

    @Test
    void selectingOpenFromPinnedDocumentActionsRequestsOpeningThatDocument() {
        DocumentListDialogState state = DocumentListDialogState.pinnedActions(document());

        TestAssertions.assertTrue(state.select(0) instanceof DocumentListCommand.OpenDocument,
                "the first pinned-document action must open the selected document");
    }

    @Test
    void selectingUnpinFromPinnedDocumentActionsRequestsIndividualUnpin() {
        DocumentListDialogState state = DocumentListDialogState.pinnedActions(document());

        TestAssertions.assertTrue(state.select(1) instanceof DocumentListCommand.UnpinDocument,
                "the second pinned-document action must remove only the selected bookmark");
    }

    @Test
    void selectingOutsidePinnedDocumentActionsProducesNoCommand() {
        DocumentListDialogState state = DocumentListDialogState.pinnedActions(document());

        TestAssertions.assertTrue(state.select(2) instanceof DocumentListCommand.None,
                "an invalid pinned-document action must remain a total no-op");
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
    void selectingWhileClosedProducesNoCommand() {
        TestAssertions.assertTrue(
                DocumentListDialogState.closed().select(0) instanceof DocumentListCommand.None,
                "a delayed callback after closing must remain a total no-op");
    }

    private static RecentDocument document() {
        return RecentDocument.of("guide.md", "content://guide");
    }

}
