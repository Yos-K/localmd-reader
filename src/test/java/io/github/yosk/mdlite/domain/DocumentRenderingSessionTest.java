package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class DocumentRenderingSessionTest {
    private static final DocumentUri DOCUMENT_URI = DocumentUri.from("content://guide");
    private static final String MARKDOWN = "# Guide\n\n```mermaid\ngraph TD\nA-->B\n```";

    @Test
    void openingDocumentProducesOneAlwaysValidRenderInput() {
        DocumentOpeningPlan plan = DocumentRenderingSession.empty().open(
                DOCUMENT_URI,
                null,
                freeProfile());

        TestAssertions.assertEquals(DOCUMENT_URI.value(), plan.renderInput().documentUri().value(),
                "opening a document must produce its own render input");
    }

    @Test
    void openingDocumentNormalizesMissingMarkdownToEmptyText() {
        DocumentOpeningPlan plan = DocumentRenderingSession.empty().open(
                DOCUMENT_URI,
                null,
                freeProfile());

        TestAssertions.assertEquals("", plan.renderInput().markdown(), "render input Markdown must never be null");
    }

    @Test
    void openingMermaidDocumentSchedulesItsDiagram() {
        DocumentOpeningPlan plan = openMermaidDocument();

        TestAssertions.assertEquals(1, plan.jobs().length, "opening Mermaid Markdown must schedule its diagram once");
    }

    @Test
    void openedMarkdownRemainsAvailableForDocumentNavigation() {
        DocumentOpeningPlan opened = openMermaidDocument();

        TestAssertions.assertEquals(MARKDOWN, opened.session().markdownFor(DOCUMENT_URI),
                "opened Markdown must remain available to derived navigation models");
    }

    @Test
    void unknownDocumentHasSafeEmptyMarkdown() {
        DocumentRenderingSession session = DocumentRenderingSession.empty();

        TestAssertions.assertEquals("", session.markdownFor(DocumentUri.from("content://missing")),
                "unknown document must not expose a nullable Markdown source");
    }

    @Test
    void completedDiagramProducesAnUpdatedRenderInput() {
        DocumentOpeningPlan opened = openMermaidDocument();
        SafeHtml svg = SafeHtml.fromTrustedRendererOutput("<svg>complete</svg>");
        CapturedRenderInput capture = new CapturedRenderInput();

        DocumentRenderingCompletion completed = opened.session().complete(opened.jobs()[0], svg);
        completed.dispatch(capture);

        TestAssertions.assertSame(
                svg,
                capture.input.renderedMermaidDiagrams().get(Integer.valueOf(0)),
                "accepted Mermaid completion must produce a render input containing its SVG");
    }

    @Test
    void staleCompletionProducesNoRenderInput() {
        DocumentOpeningPlan oldTheme = openMermaidDocument();
        DocumentRenderingBatchPlan newTheme = oldTheme.session().resetForTheme(freeProfile());
        RenderDispatchCount count = new RenderDispatchCount();

        DocumentRenderingCompletion stale = newTheme.session().complete(
                oldTheme.jobs()[0],
                SafeHtml.fromTrustedRendererOutput("<svg>stale</svg>"));
        stale.dispatch(count);

        TestAssertions.assertEquals(0, count.value, "stale completion must not request document re-rendering");
    }

    @Test
    void themeResetSchedulesEveryKnownDiagramAgain() {
        DocumentOpeningPlan opened = openMermaidDocument();

        DocumentRenderingBatchPlan reset = opened.session().resetForTheme(freeProfile());

        TestAssertions.assertEquals(1, reset.jobs().length, "theme reset must schedule every known diagram for the new theme");
    }

    @Test
    void themeResetProducesRenderInputForEveryKnownDocument() {
        DocumentOpeningPlan first = openMermaidDocument();
        DocumentOpeningPlan second = first.session().open(
                DocumentUri.from("content://notes"),
                "# Notes",
                freeProfile());

        DocumentRenderingBatchPlan reset = second.session().resetForTheme(freeProfile());

        TestAssertions.assertEquals(2, reset.renderInputs().length, "theme reset must refresh every known document");
    }

    @Test
    void closingDocumentRemovesItsMarkdownFromNavigation() {
        DocumentOpeningPlan opened = openMermaidDocument();

        DocumentRenderingSession closed = opened.session().close(DOCUMENT_URI);

        TestAssertions.assertEquals("", closed.markdownFor(DOCUMENT_URI),
                "closed document Markdown must not remain available to navigation");
    }

    @Test
    void closingDocumentPreventsItsDiagramFromBeingScheduledAgain() {
        DocumentOpeningPlan opened = openMermaidDocument();
        DocumentRenderingSession closed = opened.session().close(DOCUMENT_URI);

        DocumentRenderingBatchPlan reset = closed.resetForTheme(freeProfile());

        TestAssertions.assertEquals(0, reset.jobs().length,
                "closed document diagrams must not be scheduled after a theme reset");
    }

    @Test
    void completingClosedDocumentDiagramProducesNoRenderInput() {
        DocumentOpeningPlan opened = openMermaidDocument();
        DocumentRenderingSession closed = opened.session().close(DOCUMENT_URI);
        RenderDispatchCount count = new RenderDispatchCount();

        DocumentRenderingCompletion completed = closed.complete(
                opened.jobs()[0],
                SafeHtml.fromTrustedRendererOutput("<svg>late</svg>"));
        completed.dispatch(count);

        TestAssertions.assertEquals(0, count.value,
                "late diagram completion must not revive a closed document");
    }

    @Test
    void closingUnknownDocumentKeepsTheRenderingSessionUnchanged() {
        DocumentOpeningPlan opened = openMermaidDocument();

        DocumentRenderingSession unchanged = opened.session().close(DocumentUri.from("content://missing"));

        TestAssertions.assertSame(opened.session(), unchanged,
                "closing an unknown document must preserve the existing rendering session");
    }

    private static DocumentOpeningPlan openMermaidDocument() {
        return DocumentRenderingSession.empty().open(DOCUMENT_URI, MARKDOWN, freeProfile());
    }

    private static final class CapturedRenderInput implements DocumentRenderingCompletion.Handler {
        private DocumentRenderInput input;

        @Override public void rendered(DocumentRenderInput input) { this.input = input; }
        @Override public void unchanged() { }
    }

    private static final class RenderDispatchCount implements DocumentRenderingCompletion.Handler {
        private int value;

        @Override public void rendered(DocumentRenderInput input) { value++; }
        @Override public void unchanged() { }
    }

    private static DocumentRenderingProfile freeProfile() {
        return DocumentRenderingProfile.fromEntitlement(FeatureEntitlement.free());
    }
}
