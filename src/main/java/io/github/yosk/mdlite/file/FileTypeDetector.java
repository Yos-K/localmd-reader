package io.github.yosk.mdlite.file;

import java.util.Locale;

public final class FileTypeDetector {
    private FileTypeDetector() {
    }

    public static boolean isMarkdownDisplayName(String displayName) {
        if (displayName == null) {
            return false;
        }
        String normalized = displayName.trim().toLowerCase(Locale.ROOT);
        return normalized.endsWith(".md") || normalized.endsWith(".markdown");
    }
}
