package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class TabPinningDecisionTest {
    @Test
    void proFileTabWithoutPinProducesPinDecision() {
        TabPinningDecision decision = TabPinningDecision.from(
                true, fileTab(), false);

        TestAssertions.assertTrue(decision instanceof TabPinningDecision.Pin,
                "An unpinned Pro file tab must be pinnable from long press");
    }

    @Test
    void proPinnedFileTabProducesUnpinDecision() {
        TabPinningDecision decision = TabPinningDecision.from(
                true, fileTab(), true);

        TestAssertions.assertTrue(decision instanceof TabPinningDecision.Unpin,
                "A pinned Pro file tab must be unpinnable from the same long press");
    }

    @Test
    void freeFileTabProducesUnavailableDecision() {
        TabPinningDecision decision = TabPinningDecision.from(
                false, fileTab(), false);

        TestAssertions.assertTrue(decision instanceof TabPinningDecision.Unavailable,
                "Free tabs must not expose the Pro pinning interaction");
    }

    @Test
    void draftTabProducesUnavailableDecision() {
        TabPinningDecision decision = TabPinningDecision.from(
                true, OpenDocumentTab.clipboardDraft("Draft", "draft://1", SafeHtml.fromTrustedRendererOutput("")), false);

        TestAssertions.assertTrue(decision instanceof TabPinningDecision.Unavailable,
                "Temporary tabs must not enter persistent pinned files");
    }

    @Test
    void pinnedTabDescriptionCommunicatesItsPersistentBookmark() {
        TabPinningDecision decision = TabPinningDecision.from(true, fileTab(), true);

        TestAssertions.assertEquals("Pinned tab: guide.md",
                decision.tabDescription(ViewerText.fromLanguage(ViewerLanguage.english()), "guide.md"),
                "A pinned tab must expose its bookmark state to accessibility services");
    }

    @Test
    void unpinnedTabDescriptionDoesNotClaimToBePinned() {
        TabPinningDecision decision = TabPinningDecision.from(true, fileTab(), false);

        TestAssertions.assertEquals("Tab: guide.md",
                decision.tabDescription(ViewerText.fromLanguage(ViewerLanguage.english()), "guide.md"),
                "An unpinned file tab must expose only its tab identity");
    }

    private static OpenDocumentTab fileTab() {
        return OpenDocumentTab.fileDocument("guide.md", "content://guide",
                SafeHtml.fromTrustedRendererOutput(""));
    }
}
