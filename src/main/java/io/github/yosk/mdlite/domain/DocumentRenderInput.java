package io.github.yosk.mdlite.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DocumentRenderInput {
    private final String documentUri;
    private final String markdown;
    private final Map<Integer, SafeHtml> renderedMermaidDiagrams;

    DocumentRenderInput(
            String documentUri,
            String markdown,
            Map<Integer, SafeHtml> renderedMermaidDiagrams) {
        this.documentUri = documentUri;
        this.markdown = markdown;
        this.renderedMermaidDiagrams = Collections.unmodifiableMap(
                new HashMap<Integer, SafeHtml>(renderedMermaidDiagrams));
    }

    public String documentUri() {
        return documentUri;
    }

    public String markdown() {
        return markdown;
    }

    public Map<Integer, SafeHtml> renderedMermaidDiagrams() {
        return renderedMermaidDiagrams;
    }
}
