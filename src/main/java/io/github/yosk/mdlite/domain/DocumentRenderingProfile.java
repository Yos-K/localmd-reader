package io.github.yosk.mdlite.domain;

public final class DocumentRenderingProfile {
    private final CodeHighlighting codeHighlighting;
    private final MermaidRendering mermaidRendering;
    private final RelativeLinkRendering relativeLinkRendering;
    private final RelativeImageRendering relativeImageRendering;

    private DocumentRenderingProfile(
            CodeHighlighting codeHighlighting,
            MermaidRendering mermaidRendering,
            RelativeLinkRendering relativeLinkRendering,
            RelativeImageRendering relativeImageRendering) {
        this.codeHighlighting = codeHighlighting;
        this.mermaidRendering = mermaidRendering;
        this.relativeLinkRendering = relativeLinkRendering;
        this.relativeImageRendering = relativeImageRendering;
    }

    public static DocumentRenderingProfile fromEntitlement(FeatureEntitlement entitlement) {
        return new DocumentRenderingProfile(
                CodeHighlightingPolicy.fromEntitlement(entitlement),
                MermaidRenderingPolicy.fromEntitlement(entitlement),
                RelativeLinkRenderingPolicy.fromEntitlement(entitlement),
                RelativeImageRenderingPolicy.fromEntitlement(entitlement));
    }

    public CodeHighlighting codeHighlighting() {
        return codeHighlighting;
    }

    public MermaidRendering mermaidRendering() {
        return mermaidRendering;
    }

    public RelativeLinkRendering relativeLinkRendering() {
        return relativeLinkRendering;
    }

    public RelativeImageRendering relativeImageRendering() {
        return relativeImageRendering;
    }
}
