package io.github.yosk.mdlite.file;

import java.util.Locale;

public abstract class MarkdownLibraryQuery {
    private MarkdownLibraryQuery() {
    }

    public static MarkdownLibraryQuery from(String raw) {
        String normalized = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        return normalized.length() == 0 ? new AllItems() : new MatchingName(normalized);
    }

    abstract boolean matches(MarkdownLibraryItem item);

    private static final class AllItems extends MarkdownLibraryQuery {
        @Override
        boolean matches(MarkdownLibraryItem item) {
            return true;
        }
    }

    private static final class MatchingName extends MarkdownLibraryQuery {
        private final String normalizedText;

        private MatchingName(String normalizedText) {
            this.normalizedText = normalizedText;
        }

        @Override
        boolean matches(MarkdownLibraryItem item) {
            return item.displayName().toLowerCase(Locale.ROOT).contains(normalizedText);
        }
    }
}
