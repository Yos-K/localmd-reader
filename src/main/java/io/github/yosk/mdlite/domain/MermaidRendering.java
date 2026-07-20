package io.github.yosk.mdlite.domain;

public final class MermaidRendering {
    private static final MermaidRendering PLAIN_CODE = new MermaidRendering(false);
    private static final MermaidRendering DIAGRAMS = new MermaidRendering(true);

    private final boolean enabled;

    private MermaidRendering(boolean enabled) {
        this.enabled = enabled;
    }

    public static MermaidRendering plainCode() {
        return PLAIN_CODE;
    }

    public static MermaidRendering diagrams() {
        return DIAGRAMS;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
