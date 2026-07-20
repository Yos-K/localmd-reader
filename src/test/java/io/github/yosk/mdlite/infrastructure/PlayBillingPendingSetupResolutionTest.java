package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class PlayBillingPendingSetupResolutionTest {

    @Test
    void newSetupResolutionStartsPendingSoDisconnectCanResolveIt() {
        PlayBillingPendingSetupResolution resolution = PlayBillingPendingSetupResolution.pending();

        TestAssertions.assertTrue(
                resolution.resolveIfPending(),
                "A newly pending Billing setup can be resolved by a disconnect callback");
    }

    @Test
    void setupResolutionCanOnlyBeResolvedOnce() {
        PlayBillingPendingSetupResolution resolution = PlayBillingPendingSetupResolution.pending();

        resolution.resolveIfPending();

        TestAssertions.assertFalse(
                resolution.resolveIfPending(),
                "A Billing setup resolution must ignore stale callbacks after the first resolution");
    }

    @Test
    void successfulSetupMarksResolutionCompleteSoLaterDisconnectIsStale() {
        PlayBillingPendingSetupResolution resolution = PlayBillingPendingSetupResolution.pending();

        resolution.complete();

        TestAssertions.assertFalse(
                resolution.resolveIfPending(),
                "A disconnect after successful Billing setup must not overwrite a completed request");
    }
}
