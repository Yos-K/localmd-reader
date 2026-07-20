package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class FolderBrowsingActionTest {
    @Test
    void freeFolderModeExposesChooseFromFolderAction() {
        FolderBrowsingAction action =
                FolderBrowsingMode.from(FeatureEntitlement.free()).action();

        TestAssertions.assertTrue(action instanceof FolderBrowsingAction.ChooseFromFolder,
                "Free mode must expose the Android folder-selection action");
    }

    @Test
    void proFolderModeExposesOpenProjectLibraryAction() {
        FolderBrowsingAction action =
                FolderBrowsingMode.from(FeatureEntitlement.pro()).action();

        TestAssertions.assertTrue(action instanceof FolderBrowsingAction.OpenProjectLibrary,
                "Pro mode must expose the remembered project-library action");
    }

    @Test
    void flatFolderSelectionClosesTheMenuBeforeOpeningAndroidPicker() {
        FolderBrowsingAction action =
                FolderBrowsingMode.from(FeatureEntitlement.free()).action();

        TestAssertions.assertTrue(action.closesMenuBeforeOpening(),
                "The one-shot free picker must leave the app menu before opening Android UI");
    }

    @Test
    void projectLibraryKeepsTheMenuOpenToRevealItsPersistentTree() {
        FolderBrowsingAction action =
                FolderBrowsingMode.from(FeatureEntitlement.pro()).action();

        TestAssertions.assertFalse(action.closesMenuBeforeOpening(),
                "The Pro library action must keep its persistent menu tree visible");
    }
}
