package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.domain.TableReadingMode;
import io.github.yosk.mdlite.viewer.ViewerTheme;
import io.github.yosk.mdlite.viewer.FontSize;

public final class HtmlPageBuilder {
    private HtmlPageBuilder() {
    }

    public static String buildPage(SafeHtml body, ViewerTheme theme) {
        return buildPage(body, theme, FontSize.defaultSize());
    }

    public static String buildPage(SafeHtml body, ViewerTheme theme, FontSize fontSize) {
        return buildPage(body, theme, fontSize, TableReadingMode.standard());
    }

    public static String buildPage(SafeHtml body, ViewerTheme theme, FontSize fontSize, TableReadingMode tableReadingMode) {
        HtmlThemeStyle style = HtmlThemeStyle.from(theme);
        FontSize safeFontSize = fontSize == null ? FontSize.defaultSize() : fontSize;
        TableReadingMode safeTableReadingMode = tableReadingMode == null ? TableReadingMode.standard() : tableReadingMode;
        int bodyFontSize = safeFontSize.sp();
        int h1FontSize = bodyFontSize + 8;
        int h2FontSize = bodyFontSize + 5;
        String diagramScale = diagramScale(safeFontSize);
        String tableScrollClass = safeTableReadingMode.isEnhanced() ? "table-scroll enhanced-table-reading" : "table-scroll";

        return "<!doctype html>"
                + "<html><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                + "<style>"
                + ":root{"
                + "--localmd-body-font-size:" + bodyFontSize + "px;"
                + "--localmd-small-font-size:" + (bodyFontSize - 1) + "px;"
                + "--localmd-kicker-font-size:" + (bodyFontSize - 2) + "px;"
                + "--localmd-lead-font-size:" + (bodyFontSize + 2) + "px;"
                + "--localmd-h1-font-size:" + h1FontSize + "px;"
                + "--localmd-h2-font-size:" + h2FontSize + "px;"
                + "--localmd-h3-font-size:" + (bodyFontSize + 3) + "px;"
                + "--localmd-h4-font-size:" + (bodyFontSize + 1) + "px;"
                + "--localmd-diagram-scale:" + diagramScale + ";"
                + "}"
                + "body{font-family:sans-serif;margin:0 auto;padding:24px 24px 40vh;line-height:1.6;color:" + style.text + ";background:" + style.background + ";}"
                + "body{max-width:760px;box-sizing:border-box;}"
                + "h1{font-size:var(--localmd-h1-font-size);margin:0 0 16px;line-height:1.2;}"
                + "h2{font-size:var(--localmd-h2-font-size);margin:28px 0 12px;border-bottom:1px solid " + style.border + ";padding-bottom:6px;line-height:1.25;}"
                + "h3{font-size:var(--localmd-h3-font-size);margin:24px 0 10px;line-height:1.3;}"
                + "h4{font-size:var(--localmd-h4-font-size);margin:20px 0 8px;line-height:1.35;}"
                + "h5,h6{font-size:var(--localmd-body-font-size);margin:18px 0 8px;line-height:1.4;}"
                + "h6{color:" + style.muted + ";}"
                + "p{font-size:var(--localmd-body-font-size);margin:0 0 14px;}"
                + "ul,ol{font-size:var(--localmd-body-font-size);margin:0 0 16px;padding-left:24px;}"
                + "blockquote{font-size:var(--localmd-body-font-size);color:" + style.muted + ";}"
                + "pre{font-size:var(--localmd-body-font-size);}"
                + "code{background:" + style.codeBackground + ";padding:2px 5px;border-radius:4px;}"
                + "pre{background:" + style.codeBackground + ";padding:14px;overflow-x:auto;border-radius:8px;border:1px solid " + style.border + ";}"
                + "pre code{background:transparent;padding:0;}"
                + ".code-keyword{color:" + style.codeKeyword + ";font-weight:700;}"
                + ".code-literal{color:" + style.codeLiteral + ";font-weight:700;}"
                + ".code-string{color:" + style.codeString + ";}"
                + ".code-command{color:" + style.codeCommand + ";font-weight:700;}"
                + ".code-type{color:" + style.codeString + ";font-weight:700;}"
                + ".code-function{color:" + style.codeCommand + ";font-weight:700;}"
                + ".code-variable{color:" + style.link + ";}"
                + ".code-preview-toggle{background:" + style.surface + ";border:1px solid " + style.border + ";border-radius:8px;padding:10px;margin:0 0 16px;}"
                + ".code-preview-toggle pre{margin:0;}"
                + ".code-preview-radio{position:absolute;opacity:0;pointer-events:none;}"
                + ".code-preview-label{display:inline-block;font-size:var(--localmd-small-font-size);font-weight:700;border:1px solid " + style.border + ";border-radius:8px;padding:6px 10px;margin:0 6px 10px 0;color:" + style.text + ";background:" + style.background + ";}"
                + ".code-preview-radio:checked+.code-preview-label{color:" + style.onPrimary + ";background:" + style.primary + ";border-color:" + style.primary + ";}"
                + ".code-preview-pane{display:none;}"
                + ".code-preview-raw-radio:checked~.code-preview-raw{display:block;}"
                + ".code-preview-preview-radio:checked~.code-preview-rendered{display:block;}"
                + ".code-preview-rendered{background:" + style.background + ";border:1px solid " + style.border + ";border-radius:8px;padding:14px;overflow:auto;}"
                + ".code-preview-rendered>:last-child{margin-bottom:0;}"
                + ".mermaid-placeholder{background:" + style.surface + ";border:1px solid " + style.border + ";border-radius:8px;padding:14px;margin:0 0 16px;}"
                + ".mermaid-placeholder strong{display:block;color:" + style.text + ";font-size:var(--localmd-body-font-size);margin:0 0 4px;}"
                + ".mermaid-placeholder span{display:block;color:" + style.muted + ";font-size:var(--localmd-small-font-size);margin:0 0 10px;}"
                + ".mermaid-placeholder pre{margin:0;max-height:180px;}"
                + ".mermaid-diagram{background:" + style.surface + ";border:1px solid " + style.border + ";border-radius:8px;padding:12px;margin:0 0 16px;overflow:auto;}"
                + ".mermaid-diagram-scale{display:inline-block;min-width:100%;transform:scale(var(--localmd-diagram-scale));transform-origin:top left;}"
                + ".mermaid-diagram-scale svg{max-width:none;height:auto;display:block;}"
                + ".mermaid-diagram-scale svg,.mermaid-diagram-scale svg *{font-size:16px;}"
                + ".mermaid-diagram-scale::after{content:\"\";display:block;height:calc((var(--localmd-diagram-scale) - 1) * 100%);}"
                + ".mermaid-error{color:" + style.muted + ";font-size:var(--localmd-body-font-size);}"
                + ".mermaid-error strong{display:block;color:" + style.text + ";font-size:var(--localmd-body-font-size);margin:0 0 6px;}"
                + ".mermaid-error span{display:block;color:" + style.muted + ";font-size:var(--localmd-small-font-size);margin:0 0 10px;}"
                + ".mermaid-error pre{margin:0;max-height:240px;}"
                + "a{color:" + style.link + ";text-decoration:underline;}"
                + "img{max-width:100%;height:auto;border-radius:8px;}"
                + "blockquote{border-left:4px solid " + style.border + ";margin:0 0 16px;padding:6px 0 6px 14px;}"
                + "li{margin:4px 0;}"
                + "ul.checklist{list-style:none;padding-left:0;}"
                + "ul.checklist input{margin-right:8px;}"
                + ".table-scroll{overflow-x:auto;margin:0 0 16px;background:linear-gradient(to right," + style.tableBackground + " 30%,rgba(" + style.tableBackgroundRgb + ",0)),linear-gradient(to right,rgba(" + style.tableBackgroundRgb + ",0)," + style.tableBackground + " 70%) 100% 0,linear-gradient(to right,rgba(" + style.tableScrollHintRgb + "," + style.tableScrollHintOpacity + "),rgba(" + style.tableScrollHintRgb + ",0)),linear-gradient(to left,rgba(" + style.tableScrollHintRgb + "," + style.tableScrollHintOpacity + "),rgba(" + style.tableScrollHintRgb + ",0)) 100% 0;background-repeat:no-repeat;background-size:32px 100%,32px 100%,16px 100%,16px 100%;background-attachment:local,local,scroll,scroll;}"
                + ".table-scroll::-webkit-scrollbar{height:8px;}"
                + ".table-scroll::-webkit-scrollbar-track{background:" + style.tableScrollbarTrack + ";}"
                + ".table-scroll::-webkit-scrollbar-thumb{background:" + style.tableScrollHint + ";border-radius:4px;}"
                + "table{font-size:var(--localmd-body-font-size);border-collapse:collapse;min-width:max-content;background:" + style.tableCellBackground + ";}"
                + "th,td{border:1px solid " + style.border + ";padding:6px 8px;text-align:left;background:" + style.tableCellBackground + ";}"
                + ".table-scroll.enhanced-table-reading th{position:sticky;top:0;z-index:3;background:" + style.tableCellBackground + ";}"
                + ".table-scroll.enhanced-table-reading th:first-child,.table-scroll.enhanced-table-reading td:first-child{position:sticky;left:0;z-index:2;background:" + style.tableCellBackground + ";}"
                + ".table-scroll.enhanced-table-reading th:first-child{z-index:4;}"
                + "hr{border:0;border-top:1px solid " + style.border + ";margin:20px 0;}"
                + ".welcome{padding:4px 0 0;}"
                + ".welcome-kicker{font-size:var(--localmd-kicker-font-size);color:" + style.primary + ";font-weight:700;margin:0 0 8px;}"
                + ".welcome-lead{font-size:var(--localmd-lead-font-size);line-height:1.55;margin:0 0 20px;}"
                + ".welcome-primary-action{display:block;box-sizing:border-box;width:100%;background:" + style.primary + ";color:" + style.onPrimary + ";text-align:center;text-decoration:none;font-weight:700;border-radius:8px;padding:14px;margin:0 0 14px;}"
                + ".welcome-grid{display:grid;gap:10px;margin:0 0 18px;}"
                + ".welcome-card{background:" + style.surface + ";border:1px solid " + style.border + ";border-radius:8px;padding:14px;}"
                + ".welcome-card strong{display:block;font-size:var(--localmd-body-font-size);margin:0 0 4px;color:" + style.text + ";}"
                + ".welcome-card span{display:block;font-size:var(--localmd-small-font-size);color:" + style.muted + ";line-height:1.45;}"
                + ".welcome-note{background:" + style.surfaceAlt + ";border-left:4px solid " + style.primary + ";padding:10px 12px;margin:0;color:" + style.muted + ";}"
                + "</style></head><body>"
                + body.value().replace("class=\"table-scroll\"", "class=\"" + tableScrollClass + "\"")
                + "</body></html>";
    }

    public static String diagramScale(FontSize fontSize) {
        FontSize safeFontSize = fontSize == null ? FontSize.defaultSize() : fontSize;
        float scale = safeFontSize.sp() / (float) FontSize.defaultSize().sp();
        return String.format(java.util.Locale.US, "%.3f", Float.valueOf(scale));
    }

    public static String fontSizeUpdateScript(FontSize fontSize) {
        FontSize safeFontSize = fontSize == null ? FontSize.defaultSize() : fontSize;
        int bodyFontSize = safeFontSize.sp();
        return "(function(){"
                + "var s=document.documentElement.style;"
                + "s.setProperty('--localmd-body-font-size','" + bodyFontSize + "px');"
                + "s.setProperty('--localmd-small-font-size','" + (bodyFontSize - 1) + "px');"
                + "s.setProperty('--localmd-kicker-font-size','" + (bodyFontSize - 2) + "px');"
                + "s.setProperty('--localmd-lead-font-size','" + (bodyFontSize + 2) + "px');"
                + "s.setProperty('--localmd-h1-font-size','" + (bodyFontSize + 8) + "px');"
                + "s.setProperty('--localmd-h2-font-size','" + (bodyFontSize + 5) + "px');"
                + "s.setProperty('--localmd-h3-font-size','" + (bodyFontSize + 3) + "px');"
                + "s.setProperty('--localmd-h4-font-size','" + (bodyFontSize + 1) + "px');"
                + "s.setProperty('--localmd-diagram-scale','" + diagramScale(safeFontSize) + "');"
                + "})()";
    }
}
