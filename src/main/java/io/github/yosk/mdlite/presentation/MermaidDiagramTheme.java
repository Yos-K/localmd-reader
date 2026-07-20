package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.viewer.ViewerTheme;
import io.github.yosk.mdlite.viewer.ViewerThemeStyle;

final class MermaidDiagramTheme {
    final String background;
    final String text;
    final String line;
    final String primary;
    final String secondary;

    private MermaidDiagramTheme(
            String background,
            String text,
            String line,
            String primary,
            String secondary) {
        this.background = background;
        this.text = text;
        this.line = line;
        this.primary = primary;
        this.secondary = secondary;
    }

    static MermaidDiagramTheme from(ViewerTheme theme) {
        ViewerThemeStyle style = ViewerThemeStyle.from(theme);
        return new MermaidDiagramTheme(
                style.diagramBackground,
                style.diagramText,
                style.diagramLine,
                style.diagramPrimary,
                style.diagramSecondary);
    }

    String cacheKey() {
        return background + "|" + text + "|" + line + "|" + primary + "|" + secondary;
    }
}
