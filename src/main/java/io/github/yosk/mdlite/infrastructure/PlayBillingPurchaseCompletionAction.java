package io.github.yosk.mdlite.infrastructure;

public final class PlayBillingPurchaseCompletionAction {
    private static final int IGNORE = 1;
    private static final int REFRESH = 2;
    private static final int ACKNOWLEDGE_THEN_REFRESH = 3;

    private final int value;

    private PlayBillingPurchaseCompletionAction(int value) {
        this.value = value;
    }

    public static PlayBillingPurchaseCompletionAction ignore() {
        return new PlayBillingPurchaseCompletionAction(IGNORE);
    }

    public static PlayBillingPurchaseCompletionAction refresh() {
        return new PlayBillingPurchaseCompletionAction(REFRESH);
    }

    public static PlayBillingPurchaseCompletionAction acknowledgeThenRefresh() {
        return new PlayBillingPurchaseCompletionAction(ACKNOWLEDGE_THEN_REFRESH);
    }

    public boolean shouldRefresh() {
        return value == REFRESH;
    }

    public boolean shouldAcknowledgeThenRefresh() {
        return value == ACKNOWLEDGE_THEN_REFRESH;
    }

    public String persistenceCode() {
        if (value == REFRESH) {
            return "refresh";
        }
        if (value == ACKNOWLEDGE_THEN_REFRESH) {
            return "acknowledge_then_refresh";
        }
        return "ignore";
    }
}
