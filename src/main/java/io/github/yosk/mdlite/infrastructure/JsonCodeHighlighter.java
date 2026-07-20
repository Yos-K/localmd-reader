package io.github.yosk.mdlite.infrastructure;

public final class JsonCodeHighlighter {
    private JsonCodeHighlighter() {
    }

    public static String highlightLine(String line) {
        String source = line == null ? "" : line;
        StringBuilder html = new StringBuilder();
        int index = 0;
        while (index < source.length()) {
            char c = source.charAt(index);
            if (c == '"') {
                int end = stringEnd(source, index + 1);
                html.append("<span class=\"code-string\">")
                        .append(escapeHtml(source.substring(index, end)))
                        .append("</span>");
                index = end;
                continue;
            }
            if (isLiteralStart(source, index, "true")) {
                html.append("<span class=\"code-literal\">true</span>");
                index += 4;
                continue;
            }
            if (isLiteralStart(source, index, "false")) {
                html.append("<span class=\"code-literal\">false</span>");
                index += 5;
                continue;
            }
            if (isLiteralStart(source, index, "null")) {
                html.append("<span class=\"code-literal\">null</span>");
                index += 4;
                continue;
            }
            appendEscapedCharacter(html, c);
            index++;
        }
        return html.toString();
    }

    private static int stringEnd(String source, int index) {
        boolean escaped = false;
        while (index < source.length()) {
            char c = source.charAt(index);
            if (c == '"' && !escaped) {
                return index + 1;
            }
            escaped = c == '\\' && !escaped;
            if (c != '\\') {
                escaped = false;
            }
            index++;
        }
        return source.length();
    }

    private static boolean isLiteralStart(String source, int index, String literal) {
        int end = index + literal.length();
        return end <= source.length() && literal.equals(source.substring(index, end));
    }

    private static String escapeHtml(String value) {
        StringBuilder html = new StringBuilder();
        int index = 0;
        while (index < value.length()) {
            appendEscapedCharacter(html, value.charAt(index));
            index++;
        }
        return html.toString();
    }

    private static void appendEscapedCharacter(StringBuilder html, char c) {
        if (c == '&') {
            html.append("&amp;");
            return;
        }
        if (c == '<') {
            html.append("&lt;");
            return;
        }
        if (c == '>') {
            html.append("&gt;");
            return;
        }
        if (c == '"') {
            html.append("&quot;");
            return;
        }
        html.append(c);
    }
}
