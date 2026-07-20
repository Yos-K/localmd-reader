package io.github.yosk.mdlite.domain;

public final class RelativeLinkRendering {
    private static final RelativeLinkRendering DISABLED = new RelativeLinkRendering(false);
    private static final RelativeLinkRendering ENABLED = new RelativeLinkRendering(true);

    private final boolean enabled;

    private RelativeLinkRendering(boolean enabled) {
        this.enabled = enabled;
    }

    public static RelativeLinkRendering disabled() {
        return DISABLED;
    }

    public static RelativeLinkRendering enabled() {
        return ENABLED;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
