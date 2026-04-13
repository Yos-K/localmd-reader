package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.domain.ViewerTheme;

public final class HtmlPageBuilder {
    private HtmlPageBuilder() {
    }

    public static String buildPage(SafeHtml body, ViewerTheme theme) {
        ViewerTheme safeTheme = theme == null ? ViewerTheme.light() : theme;
        String background = safeTheme.isDark() ? "#101414" : "#f8fbfa";
        String text = safeTheme.isDark() ? "#edf5f2" : "#172121";
        String codeBackground = safeTheme.isDark() ? "#25302f" : "#e6eeee";
        String border = safeTheme.isDark() ? "#3c4b49" : "#c9d8d5";

        return "<!doctype html>"
                + "<html><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                + "<style>"
                + "body{font-family:sans-serif;margin:24px;line-height:1.55;color:" + text + ";background:" + background + ";}"
                + "h1{font-size:24px;margin:0 0 12px;}"
                + "h2{font-size:21px;margin:24px 0 10px;border-bottom:1px solid " + border + ";padding-bottom:4px;}"
                + "p{font-size:16px;margin:0 0 12px;}"
                + "code{background:" + codeBackground + ";padding:2px 4px;border-radius:4px;}"
                + "pre{background:" + codeBackground + ";padding:12px;overflow-x:auto;border-radius:4px;}"
                + "pre code{background:transparent;padding:0;}"
                + "blockquote{border-left:4px solid " + border + ";margin:0 0 12px;padding:4px 0 4px 12px;}"
                + "li{margin:4px 0;}"
                + "hr{border:0;border-top:1px solid " + border + ";margin:20px 0;}"
                + "</style></head><body>"
                + body.value()
                + "</body></html>";
    }
}
