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
            Collections.<DocumentUri, DocumentState>emptyMap());

    private final Map<DocumentUri, DocumentState> documents;

    private MermaidRenderSessions(Map<DocumentUri, DocumentState> documents) {
        this.documents = Collections.unmodifiableMap(new HashMap<DocumentUri, DocumentState>(documents));
    }

    public static MermaidRenderSessions empty() {
        return EMPTY;
    }

    public MermaidRenderSessions register(DocumentUri documentUri, MermaidDiagramBlocks blocks) {
        requireDocument(documentUri, blocks);
        DocumentState previous = documents.get(documentUri);
        long generation = previous == null ? 0L : nextGeneration(previous.generation);
        Map<DocumentUri, DocumentState> next = mutableDocuments();
        next.put(documentUri, DocumentState.initial(blocks, generation));
        return new MermaidRenderSessions(next);
    }

    public MermaidRenderSessions close(DocumentUri documentUri) {
        if (!documents.containsKey(documentUri)) {
            return this;
        }
        Map<DocumentUri, DocumentState> next = mutableDocuments();
        next.remove(documentUri);
        return next.isEmpty() ? EMPTY : new MermaidRenderSessions(next);
    }

    public MermaidRenderSchedule schedule(DocumentUri documentUri) {
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
        Map<DocumentUri, DocumentState> next = mutableDocuments();
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
        Map<DocumentUri, DocumentState> next = mutableDocuments();
        next.put(job.documentUri(), document.withResults(pending, rendered));
        return new MermaidRenderSessions(next);
    }

    public MermaidRenderSessions resetRendered() {
        if (documents.isEmpty()) {
            return this;
        }
        HashMap<DocumentUri, DocumentState> next = new HashMap<DocumentUri, DocumentState>();
        for (Map.Entry<DocumentUri, DocumentState> entry : documents.entrySet()) {
            DocumentState document = entry.getValue();
            next.put(entry.getKey(), DocumentState.initial(
                    document.blocks,
                    nextGeneration(document.generation)));
        }
        return new MermaidRenderSessions(next);
    }

    public Map<Integer, SafeHtml> renderedFor(DocumentUri documentUri) {
        DocumentState document = documents.get(documentUri);
        return document == null ? Collections.<Integer, SafeHtml>emptyMap() : document.rendered;
    }

    public List<DocumentUri> documentUris() {
        return Collections.unmodifiableList(new ArrayList<DocumentUri>(documents.keySet()));
    }

    private Map<DocumentUri, DocumentState> mutableDocuments() {
        return new HashMap<DocumentUri, DocumentState>(documents);
    }

    private static void requireDocument(DocumentUri documentUri, MermaidDiagramBlocks blocks) {
        if (documentUri == null || blocks == null) {
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
