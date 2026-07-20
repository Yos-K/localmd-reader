package io.github.yosk.mdlite.infrastructure;

public final class YamlCodeHighlighter {
    private YamlCodeHighlighter() {
    }

    public static String highlightLine(String line) {
        String source = line == null ? "" : line;
        int colon = source.indexOf(':');
        if (colon <= 0) {
            return escapeHtml(source);
        }
        StringBuilder html = new StringBuilder();
        html.append("<span class=\"code-variable\">")
                .append(escapeHtml(source.substring(0, colon)))
                .append("</span>:");
        appendValue(html, source.substring(colon + 1));
        return html.toString();
    }

    private static void appendValue(StringBuilder html, String rawValue) {
        String value = rawValue.trim();
        int leadingSpaces = rawValue.length() - trimLeft(rawValue).length();
        html.append(rawValue.substring(0, leadingSpaces));
        if ("true".equals(value) || "false".equals(value) || "null".equals(value)) {
            html.append("<span class=\"code-literal\">").append(value).append("</span>");
            return;
        }
        if (value.startsWith("\"") || value.startsWith("'")) {
            html.append("<span class=\"code-string\">").append(escapeHtml(value)).append("</span>");
            return;
        }
        html.append(escapeHtml(value));
    }

    private static String trimLeft(String value) {
        int index = 0;
        while (index < value.length() && value.charAt(index) == ' ') {
            index++;
        }
        return value.substring(index);
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
