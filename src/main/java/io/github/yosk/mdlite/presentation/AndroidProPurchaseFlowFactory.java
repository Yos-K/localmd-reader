package io.github.yosk.mdlite.presentation;

import android.app.Activity;
import io.github.yosk.mdlite.domain.ProPurchaseFlow;
import io.github.yosk.mdlite.domain.UnavailableProPurchaseFlow;
import io.github.yosk.mdlite.infrastructure.BuildConfig;
import io.github.yosk.mdlite.infrastructure.ProPurchaseStatusCallback;
import io.github.yosk.mdlite.infrastructure.ProPurchaseStatusRefresh;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class AndroidProPurchaseFlowFactory {
    private static final String PLAY_BILLING_FACTORY =
            "io.github.yosk.mdlite.infrastructure.PlayBillingPurchaseFlowFactory";

    private AndroidProPurchaseFlowFactory() {
    }

    static ProPurchaseFlow current(
            Activity activity,
            ProPurchaseStatusRefresh statusRefresh,
            ProPurchaseStatusCallback statusCallback) {
        if (!BuildConfig.PLAY_BILLING_ENABLED) {
            return UnavailableProPurchaseFlow.instance();
        }
        try {
            Class<?> factoryClass = Class.forName(PLAY_BILLING_FACTORY);
            Method method = factoryClass.getMethod(
                    "current",
                    Activity.class,
                    ProPurchaseStatusRefresh.class,
                    ProPurchaseStatusCallback.class);
            Object flow = method.invoke(null, activity, statusRefresh, statusCallback);
            if (flow instanceof ProPurchaseFlow) {
                return (ProPurchaseFlow) flow;
            }
        } catch (ClassNotFoundException e) {
            return UnavailableProPurchaseFlow.instance();
        } catch (NoSuchMethodException e) {
            return UnavailableProPurchaseFlow.instance();
        } catch (IllegalAccessException e) {
            return UnavailableProPurchaseFlow.instance();
        } catch (InvocationTargetException e) {
            return UnavailableProPurchaseFlow.instance();
        }
        return UnavailableProPurchaseFlow.instance();
    }
}
