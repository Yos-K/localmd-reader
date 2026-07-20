package io.github.yosk.mdlite.presentation;

import android.content.Context;
import io.github.yosk.mdlite.infrastructure.BuildConfig;
import io.github.yosk.mdlite.infrastructure.BuildProPurchaseStatusProvider;
import io.github.yosk.mdlite.infrastructure.ProPurchaseStatusProvider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class AndroidProPurchaseStatusProviderFactory {
    private static final String PLAY_BILLING_FACTORY =
            "io.github.yosk.mdlite.infrastructure.PlayBillingPurchaseStatusProviderFactory";

    private AndroidProPurchaseStatusProviderFactory() {
    }

    static ProPurchaseStatusProvider current(Context context) {
        if (!BuildConfig.PLAY_BILLING_ENABLED) {
            return BuildProPurchaseStatusProvider.current();
        }
        try {
            Class<?> factoryClass = Class.forName(PLAY_BILLING_FACTORY);
            Method method = factoryClass.getMethod("current", Context.class);
            Object provider = method.invoke(null, context);
            if (provider instanceof ProPurchaseStatusProvider) {
                return (ProPurchaseStatusProvider) provider;
            }
        } catch (ClassNotFoundException e) {
            return BuildProPurchaseStatusProvider.current();
        } catch (NoSuchMethodException e) {
            return BuildProPurchaseStatusProvider.current();
        } catch (IllegalAccessException e) {
            return BuildProPurchaseStatusProvider.current();
        } catch (InvocationTargetException e) {
            return BuildProPurchaseStatusProvider.current();
        }
        return BuildProPurchaseStatusProvider.current();
    }
}
