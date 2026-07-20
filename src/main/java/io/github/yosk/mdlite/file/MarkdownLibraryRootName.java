package io.github.yosk.mdlite.file;

public final class MarkdownLibraryRootName {
    private final String value;

    private MarkdownLibraryRootName(String value) {
        this.value = value;
    }

    public static MarkdownLibraryRootName fromProviderValue(String providerValue, String fallback) {
        String validFallback = required(fallback, "fallback root name");
        String candidate = providerValue == null ? "" : providerValue.trim();
        return new MarkdownLibraryRootName(candidate.length() == 0 ? validFallback : candidate);
    }

    public String value() {
        return value;
    }

    private static String required(String value, String label) {
        String safe = value == null ? "" : value.trim();
        if (safe.length() == 0) {
            throw new IllegalArgumentException(label + " must not be empty");
        }
        return safe;
    }
}
