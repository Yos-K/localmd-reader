package io.github.yosk.mdlite.viewer;

/**
 * Concrete colors for a viewer theme.
 *
 * Restructured for issue #71: the former 29-positional-String constructor is
 * now five semantic groups (base / text / code / table / diagram), so a swap
 * across groups is a compile error instead of a silent color bug. The public
 * String fields are unchanged, and ViewerThemeStyleCharacterizationTest pins
 * every token of every theme to its pre-restructuring value.
 */
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
    public final String onPrimary;
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
    public final String diagramBackground;
    public final String diagramText;
    public final String diagramLine;
    public final String diagramPrimary;
    public final String diagramSecondary;

    private ViewerThemeStyle(
            ThemeBaseColors base,
            ThemeTextColors textColors,
            ThemeCodeColors code,
            ThemeTableColors table,
            ThemeDiagramColors diagram) {
        this.background = base.background;
        this.cssBackground = base.cssBackground;
        this.surface = base.surface;
        this.surfaceAlt = base.surfaceAlt;
        this.border = base.border;
        this.message = base.message;
        this.text = textColors.text;
        this.muted = textColors.muted;
        this.primary = textColors.primary;
        this.primaryStrong = textColors.primaryStrong;
        this.onPrimary = textColors.onPrimary;
        this.link = textColors.link;
        this.codeBackground = code.codeBackground;
        this.codeKeyword = code.codeKeyword;
        this.codeLiteral = code.codeLiteral;
        this.codeString = code.codeString;
        this.codeCommand = code.codeCommand;
        this.tableBackground = table.tableBackground;
        this.tableCellBackground = table.tableCellBackground;
        this.tableScrollHint = table.tableScrollHint;
        this.tableScrollHintRgb = table.tableScrollHintRgb;
        this.tableScrollHintOpacity = table.tableScrollHintOpacity;
        this.tableBackgroundRgb = table.tableBackgroundRgb;
        this.tableScrollbarTrack = table.tableScrollbarTrack;
        this.diagramBackground = diagram.diagramBackground;
        this.diagramText = diagram.diagramText;
        this.diagramLine = diagram.diagramLine;
        this.diagramPrimary = diagram.diagramPrimary;
        this.diagramSecondary = diagram.diagramSecondary;
    }

    /**
     * Whether the solid background is darker than the text drawn on it. Drives
     * system bar icon brightness; luminance-based so it stays correct for
     * future themes (aurora is dark here despite not being in the dark/amoled
     * family).
     */
    public boolean hasDarkBackground() {
        return relativeLuminance(background) < relativeLuminance(text);
    }

    private static double relativeLuminance(String hexColor) {
        int rgb = Integer.parseInt(hexColor.substring(1), 16);
        return 0.2126 * linearChannel((rgb >> 16) & 0xff)
                + 0.7152 * linearChannel((rgb >> 8) & 0xff)
                + 0.0722 * linearChannel(rgb & 0xff);
    }

    private static double linearChannel(int channel) {
        double c = channel / 255.0;
        return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
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

    /**
     * The four light-family themes share the same four code accents; only the
     * code background differs. The duplication used to be copy-pasted literals.
     */
    private static ThemeCodeColors lightFamilyCode(String codeBackground) {
        return new ThemeCodeColors(codeBackground, "#0b5cad", "#8f4b00", "#3f6f1f", "#5c4da8");
    }

    private static ViewerThemeStyle light() {
        return new ViewerThemeStyle(
                new ThemeBaseColors("#f8fbfa", "#f8fbfa", "#ffffff", "#eef5f3", "#c9d8d5", "#e6eeee"),
                new ThemeTextColors("#172121", "#566664", "#006d77", "#0f3d3e", "#f8fbfa", "#0b6f87"),
                lightFamilyCode("#e6eeee"),
                new ThemeTableColors("#f8fbfa", "#ffffff", "#c9d8d5", "201,216,213", "0.45", "248,251,250", "#eef5f3"),
                new ThemeDiagramColors("#ffffff", "#172121", "#46615d", "#dff1ee", "#eef5f3"));
    }

    private static ViewerThemeStyle dark() {
        return new ViewerThemeStyle(
                new ThemeBaseColors("#141a1f", "#141a1f", "#202a31", "#2b3740", "#46545c", "#2b3740"),
                new ThemeTextColors("#f4faf8", "#b7c6c1", "#49b3a5", "#9dd8cf", "#141a1f", "#8bd7ff"),
                new ThemeCodeColors("#2b3740", "#94d7ff", "#f4c47b", "#bae89f", "#d7c6ff"),
                new ThemeTableColors("#141a1f", "#202a31", "#8fb8ad", "143,184,173", "0.58", "20,26,31", "#202a31"),
                new ThemeDiagramColors("#202a31", "#f4faf8", "#a9c8c0", "#294641", "#2b3740"));
    }

    private static ViewerThemeStyle amoled() {
        return new ViewerThemeStyle(
                new ThemeBaseColors("#000000", "#000000", "#050505", "#101010", "#252525", "#101010"),
                new ThemeTextColors("#f7fff9", "#a9b8ad", "#4ee88a", "#a8ffbf", "#000000", "#80ffd4"),
                new ThemeCodeColors("#101010", "#8de8ff", "#ffd487", "#baff94", "#d8c2ff"),
                new ThemeTableColors("#000000", "#050505", "#6cff9e", "108,255,158", "0.62", "0,0,0", "#050505"),
                new ThemeDiagramColors("#050505", "#f7fff9", "#8edfb0", "#0d2618", "#101010"));
    }

    private static ViewerThemeStyle gradient() {
        return new ViewerThemeStyle(
                new ThemeBaseColors("#f3f7ff", "linear-gradient(135deg,#f3f7ff 0%,#d9f2ec 32%,#ffe2d1 68%,#f8f0ff 100%)", "#ffffff", "#e5edf7", "#91a8bd", "#e5edf7"),
                new ThemeTextColors("#142032", "#536170", "#005f73", "#123047", "#ffffff", "#005a8c"),
                lightFamilyCode("#dde8f2"),
                new ThemeTableColors("#f3f7ff", "#ffffff", "#7f9ab1", "127,154,177", "0.55", "243,247,255", "#e5edf7"),
                new ThemeDiagramColors("#ffffff", "#142032", "#42576f", "#dcecf2", "#ffe7d6"));
    }

    private static ViewerThemeStyle aurora() {
        return new ViewerThemeStyle(
                new ThemeBaseColors("#081411", "linear-gradient(135deg,#081411 0%,#123d36 34%,#0a5c58 64%,#3a5b2a 100%)", "#10211d", "#18342e", "#4f7569", "#18342e"),
                new ThemeTextColors("#f2fff7", "#aec9bd", "#54d6a7", "#b8ffd7", "#081411", "#8ceeff"),
                new ThemeCodeColors("#18342e", "#9eeeff", "#ffd184", "#b9f28f", "#d4c4ff"),
                new ThemeTableColors("#10211d", "#10211d", "#7ce7b6", "124,231,182", "0.65", "16,33,29", "#10211d"),
                new ThemeDiagramColors("#10211d", "#f2fff7", "#a6d8c0", "#123d36", "#18342e"));
    }

    private static ViewerThemeStyle mist() {
        return new ViewerThemeStyle(
                new ThemeBaseColors("#ffffff", "#ffffff", "#ffffff", "#f0f0f0", "#4b5563", "#f0f0f0"),
                new ThemeTextColors("#050505", "#404040", "#003f5c", "#111827", "#ffffff", "#004f8f"),
                lightFamilyCode("#eeeeee"),
                new ThemeTableColors("#ffffff", "#ffffff", "#4b5563", "75,85,99", "0.70", "255,255,255", "#f0f0f0"),
                new ThemeDiagramColors("#ffffff", "#050505", "#374151", "#eeeeee", "#f7f7f7"));
    }

    private static ViewerThemeStyle dusk() {
        return new ViewerThemeStyle(
                new ThemeBaseColors("#f4ead7", "linear-gradient(135deg,#f4ead7 0%,#ead8b8 52%,#f8f0df 100%)", "#fff8eb", "#eadcc3", "#b69b72", "#eadcc3"),
                new ThemeTextColors("#2a2118", "#66533b", "#6b3f00", "#3b260f", "#fff8eb", "#6b4a1f"),
                new ThemeCodeColors("#eadcc3", "#0b5cad", "#8f4b00", "#2f5b17", "#5c4da8"),
                new ThemeTableColors("#f4ead7", "#fff8eb", "#a88c62", "168,140,98", "0.56", "244,234,215", "#eadcc3"),
                new ThemeDiagramColors("#fff8eb", "#2a2118", "#6f5734", "#eadcc3", "#f8f0df"));
    }
}
