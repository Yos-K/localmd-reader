package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class FolderBrowsingModeTest {
    @Test
    void freeEntitlementUsesFlatMarkdownFileSelection() {
        FolderBrowsingMode mode = FolderBrowsingMode.from(FeatureEntitlement.free());

        TestAssertions.assertTrue(mode instanceof FolderBrowsingMode.FlatFolderSelection,
                "Free folder browsing must keep the existing direct Markdown file selection");
    }

    @Test
    void proEntitlementUsesNestedProjectNavigation() {
        FolderBrowsingMode mode = FolderBrowsingMode.from(FeatureEntitlement.pro());

        TestAssertions.assertTrue(mode instanceof FolderBrowsingMode.ProjectFolderNavigation,
                "Pro folder browsing must allow navigation through nested project folders");
    }

    @Test
    void freeFolderActionDoesNotAdvertiseAnExpandableMenuTree() {
        FolderBrowsingAction action = FolderBrowsingMode.from(FeatureEntitlement.free()).action();

        TestAssertions.assertFalse(action.hasExpandableMenuTree(),
                "The ordinary free file action must not display a tree chevron");
    }

    @Test
    void proLibraryActionAdvertisesItsExpandableMenuTree() {
        FolderBrowsingAction action = FolderBrowsingMode.from(FeatureEntitlement.pro()).action();

        TestAssertions.assertTrue(action.hasExpandableMenuTree(),
                "The Pro library action must display its tree chevron");
    }
}
