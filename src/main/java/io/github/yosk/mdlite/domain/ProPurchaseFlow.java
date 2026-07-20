package io.github.yosk.mdlite.domain;

public interface ProPurchaseFlow {
    ProPurchaseStartResult start(ProProduct product, ProPurchaseFlowCallback callback);
}
