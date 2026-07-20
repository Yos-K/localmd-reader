package io.github.yosk.mdlite.infrastructure;

public final class ShellCodeHighlighter {
    private ShellCodeHighlighter() {
    }

    public static String highlightLine(String line) {
        String source = line == null ? "" : line;
        int commandEnd = firstTokenEnd(source);
        if (commandEnd == 0) {
            return escapeHtml(source);
        }
        return "<span class=\"code-command\">"
                + escapeHtml(source.substring(0, commandEnd))
                + "</span>"
                + escapeHtml(source.substring(commandEnd));
    }

    private static int firstTokenEnd(String source) {
        int index = 0;
        while (index < source.length() && source.charAt(index) == ' ') {
            index++;
        }
        int start = index;
        while (index < source.length() && source.charAt(index) != ' ' && source.charAt(index) != '\t') {
            index++;
        }
        if (start == index) {
            return 0;
        }
        return index;
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
