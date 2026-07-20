package io.github.yosk.mdlite.infrastructure;

public final class CssCodeHighlighter {
    private CssCodeHighlighter() {
    }

    public static String highlightLine(String line) {
        String source = line == null ? "" : line;
        StringBuilder html = new StringBuilder();
        int colon = source.indexOf(':');
        int brace = source.indexOf('{');
        if (colon > 0 && (brace < 0 || colon > brace)) {
            appendPropertyLine(html, source, colon);
            return html.toString();
        }
        appendSelectorLine(html, source);
        return html.toString();
    }

    private static void appendPropertyLine(StringBuilder html, String source, int colon) {
        int propertyStart = firstNonSpace(source);
        appendEscaped(html, source.substring(0, propertyStart));
        html.append("<span class=\"code-variable\">")
                .append(escapeHtml(source.substring(propertyStart, colon).trim()))
                .append("</span>");
        appendEscaped(html, source.substring(colon));
    }

    private static void appendSelectorLine(StringBuilder html, String source) {
        int brace = source.indexOf('{');
        if (brace < 0) {
            appendEscaped(html, source);
            return;
        }
        int selectorStart = firstNonSpace(source);
        int selectorEnd = lastNonSpaceBefore(source, brace);
        appendEscaped(html, source.substring(0, selectorStart));
        html.append("<span class=\"code-type\">")
                .append(escapeHtml(source.substring(selectorStart, selectorEnd)))
                .append("</span>");
        appendEscaped(html, source.substring(selectorEnd, brace));
        appendEscaped(html, source.substring(brace));
    }

    private static int firstNonSpace(String source) {
        int index = 0;
        while (index < source.length() && source.charAt(index) == ' ') {
            index++;
        }
        return index;
    }

    private static int lastNonSpaceBefore(String source, int index) {
        while (index > 0 && source.charAt(index - 1) == ' ') {
            index--;
        }
        return index;
    }

    private static void appendEscaped(StringBuilder html, String value) {
        html.append(escapeHtml(value));
    }

    private static String escapeHtml(String value) {
        StringBuilder html = new StringBuilder();
        int index = 0;
        while (index < value.length()) {
            char c = value.charAt(index);
            if (c == '&') {
                html.append("&amp;");
            } else if (c == '<') {
                html.append("&lt;");
            } else if (c == '>') {
                html.append("&gt;");
            } else if (c == '"') {
                html.append("&quot;");
            } else {
                html.append(c);
            }
            index++;
        }
        return html.toString();
    }
}
