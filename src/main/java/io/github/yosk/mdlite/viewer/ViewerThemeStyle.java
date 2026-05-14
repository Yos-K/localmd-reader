package io.github.yosk.mdlite.viewer;

public final class ViewerThemeStyle {
    public final String background;
    public final String cssBackground;
    public final String tableBackground;
    public final String tableCellBackground;
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
            String tableCellBackground,
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
        this.tableCellBackground = tableCellBackground;
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
                "#f8fbfa", "#f8fbfa", "#f8fbfa", "#ffffff", "#ffffff", "#eef5f3", "#172121", "#566664",
                "#006d77", "#0f3d3e", "#0b6f87", "#e6eeee", "#0b5cad", "#8f4b00",
                "#3f6f1f", "#5c4da8", "#c9d8d5", "#e6eeee", "#c9d8d5",
                "201,216,213", "0.45", "248,251,250", "#eef5f3");
    }

    private static ViewerThemeStyle dark() {
        return new ViewerThemeStyle(
                "#141a1f", "#141a1f", "#141a1f", "#202a31", "#202a31", "#2b3740", "#f4faf8", "#b7c6c1",
                "#49b3a5", "#9dd8cf", "#8bd7ff", "#2b3740", "#94d7ff", "#f4c47b",
                "#bae89f", "#d7c6ff", "#46545c", "#2b3740", "#8fb8ad",
                "143,184,173", "0.58", "20,26,31", "#202a31");
    }

    private static ViewerThemeStyle amoled() {
        return new ViewerThemeStyle(
                "#000000", "#000000", "#000000", "#050505", "#050505", "#101010", "#f7fff9", "#a9b8ad",
                "#4ee88a", "#a8ffbf", "#80ffd4", "#101010", "#8de8ff", "#ffd487",
                "#baff94", "#d8c2ff", "#252525", "#101010", "#6cff9e",
                "108,255,158", "0.62", "0,0,0", "#050505");
    }

    private static ViewerThemeStyle gradient() {
        return new ViewerThemeStyle(
                "#f7fbf9", "linear-gradient(135deg,#f7fbf9 0%,#dceee9 36%,#f2e7dc 70%,#f7f1e7 100%)", "#f7fbf9", "#ffffff", "#ffffff", "#e9f1ee", "#172121", "#5d6a66",
                "#0d756d", "#0f3d3e", "#0b6f87", "#e6eeee", "#0b5cad", "#8f4b00",
                "#3f6f1f", "#5c4da8", "#b8d0cb", "#e9f1ee", "#9fb9b4",
                "159,185,180", "0.48", "247,251,249", "#e9f1ee");
    }

    private static ViewerThemeStyle aurora() {
        return new ViewerThemeStyle(
                "#081411", "linear-gradient(135deg,#081411 0%,#123d36 34%,#0a5c58 64%,#3a5b2a 100%)", "#10211d", "#10211d", "#10211d", "#18342e", "#f2fff7", "#aec9bd",
                "#54d6a7", "#b8ffd7", "#8ceeff", "#18342e", "#9eeeff", "#ffd184",
                "#b9f28f", "#d4c4ff", "#4f7569", "#18342e", "#7ce7b6",
                "124,231,182", "0.65", "16,33,29", "#10211d");
    }

    private static ViewerThemeStyle mist() {
        return new ViewerThemeStyle(
                "#f3f8f7", "linear-gradient(135deg,#f3f8f7 0%,#e4efed 50%,#f7f4ef 100%)", "#f3f8f7", "#ffffff", "#ffffff", "#e5eeec", "#1c2524", "#5f6f6d",
                "#437b74", "#244f4a", "#386f7a", "#e6eeee", "#0b5cad", "#8f4b00",
                "#3f6f1f", "#5c4da8", "#c2d1ce", "#e5eeec", "#c2d1ce",
                "201,216,213", "0.45", "243,248,247", "#e5eeec");
    }

    private static ViewerThemeStyle dusk() {
        return new ViewerThemeStyle(
                "#fbf6f3", "linear-gradient(135deg,#fbf6f3 0%,#efe3dc 45%,#dbe9e4 100%)", "#fbf6f3", "#ffffff", "#ffffff", "#f0e8e3", "#241d1b", "#6d5d57",
                "#735f5b", "#463936", "#70595f", "#f0e8e3", "#0b5cad", "#8f4b00",
                "#3f6f1f", "#5c4da8", "#d8c8c0", "#f0e8e3", "#d8c8c0",
                "201,216,213", "0.45", "251,246,243", "#f0e8e3");
    }

}
