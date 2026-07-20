package io.github.yosk.mdlite.infrastructure;

public final class PlayBillingPendingSetupResolution {
    private boolean pending;

    private PlayBillingPendingSetupResolution(boolean pending) {
        this.pending = pending;
    }

    public static PlayBillingPendingSetupResolution pending() {
        return new PlayBillingPendingSetupResolution(true);
    }

    public void complete() {
        pending = false;
    }

    public boolean resolveIfPending() {
        boolean canResolve = pending;
        pending = false;
        return canResolve;
    }
}
