package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.ViewerTheme;

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

    private HtmlThemeStyle(
            String background,
            String tableBackground,
            String surface,
            String surfaceAlt,
            String text,
            String muted,
            String primary,
            String link,
            String codeBackground,
            String codeKeyword,
            String codeLiteral,
            String codeString,
            String codeCommand,
            String border,
            String tableScrollHint,
            String tableScrollHintRgb,
            String tableScrollHintOpacity,
            String tableBackgroundRgb,
            String tableScrollbarTrack) {
        this.background = background;
        this.tableBackground = tableBackground;
        this.surface = surface;
        this.surfaceAlt = surfaceAlt;
        this.text = text;
        this.muted = muted;
        this.primary = primary;
        this.link = link;
        this.codeBackground = codeBackground;
        this.codeKeyword = codeKeyword;
        this.codeLiteral = codeLiteral;
        this.codeString = codeString;
        this.codeCommand = codeCommand;
        this.border = border;
        this.tableScrollHint = tableScrollHint;
        this.tableScrollHintRgb = tableScrollHintRgb;
        this.tableScrollHintOpacity = tableScrollHintOpacity;
        this.tableBackgroundRgb = tableBackgroundRgb;
        this.tableScrollbarTrack = tableScrollbarTrack;
    }

    static HtmlThemeStyle from(ViewerTheme theme) {
        ViewerTheme safeTheme = theme == null ? ViewerTheme.light() : theme;
        if (safeTheme.isAmoled()) {
            return amoled();
        }
        if (safeTheme.isGradient()) {
            return gradient();
        }
        if (safeTheme.isAurora()) {
            return aurora();
        }
        if (safeTheme.isMist()) {
            return mist();
        }
        if (safeTheme.isDusk()) {
            return dusk();
        }
        return safeTheme.isDark() ? dark() : light();
    }

    private static HtmlThemeStyle light() {
        return new HtmlThemeStyle(
                "#f8fbfa", "#f8fbfa", "#ffffff", "#eef5f3", "#172121", "#566664",
                "#006d77", "#0b6f87", "#e6eeee", "#0b5cad", "#8f4b00", "#3f6f1f",
                "#5c4da8", "#c9d8d5", "#c9d8d5", "201,216,213", "0.45",
                "248,251,250", "#eef5f3");
    }

    private static HtmlThemeStyle dark() {
        return new HtmlThemeStyle(
                "#101414", "#101414", "#1b2423", "#25302f", "#edf5f2", "#a7bbb7",
                "#2a9d8f", "#7ccbe0", "#25302f", "#90d7ff", "#f3bd76", "#a7d98f",
                "#d4c5ff", "#3c4b49", "#80a8a1", "128,168,161", "0.55",
                "16,20,20", "#1b2423");
    }

    private static HtmlThemeStyle amoled() {
        return new HtmlThemeStyle(
                "#000000", "#000000", "#080c0b", "#101817", "#f2f7f5", "#9fb2ae",
                "#35b8a8", "#8ad9ed", "#101817", "#9bdcff", "#f5c681", "#b0e69b",
                "#dacbff", "#263432", "#6f9b93", "111,155,147", "0.55",
                "0,0,0", "#080c0b");
    }

    private static HtmlThemeStyle gradient() {
        return new HtmlThemeStyle(
                "linear-gradient(135deg,#f7fbf8 0%,#dcefea 45%,#f4dedb 100%)",
                "#f7fbf8", "#ffffff", "#e9f3ef", "#172121", "#566664",
                "#0d756d", "#0b6f87", "#e6eeee", "#0b5cad", "#8f4b00", "#3f6f1f",
                "#5c4da8", "#b8d0cb", "#b8d0cb", "201,216,213", "0.45",
                "248,251,250", "#eef5f3");
    }

    private static HtmlThemeStyle aurora() {
        return new HtmlThemeStyle(
                "linear-gradient(135deg,#f6fbf9 0%,#d8f0eb 38%,#f7e6ee 100%)",
                "#f6fbf9", "#ffffff", "#e8f6f1", "#162321", "#58706b",
                "#087f73", "#0a6f86", "#e6eeee", "#0b5cad", "#8f4b00", "#3f6f1f",
                "#5c4da8", "#b6d8d0", "#b6d8d0", "201,216,213", "0.45",
                "246,251,249", "#e8f6f1");
    }

    private static HtmlThemeStyle mist() {
        return new HtmlThemeStyle(
                "linear-gradient(135deg,#f3f8f7 0%,#e4efed 50%,#f7f4ef 100%)",
                "#f3f8f7", "#ffffff", "#e5eeec", "#1c2524", "#5f6f6d",
                "#437b74", "#386f7a", "#e6eeee", "#0b5cad", "#8f4b00", "#3f6f1f",
                "#5c4da8", "#c2d1ce", "#c2d1ce", "201,216,213", "0.45",
                "243,248,247", "#e5eeec");
    }

    private static HtmlThemeStyle dusk() {
        return new HtmlThemeStyle(
                "linear-gradient(135deg,#fbf6f3 0%,#efe3dc 45%,#dbe9e4 100%)",
                "#fbf6f3", "#ffffff", "#f0e8e3", "#241d1b", "#6d5d57",
                "#735f5b", "#70595f", "#f0e8e3", "#0b5cad", "#8f4b00", "#3f6f1f",
                "#5c4da8", "#d8c8c0", "#d8c8c0", "201,216,213", "0.45",
                "251,246,243", "#f0e8e3");
    }
}
