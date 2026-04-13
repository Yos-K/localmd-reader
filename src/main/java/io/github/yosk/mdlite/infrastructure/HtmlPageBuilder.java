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
        String muted = safeTheme.isDark() ? "#a7bbb7" : "#566664";
        String link = safeTheme.isDark() ? "#7ccbe0" : "#0b6f87";
        String codeBackground = safeTheme.isDark() ? "#25302f" : "#e6eeee";
        String border = safeTheme.isDark() ? "#3c4b49" : "#c9d8d5";
        String tableScrollHint = safeTheme.isDark() ? "#80a8a1" : border;
        String tableScrollHintRgb = safeTheme.isDark() ? "128,168,161" : "201,216,213";
        String tableScrollHintOpacity = safeTheme.isDark() ? "0.55" : "0.45";
        String tableBackgroundRgb = safeTheme.isDark() ? "16,20,20" : "248,251,250";
        String tableScrollbarTrack = safeTheme.isDark() ? "#1b2423" : "#eef5f3";
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
                + "a{color:" + link + ";text-decoration:underline;}"
                + "blockquote{border-left:4px solid " + border + ";margin:0 0 16px;padding:6px 0 6px 14px;}"
                + "li{margin:4px 0;}"
                + "ul.checklist{list-style:none;padding-left:0;}"
                + "ul.checklist input{margin-right:8px;}"
                + ".table-scroll{overflow-x:auto;margin:0 0 16px;background:linear-gradient(to right," + background + " 30%,rgba(" + tableBackgroundRgb + ",0)),linear-gradient(to right,rgba(" + tableBackgroundRgb + ",0)," + background + " 70%) 100% 0,linear-gradient(to right,rgba(" + tableScrollHintRgb + "," + tableScrollHintOpacity + "),rgba(" + tableScrollHintRgb + ",0)),linear-gradient(to left,rgba(" + tableScrollHintRgb + "," + tableScrollHintOpacity + "),rgba(" + tableScrollHintRgb + ",0)) 100% 0;background-repeat:no-repeat;background-size:32px 100%,32px 100%,16px 100%,16px 100%;background-attachment:local,local,scroll,scroll;}"
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
