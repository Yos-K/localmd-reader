package io.github.yosk.mdlite.presentation;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.ViewerFeature;
import io.github.yosk.mdlite.viewer.ControlsPlacement;
import io.github.yosk.mdlite.viewer.CustomGestureShape;
import io.github.yosk.mdlite.viewer.CustomGestureShortcut;
import io.github.yosk.mdlite.viewer.GestureShortcutAction;
import io.github.yosk.mdlite.viewer.GestureShortcutBinding;
import io.github.yosk.mdlite.viewer.GestureShortcutBindings;
import io.github.yosk.mdlite.viewer.GestureShortcutTrigger;
import io.github.yosk.mdlite.viewer.ViewerLanguage;
import io.github.yosk.mdlite.viewer.ViewerTheme;

final class ViewerSettingsStore {
    private static final String SETTINGS_PREFS = "viewer_settings";
    private static final String CONTROLS_PLACEMENT = "controls_placement";
    private static final String VIEWER_LANGUAGE = "viewer_language";
    private static final String VIEWER_THEME = "viewer_theme";
    private static final String DOUBLE_TAP_SHORTCUT = "double_tap_shortcut";
    private static final String CIRCLE_GESTURE_SHORTCUT = "circle_gesture_shortcut";
    private static final String CUSTOM_GESTURE_SHAPE = "custom_gesture_shape";
    private static final String CUSTOM_GESTURE_ACTION = "custom_gesture_action";
    private static final String SWIPE_LEFT_SHORTCUT = "swipe_left_shortcut";
    private static final String SWIPE_RIGHT_SHORTCUT = "swipe_right_shortcut";
    private static final String SWIPE_UP_SHORTCUT = "swipe_up_shortcut";
    private static final String SWIPE_DOWN_SHORTCUT = "swipe_down_shortcut";

    private final Context context;
    private final FeatureEntitlement entitlement;

    ViewerSettingsStore(Context context, FeatureEntitlement entitlement) {
        this.context = context;
        this.entitlement = entitlement;
    }

    ControlsPlacement loadControlsPlacement() {
        return ControlsPlacement.fromStoredValue(prefs().getString(CONTROLS_PLACEMENT, ControlsPlacement.TOP_VALUE));
    }

    void saveControlsPlacement(ControlsPlacement placement) {
        prefs().edit().putString(CONTROLS_PLACEMENT, placement.storedValue()).apply();
    }

    ViewerLanguage loadViewerLanguage() {
        return ViewerLanguage.fromStoredValue(prefs().getString(VIEWER_LANGUAGE, ViewerLanguage.ENGLISH_VALUE));
    }

    void saveViewerLanguage(ViewerLanguage language) {
        prefs().edit().putString(VIEWER_LANGUAGE, language.storedValue()).apply();
    }

    ViewerTheme loadViewerTheme() {
        ViewerTheme stored = ViewerTheme.fromStoredValue(storedThemeValueOrSystemDefault());
        return stored.clampedForEntitlement(entitlement);
    }

    /**
     * First launch only (#70): without a saved theme, follow the system dark
     * mode setting. Once the user saves a theme, the saved value always wins.
     * ViewerTheme stays Android-free; the Configuration lookup lives here at
     * the persistence boundary.
     */
    private String storedThemeValueOrSystemDefault() {
        String storedValue = prefs().getString(VIEWER_THEME, null);
        if (storedValue != null) {
            return storedValue;
        }
        return systemPrefersDark() ? ViewerTheme.DARK_VALUE : ViewerTheme.LIGHT_VALUE;
    }

    private boolean systemPrefersDark() {
        int nightMask = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return nightMask == Configuration.UI_MODE_NIGHT_YES;
    }

    void saveViewerTheme(ViewerTheme theme) {
        prefs().edit().putString(VIEWER_THEME, theme.storedValue()).apply();
    }

    GestureShortcutAction loadDoubleTapShortcut() {
        return loadGestureShortcutBindings().actionFor(GestureShortcutTrigger.doubleTap());
    }

    void saveDoubleTapShortcut(GestureShortcutAction action) {
        saveGestureShortcutBinding(GestureShortcutBinding.of(GestureShortcutTrigger.doubleTap(), action));
    }

    GestureShortcutAction loadCircleGestureShortcut() {
        return loadGestureShortcutBindings().actionFor(GestureShortcutTrigger.circle());
    }

    void saveCircleGestureShortcut(GestureShortcutAction action) {
        saveGestureShortcutBinding(GestureShortcutBinding.of(GestureShortcutTrigger.circle(), action));
    }

    GestureShortcutBindings loadGestureShortcutBindings() {
        GestureShortcutBindings bindings = GestureShortcutBindings.empty()
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.doubleTap(), loadDoubleTapAction()))
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.circle(), loadAdvancedGestureAction(CIRCLE_GESTURE_SHORTCUT)))
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.swipeLeft(), loadAdvancedGestureAction(SWIPE_LEFT_SHORTCUT)))
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.swipeRight(), loadAdvancedGestureAction(SWIPE_RIGHT_SHORTCUT)))
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.swipeUp(), loadAdvancedGestureAction(SWIPE_UP_SHORTCUT)))
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.swipeDown(), loadAdvancedGestureAction(SWIPE_DOWN_SHORTCUT)));
        CustomGestureShortcut customShortcut = loadCustomGestureShortcut();
        if (customShortcut == null) {
            return bindings;
        }
        return bindings.put(customShortcut.binding());
    }

    void saveGestureShortcutBinding(GestureShortcutBinding binding) {
        if (binding.trigger().isDoubleTap()) {
            saveShortcut(DOUBLE_TAP_SHORTCUT, binding.action());
            return;
        }
        if (binding.trigger().isCircle()) {
            saveShortcut(CIRCLE_GESTURE_SHORTCUT, binding.action());
            return;
        }
        if (binding.trigger().isSwipeLeft()) {
            saveShortcut(SWIPE_LEFT_SHORTCUT, binding.action());
            return;
        }
        if (binding.trigger().isSwipeRight()) {
            saveShortcut(SWIPE_RIGHT_SHORTCUT, binding.action());
            return;
        }
        if (binding.trigger().isSwipeUp()) {
            saveShortcut(SWIPE_UP_SHORTCUT, binding.action());
            return;
        }
        if (binding.trigger().isSwipeDown()) {
            saveShortcut(SWIPE_DOWN_SHORTCUT, binding.action());
        }
    }

    CustomGestureShortcut loadCustomGestureShortcut() {
        if (!entitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS)) {
            return null;
        }
        String shapeValue = prefs().getString(CUSTOM_GESTURE_SHAPE, "");
        GestureShortcutAction action = GestureShortcutAction.fromStoredValue(prefs().getString(CUSTOM_GESTURE_ACTION, "off"));
        if (action.isOff()) {
            return null;
        }
        try {
            return CustomGestureShortcut.of(CustomGestureShape.fromStoredValue(shapeValue), action);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    void saveCustomGestureShortcut(CustomGestureShortcut shortcut) {
        prefs().edit()
                .putString(CUSTOM_GESTURE_SHAPE, shortcut.shape().storedValue())
                .putString(CUSTOM_GESTURE_ACTION, shortcut.action().storedValue())
                .apply();
    }

    void clearCustomGestureShortcut() {
        prefs().edit()
                .remove(CUSTOM_GESTURE_SHAPE)
                .remove(CUSTOM_GESTURE_ACTION)
                .apply();
    }

    private GestureShortcutAction loadDoubleTapAction() {
        GestureShortcutAction stored = GestureShortcutAction.fromStoredValue(prefs().getString(DOUBLE_TAP_SHORTCUT, "off"));
        if (!entitlement.allows(ViewerFeature.DOUBLE_TAP_SHORTCUTS)) {
            return GestureShortcutAction.off();
        }
        return stored;
    }

    private GestureShortcutAction loadAdvancedGestureAction(String key) {
        GestureShortcutAction stored = GestureShortcutAction.fromStoredValue(prefs().getString(key, "off"));
        if (!entitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS)) {
            return GestureShortcutAction.off();
        }
        return stored;
    }

    private void saveShortcut(String key, GestureShortcutAction action) {
        prefs().edit().putString(key, action.storedValue()).apply();
    }

    private SharedPreferences prefs() {
        return context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE);
    }
}
