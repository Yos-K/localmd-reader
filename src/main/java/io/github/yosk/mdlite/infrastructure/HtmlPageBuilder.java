package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.domain.ViewerTheme;
import io.github.yosk.mdlite.domain.FontSize;

public final class HtmlPageBuilder {
    private HtmlPageBuilder() {
    }

    public static String buildPage(SafeHtml body, ViewerTheme theme) {
        return buildPage(body, theme, FontSize.defaultSize());
    }

    public static String buildPage(SafeHtml body, ViewerTheme theme, FontSize fontSize) {
        ViewerTheme safeTheme = theme == null ? ViewerTheme.light() : theme;
        FontSize safeFontSize = fontSize == null ? FontSize.defaultSize() : fontSize;
        String background = background(safeTheme);
        String tableBackground = tableBackground(safeTheme);
        String surface = surface(safeTheme);
        String surfaceAlt = surfaceAlt(safeTheme);
        String text = text(safeTheme);
        String muted = muted(safeTheme);
        String primary = primary(safeTheme);
        String link = link(safeTheme);
        String codeBackground = codeBackground(safeTheme);
        String codeKeyword = safeTheme.isAmoled() ? "#9bdcff" : (safeTheme.isDark() ? "#90d7ff" : "#0b5cad");
        String codeLiteral = safeTheme.isAmoled() ? "#f5c681" : (safeTheme.isDark() ? "#f3bd76" : "#8f4b00");
        String codeString = safeTheme.isAmoled() ? "#b0e69b" : (safeTheme.isDark() ? "#a7d98f" : "#3f6f1f");
        String codeCommand = safeTheme.isAmoled() ? "#dacbff" : (safeTheme.isDark() ? "#d4c5ff" : "#5c4da8");
        String border = border(safeTheme);
        String tableScrollHint = safeTheme.isAmoled() ? "#6f9b93" : (safeTheme.isDark() ? "#80a8a1" : border);
        String tableScrollHintRgb = safeTheme.isAmoled() ? "111,155,147" : (safeTheme.isDark() ? "128,168,161" : "201,216,213");
        String tableScrollHintOpacity = safeTheme.isDark() ? "0.55" : "0.45";
        String tableBackgroundRgb = tableBackgroundRgb(safeTheme);
        String tableScrollbarTrack = tableScrollbarTrack(safeTheme);
        int bodyFontSize = safeFontSize.sp();
        int h1FontSize = bodyFontSize + 8;
        int h2FontSize = bodyFontSize + 5;

        return "<!doctype html>"
                + "<html><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                + "<style>"
                + "body{font-family:sans-serif;margin:0;padding:24px;line-height:1.6;color:" + text + ";background:" + background + ";}"
                + "body{max-width:760px;box-sizing:border-box;}"
                + "h1{font-size:" + h1FontSize + "px;margin:0 0 16px;line-height:1.2;}"
                + "h2{font-size:" + h2FontSize + "px;margin:28px 0 12px;border-bottom:1px solid " + border + ";padding-bottom:6px;line-height:1.25;}"
                + "p{font-size:" + bodyFontSize + "px;margin:0 0 14px;}"
                + "ul,ol{font-size:" + bodyFontSize + "px;margin:0 0 16px;padding-left:24px;}"
                + "blockquote{font-size:" + bodyFontSize + "px;color:" + muted + ";}"
                + "pre{font-size:" + bodyFontSize + "px;}"
                + "code{background:" + codeBackground + ";padding:2px 5px;border-radius:4px;}"
                + "pre{background:" + codeBackground + ";padding:14px;overflow-x:auto;border-radius:8px;border:1px solid " + border + ";}"
                + "pre code{background:transparent;padding:0;}"
                + ".code-keyword{color:" + codeKeyword + ";font-weight:700;}"
                + ".code-literal{color:" + codeLiteral + ";font-weight:700;}"
                + ".code-string{color:" + codeString + ";}"
                + ".code-command{color:" + codeCommand + ";font-weight:700;}"
                + "a{color:" + link + ";text-decoration:underline;}"
                + "blockquote{border-left:4px solid " + border + ";margin:0 0 16px;padding:6px 0 6px 14px;}"
                + "li{margin:4px 0;}"
                + "ul.checklist{list-style:none;padding-left:0;}"
                + "ul.checklist input{margin-right:8px;}"
                + ".table-scroll{overflow-x:auto;margin:0 0 16px;background:linear-gradient(to right," + tableBackground + " 30%,rgba(" + tableBackgroundRgb + ",0)),linear-gradient(to right,rgba(" + tableBackgroundRgb + ",0)," + tableBackground + " 70%) 100% 0,linear-gradient(to right,rgba(" + tableScrollHintRgb + "," + tableScrollHintOpacity + "),rgba(" + tableScrollHintRgb + ",0)),linear-gradient(to left,rgba(" + tableScrollHintRgb + "," + tableScrollHintOpacity + "),rgba(" + tableScrollHintRgb + ",0)) 100% 0;background-repeat:no-repeat;background-size:32px 100%,32px 100%,16px 100%,16px 100%;background-attachment:local,local,scroll,scroll;}"
                + ".table-scroll::-webkit-scrollbar{height:8px;}"
                + ".table-scroll::-webkit-scrollbar-track{background:" + tableScrollbarTrack + ";}"
                + ".table-scroll::-webkit-scrollbar-thumb{background:" + tableScrollHint + ";border-radius:4px;}"
                + "table{font-size:" + bodyFontSize + "px;border-collapse:collapse;min-width:max-content;}"
                + "th,td{border:1px solid " + border + ";padding:6px 8px;text-align:left;}"
                + "hr{border:0;border-top:1px solid " + border + ";margin:20px 0;}"
                + ".welcome{padding:4px 0 0;}"
                + ".welcome-kicker{font-size:" + (bodyFontSize - 2) + "px;color:" + primary + ";font-weight:700;margin:0 0 8px;}"
                + ".welcome-lead{font-size:" + (bodyFontSize + 2) + "px;line-height:1.55;margin:0 0 20px;}"
                + ".welcome-primary-action{display:block;box-sizing:border-box;width:100%;background:" + primary + ";color:" + background + ";text-align:center;text-decoration:none;font-weight:700;border-radius:8px;padding:14px;margin:0 0 14px;}"
                + ".welcome-grid{display:grid;gap:10px;margin:0 0 18px;}"
                + ".welcome-card{background:" + surface + ";border:1px solid " + border + ";border-radius:8px;padding:14px;}"
                + ".welcome-card strong{display:block;font-size:" + bodyFontSize + "px;margin:0 0 4px;color:" + text + ";}"
                + ".welcome-card span{display:block;font-size:" + (bodyFontSize - 1) + "px;color:" + muted + ";line-height:1.45;}"
                + ".welcome-note{background:" + surfaceAlt + ";border-left:4px solid " + primary + ";padding:10px 12px;margin:0;color:" + muted + ";}"
                + "</style></head><body>"
                + body.value()
                + "</body></html>";
    }

    private static String background(ViewerTheme theme) {
        if (theme.isGradient()) {
            return "linear-gradient(135deg,#f7fbf8 0%,#dcefea 45%,#f4dedb 100%)";
        }
        if (theme.isAurora()) {
            return "linear-gradient(135deg,#f6fbf9 0%,#d8f0eb 38%,#f7e6ee 100%)";
        }
        if (theme.isMist()) {
            return "linear-gradient(135deg,#f3f8f7 0%,#e4efed 50%,#f7f4ef 100%)";
        }
        if (theme.isDusk()) {
            return "linear-gradient(135deg,#fbf6f3 0%,#efe3dc 45%,#dbe9e4 100%)";
        }
        return theme.isAmoled() ? "#000000" : (theme.isDark() ? "#101414" : "#f8fbfa");
    }

    private static String tableBackground(ViewerTheme theme) {
        if (theme.isGradient()) {
            return "#f7fbf8";
        }
        if (theme.isAurora()) {
            return "#f6fbf9";
        }
        if (theme.isMist()) {
            return "#f3f8f7";
        }
        if (theme.isDusk()) {
            return "#fbf6f3";
        }
        return background(theme);
    }

    private static String surface(ViewerTheme theme) {
        return theme.isAmoled() ? "#080c0b" : (theme.isDark() ? "#1b2423" : "#ffffff");
    }

    private static String surfaceAlt(ViewerTheme theme) {
        if (theme.isGradient()) {
            return "#e9f3ef";
        }
        if (theme.isAurora()) {
            return "#e8f6f1";
        }
        if (theme.isMist()) {
            return "#e5eeec";
        }
        if (theme.isDusk()) {
            return "#f0e8e3";
        }
        return theme.isAmoled() ? "#101817" : (theme.isDark() ? "#25302f" : "#eef5f3");
    }

    private static String text(ViewerTheme theme) {
        if (theme.isAurora()) {
            return "#162321";
        }
        if (theme.isMist()) {
            return "#1c2524";
        }
        if (theme.isDusk()) {
            return "#241d1b";
        }
        return theme.isAmoled() ? "#f2f7f5" : (theme.isDark() ? "#edf5f2" : "#172121");
    }

    private static String muted(ViewerTheme theme) {
        if (theme.isAurora()) {
            return "#58706b";
        }
        if (theme.isMist()) {
            return "#5f6f6d";
        }
        if (theme.isDusk()) {
            return "#6d5d57";
        }
        return theme.isAmoled() ? "#9fb2ae" : (theme.isDark() ? "#a7bbb7" : "#566664");
    }

    private static String primary(ViewerTheme theme) {
        if (theme.isGradient()) {
            return "#0d756d";
        }
        if (theme.isAurora()) {
            return "#087f73";
        }
        if (theme.isMist()) {
            return "#437b74";
        }
        if (theme.isDusk()) {
            return "#735f5b";
        }
        return theme.isAmoled() ? "#35b8a8" : (theme.isDark() ? "#2a9d8f" : "#006d77");
    }

    private static String link(ViewerTheme theme) {
        if (theme.isAurora()) {
            return "#0a6f86";
        }
        if (theme.isMist()) {
            return "#386f7a";
        }
        if (theme.isDusk()) {
            return "#70595f";
        }
        return theme.isAmoled() ? "#8ad9ed" : (theme.isDark() ? "#7ccbe0" : "#0b6f87");
    }

    private static String codeBackground(ViewerTheme theme) {
        if (theme.isAmoled()) {
            return "#101817";
        }
        if (theme.isDark()) {
            return "#25302f";
        }
        if (theme.isDusk()) {
            return "#f0e8e3";
        }
        return "#e6eeee";
    }

    private static String border(ViewerTheme theme) {
        if (theme.isGradient()) {
            return "#b8d0cb";
        }
        if (theme.isAurora()) {
            return "#b6d8d0";
        }
        if (theme.isMist()) {
            return "#c2d1ce";
        }
        if (theme.isDusk()) {
            return "#d8c8c0";
        }
        return theme.isAmoled() ? "#263432" : (theme.isDark() ? "#3c4b49" : "#c9d8d5");
    }

    private static String tableBackgroundRgb(ViewerTheme theme) {
        if (theme.isAurora()) {
            return "246,251,249";
        }
        if (theme.isMist()) {
            return "243,248,247";
        }
        if (theme.isDusk()) {
            return "251,246,243";
        }
        return theme.isAmoled() ? "0,0,0" : (theme.isDark() ? "16,20,20" : "248,251,250");
    }

    private static String tableScrollbarTrack(ViewerTheme theme) {
        if (theme.isAurora()) {
            return "#e8f6f1";
        }
        if (theme.isMist()) {
            return "#e5eeec";
        }
        if (theme.isDusk()) {
            return "#f0e8e3";
        }
        return theme.isAmoled() ? "#080c0b" : (theme.isDark() ? "#1b2423" : "#eef5f3");
    }
}
