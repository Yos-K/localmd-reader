package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProFeaturesPresentationTest {

    @Test
    void freeReadyPresentationShowsFreeStatusLockedFeaturesAndPurchaseAction() {
        ProFeaturesPresentation presentation = ProFeaturesPresentation.from(
                FeatureEntitlement.free(),
                ProPurchaseUiState.ready(),
                englishFeatures());

        TestAssertions.assertFalse(presentation.isPro(), "Free presentation must expose Free status");
        TestAssertions.assertTrue(presentation.purchase().shouldShowAction(), "Free ready billing must expose purchase action");
        TestAssertions.assertEquals("purchase_available", presentation.purchase().messageCode(), "Free ready billing message");
        lockedPresentationItem(presentation.features()[0], "first item");
        lockedPresentationItem(presentation.features()[1], "second item");
        lockedPresentationItem(presentation.features()[2], "third item");
        lockedPresentationItem(presentation.features()[3], "fourth item");
    }

    @Test
    void proPresentationShowsProStatusAvailableFeaturesAndNoPurchaseAction() {
        ProFeaturesPresentation presentation = ProFeaturesPresentation.from(
                FeatureEntitlement.pro(),
                ProPurchaseUiState.ready(),
                englishFeatures());

        TestAssertions.assertTrue(presentation.isPro(), "Pro presentation must expose Pro status");
        TestAssertions.assertFalse(presentation.purchase().shouldShowAction(), "Pro presentation must hide purchase action");
        TestAssertions.assertEquals("pro_active", presentation.purchase().messageCode(), "Pro active message");
        availablePresentationItem(presentation.features()[0], "first item");
        availablePresentationItem(presentation.features()[1], "second item");
        availablePresentationItem(presentation.features()[2], "third item");
        availablePresentationItem(presentation.features()[3], "fourth item");
    }

    @Test
    void unavailableBillingKeepsFreeFeaturesLockedAndHidesPurchaseAction() {
        ProFeaturesPresentation presentation = ProFeaturesPresentation.from(
                FeatureEntitlement.free(),
                ProPurchaseUiState.unavailable(),
                englishFeatures());

        TestAssertions.assertFalse(presentation.isPro(), "Unavailable billing must keep Free status");
        TestAssertions.assertFalse(presentation.purchase().shouldShowAction(), "Unavailable billing must hide purchase action");
        TestAssertions.assertEquals("purchase_unavailable", presentation.purchase().messageCode(), "Unavailable billing message");
        lockedPresentationItem(presentation.features()[0], "first item");
        lockedPresentationItem(presentation.features()[1], "second item");
        lockedPresentationItem(presentation.features()[2], "third item");
        lockedPresentationItem(presentation.features()[3], "fourth item");
    }

    // features() hands back a clone: mutating the returned array must not corrupt the
    // presentation's internal state. (exploration 2026-06-13 P9)
    @Test
    void featuresReturnsADefensiveCopy() {
        ProFeaturesPresentation presentation = ProFeaturesPresentation.from(
                FeatureEntitlement.pro(), ProPurchaseUiState.ready(), englishFeatures());

        ProFeaturePresentationItem[] first = presentation.features();
        int originalLength = first.length;
        first[0] = null;

        ProFeaturePresentationItem[] second = presentation.features();
        TestAssertions.assertEquals(originalLength, second.length, "features() length is stable after mutating a returned array");
        TestAssertions.assertTrue(second[0] != null, "mutating the returned array must not null out the internal item");
    }

    private static void lockedPresentationItem(ProFeaturePresentationItem item, String label) {
        TestAssertions.assertFalse(item.isAvailable(), "Free entitlement must lock Pro presentation " + label);
    }

    private static void availablePresentationItem(ProFeaturePresentationItem item, String label) {
        TestAssertions.assertTrue(item.isAvailable(), "Pro entitlement must make Pro presentation " + label + " available");
    }

    private static ProFeatureDescriptor[] englishFeatures() {
        return ProFeatureFixtures.descriptors();
    }
}
