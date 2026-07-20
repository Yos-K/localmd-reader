package io.github.yosk.mdlite.infrastructure;

public final class JavaScriptCodeHighlighter {
    private static final String[] KEYWORDS = {
        "async", "await", "break", "case", "catch", "class", "const",
        "continue", "default", "delete", "do", "else", "export", "extends",
        "finally", "for", "from", "function", "if", "import", "in",
        "instanceof", "let", "new", "of", "readonly", "return", "switch", "throw",
        "try", "typeof", "var", "void", "while", "yield"
    };
    private static final String[] LITERALS = { "true", "false", "null", "undefined" };
    private static final String[] TYPE_INTRODUCERS = { "class", "interface" };
    private static final String[] FUNCTION_INTRODUCERS = { "function" };
    private static final String[] VARIABLE_INTRODUCERS = { "const", "let", "var", "readonly" };

    private JavaScriptCodeHighlighter() {
    }

    public static String highlightLine(String line) {
        return KeywordCodeHighlighter.highlightLine(line, KEYWORDS, LITERALS, TYPE_INTRODUCERS, FUNCTION_INTRODUCERS, VARIABLE_INTRODUCERS);
    }
}
