package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.model.RestorableOpenDocumentLoader;

final class MainActivityRestorableOpenDocumentRenderer implements RestorableOpenDocumentLoader.Renderer {
    private final MainActivity activity;

    MainActivityRestorableOpenDocumentRenderer(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public SafeHtml render(String uri, String markdown) {
        return activity.renderMarkdownForUri(uri, markdown);
    }
}
