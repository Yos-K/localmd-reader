package io.github.yosk.mdlite.infrastructure;

public final class PythonCodeHighlighter {
    private static final String[] KEYWORDS = {
        "and", "as", "assert", "async", "await", "break", "class", "continue",
        "def", "del", "elif", "else", "except", "finally", "for", "from",
        "global", "if", "import", "in", "is", "lambda", "nonlocal", "not",
        "or", "pass", "raise", "return", "try", "while", "with", "yield"
    };
    private static final String[] LITERALS = { "True", "False", "None" };
    private static final String[] TYPE_INTRODUCERS = { "class" };
    private static final String[] FUNCTION_INTRODUCERS = { "def" };
    private static final String[] VARIABLE_INTRODUCERS = new String[0];

    private PythonCodeHighlighter() {
    }

    public static String highlightLine(String line) {
        return KeywordCodeHighlighter.highlightLine(line, KEYWORDS, LITERALS, TYPE_INTRODUCERS, FUNCTION_INTRODUCERS, VARIABLE_INTRODUCERS);
    }
}
