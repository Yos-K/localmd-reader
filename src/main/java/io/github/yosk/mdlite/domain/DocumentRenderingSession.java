package io.github.yosk.mdlite.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DocumentRenderingSession {
    private static final DocumentRenderingSession EMPTY = new DocumentRenderingSession(
            Collections.<String, String>emptyMap(),
            MermaidRenderSessions.empty());

    private final Map<String, String> markdownByUri;
    private final MermaidRenderSessions mermaidSessions;

    private DocumentRenderingSession(
            Map<String, String> markdownByUri,
            MermaidRenderSessions mermaidSessions) {
        this.markdownByUri = Collections.unmodifiableMap(new HashMap<String, String>(markdownByUri));
        this.mermaidSessions = mermaidSessions;
    }

    public static DocumentRenderingSession empty() {
        return EMPTY;
    }

    public String markdownFor(String documentUri) {
        String markdown = markdownByUri.get(documentUri);
        return markdown == null ? "" : markdown;
    }

    public DocumentRenderingPlan open(
            String documentUri,
            String markdown,
            DocumentRenderingProfile profile) {
        requireDocumentUri(documentUri);
        String source = markdown == null ? "" : markdown;
        DocumentRenderingProfile safeProfile = safeProfile(profile);
        Map<String, String> nextMarkdown = new HashMap<String, String>(markdownByUri);
        nextMarkdown.put(documentUri, source);
        MermaidRenderSessions registered = mermaidSessions.register(
                documentUri,
                MermaidDiagramBlocks.fromMarkdown(source));
        MermaidRenderJob[] jobs = new MermaidRenderJob[0];
        if (safeProfile.mermaidRendering().isEnabled()) {
            MermaidRenderSchedule schedule = registered.schedule(documentUri);
            registered = schedule.session();
            jobs = schedule.jobs();
        }
        DocumentRenderingSession next = new DocumentRenderingSession(nextMarkdown, registered);
        return new DocumentRenderingPlan(
                next,
                new DocumentRenderInput[] {next.renderInput(documentUri)},
                jobs);
    }

    public DocumentRenderingSession close(String documentUri) {
        requireDocumentUri(documentUri);
        if (!markdownByUri.containsKey(documentUri)) {
            return this;
        }
        Map<String, String> nextMarkdown = new HashMap<String, String>(markdownByUri);
        nextMarkdown.remove(documentUri);
        if (nextMarkdown.isEmpty()) {
            return EMPTY;
        }
        return new DocumentRenderingSession(nextMarkdown, mermaidSessions.close(documentUri));
    }

    public DocumentRenderingPlan complete(MermaidRenderJob job, SafeHtml renderedDiagram) {
        MermaidRenderSessions completed = mermaidSessions.complete(job, renderedDiagram);
        if (completed == mermaidSessions) {
            return emptyPlan(this);
        }
        DocumentRenderingSession next = new DocumentRenderingSession(markdownByUri, completed);
        return new DocumentRenderingPlan(
                next,
                new DocumentRenderInput[] {next.renderInput(job.documentUri())},
                new MermaidRenderJob[0]);
    }

    public DocumentRenderingPlan resetForTheme(DocumentRenderingProfile profile) {
        DocumentRenderingProfile safeProfile = safeProfile(profile);
        MermaidRenderSessions reset = mermaidSessions.resetRendered();
        ArrayList<MermaidRenderJob> jobs = new ArrayList<MermaidRenderJob>();
        if (safeProfile.mermaidRendering().isEnabled()) {
            for (String documentUri : markdownByUri.keySet()) {
                MermaidRenderSchedule schedule = reset.schedule(documentUri);
                reset = schedule.session();
                Collections.addAll(jobs, schedule.jobs());
            }
        }
        DocumentRenderingSession next = new DocumentRenderingSession(markdownByUri, reset);
        DocumentRenderInput[] inputs = new DocumentRenderInput[markdownByUri.size()];
        int index = 0;
        for (String documentUri : markdownByUri.keySet()) {
            inputs[index] = next.renderInput(documentUri);
            index++;
        }
        return new DocumentRenderingPlan(
                next,
                inputs,
                jobs.toArray(new MermaidRenderJob[jobs.size()]));
    }

    private DocumentRenderInput renderInput(String documentUri) {
        return new DocumentRenderInput(
                documentUri,
                markdownByUri.get(documentUri),
                mermaidSessions.renderedFor(documentUri));
    }

    private static DocumentRenderingPlan emptyPlan(DocumentRenderingSession session) {
        return new DocumentRenderingPlan(
                session,
                new DocumentRenderInput[0],
                new MermaidRenderJob[0]);
    }

    private static DocumentRenderingProfile safeProfile(DocumentRenderingProfile profile) {
        return profile == null ? DocumentRenderingProfile.fromEntitlement(null) : profile;
    }

    private static void requireDocumentUri(String documentUri) {
        if (documentUri == null || documentUri.trim().length() == 0) {
            throw new IllegalArgumentException("document rendering requires a URI");
        }
    }
}
