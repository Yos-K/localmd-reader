package io.github.yosk.mdlite.infrastructure;

public interface ProPurchaseStatusProvider {
    void queryCurrentSnapshot(ProPurchaseStatusCallback callback);
}
