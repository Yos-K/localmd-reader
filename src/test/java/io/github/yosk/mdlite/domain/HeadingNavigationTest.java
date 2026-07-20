package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class HeadingNavigationTest {
    @Test
    void nextFromTheFirstVisibleHeadingTargetsTheSecondHeading() {
        HeadingNavigation navigation = navigationAtTop("# First\n\n## Second\n\n## Third");

        TestAssertions.assertEquals("second", anchorOf(navigation.next()),
                "next navigation must advance from the visible heading");
    }

    @Test
    void previousFromTheFirstVisibleHeadingWrapsToTheLastHeading() {
        HeadingNavigation navigation = navigationAtTop("# First\n\n## Second\n\n## Third");

        TestAssertions.assertEquals("third", anchorOf(navigation.previous()),
                "previous navigation must wrap from the first heading to the last heading");
    }

    @Test
    void nextFromTheLastVisibleHeadingWrapsToTheFirstHeading() {
        HeadingNavigation navigation = navigationAtBottom("# First\n\n## Second\n\n## Third");

        TestAssertions.assertEquals("first", anchorOf(navigation.next()),
                "next navigation must wrap from the last heading to the first heading");
    }

    @Test
    void documentWithoutHeadingsProducesNoNavigationDestination() {
        HeadingNavigation navigation = navigationAtTop("plain text");

        TestAssertions.assertEquals("none", anchorOf(navigation.next()),
                "heading-free documents must produce an explicit no-destination result");
    }

    @Test
    void selectingAModeledHeadingProducesThatHeadingAsDestination() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# First\n\n## Second");
        TableOfContentsItems items = TableOfContentsItems.from(headings);

        TestAssertions.assertEquals("second", anchorOf(HeadingNavigation.selected(items, headings.at(1))),
                "selecting a modeled heading must preserve its exact destination");
    }

    @Test
    void selectingAHeadingOutsideTheDocumentProducesNoNavigationDestination() {
        TableOfContentsItems items = TableOfContentsItems.from(
                MarkdownHeadings.fromMarkdown("# First"));
        MarkdownHeading unrelated = MarkdownHeadings.fromMarkdown("# Other").at(0);

        TestAssertions.assertEquals("none", anchorOf(HeadingNavigation.selected(items, unrelated)),
                "an unrelated heading must not create an invalid document destination");
    }

    private static HeadingNavigation navigationAtTop(String markdown) {
        return HeadingNavigation.from(
                TableOfContentsItems.from(MarkdownHeadings.fromMarkdown(markdown)),
                HeadingScrollPosition.from(0, 100));
    }

    private static HeadingNavigation navigationAtBottom(String markdown) {
        return HeadingNavigation.from(
                TableOfContentsItems.from(MarkdownHeadings.fromMarkdown(markdown)),
                HeadingScrollPosition.from(100, 100));
    }

    private static String anchorOf(HeadingNavigation navigation) {
        AnchorProbe probe = new AnchorProbe();
        navigation.handle(probe);
        return probe.anchor;
    }

    private static final class AnchorProbe implements HeadingNavigation.Handler {
        private String anchor = "none";

        @Override
        public void unavailable() {
            anchor = "none";
        }

        @Override
        public void destination(MarkdownHeading heading) {
            anchor = heading.anchorId();
        }
    }
}
