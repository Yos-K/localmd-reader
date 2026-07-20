package io.github.yosk.mdlite.file;

public final class FileInfo {
    public final String displayName;
    public final long sizeBytes;

    public FileInfo(String displayName, long sizeBytes) {
        this.displayName = displayName;
        this.sizeBytes = sizeBytes;
    }
}
