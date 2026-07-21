package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.DocumentRenderInput;
import io.github.yosk.mdlite.domain.DocumentRenderingProfile;
import io.github.yosk.mdlite.domain.DocumentUri;
import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.MermaidRenderJob;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class DocumentRenderingCoordinatorTest {
    private static final DocumentUri DOCUMENT_URI = DocumentUri.from("content://guide");
    private static final SafeHtml RENDERED = SafeHtml.fromTrustedRendererOutput("<h1>Guide</h1>");

    @Test
    void openingDocumentReturnsOutputRenderedFromItsRequiredInput() {
        RecordingOutput output = new RecordingOutput();
        DocumentRenderingCoordinator coordinator = new DocumentRenderingCoordinator(output);

        SafeHtml rendered = coordinator.open(DOCUMENT_URI, "# Guide", freeProfile());

        TestAssertions.assertSame(RENDERED, rendered,
                "opening must return the output rendered from its required input");
    }

    @Test
    void openingMermaidDocumentEnqueuesItsBackgroundJob() {
        RecordingOutput output = new RecordingOutput();
        DocumentRenderingCoordinator coordinator = new DocumentRenderingCoordinator(output);

        coordinator.open(DOCUMENT_URI, mermaidMarkdown(), freeProfile());

        TestAssertions.assertEquals(1, output.enqueuedJobs.length,
                "opening Mermaid Markdown must enqueue the planned background job");
    }

    @Test
    void acceptedMermaidCompletionRefreshesItsDocument() {
        RecordingOutput output = new RecordingOutput();
        DocumentRenderingCoordinator coordinator = new DocumentRenderingCoordinator(output);
        coordinator.open(DOCUMENT_URI, mermaidMarkdown(), freeProfile());

        coordinator.complete(
                output.enqueuedJobs[0],
                SafeHtml.fromTrustedRendererOutput("<svg>complete</svg>"));

        TestAssertions.assertEquals(1, output.refreshCount,
                "accepted Mermaid completion must refresh exactly its document");
    }

    @Test
    void closingTabAlsoClosesItsMarkdownSource() {
        RecordingOutput output = new RecordingOutput();
        DocumentRenderingCoordinator coordinator = new DocumentRenderingCoordinator(output);
        coordinator.open(DOCUMENT_URI, "# Guide", freeProfile());
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(tab("Guide", DOCUMENT_URI.value()));
        DocumentTabCloseResult closeResult = tabs.closeOrFallback(
                0,
                tab("Welcome", "app://welcome"));

        coordinator.close(closeResult);

        TestAssertions.assertEquals("", coordinator.markdownFor(DOCUMENT_URI),
                "closing a tab must remove its Markdown from the owned rendering session");
    }

    private static OpenDocumentTab tab(String title, String uri) {
        return OpenDocumentTab.fileDocument(title, uri, RENDERED);
    }

    private static String mermaidMarkdown() {
        return "```mermaid\ngraph TD\nA-->B\n```";
    }

    private static DocumentRenderingProfile freeProfile() {
        return DocumentRenderingProfile.fromEntitlement(FeatureEntitlement.free());
    }

    private static final class RecordingOutput implements DocumentRenderingCoordinator.Output {
        private MermaidRenderJob[] enqueuedJobs = new MermaidRenderJob[0];
        private int refreshCount;

        @Override public SafeHtml render(DocumentRenderInput input) { return RENDERED; }
        @Override public void enqueue(MermaidRenderJob[] jobs) { enqueuedJobs = jobs; }
        @Override public void refresh(DocumentRenderInput input) { refreshCount++; }
    }
}
