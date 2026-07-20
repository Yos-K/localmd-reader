package io.github.yosk.mdlite.domain;

public final class MermaidDiagramBlock {
    private final String source;

    private MermaidDiagramBlock(String source) {
        this.source = source;
    }

    public static MermaidDiagramBlock fromSource(String source) {
        String normalized = source == null ? "" : source.trim();
        if (normalized.length() == 0) {
            throw new IllegalArgumentException("Mermaid diagram source must not be empty.");
        }
        return new MermaidDiagramBlock(normalized);
    }

    public String source() {
        return source;
    }
}
