package io.github.yosk.mdlite.domain;

public final class ViewerTheme {
    private static final int LIGHT = 1;
    private static final int DARK = 2;

    private final int value;

    private ViewerTheme(int value) {
        if (value != LIGHT && value != DARK) {
            throw new IllegalArgumentException("unsupported viewer theme");
        }
        this.value = value;
    }

    public static ViewerTheme light() {
        return new ViewerTheme(LIGHT);
    }

    public static ViewerTheme dark() {
        return new ViewerTheme(DARK);
    }

    public boolean isDark() {
        return value == DARK;
    }

    public ViewerTheme toggled() {
        return isDark() ? light() : dark();
    }
}
