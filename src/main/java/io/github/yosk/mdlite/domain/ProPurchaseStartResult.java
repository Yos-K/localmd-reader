package io.github.yosk.mdlite.domain;

public final class ProPurchaseStartResult {
    private static final int STARTED = 1;
    private static final int UNAVAILABLE = 2;
    private static final int PENDING = 3;

    private final int value;

    private ProPurchaseStartResult(int value) {
        this.value = value;
    }

    public static ProPurchaseStartResult started() {
        return new ProPurchaseStartResult(STARTED);
    }

    public static ProPurchaseStartResult unavailable() {
        return new ProPurchaseStartResult(UNAVAILABLE);
    }

    public static ProPurchaseStartResult pending() {
        return new ProPurchaseStartResult(PENDING);
    }

    public ProPurchaseUiState uiState() {
        if (value == STARTED) {
            return ProPurchaseUiState.inProgress();
        }
        if (value == PENDING) {
            return ProPurchaseUiState.pending();
        }
        return ProPurchaseUiState.unavailable();
    }
}
