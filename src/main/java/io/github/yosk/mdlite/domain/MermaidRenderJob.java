package io.github.yosk.mdlite.domain;

public final class MermaidRenderJob {
    private final DocumentUri documentUri;
    private final int diagramIndex;
    private final long generation;
    private final MermaidDiagramBlock block;

    MermaidRenderJob(DocumentUri documentUri, int diagramIndex, long generation, MermaidDiagramBlock block) {
        this.documentUri = documentUri;
        this.diagramIndex = diagramIndex;
        this.generation = generation;
        this.block = block;
    }

    public DocumentUri documentUri() {
        return documentUri;
    }

    public int diagramIndex() {
        return diagramIndex;
    }

    public MermaidDiagramBlock block() {
        return block;
    }

    public boolean matches(String callbackDocumentUri, int callbackDiagramIndex) {
        return documentUri.value().equals(callbackDocumentUri) && diagramIndex == callbackDiagramIndex;
    }

    long generation() {
        return generation;
    }
}
