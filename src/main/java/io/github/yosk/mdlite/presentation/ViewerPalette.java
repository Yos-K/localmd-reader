package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.domain.ViewerTheme;
import io.github.yosk.mdlite.domain.ViewerThemeStyle;

final class ViewerPalette {
    final int background;
    final int surface;
    final int surfaceAlt;
    final int text;
    final int muted;
    final int primary;
    final int primaryStrong;
    final int border;
    final int message;

    private ViewerPalette(ViewerThemeStyle style) {
        this.background = argb(style.background);
        this.surface = argb(style.surface);
        this.surfaceAlt = argb(style.surfaceAlt);
        this.text = argb(style.text);
        this.muted = argb(style.muted);
        this.primary = argb(style.primary);
        this.primaryStrong = argb(style.primaryStrong);
        this.border = argb(style.border);
        this.message = argb(style.message);
    }

    static ViewerPalette from(ViewerTheme theme) {
        return new ViewerPalette(ViewerThemeStyle.from(theme));
    }

    private static int argb(String color) {
        return (int) (0xff000000L | Long.parseLong(color.substring(1), 16));
    }
}
