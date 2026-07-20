package io.github.yosk.mdlite.domain;

public final class MermaidRenderSchedule {
    private final MermaidRenderSessions session;
    private final MermaidRenderJob[] jobs;

    MermaidRenderSchedule(MermaidRenderSessions session, MermaidRenderJob[] jobs) {
        this.session = session;
        this.jobs = copy(jobs);
    }

    public MermaidRenderSessions session() {
        return session;
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
