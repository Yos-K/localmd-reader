package io.github.yosk.mdlite.viewer;

/**
 * Table fills plus the horizontal scroll affordance. One of the five semantic groups that replaced the
 * 29-positional-argument ViewerThemeStyle constructor (issue #71): a swap
 * across groups is now a compile error instead of a silent color bug.
 */
final class ThemeTableColors {
    final String tableBackground;
    final String tableCellBackground;
    final String tableScrollHint;
    final String tableScrollHintRgb;
    final String tableScrollHintOpacity;
    final String tableBackgroundRgb;
    final String tableScrollbarTrack;

    ThemeTableColors(
            String tableBackground,
            String tableCellBackground,
            String tableScrollHint,
            String tableScrollHintRgb,
            String tableScrollHintOpacity,
            String tableBackgroundRgb,
            String tableScrollbarTrack) {
        this.tableBackground = tableBackground;
        this.tableCellBackground = tableCellBackground;
        this.tableScrollHint = tableScrollHint;
        this.tableScrollHintRgb = tableScrollHintRgb;
        this.tableScrollHintOpacity = tableScrollHintOpacity;
        this.tableBackgroundRgb = tableBackgroundRgb;
        this.tableScrollbarTrack = tableScrollbarTrack;
    }
}
