package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.FeatureEntitlement;

public final class ViewerPreferences {
    private final ViewerLanguage language;
    private final ViewerTheme theme;
    private final ControlsPlacement controlsPlacement;

    public ViewerPreferences(ViewerLanguage language, ViewerTheme theme, ControlsPlacement controlsPlacement) {
        if (language == null || theme == null || controlsPlacement == null) {
            throw new IllegalArgumentException("viewer preferences must be complete");
        }
        this.language = language;
        this.theme = theme;
        this.controlsPlacement = controlsPlacement;
    }

    public static ViewerPreferences defaults() {
        return new ViewerPreferences(ViewerLanguage.english(), ViewerTheme.light(), ControlsPlacement.top());
    }

    public ViewerLanguage language() {
        return language;
    }

    public ViewerTheme theme() {
        return theme;
    }

    public ControlsPlacement controlsPlacement() {
        return controlsPlacement;
    }

    public ViewerPreferences withLanguage(ViewerLanguage changedLanguage) {
        return new ViewerPreferences(changedLanguage, theme, controlsPlacement);
    }

    public ViewerPreferences withTheme(ViewerTheme changedTheme) {
        return new ViewerPreferences(language, changedTheme, controlsPlacement);
    }

    public ViewerPreferences withControlsPlacement(ControlsPlacement changedPlacement) {
        return new ViewerPreferences(language, theme, changedPlacement);
    }

    public ViewerPreferences clampTheme(FeatureEntitlement entitlement) {
        return withTheme(theme.clampedForEntitlement(entitlement));
    }
}
