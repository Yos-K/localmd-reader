package io.github.yosk.mdlite.domain;

public final class DocumentOpeningPlan {
    private final DocumentRenderingSession session;
    private final DocumentRenderInput renderInput;
    private final MermaidRenderJob[] jobs;

    DocumentOpeningPlan(
            DocumentRenderingSession session,
            DocumentRenderInput renderInput,
            MermaidRenderJob[] jobs) {
        if (session == null || renderInput == null || jobs == null) {
            throw new IllegalArgumentException("document opening requires a session, input, and jobs");
        }
        this.session = session;
        this.renderInput = renderInput;
        this.jobs = copy(jobs);
    }

    public DocumentRenderingSession session() {
        return session;
    }

    public DocumentRenderInput renderInput() {
        return renderInput;
    }

    public MermaidRenderJob[] jobs() {
        return copy(jobs);
    }

    private static MermaidRenderJob[] copy(MermaidRenderJob[] source) {
        MermaidRenderJob[] result = new MermaidRenderJob[source.length];
        System.arraycopy(source, 0, result, 0, source.length);
        return result;
    }
}
