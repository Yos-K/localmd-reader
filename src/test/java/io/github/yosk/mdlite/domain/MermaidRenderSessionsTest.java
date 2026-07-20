package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MermaidRenderSessionsTest {
    private static final String DOCUMENT_URI = "content://guide";

    @Test
    void registeredDocumentSchedulesEveryDiagramForBackgroundRendering() {
        MermaidRenderSchedule schedule = registeredSession().schedule(DOCUMENT_URI);

        TestAssertions.assertEquals(2, schedule.jobs().length, "every registered Mermaid block must be scheduled once");
    }

    @Test
    void scheduledDocumentDoesNotSchedulePendingDiagramsAgain() {
        MermaidRenderSchedule first = registeredSession().schedule(DOCUMENT_URI);

        MermaidRenderSchedule second = first.session().schedule(DOCUMENT_URI);

        TestAssertions.assertEquals(0, second.jobs().length, "pending Mermaid jobs must not be duplicated");
    }

    @Test
    void scheduledJobMatchesOnlyItsOwnCallbackCoordinates() {
        MermaidRenderJob job = registeredSession().schedule(DOCUMENT_URI).jobs()[0];

        TestAssertions.assertFalse(job.matches(DOCUMENT_URI, 1), "a callback for another diagram must not complete the active job");
    }

    @Test
    void scheduledJobAcceptsItsOwnCallbackCoordinates() {
        MermaidRenderJob job = registeredSession().schedule(DOCUMENT_URI).jobs()[0];

        TestAssertions.assertTrue(job.matches(DOCUMENT_URI, 0), "the matching callback must complete the active job");
    }

    @Test
    void completedDiagramIsExposedAtItsMarkdownBlockIndex() {
        MermaidRenderSchedule schedule = registeredSession().schedule(DOCUMENT_URI);
        SafeHtml svg = SafeHtml.fromTrustedRendererOutput("<svg>first</svg>");

        MermaidRenderSessions completed = schedule.session().complete(schedule.jobs()[0], svg);

        TestAssertions.assertSame(svg, completed.renderedFor(DOCUMENT_URI).get(Integer.valueOf(0)),
                "completed SVG must be exposed at the matching Mermaid block index");
    }

    @Test
    void resetMakesEveryDiagramEligibleForTheNewTheme() {
        MermaidRenderSchedule initial = registeredSession().schedule(DOCUMENT_URI);
        MermaidRenderSessions completed = initial.session().complete(
                initial.jobs()[0],
                SafeHtml.fromTrustedRendererOutput("<svg>old theme</svg>"));

        MermaidRenderSchedule reset = completed.resetRendered().schedule(DOCUMENT_URI);

        TestAssertions.assertEquals(2, reset.jobs().length, "theme reset must schedule every diagram with the new theme");
    }

    @Test
    void completionFromThePreviousThemeCannotReplaceCurrentResults() {
        MermaidRenderSchedule oldTheme = registeredSession().schedule(DOCUMENT_URI);
        MermaidRenderSessions newTheme = oldTheme.session().resetRendered();

        MermaidRenderSessions unchanged = newTheme.complete(
                oldTheme.jobs()[0],
                SafeHtml.fromTrustedRendererOutput("<svg>stale</svg>"));

        TestAssertions.assertSame(newTheme, unchanged, "stale Mermaid completion must not mutate the current render generation");
    }

    @Test
    void registeringUpdatedMarkdownInvalidatesPriorDiagramResults() {
        MermaidRenderSchedule initial = registeredSession().schedule(DOCUMENT_URI);
        MermaidRenderSessions completed = initial.session().complete(
                initial.jobs()[0],
                SafeHtml.fromTrustedRendererOutput("<svg>old source</svg>"));

        MermaidRenderSchedule updated = completed
                .register(DOCUMENT_URI, MermaidDiagramBlocks.fromMarkdown("```mermaid\ngraph BT\nC-->D\n```"))
                .schedule(DOCUMENT_URI);

        TestAssertions.assertEquals(1, updated.jobs().length, "updated Markdown must replace prior results and schedule its current blocks");
    }

    private static MermaidRenderSessions registeredSession() {
        return MermaidRenderSessions.empty().register(
                DOCUMENT_URI,
                MermaidDiagramBlocks.fromMarkdown(
                        "```mermaid\ngraph TD\nA-->B\n```\n\n```mermaid\nsequenceDiagram\nA->>B: Hello\n```"));
    }
}
