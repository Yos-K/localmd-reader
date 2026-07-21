package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.DocumentOpeningPlan;
import io.github.yosk.mdlite.domain.DocumentRenderInput;
import io.github.yosk.mdlite.domain.DocumentRenderingBatchPlan;
import io.github.yosk.mdlite.domain.DocumentRenderingCompletion;
import io.github.yosk.mdlite.domain.DocumentRenderingProfile;
import io.github.yosk.mdlite.domain.DocumentRenderingSession;
import io.github.yosk.mdlite.domain.DocumentUri;
import io.github.yosk.mdlite.domain.MermaidRenderJob;
import io.github.yosk.mdlite.domain.SafeHtml;

public final class DocumentRenderingCoordinator {
    private final Output output;
    private DocumentRenderingSession session;

    public DocumentRenderingCoordinator(Output output) {
        if (output == null) {
            throw new IllegalArgumentException("document rendering coordinator requires output");
        }
        this.output = output;
        this.session = DocumentRenderingSession.empty();
    }

    public SafeHtml open(
            DocumentUri documentUri,
            String markdown,
            DocumentRenderingProfile profile) {
        DocumentOpeningPlan plan = session.open(documentUri, markdown, profile);
        session = plan.session();
        output.enqueue(plan.jobs());
        return output.render(plan.renderInput());
    }

    public void complete(MermaidRenderJob job, SafeHtml renderedDiagram) {
        DocumentRenderingCompletion completion = session.complete(job, renderedDiagram);
        session = completion.session();
        completion.dispatch(new DocumentRenderingCompletion.Handler() {
            @Override public void rendered(DocumentRenderInput input) { output.refresh(input); }
            @Override public void unchanged() { }
        });
    }

    public void resetForTheme(DocumentRenderingProfile profile) {
        DocumentRenderingBatchPlan plan = session.resetForTheme(profile);
        session = plan.session();
        output.enqueue(plan.jobs());
        DocumentRenderInput[] inputs = plan.renderInputs();
        for (int i = 0; i < inputs.length; i++) {
            output.refresh(inputs[i]);
        }
    }

    public void close(DocumentTabCloseResult result) {
        if (result == null) {
            throw new IllegalArgumentException("document rendering close requires a tab close result");
        }
        session = result.renderingSessionAfter(session);
    }

    public String markdownFor(DocumentUri documentUri) {
        return session.markdownFor(documentUri);
    }

    public interface Output {
        SafeHtml render(DocumentRenderInput input);
        void enqueue(MermaidRenderJob[] jobs);
        void refresh(DocumentRenderInput input);
    }
}
