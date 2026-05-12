package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.domain.ViewerTheme;

final class ViewerPalette {
    final int background;
    final int surface;
    final int surfaceAlt;
    final int text;
    final int muted;
    final int primary;
    final int primaryStrong;
    final int border;
    final int message;

    private ViewerPalette(
            int background,
            int surface,
            int surfaceAlt,
            int text,
            int muted,
            int primary,
            int primaryStrong,
            int border,
            int message) {
        this.background = background;
        this.surface = surface;
        this.surfaceAlt = surfaceAlt;
        this.text = text;
        this.muted = muted;
        this.primary = primary;
        this.primaryStrong = primaryStrong;
        this.border = border;
        this.message = message;
    }

    static ViewerPalette from(ViewerTheme theme) {
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

    private static ViewerPalette light() {
        return new ViewerPalette(0xfff8fbfa, 0xffffffff, 0xffeef5f3, 0xff172121, 0xff566664, 0xff006d77, 0xff0f3d3e, 0xffc9d8d5, 0xffe6eeee);
    }

    private static ViewerPalette dark() {
        return new ViewerPalette(0xff101414, 0xff1b2423, 0xff25302f, 0xffedf5f2, 0xffa7bbb7, 0xff2a9d8f, 0xff7ccbe0, 0xff3c4b49, 0xff25302f);
    }

    private static ViewerPalette amoled() {
        return new ViewerPalette(0xff000000, 0xff080c0b, 0xff101817, 0xfff2f7f5, 0xff9fb2ae, 0xff35b8a8, 0xff8ad9ed, 0xff263432, 0xff101817);
    }

    private static ViewerPalette gradient() {
        return new ViewerPalette(0xfff7fbf8, 0xffffffff, 0xffe9f3ef, 0xff172121, 0xff566664, 0xff0d756d, 0xff0f3d3e, 0xffb8d0cb, 0xffe9f3ef);
    }

    private static ViewerPalette aurora() {
        return new ViewerPalette(0xfff6fbf9, 0xffffffff, 0xffe8f6f1, 0xff162321, 0xff58706b, 0xff087f73, 0xff0e4a45, 0xffb6d8d0, 0xffe8f6f1);
    }

    private static ViewerPalette mist() {
        return new ViewerPalette(0xfff3f8f7, 0xffffffff, 0xffe5eeec, 0xff1c2524, 0xff5f6f6d, 0xff437b74, 0xff244f4a, 0xffc2d1ce, 0xffe5eeec);
    }

    private static ViewerPalette dusk() {
        return new ViewerPalette(0xfffbf6f3, 0xffffffff, 0xfff0e8e3, 0xff241d1b, 0xff6d5d57, 0xff735f5b, 0xff463936, 0xffd8c8c0, 0xfff0e8e3);
    }
}
