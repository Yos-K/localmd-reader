package io.github.yosk.mdlite.viewer;

/**
 * Page and chrome surfaces: the canvas everything else sits on. One of the five semantic groups that replaced the
 * 29-positional-argument ViewerThemeStyle constructor (issue #71): a swap
 * across groups is now a compile error instead of a silent color bug.
 */
final class ThemeBaseColors {
    final String background;
    final String cssBackground;
    final String surface;
    final String surfaceAlt;
    final String border;
    final String message;

    ThemeBaseColors(
            String background,
            String cssBackground,
            String surface,
            String surfaceAlt,
            String border,
            String message) {
        this.background = background;
        this.cssBackground = cssBackground;
        this.surface = surface;
        this.surfaceAlt = surfaceAlt;
        this.border = border;
        this.message = message;
    }
}
