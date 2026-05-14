package io.github.yosk.mdlite.presentation;

import android.content.Context;
import android.content.SharedPreferences;
import io.github.yosk.mdlite.viewer.ControlsPlacement;
import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.viewer.GestureShortcutAction;
import io.github.yosk.mdlite.domain.ViewerFeature;
import io.github.yosk.mdlite.viewer.ViewerLanguage;

final class ViewerSettingsStore {
    private static final String SETTINGS_PREFS = "viewer_settings";
    private static final String CONTROLS_PLACEMENT = "controls_placement";
    private static final String VIEWER_LANGUAGE = "viewer_language";
    private static final String DOUBLE_TAP_SHORTCUT = "double_tap_shortcut";
    private static final String CIRCLE_GESTURE_SHORTCUT = "circle_gesture_shortcut";

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

    GestureShortcutAction loadDoubleTapShortcut() {
        return loadShortcut(DOUBLE_TAP_SHORTCUT);
    }

    void saveDoubleTapShortcut(GestureShortcutAction action) {
        saveShortcut(DOUBLE_TAP_SHORTCUT, action);
    }

    GestureShortcutAction loadCircleGestureShortcut() {
        return loadShortcut(CIRCLE_GESTURE_SHORTCUT);
    }

    void saveCircleGestureShortcut(GestureShortcutAction action) {
        saveShortcut(CIRCLE_GESTURE_SHORTCUT, action);
    }

    private GestureShortcutAction loadShortcut(String key) {
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
