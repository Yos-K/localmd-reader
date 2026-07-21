package io.github.yosk.mdlite.domain;

public final class DocumentUri {
    private final String value;

    private DocumentUri(String value) {
        this.value = value;
    }

    public static DocumentUri from(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.length() == 0) {
            throw new IllegalArgumentException("document URI must not be empty");
        }
        return new DocumentUri(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || other instanceof DocumentUri
                && value.equals(((DocumentUri) other).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
