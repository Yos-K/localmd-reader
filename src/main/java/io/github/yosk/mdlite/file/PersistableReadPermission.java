package io.github.yosk.mdlite.file;

public final class PersistableReadPermission {
    private final int flags;

    private PersistableReadPermission(int flags) {
        this.flags = flags;
    }

    public static PersistableReadPermission fromResultFlags(int resultFlags, int readFlag) {
        return new PersistableReadPermission(resultFlags & readFlag);
    }

    public boolean isGranted() {
        return flags != 0;
    }

    public int flags() {
        return flags;
    }
}
