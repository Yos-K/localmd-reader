package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class PinnedDocumentMenuVisibilityTest {

    @Test
    void pinCurrentFileIsVisibleOnlyForUnpinnedFileWhenPinnedDocumentsAreAvailable() {
        PinnedDocumentMenuVisibility visibility = PinnedDocumentMenuVisibility.of(true, true, false);

        TestAssertions.assertTrue(visibility.canPinCurrentFile(), "un-pinned file tabs must show the pin action");
        TestAssertions.assertFalse(visibility.canUnpinCurrentFile(), "un-pinned file tabs must not show the unpin action");
    }

    @Test
    void unpinCurrentFileIsVisibleOnlyForPinnedFileWhenPinnedDocumentsAreAvailable() {
        PinnedDocumentMenuVisibility visibility = PinnedDocumentMenuVisibility.of(true, true, true);

        TestAssertions.assertFalse(visibility.canPinCurrentFile(), "pinned file tabs must not show the pin action");
        TestAssertions.assertTrue(visibility.canUnpinCurrentFile(), "pinned file tabs must show the unpin action");
    }

    @Test
    void pinningActionsAreHiddenWhenTheActiveTabIsNotAFile() {
        PinnedDocumentMenuVisibility visibility = PinnedDocumentMenuVisibility.of(true, false, false);

        TestAssertions.assertFalse(visibility.canPinCurrentFile(), "non-file tabs must not show the pin action");
        TestAssertions.assertFalse(visibility.canUnpinCurrentFile(), "non-file tabs must not show the unpin action");
    }

    @Test
    void pinningActionsAreHiddenWhenPinnedDocumentsAreUnavailable() {
        PinnedDocumentMenuVisibility visibility = PinnedDocumentMenuVisibility.of(false, true, false);

        TestAssertions.assertFalse(visibility.canPinCurrentFile(), "unavailable pinned documents must hide the pin action");
        TestAssertions.assertFalse(visibility.canUnpinCurrentFile(), "unavailable pinned documents must hide the unpin action");
    }
}
