package io.github.yosk.mdlite.domain;

public final class ProPurchasePresentation {
    private static final String PRO_ACTIVE = "pro_active";
    private static final String PURCHASE_AVAILABLE = "purchase_available";
    private static final String PURCHASE_UNAVAILABLE = "purchase_unavailable";
    private static final String PURCHASE_PENDING = "purchase_pending";
    private static final String PURCHASE_IN_PROGRESS = "purchase_in_progress";

    private final boolean showAction;
    private final boolean showRestoreAction;
    private final String messageCode;

    private ProPurchasePresentation(boolean showAction, boolean showRestoreAction, String messageCode) {
        this.showAction = showAction;
        this.showRestoreAction = showRestoreAction;
        this.messageCode = messageCode;
    }

    public static ProPurchasePresentation from(FeatureEntitlement entitlement, ProPurchaseUiState uiState) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        ProPurchaseUiState safeUiState = ProPurchaseUiState.safe(uiState);
        if (safeEntitlement.isPro()) {
            return new ProPurchasePresentation(false, false, PRO_ACTIVE);
        }
        if (safeUiState.isReady()) {
            return new ProPurchasePresentation(true, true, PURCHASE_AVAILABLE);
        }
        if (safeUiState.isPending()) {
            return new ProPurchasePresentation(false, false, PURCHASE_PENDING);
        }
        if (safeUiState.isInProgress()) {
            return new ProPurchasePresentation(false, false, PURCHASE_IN_PROGRESS);
        }
        return new ProPurchasePresentation(false, false, PURCHASE_UNAVAILABLE);
    }

    public boolean shouldShowAction() {
        return showAction;
    }

    public boolean shouldShowRestoreAction() {
        return showRestoreAction;
    }

    public String messageCode() {
        return messageCode;
    }
}
