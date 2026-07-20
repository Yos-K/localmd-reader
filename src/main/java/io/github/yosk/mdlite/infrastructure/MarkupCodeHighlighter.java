package io.github.yosk.mdlite.infrastructure;

public final class MarkupCodeHighlighter {
    private MarkupCodeHighlighter() {
    }

    public static String highlightLine(String line) {
        String source = line == null ? "" : line;
        StringBuilder html = new StringBuilder();
        int index = 0;
        while (index < source.length()) {
            char c = source.charAt(index);
            if (c == '<') {
                index = appendTag(html, source, index);
                continue;
            }
            appendEscapedCharacter(html, c);
            index++;
        }
        return html.toString();
    }

    private static int appendTag(StringBuilder html, String source, int index) {
        html.append("&lt;");
        index++;
        if (index < source.length() && source.charAt(index) == '/') {
            html.append('/');
            index++;
        }
        int nameStart = index;
        while (index < source.length() && isNamePart(source.charAt(index))) {
            index++;
        }
        appendType(html, source.substring(nameStart, index));
        while (index < source.length()) {
            char c = source.charAt(index);
            if (c == '>') {
                html.append("&gt;");
                return index + 1;
            }
            if (isNameStart(c)) {
                int attributeStart = index;
                while (index < source.length() && isNamePart(source.charAt(index))) {
                    index++;
                }
                html.append("<span class=\"code-variable\">")
                        .append(source.substring(attributeStart, index))
                        .append("</span>");
                continue;
            }
            if (c == '"' || c == '\'') {
                index = appendQuotedString(html, source, index, c);
                continue;
            }
            appendEscapedCharacter(html, c);
            index++;
        }
        return index;
    }

    private static int appendQuotedString(StringBuilder html, String source, int index, char quote) {
        int start = index;
        index++;
        while (index < source.length() && source.charAt(index) != quote) {
            index++;
        }
        if (index < source.length()) {
            index++;
        }
        html.append("<span class=\"code-string\">")
                .append(escapeHtml(source.substring(start, index)))
                .append("</span>");
        return index;
    }

    private static void appendType(StringBuilder html, String token) {
        if (token.length() == 0) {
            return;
        }
        html.append("<span class=\"code-type\">").append(token).append("</span>");
    }

    private static boolean isNameStart(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || c == ':';
    }

    private static boolean isNamePart(char c) {
        return isNameStart(c) || (c >= '0' && c <= '9') || c == '-' || c == '.';
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
