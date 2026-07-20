package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.SafeHtml;

public final class DraftMarkdownDocument {
    private final String displayName;
    private final String uri;
    private final String markdown;
    private final SafeHtml rendered;

    public DraftMarkdownDocument(String displayName, String uri, String markdown, SafeHtml rendered) {
        this.displayName = displayName;
        this.uri = uri;
        this.markdown = markdown;
        this.rendered = rendered;
    }

    public String displayName() {
        return displayName;
    }

    public String uri() {
        return uri;
    }

    public String markdown() {
        return markdown;
    }

    public SafeHtml rendered() {
        return rendered;
    }
}
