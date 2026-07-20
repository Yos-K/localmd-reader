package io.github.yosk.mdlite.viewer;

/**
 * Mermaid diagram palette handed to the JS render engine. One of the five semantic groups that replaced the
 * 29-positional-argument ViewerThemeStyle constructor (issue #71): a swap
 * across groups is now a compile error instead of a silent color bug.
 */
final class ThemeDiagramColors {
    final String diagramBackground;
    final String diagramText;
    final String diagramLine;
    final String diagramPrimary;
    final String diagramSecondary;

    ThemeDiagramColors(
            String diagramBackground,
            String diagramText,
            String diagramLine,
            String diagramPrimary,
            String diagramSecondary) {
        this.diagramBackground = diagramBackground;
        this.diagramText = diagramText;
        this.diagramLine = diagramLine;
        this.diagramPrimary = diagramPrimary;
        this.diagramSecondary = diagramSecondary;
    }
}
