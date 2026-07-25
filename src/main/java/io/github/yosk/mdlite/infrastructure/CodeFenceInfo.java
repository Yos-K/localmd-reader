package io.github.yosk.mdlite.infrastructure;

import java.util.Locale;

final class CodeFenceInfo {
    private static final CodeFenceInfo PLAIN = new CodeFenceInfo("");
    private final String language;

    private CodeFenceInfo(String language) {
        this.language = language;
    }

    static CodeFenceInfo plain() {
        return PLAIN;
    }

    static CodeFenceInfo fromFenceLine(String fenceLine) {
        String trimmed = fenceLine == null ? "" : fenceLine.trim();
        if (trimmed.length() <= 3) {
            return PLAIN;
        }
        String token = firstToken(trimmed.substring(3).trim());
        return isSafeLanguage(token)
                ? new CodeFenceInfo(token.toLowerCase(Locale.US)) : PLAIN;
    }

    String language() {
        return language;
    }

    boolean isMermaid() {
        return "mermaid".equals(language);
    }

    boolean isPreviewable() {
        return "html".equals(language) || isMarkdownPreview();
    }

    boolean isMarkdownPreview() {
        return "markdown".equals(language) || "md".equals(language);
    }

    private static String firstToken(String infoString) {
        int end = 0;
        while (end < infoString.length() && !Character.isWhitespace(infoString.charAt(end))) {
            end++;
        }
        return infoString.substring(0, end);
    }

    private static boolean isSafeLanguage(String language) {
        if (language.length() == 0) {
            return false;
        }
        for (int i = 0; i < language.length(); i++) {
            char value = language.charAt(i);
            boolean safe = Character.isLetterOrDigit(value) || value == '_' || value == '-';
            if (!safe) {
                return false;
            }
        }
        return true;
    }
}
