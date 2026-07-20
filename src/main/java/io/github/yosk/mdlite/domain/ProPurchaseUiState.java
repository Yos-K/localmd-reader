package io.github.yosk.mdlite.domain;

public final class ProPurchaseUiState {
    private static final int READY = 1;
    private static final int UNAVAILABLE = 2;
    private static final int PENDING = 3;
    private static final int IN_PROGRESS = 4;

    private final int value;

    private ProPurchaseUiState(int value) {
        this.value = value;
    }

    public static ProPurchaseUiState ready() {
        return new ProPurchaseUiState(READY);
    }

    public static ProPurchaseUiState unavailable() {
        return new ProPurchaseUiState(UNAVAILABLE);
    }

    public static ProPurchaseUiState pending() {
        return new ProPurchaseUiState(PENDING);
    }

    public static ProPurchaseUiState inProgress() {
        return new ProPurchaseUiState(IN_PROGRESS);
    }

    public static ProPurchaseUiState safe(ProPurchaseUiState state) {
        return state == null ? unavailable() : state;
    }

    public static ProPurchaseUiState fromPurchaseState(ProPurchaseState state) {
        ProPurchaseState safeState = state == null ? ProPurchaseState.unknown() : state;
        if (safeState.isPending()) {
            return pending();
        }
        if (safeState.isUnavailableOrUnknown()) {
            return unavailable();
        }
        return ready();
    }

    boolean isReady() {
        return value == READY;
    }

    boolean isPending() {
        return value == PENDING;
    }

    boolean isInProgress() {
        return value == IN_PROGRESS;
    }
}
