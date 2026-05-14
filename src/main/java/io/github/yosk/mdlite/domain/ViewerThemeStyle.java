package io.github.yosk.mdlite.domain;

public final class ViewerThemeStyle {
    public final String background;
    public final String cssBackground;
    public final String tableBackground;
    public final String surface;
    public final String surfaceAlt;
    public final String text;
    public final String muted;
    public final String primary;
    public final String primaryStrong;
    public final String link;
    public final String codeBackground;
    public final String codeKeyword;
    public final String codeLiteral;
    public final String codeString;
    public final String codeCommand;
    public final String border;
    public final String message;
    public final String tableScrollHint;
    public final String tableScrollHintRgb;
    public final String tableScrollHintOpacity;
    public final String tableBackgroundRgb;
    public final String tableScrollbarTrack;

    private ViewerThemeStyle(
            String background,
            String cssBackground,
            String tableBackground,
            String surface,
            String surfaceAlt,
            String text,
            String muted,
            String primary,
            String primaryStrong,
            String link,
            String codeBackground,
            String codeKeyword,
            String codeLiteral,
            String codeString,
            String codeCommand,
            String border,
            String message,
            String tableScrollHint,
            String tableScrollHintRgb,
            String tableScrollHintOpacity,
            String tableBackgroundRgb,
            String tableScrollbarTrack) {
        this.background = background;
        this.cssBackground = cssBackground;
        this.tableBackground = tableBackground;
        this.surface = surface;
        this.surfaceAlt = surfaceAlt;
        this.text = text;
        this.muted = muted;
        this.primary = primary;
        this.primaryStrong = primaryStrong;
        this.link = link;
        this.codeBackground = codeBackground;
        this.codeKeyword = codeKeyword;
        this.codeLiteral = codeLiteral;
        this.codeString = codeString;
        this.codeCommand = codeCommand;
        this.border = border;
        this.message = message;
        this.tableScrollHint = tableScrollHint;
        this.tableScrollHintRgb = tableScrollHintRgb;
        this.tableScrollHintOpacity = tableScrollHintOpacity;
        this.tableBackgroundRgb = tableBackgroundRgb;
        this.tableScrollbarTrack = tableScrollbarTrack;
    }

    public static ViewerThemeStyle from(ViewerTheme theme) {
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

    private static ViewerThemeStyle light() {
        return new ViewerThemeStyle(
                "#f8fbfa", "#f8fbfa", "#f8fbfa", "#ffffff", "#eef5f3", "#172121", "#566664",
                "#006d77", "#0f3d3e", "#0b6f87", "#e6eeee", "#0b5cad", "#8f4b00",
                "#3f6f1f", "#5c4da8", "#c9d8d5", "#e6eeee", "#c9d8d5",
                "201,216,213", "0.45", "248,251,250", "#eef5f3");
    }

    private static ViewerThemeStyle dark() {
        return new ViewerThemeStyle(
                "#101414", "#101414", "#101414", "#1b2423", "#25302f", "#edf5f2", "#a7bbb7",
                "#2a9d8f", "#7ccbe0", "#7ccbe0", "#25302f", "#90d7ff", "#f3bd76",
                "#a7d98f", "#d4c5ff", "#3c4b49", "#25302f", "#80a8a1",
                "128,168,161", "0.55", "16,20,20", "#1b2423");
    }

    private static ViewerThemeStyle amoled() {
        return new ViewerThemeStyle(
                "#000000", "#000000", "#000000", "#080c0b", "#101817", "#f2f7f5", "#9fb2ae",
                "#35b8a8", "#8ad9ed", "#8ad9ed", "#101817", "#9bdcff", "#f5c681",
                "#b0e69b", "#dacbff", "#263432", "#101817", "#6f9b93",
                "111,155,147", "0.55", "0,0,0", "#080c0b");
    }

    private static ViewerThemeStyle gradient() {
        return new ViewerThemeStyle(
                "#f7fbf8", "linear-gradient(135deg,#f7fbf8 0%,#dcefea 45%,#f4dedb 100%)", "#f7fbf8", "#ffffff", "#e9f3ef", "#172121", "#566664",
                "#0d756d", "#0f3d3e", "#0b6f87", "#e6eeee", "#0b5cad", "#8f4b00",
                "#3f6f1f", "#5c4da8", "#b8d0cb", "#e9f3ef", "#b8d0cb",
                "201,216,213", "0.45", "248,251,250", "#eef5f3");
    }

    private static ViewerThemeStyle aurora() {
        return new ViewerThemeStyle(
                "#f6fbf9", "linear-gradient(135deg,#f6fbf9 0%,#d8f0eb 38%,#f7e6ee 100%)", "#f6fbf9", "#ffffff", "#e8f6f1", "#162321", "#58706b",
                "#087f73", "#0e4a45", "#0a6f86", "#e6eeee", "#0b5cad", "#8f4b00",
                "#3f6f1f", "#5c4da8", "#b6d8d0", "#e8f6f1", "#b6d8d0",
                "201,216,213", "0.45", "246,251,249", "#e8f6f1");
    }

    private static ViewerThemeStyle mist() {
        return new ViewerThemeStyle(
                "#f3f8f7", "linear-gradient(135deg,#f3f8f7 0%,#e4efed 50%,#f7f4ef 100%)", "#f3f8f7", "#ffffff", "#e5eeec", "#1c2524", "#5f6f6d",
                "#437b74", "#244f4a", "#386f7a", "#e6eeee", "#0b5cad", "#8f4b00",
                "#3f6f1f", "#5c4da8", "#c2d1ce", "#e5eeec", "#c2d1ce",
                "201,216,213", "0.45", "243,248,247", "#e5eeec");
    }

    private static ViewerThemeStyle dusk() {
        return new ViewerThemeStyle(
                "#fbf6f3", "linear-gradient(135deg,#fbf6f3 0%,#efe3dc 45%,#dbe9e4 100%)", "#fbf6f3", "#ffffff", "#f0e8e3", "#241d1b", "#6d5d57",
                "#735f5b", "#463936", "#70595f", "#f0e8e3", "#0b5cad", "#8f4b00",
                "#3f6f1f", "#5c4da8", "#d8c8c0", "#f0e8e3", "#d8c8c0",
                "201,216,213", "0.45", "251,246,243", "#f0e8e3");
    }

}
