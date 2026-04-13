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
        String background = safeTheme.isDark() ? "#101414" : "#f8fbfa";
        String text = safeTheme.isDark() ? "#edf5f2" : "#172121";
        String codeBackground = safeTheme.isDark() ? "#25302f" : "#e6eeee";
        String border = safeTheme.isDark() ? "#3c4b49" : "#c9d8d5";
        String tableScrollHint = safeTheme.isDark() ? "#80a8a1" : border;
        String tableScrollbarTrack = safeTheme.isDark() ? "#1b2423" : "#eef5f3";
        int bodyFontSize = safeFontSize.sp();
        int h1FontSize = bodyFontSize + 8;
        int h2FontSize = bodyFontSize + 5;

        return "<!doctype html>"
                + "<html><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                + "<style>"
                + "body{font-family:sans-serif;margin:24px;line-height:1.55;color:" + text + ";background:" + background + ";}"
                + "h1{font-size:" + h1FontSize + "px;margin:0 0 12px;}"
                + "h2{font-size:" + h2FontSize + "px;margin:24px 0 10px;border-bottom:1px solid " + border + ";padding-bottom:4px;}"
                + "p{font-size:" + bodyFontSize + "px;margin:0 0 12px;}"
                + "ul,ol{font-size:" + bodyFontSize + "px;}"
                + "blockquote{font-size:" + bodyFontSize + "px;}"
                + "pre{font-size:" + bodyFontSize + "px;}"
                + "code{background:" + codeBackground + ";padding:2px 4px;border-radius:4px;}"
                + "pre{background:" + codeBackground + ";padding:12px;overflow-x:auto;border-radius:4px;}"
                + "pre code{background:transparent;padding:0;}"
                + "blockquote{border-left:4px solid " + border + ";margin:0 0 12px;padding:4px 0 4px 12px;}"
                + "li{margin:4px 0;}"
                + "ul.checklist{list-style:none;padding-left:0;}"
                + "ul.checklist input{margin-right:8px;}"
                + ".table-scroll{overflow-x:auto;margin:0 0 16px;border:1px solid " + border + ";border-radius:4px;box-shadow:inset -18px 0 14px -12px " + tableScrollHint + ";}"
                + ".table-scroll::-webkit-scrollbar{height:8px;}"
                + ".table-scroll::-webkit-scrollbar-track{background:" + tableScrollbarTrack + ";}"
                + ".table-scroll::-webkit-scrollbar-thumb{background:" + tableScrollHint + ";border-radius:4px;}"
                + "table{font-size:" + bodyFontSize + "px;border-collapse:collapse;min-width:max-content;}"
                + "th,td{border:1px solid " + border + ";padding:6px 8px;text-align:left;}"
                + "hr{border:0;border-top:1px solid " + border + ";margin:20px 0;}"
                + "</style></head><body>"
                + body.value()
                + "</body></html>";
    }
}
