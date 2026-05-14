package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.ViewerTheme;
import io.github.yosk.mdlite.domain.ViewerThemeStyle;

final class HtmlThemeStyle {
    final String background;
    final String tableBackground;
    final String surface;
    final String surfaceAlt;
    final String text;
    final String muted;
    final String primary;
    final String link;
    final String codeBackground;
    final String codeKeyword;
    final String codeLiteral;
    final String codeString;
    final String codeCommand;
    final String border;
    final String tableScrollHint;
    final String tableScrollHintRgb;
    final String tableScrollHintOpacity;
    final String tableBackgroundRgb;
    final String tableScrollbarTrack;

    private HtmlThemeStyle(ViewerThemeStyle style) {
        this.background = style.cssBackground;
        this.tableBackground = style.tableBackground;
        this.surface = style.surface;
        this.surfaceAlt = style.surfaceAlt;
        this.text = style.text;
        this.muted = style.muted;
        this.primary = style.primary;
        this.link = style.link;
        this.codeBackground = style.codeBackground;
        this.codeKeyword = style.codeKeyword;
        this.codeLiteral = style.codeLiteral;
        this.codeString = style.codeString;
        this.codeCommand = style.codeCommand;
        this.border = style.border;
        this.tableScrollHint = style.tableScrollHint;
        this.tableScrollHintRgb = style.tableScrollHintRgb;
        this.tableScrollHintOpacity = style.tableScrollHintOpacity;
        this.tableBackgroundRgb = style.tableBackgroundRgb;
        this.tableScrollbarTrack = style.tableScrollbarTrack;
    }

    static HtmlThemeStyle from(ViewerTheme theme) {
        return new HtmlThemeStyle(ViewerThemeStyle.from(theme));
    }
}
