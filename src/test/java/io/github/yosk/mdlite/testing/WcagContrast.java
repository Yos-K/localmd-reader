package io.github.yosk.mdlite.testing;

/**
 * WCAG 2.x contrast ratio between two solid colors.
 *
 * Test-only fitness helper: theme palettes are specified as hex strings in
 * ViewerThemeStyle, so readability rules (WCAG AA, 4.5:1 for body text) can be
 * verified as plain JVM assertions without rendering anything.
 */
public final class WcagContrast {
    private WcagContrast() {
    }

    /** Contrast ratio in [1, 21] between two "#rrggbb" colors. Order does not matter. */
    public static double ratio(String firstHexColor, String secondHexColor) {
        double first = relativeLuminance(firstHexColor);
        double second = relativeLuminance(secondHexColor);
        double lighter = Math.max(first, second);
        double darker = Math.min(first, second);
        return (lighter + 0.05) / (darker + 0.05);
    }

    private static double relativeLuminance(String hexColor) {
        int rgb = Integer.parseInt(hexColor.substring(1), 16);
        double red = linear((rgb >> 16) & 0xff);
        double green = linear((rgb >> 8) & 0xff);
        double blue = linear(rgb & 0xff);
        return 0.2126 * red + 0.7152 * green + 0.0722 * blue;
    }

    private static double linear(int channel) {
        double c = channel / 255.0;
        return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    }
}
