package io.github.yosk.mdlite.domain;

public final class MermaidRenderJob {
    private final String documentUri;
    private final int diagramIndex;
    private final long generation;
    private final MermaidDiagramBlock block;

    MermaidRenderJob(String documentUri, int diagramIndex, long generation, MermaidDiagramBlock block) {
        this.documentUri = documentUri;
        this.diagramIndex = diagramIndex;
        this.generation = generation;
        this.block = block;
    }

    public String documentUri() {
        return documentUri;
    }

    public int diagramIndex() {
        return diagramIndex;
    }

    public MermaidDiagramBlock block() {
        return block;
    }

    public boolean matches(String callbackDocumentUri, int callbackDiagramIndex) {
        return documentUri.equals(callbackDocumentUri) && diagramIndex == callbackDiagramIndex;
    }

    long generation() {
        return generation;
    }
}
