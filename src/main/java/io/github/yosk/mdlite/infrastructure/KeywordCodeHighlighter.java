package io.github.yosk.mdlite.infrastructure;

public final class KeywordCodeHighlighter {
    private KeywordCodeHighlighter() {
    }

    public static String highlightLine(String line, String[] keywords, String[] literals) {
        String source = line == null ? "" : line;
        StringBuilder html = new StringBuilder();
        int index = 0;
        while (index < source.length()) {
            char c = source.charAt(index);
            if (isIdentifierStart(c)) {
                int end = index + 1;
                while (end < source.length() && isIdentifierPart(source.charAt(end))) {
                    end++;
                }
                String token = source.substring(index, end);
                appendToken(html, token, keywords, literals);
                index = end;
                continue;
            }
            appendEscapedCharacter(html, c);
            index++;
        }
        return html.toString();
    }

    private static void appendToken(StringBuilder html, String token, String[] keywords, String[] literals) {
        if (contains(keywords, token)) {
            html.append("<span class=\"code-keyword\">").append(token).append("</span>");
            return;
        }
        if (contains(literals, token)) {
            html.append("<span class=\"code-literal\">").append(token).append("</span>");
            return;
        }
        html.append(token);
    }

    private static boolean contains(String[] values, String token) {
        if (values == null) {
            return false;
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(token)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || c == '$';
    }

    private static boolean isIdentifierPart(char c) {
        return isIdentifierStart(c) || (c >= '0' && c <= '9');
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
