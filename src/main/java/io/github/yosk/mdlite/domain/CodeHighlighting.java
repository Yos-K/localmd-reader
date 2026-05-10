package io.github.yosk.mdlite.domain;

public final class CodeHighlighting {
    private static final CodeHighlighting PLAIN = new CodeHighlighting(false);
    private static final CodeHighlighting SYNTAX_HIGHLIGHTED = new CodeHighlighting(true);

    private final boolean enabled;

    private CodeHighlighting(boolean enabled) {
        this.enabled = enabled;
    }

    public static CodeHighlighting plain() {
        return PLAIN;
    }

    public static CodeHighlighting syntaxHighlighted() {
        return SYNTAX_HIGHLIGHTED;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
