package io.github.yosk.mdlite.domain;

public abstract class MarkdownFileOpenResult {
    private MarkdownFileOpenResult() {
    }

    public static MarkdownFileOpenResult from(String displayName, long sizeBytes, FileSizePolicy sizePolicy) {
        if (!FileTypeDetector.isMarkdownDisplayName(displayName)) {
            return new UnsupportedMarkdownFile();
        }
        if (!sizePolicy.isReadableSize(sizeBytes)) {
            return new OversizedMarkdownFile();
        }
        return ReadableMarkdownFile.of(displayName, sizeBytes);
    }

    public static final class ReadableMarkdownFile extends MarkdownFileOpenResult {
        private final String displayName;
        private final long sizeBytes;

        private ReadableMarkdownFile(String displayName, long sizeBytes) {
            this.displayName = displayName;
            this.sizeBytes = sizeBytes;
        }

        public static ReadableMarkdownFile of(String displayName, long sizeBytes) {
            String safeDisplayName = displayName == null ? "" : displayName.trim();
            if (!FileTypeDetector.isMarkdownDisplayName(safeDisplayName)) {
                throw new IllegalArgumentException("readable Markdown file must have a supported display name");
            }
            if (sizeBytes < FileSizePolicy.UNKNOWN_SIZE) {
                throw new IllegalArgumentException("readable Markdown file size must not be invalid");
            }
            return new ReadableMarkdownFile(safeDisplayName, sizeBytes);
        }

        public String displayName() {
            return displayName;
        }

        public long sizeBytes() {
            return sizeBytes;
        }
    }

    public static final class UnsupportedMarkdownFile extends MarkdownFileOpenResult {
        private UnsupportedMarkdownFile() {
        }
    }

    public static final class OversizedMarkdownFile extends MarkdownFileOpenResult {
        private OversizedMarkdownFile() {
        }
    }
}
