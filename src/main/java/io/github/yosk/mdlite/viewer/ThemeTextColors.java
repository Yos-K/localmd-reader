package io.github.yosk.mdlite.viewer;

/**
 * Text roles, including onPrimary (text drawn on the primary accent). One of the five semantic groups that replaced the
 * 29-positional-argument ViewerThemeStyle constructor (issue #71): a swap
 * across groups is now a compile error instead of a silent color bug.
 */
final class ThemeTextColors {
    final String text;
    final String muted;
    final String primary;
    final String primaryStrong;
    final String onPrimary;
    final String link;

    ThemeTextColors(
            String text,
            String muted,
            String primary,
            String primaryStrong,
            String onPrimary,
            String link) {
        this.text = text;
        this.muted = muted;
        this.primary = primary;
        this.primaryStrong = primaryStrong;
        this.onPrimary = onPrimary;
        this.link = link;
    }
}
