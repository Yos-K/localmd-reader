package io.github.yosk.mdlite.domain;

public final class DocumentRenderingPlan {
    private final DocumentRenderingSession session;
    private final DocumentRenderInput[] renderInputs;
    private final MermaidRenderJob[] jobs;

    DocumentRenderingPlan(
            DocumentRenderingSession session,
            DocumentRenderInput[] renderInputs,
            MermaidRenderJob[] jobs) {
        this.session = session;
        this.renderInputs = copy(renderInputs);
        this.jobs = copy(jobs);
    }

    public DocumentRenderingSession session() {
        return session;
    }

    public DocumentRenderInput[] renderInputs() {
        return copy(renderInputs);
    }

    public MermaidRenderJob[] jobs() {
        return copy(jobs);
    }

    private static DocumentRenderInput[] copy(DocumentRenderInput[] source) {
        DocumentRenderInput[] result = new DocumentRenderInput[source.length];
        System.arraycopy(source, 0, result, 0, source.length);
        return result;
    }

    private static MermaidRenderJob[] copy(MermaidRenderJob[] source) {
        MermaidRenderJob[] result = new MermaidRenderJob[source.length];
        System.arraycopy(source, 0, result, 0, source.length);
        return result;
    }
}
