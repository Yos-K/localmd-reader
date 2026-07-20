package io.github.yosk.mdlite.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MarkdownLibraryPath {
    private final List<String> segments;

    private MarkdownLibraryPath(List<String> segments) {
        if (segments == null || segments.isEmpty()) {
            throw new IllegalArgumentException("Markdown library path must not be empty");
        }
        this.segments = Collections.unmodifiableList(new ArrayList<String>(segments));
    }

    static MarkdownLibraryPath root(String displayName) {
        ArrayList<String> segments = new ArrayList<String>();
        segments.add(required(displayName));
        return new MarkdownLibraryPath(segments);
    }

    MarkdownLibraryPath append(String displayName) {
        ArrayList<String> appended = new ArrayList<String>(segments);
        appended.add(required(displayName));
        return new MarkdownLibraryPath(appended);
    }

    public List<String> segments() {
        return segments;
    }

    public String join(String separator) {
        requireSeparator(separator);
        StringBuilder joined = new StringBuilder(segments.get(0));
        for (int i = 1; i < segments.size(); i++) {
            joined.append(separator).append(segments.get(i));
        }
        return joined.toString();
    }

    public String compactJoin(String separator, String omissionMarker, int maxSegments) {
        requireSeparator(separator);
        if (omissionMarker == null || omissionMarker.trim().length() == 0) {
            throw new IllegalArgumentException("breadcrumb omission marker must not be empty");
        }
        if (maxSegments < 3) {
            throw new IllegalArgumentException("compact breadcrumb must allow root, omission, and current segment");
        }
        if (segments.size() <= maxSegments) {
            return join(separator);
        }
        StringBuilder joined = new StringBuilder(segments.get(0));
        joined.append(separator).append(omissionMarker.trim());
        int firstVisibleTail = segments.size() - (maxSegments - 2);
        for (int i = firstVisibleTail; i < segments.size(); i++) {
            joined.append(separator).append(segments.get(i));
        }
        return joined.toString();
    }

    private static void requireSeparator(String separator) {
        if (separator == null || separator.length() == 0) {
            throw new IllegalArgumentException("breadcrumb separator must not be empty");
        }
    }

    private static String required(String displayName) {
        String safe = displayName == null ? "" : displayName.trim();
        if (safe.length() == 0) {
            throw new IllegalArgumentException("path segment must not be empty");
        }
        return safe;
    }
}
