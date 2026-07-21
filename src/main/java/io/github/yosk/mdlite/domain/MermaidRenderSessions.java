package io.github.yosk.mdlite.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MermaidRenderSessions {
    private static final MermaidRenderSessions EMPTY = new MermaidRenderSessions(
            Collections.<String, DocumentState>emptyMap());

    private final Map<String, DocumentState> documents;

    private MermaidRenderSessions(Map<String, DocumentState> documents) {
        this.documents = Collections.unmodifiableMap(new HashMap<String, DocumentState>(documents));
    }

    public static MermaidRenderSessions empty() {
        return EMPTY;
    }

    public MermaidRenderSessions register(String documentUri, MermaidDiagramBlocks blocks) {
        requireDocument(documentUri, blocks);
        DocumentState previous = documents.get(documentUri);
        long generation = previous == null ? 0L : nextGeneration(previous.generation);
        Map<String, DocumentState> next = mutableDocuments();
        next.put(documentUri, DocumentState.initial(blocks, generation));
        return new MermaidRenderSessions(next);
    }

    public MermaidRenderSessions close(String documentUri) {
        if (!documents.containsKey(documentUri)) {
            return this;
        }
        Map<String, DocumentState> next = mutableDocuments();
        next.remove(documentUri);
        return next.isEmpty() ? EMPTY : new MermaidRenderSessions(next);
    }

    public MermaidRenderSchedule schedule(String documentUri) {
        DocumentState document = documents.get(documentUri);
        if (document == null) {
            return new MermaidRenderSchedule(this, new MermaidRenderJob[0]);
        }
        MermaidDiagramBlock[] blocks = document.blocks.items();
        ArrayList<MermaidRenderJob> jobs = new ArrayList<MermaidRenderJob>();
        HashSet<Integer> pending = new HashSet<Integer>(document.pending);
        for (int index = 0; index < blocks.length; index++) {
            Integer key = Integer.valueOf(index);
            if (!document.rendered.containsKey(key) && !pending.contains(key)) {
                jobs.add(new MermaidRenderJob(documentUri, index, document.generation, blocks[index]));
                pending.add(key);
            }
        }
        if (jobs.isEmpty()) {
            return new MermaidRenderSchedule(this, new MermaidRenderJob[0]);
        }
        Map<String, DocumentState> next = mutableDocuments();
        next.put(documentUri, document.withPending(pending));
        return new MermaidRenderSchedule(
                new MermaidRenderSessions(next),
                jobs.toArray(new MermaidRenderJob[jobs.size()]));
    }

    public MermaidRenderSessions complete(MermaidRenderJob job, SafeHtml renderedDiagram) {
        if (job == null || renderedDiagram == null) {
            throw new IllegalArgumentException("Mermaid completion requires a job and safe rendered HTML.");
        }
        DocumentState document = documents.get(job.documentUri());
        Integer index = Integer.valueOf(job.diagramIndex());
        if (document == null
                || document.generation != job.generation()
                || !document.pending.contains(index)) {
            return this;
        }
        HashSet<Integer> pending = new HashSet<Integer>(document.pending);
        pending.remove(index);
        HashMap<Integer, SafeHtml> rendered = new HashMap<Integer, SafeHtml>(document.rendered);
        rendered.put(index, renderedDiagram);
        Map<String, DocumentState> next = mutableDocuments();
        next.put(job.documentUri(), document.withResults(pending, rendered));
        return new MermaidRenderSessions(next);
    }

    public MermaidRenderSessions resetRendered() {
        if (documents.isEmpty()) {
            return this;
        }
        HashMap<String, DocumentState> next = new HashMap<String, DocumentState>();
        for (Map.Entry<String, DocumentState> entry : documents.entrySet()) {
            DocumentState document = entry.getValue();
            next.put(entry.getKey(), DocumentState.initial(
                    document.blocks,
                    nextGeneration(document.generation)));
        }
        return new MermaidRenderSessions(next);
    }

    public Map<Integer, SafeHtml> renderedFor(String documentUri) {
        DocumentState document = documents.get(documentUri);
        return document == null ? Collections.<Integer, SafeHtml>emptyMap() : document.rendered;
    }

    public List<String> documentUris() {
        return Collections.unmodifiableList(new ArrayList<String>(documents.keySet()));
    }

    private Map<String, DocumentState> mutableDocuments() {
        return new HashMap<String, DocumentState>(documents);
    }

    private static void requireDocument(String documentUri, MermaidDiagramBlocks blocks) {
        if (documentUri == null || documentUri.trim().length() == 0 || blocks == null) {
            throw new IllegalArgumentException("Mermaid document requires a URI and extracted blocks.");
        }
    }

    private static long nextGeneration(long generation) {
        return generation == Long.MAX_VALUE ? 0L : generation + 1L;
    }

    private static final class DocumentState {
        private final MermaidDiagramBlocks blocks;
        private final long generation;
        private final Set<Integer> pending;
        private final Map<Integer, SafeHtml> rendered;

        private DocumentState(
                MermaidDiagramBlocks blocks,
                long generation,
                Set<Integer> pending,
                Map<Integer, SafeHtml> rendered) {
            this.blocks = blocks;
            this.generation = generation;
            this.pending = Collections.unmodifiableSet(new HashSet<Integer>(pending));
            this.rendered = Collections.unmodifiableMap(new HashMap<Integer, SafeHtml>(rendered));
        }

        private static DocumentState initial(MermaidDiagramBlocks blocks, long generation) {
            return new DocumentState(
                    blocks,
                    generation,
                    Collections.<Integer>emptySet(),
                    Collections.<Integer, SafeHtml>emptyMap());
        }

        private DocumentState withPending(Set<Integer> nextPending) {
            return new DocumentState(blocks, generation, nextPending, rendered);
        }

        private DocumentState withResults(Set<Integer> nextPending, Map<Integer, SafeHtml> nextRendered) {
            return new DocumentState(blocks, generation, nextPending, nextRendered);
        }
    }
}
