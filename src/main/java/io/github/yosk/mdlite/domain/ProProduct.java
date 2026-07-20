package io.github.yosk.mdlite.domain;

public final class ProProduct {
    private static final ProProduct PRO = new ProProduct("localmd_reader_pro");

    private final String productId;

    private ProProduct(String productId) {
        this.productId = productId;
    }

    public static ProProduct pro() {
        return PRO;
    }

    public String productId() {
        return productId;
    }

    public boolean isOneTimePurchase() {
        return true;
    }
}
