package io.github.yosk.mdlite.infrastructure;

public final class JavaCodeHighlighter {
    private JavaCodeHighlighter() {
    }

    public static String highlightLine(String line) {
        String source = line == null ? "" : line;
        StringBuilder html = new StringBuilder();
        int index = 0;
        String previousToken = "";
        while (index < source.length()) {
            char c = source.charAt(index);
            if (isIdentifierStart(c)) {
                int end = index + 1;
                while (end < source.length() && isIdentifierPart(source.charAt(end))) {
                    end++;
                }
                String token = source.substring(index, end);
                appendToken(html, token, previousToken, nextNonSpaceCharacter(source, end));
                previousToken = token;
                index = end;
                continue;
            }
            appendEscapedCharacter(html, c);
            index++;
        }
        return html.toString();
    }

    private static void appendToken(StringBuilder html, String token, String previousToken, char nextNonSpaceCharacter) {
        if (isTypeIntroducer(previousToken)) {
            html.append("<span class=\"code-type\">").append(token).append("</span>");
            return;
        }
        if (nextNonSpaceCharacter == '(' && !isControlKeyword(token)) {
            html.append("<span class=\"code-function\">").append(token).append("</span>");
            return;
        }
        if (isKeyword(token)) {
            html.append("<span class=\"code-keyword\">").append(token).append("</span>");
            return;
        }
        if (isLiteral(token)) {
            html.append("<span class=\"code-literal\">").append(token).append("</span>");
            return;
        }
        html.append(token);
    }

    private static char nextNonSpaceCharacter(String source, int index) {
        while (index < source.length() && source.charAt(index) == ' ') {
            index++;
        }
        if (index >= source.length()) {
            return '\0';
        }
        return source.charAt(index);
    }

    private static boolean isTypeIntroducer(String token) {
        return "class".equals(token) || "interface".equals(token) || "enum".equals(token);
    }

    private static boolean isControlKeyword(String token) {
        return "if".equals(token) || "for".equals(token) || "while".equals(token) || "switch".equals(token) || "catch".equals(token);
    }

    private static boolean isIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private static boolean isIdentifierPart(char c) {
        return isIdentifierStart(c) || (c >= '0' && c <= '9');
    }

    private static boolean isKeyword(String token) {
        return "abstract".equals(token)
                || "assert".equals(token)
                || "boolean".equals(token)
                || "break".equals(token)
                || "byte".equals(token)
                || "case".equals(token)
                || "catch".equals(token)
                || "char".equals(token)
                || "class".equals(token)
                || "const".equals(token)
                || "continue".equals(token)
                || "default".equals(token)
                || "do".equals(token)
                || "double".equals(token)
                || "else".equals(token)
                || "enum".equals(token)
                || "extends".equals(token)
                || "final".equals(token)
                || "finally".equals(token)
                || "float".equals(token)
                || "for".equals(token)
                || "goto".equals(token)
                || "if".equals(token)
                || "implements".equals(token)
                || "import".equals(token)
                || "instanceof".equals(token)
                || "int".equals(token)
                || "interface".equals(token)
                || "long".equals(token)
                || "native".equals(token)
                || "new".equals(token)
                || "package".equals(token)
                || "private".equals(token)
                || "protected".equals(token)
                || "public".equals(token)
                || "return".equals(token)
                || "short".equals(token)
                || "static".equals(token)
                || "strictfp".equals(token)
                || "super".equals(token)
                || "switch".equals(token)
                || "synchronized".equals(token)
                || "this".equals(token)
                || "throw".equals(token)
                || "throws".equals(token)
                || "transient".equals(token)
                || "try".equals(token)
                || "void".equals(token)
                || "volatile".equals(token)
                || "while".equals(token);
    }

    private static boolean isLiteral(String token) {
        return "true".equals(token) || "false".equals(token) || "null".equals(token);
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
