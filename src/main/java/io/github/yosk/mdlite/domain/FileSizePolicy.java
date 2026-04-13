package io.github.yosk.mdlite.domain;

public final class FileSizePolicy {
    public static final long UNKNOWN_SIZE = -1;

    private final long maxSizeBytes;

    public FileSizePolicy(long maxSizeBytes) {
        if (maxSizeBytes < 0) {
            throw new IllegalArgumentException("max file size must not be negative");
        }
        this.maxSizeBytes = maxSizeBytes;
    }

    public boolean isReadableSize(long sizeBytes) {
        return sizeBytes == UNKNOWN_SIZE || (sizeBytes >= 0 && sizeBytes <= maxSizeBytes);
    }
}
