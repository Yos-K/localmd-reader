package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.domain.DocumentRenderInput;
import io.github.yosk.mdlite.domain.DocumentUri;
import io.github.yosk.mdlite.domain.MermaidRenderJob;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.viewer.DocumentRenderingCoordinator;
import io.github.yosk.mdlite.viewer.OpenDocumentTabSession;
import io.github.yosk.mdlite.viewer.OpenDocumentTabs;

final class MainActivityDocumentRenderingOutput implements DocumentRenderingCoordinator.Output {
    private final MainActivity activity;

    MainActivityDocumentRenderingOutput(MainActivity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("document rendering output requires an activity");
        }
        this.activity = activity;
    }

    @Override
    public SafeHtml render(DocumentRenderInput input) {
        return activity.renderer.render(
                input.markdown(),
                activity.documentRenderingProfile,
                input.renderedMermaidDiagrams());
    }

    @Override
    public void enqueue(MermaidRenderJob[] jobs) {
        if (!activity.documentRenderingProfile.mermaidRendering().isEnabled()
                || activity.mermaidRenderEngine == null) {
            return;
        }
        for (int i = 0; i < jobs.length; i++) {
            activity.mermaidRenderEngine.enqueue(
                    jobs[i],
                    MermaidDiagramTheme.from(activity.currentTheme));
        }
    }

    @Override
    public void refresh(DocumentRenderInput input) {
        activity.documentTabSession.replaceRenderedDocument(
                input.documentUri(),
                render(input),
                new RefreshHandler(activity, input.documentUri()));
    }

    private static final class RefreshHandler implements OpenDocumentTabSession.ReplacementHandler {
        private final MainActivity activity;
        private final DocumentUri documentUri;

        private RefreshHandler(MainActivity activity, DocumentUri documentUri) {
            this.activity = activity;
            this.documentUri = documentUri;
        }

        @Override
        public void replaced(OpenDocumentTabs tabs) {
            activity.renderTabs();
            if (tabs.activeTab().documentUri().equals(documentUri)) {
                activity.renderCurrentDocument();
            }
        }

        @Override public void unchanged() { }
    }
}
