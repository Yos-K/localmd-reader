package io.github.yosk.mdlite.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DocumentRenderInput {
    private final DocumentUri documentUri;
    private final String markdown;
    private final Map<Integer, SafeHtml> renderedMermaidDiagrams;

    DocumentRenderInput(
            DocumentUri documentUri,
            String markdown,
            Map<Integer, SafeHtml> renderedMermaidDiagrams) {
        if (documentUri == null) {
            throw new IllegalArgumentException("render input requires a document URI");
        }
        this.documentUri = documentUri;
        this.markdown = markdown;
        this.renderedMermaidDiagrams = Collections.unmodifiableMap(
                new HashMap<Integer, SafeHtml>(renderedMermaidDiagrams));
    }

    public DocumentUri documentUri() {
        return documentUri;
    }

    public String markdown() {
        return markdown;
    }

    public Map<Integer, SafeHtml> renderedMermaidDiagrams() {
        return renderedMermaidDiagrams;
    }
}
