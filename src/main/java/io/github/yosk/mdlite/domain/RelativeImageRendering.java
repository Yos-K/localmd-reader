package io.github.yosk.mdlite.domain;

public final class RelativeImageRendering {
    private static final RelativeImageRendering DISABLED = new RelativeImageRendering(false);
    private static final RelativeImageRendering ENABLED = new RelativeImageRendering(true);

    private final boolean enabled;

    private RelativeImageRendering(boolean enabled) {
        this.enabled = enabled;
    }

    public static RelativeImageRendering disabled() {
        return DISABLED;
    }

    public static RelativeImageRendering enabled() {
        return ENABLED;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
