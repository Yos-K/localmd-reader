package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.viewer.ViewerTheme;
import io.github.yosk.mdlite.domain.FontSize;

public final class HtmlPageBuilder {
    private HtmlPageBuilder() {
    }

    public static String buildPage(SafeHtml body, ViewerTheme theme) {
        return buildPage(body, theme, FontSize.defaultSize());
    }

    public static String buildPage(SafeHtml body, ViewerTheme theme, FontSize fontSize) {
        HtmlThemeStyle style = HtmlThemeStyle.from(theme);
        FontSize safeFontSize = fontSize == null ? FontSize.defaultSize() : fontSize;
        int bodyFontSize = safeFontSize.sp();
        int h1FontSize = bodyFontSize + 8;
        int h2FontSize = bodyFontSize + 5;

        return "<!doctype html>"
                + "<html><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                + "<style>"
                + "body{font-family:sans-serif;margin:0;padding:24px;line-height:1.6;color:" + style.text + ";background:" + style.background + ";}"
                + "body{max-width:760px;box-sizing:border-box;}"
                + "h1{font-size:" + h1FontSize + "px;margin:0 0 16px;line-height:1.2;}"
                + "h2{font-size:" + h2FontSize + "px;margin:28px 0 12px;border-bottom:1px solid " + style.border + ";padding-bottom:6px;line-height:1.25;}"
                + "p{font-size:" + bodyFontSize + "px;margin:0 0 14px;}"
                + "ul,ol{font-size:" + bodyFontSize + "px;margin:0 0 16px;padding-left:24px;}"
                + "blockquote{font-size:" + bodyFontSize + "px;color:" + style.muted + ";}"
                + "pre{font-size:" + bodyFontSize + "px;}"
                + "code{background:" + style.codeBackground + ";padding:2px 5px;border-radius:4px;}"
                + "pre{background:" + style.codeBackground + ";padding:14px;overflow-x:auto;border-radius:8px;border:1px solid " + style.border + ";}"
                + "pre code{background:transparent;padding:0;}"
                + ".code-keyword{color:" + style.codeKeyword + ";font-weight:700;}"
                + ".code-literal{color:" + style.codeLiteral + ";font-weight:700;}"
                + ".code-string{color:" + style.codeString + ";}"
                + ".code-command{color:" + style.codeCommand + ";font-weight:700;}"
                + "a{color:" + style.link + ";text-decoration:underline;}"
                + "blockquote{border-left:4px solid " + style.border + ";margin:0 0 16px;padding:6px 0 6px 14px;}"
                + "li{margin:4px 0;}"
                + "ul.checklist{list-style:none;padding-left:0;}"
                + "ul.checklist input{margin-right:8px;}"
                + ".table-scroll{overflow-x:auto;margin:0 0 16px;background:linear-gradient(to right," + style.tableBackground + " 30%,rgba(" + style.tableBackgroundRgb + ",0)),linear-gradient(to right,rgba(" + style.tableBackgroundRgb + ",0)," + style.tableBackground + " 70%) 100% 0,linear-gradient(to right,rgba(" + style.tableScrollHintRgb + "," + style.tableScrollHintOpacity + "),rgba(" + style.tableScrollHintRgb + ",0)),linear-gradient(to left,rgba(" + style.tableScrollHintRgb + "," + style.tableScrollHintOpacity + "),rgba(" + style.tableScrollHintRgb + ",0)) 100% 0;background-repeat:no-repeat;background-size:32px 100%,32px 100%,16px 100%,16px 100%;background-attachment:local,local,scroll,scroll;}"
                + ".table-scroll::-webkit-scrollbar{height:8px;}"
                + ".table-scroll::-webkit-scrollbar-track{background:" + style.tableScrollbarTrack + ";}"
                + ".table-scroll::-webkit-scrollbar-thumb{background:" + style.tableScrollHint + ";border-radius:4px;}"
                + "table{font-size:" + bodyFontSize + "px;border-collapse:collapse;min-width:max-content;}"
                + "th,td{border:1px solid " + style.border + ";padding:6px 8px;text-align:left;}"
                + "hr{border:0;border-top:1px solid " + style.border + ";margin:20px 0;}"
                + ".welcome{padding:4px 0 0;}"
                + ".welcome-kicker{font-size:" + (bodyFontSize - 2) + "px;color:" + style.primary + ";font-weight:700;margin:0 0 8px;}"
                + ".welcome-lead{font-size:" + (bodyFontSize + 2) + "px;line-height:1.55;margin:0 0 20px;}"
                + ".welcome-primary-action{display:block;box-sizing:border-box;width:100%;background:" + style.primary + ";color:" + style.background + ";text-align:center;text-decoration:none;font-weight:700;border-radius:8px;padding:14px;margin:0 0 14px;}"
                + ".welcome-grid{display:grid;gap:10px;margin:0 0 18px;}"
                + ".welcome-card{background:" + style.surface + ";border:1px solid " + style.border + ";border-radius:8px;padding:14px;}"
                + ".welcome-card strong{display:block;font-size:" + bodyFontSize + "px;margin:0 0 4px;color:" + style.text + ";}"
                + ".welcome-card span{display:block;font-size:" + (bodyFontSize - 1) + "px;color:" + style.muted + ";line-height:1.45;}"
                + ".welcome-note{background:" + style.surfaceAlt + ";border-left:4px solid " + style.primary + ";padding:10px 12px;margin:0;color:" + style.muted + ";}"
                + "</style></head><body>"
                + body.value()
                + "</body></html>";
    }
}
