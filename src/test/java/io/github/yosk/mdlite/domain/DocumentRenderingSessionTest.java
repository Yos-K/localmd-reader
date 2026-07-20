package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class DocumentRenderingSessionTest {
    private static final String DOCUMENT_URI = "content://guide";
    private static final String MARKDOWN = "# Guide\n\n```mermaid\ngraph TD\nA-->B\n```";

    @Test
    void openingDocumentProducesOneAlwaysValidRenderInput() {
        DocumentRenderingPlan plan = DocumentRenderingSession.empty().open(
                DOCUMENT_URI,
                null,
                freeProfile());

        TestAssertions.assertEquals(1, plan.renderInputs().length, "opening a document must produce its render input");
    }

    @Test
    void openingDocumentNormalizesMissingMarkdownToEmptyText() {
        DocumentRenderingPlan plan = DocumentRenderingSession.empty().open(
                DOCUMENT_URI,
                null,
                freeProfile());

        TestAssertions.assertEquals("", plan.renderInputs()[0].markdown(), "render input Markdown must never be null");
    }

    @Test
    void openingMermaidDocumentSchedulesItsDiagram() {
        DocumentRenderingPlan plan = openMermaidDocument();

        TestAssertions.assertEquals(1, plan.jobs().length, "opening Mermaid Markdown must schedule its diagram once");
    }

    @Test
    void openedMarkdownRemainsAvailableForDocumentNavigation() {
        DocumentRenderingPlan opened = openMermaidDocument();

        TestAssertions.assertEquals(MARKDOWN, opened.session().markdownFor(DOCUMENT_URI),
                "opened Markdown must remain available to derived navigation models");
    }

    @Test
    void unknownDocumentHasSafeEmptyMarkdown() {
        DocumentRenderingSession session = DocumentRenderingSession.empty();

        TestAssertions.assertEquals("", session.markdownFor("content://missing"),
                "unknown document must not expose a nullable Markdown source");
    }

    @Test
    void completedDiagramProducesAnUpdatedRenderInput() {
        DocumentRenderingPlan opened = openMermaidDocument();
        SafeHtml svg = SafeHtml.fromTrustedRendererOutput("<svg>complete</svg>");

        DocumentRenderingPlan completed = opened.session().complete(opened.jobs()[0], svg);

        TestAssertions.assertSame(
                svg,
                completed.renderInputs()[0].renderedMermaidDiagrams().get(Integer.valueOf(0)),
                "accepted Mermaid completion must produce a render input containing its SVG");
    }

    @Test
    void staleCompletionProducesNoRenderInput() {
        DocumentRenderingPlan oldTheme = openMermaidDocument();
        DocumentRenderingPlan newTheme = oldTheme.session().resetForTheme(freeProfile());

        DocumentRenderingPlan stale = newTheme.session().complete(
                oldTheme.jobs()[0],
                SafeHtml.fromTrustedRendererOutput("<svg>stale</svg>"));

        TestAssertions.assertEquals(0, stale.renderInputs().length, "stale completion must not request document re-rendering");
    }

    @Test
    void themeResetSchedulesEveryKnownDiagramAgain() {
        DocumentRenderingPlan opened = openMermaidDocument();

        DocumentRenderingPlan reset = opened.session().resetForTheme(freeProfile());

        TestAssertions.assertEquals(1, reset.jobs().length, "theme reset must schedule every known diagram for the new theme");
    }

    @Test
    void themeResetProducesRenderInputForEveryKnownDocument() {
        DocumentRenderingPlan first = openMermaidDocument();
        DocumentRenderingPlan second = first.session().open(
                "content://notes",
                "# Notes",
                freeProfile());

        DocumentRenderingPlan reset = second.session().resetForTheme(freeProfile());

        TestAssertions.assertEquals(2, reset.renderInputs().length, "theme reset must refresh every known document");
    }

    private static DocumentRenderingPlan openMermaidDocument() {
        return DocumentRenderingSession.empty().open(DOCUMENT_URI, MARKDOWN, freeProfile());
    }

    private static DocumentRenderingProfile freeProfile() {
        return DocumentRenderingProfile.fromEntitlement(FeatureEntitlement.free());
    }
}
