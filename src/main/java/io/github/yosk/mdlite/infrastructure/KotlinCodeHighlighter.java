package io.github.yosk.mdlite.infrastructure;

public final class KotlinCodeHighlighter {
    private static final String[] KEYWORDS = {
        "as", "break", "class", "continue", "data", "do", "else", "enum",
        "for", "fun", "if", "in", "interface", "is",
        "object", "package", "return", "sealed", "super", "this", "throw",
        "try", "typealias", "val", "var", "when", "while"
    };
    private static final String[] LITERALS = { "true", "false", "null" };

    private KotlinCodeHighlighter() {
    }

    public static String highlightLine(String line) {
        return KeywordCodeHighlighter.highlightLine(line, KEYWORDS, LITERALS);
    }
}
