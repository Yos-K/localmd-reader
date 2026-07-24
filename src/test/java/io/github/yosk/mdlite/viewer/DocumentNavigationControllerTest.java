package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.HeadingScrollPosition;
import io.github.yosk.mdlite.domain.MarkdownHeading;
import io.github.yosk.mdlite.domain.MarkdownHeadings;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class DocumentNavigationControllerTest {
    @Test
    void activeSearchDispatchesTheNormalizedQueryToTheDocument() {
        RecordingHost host = new RecordingHost();
        DocumentNavigationController controller = new DocumentNavigationController(host);

        controller.search(DocumentSearchQuery.from("  target  "));

        TestAssertions.assertEquals("find:target", host.event,
                "an active query must immediately search the visible document");
    }

    @Test
    void emptySearchDoesNotDispatchAWebViewSearch() {
        RecordingHost host = new RecordingHost();
        DocumentNavigationController controller = new DocumentNavigationController(host);

        controller.search(DocumentSearchQuery.from("   "));

        TestAssertions.assertEquals("none", host.event,
                "an empty query must remain an inactive search session");
    }

    @Test
    void nextResultDispatchesOnlyForAnActiveSearch() {
        RecordingHost host = new RecordingHost();
        DocumentNavigationController controller = new DocumentNavigationController(host);
        controller.search(DocumentSearchQuery.from("target"));

        controller.nextSearchResult();

        TestAssertions.assertEquals("next:true", host.event,
                "next-result navigation requires and uses the owned active query");
    }

    @Test
    void documentResetClearsTheQueryAndSynchronizesTheSearchBar() {
        RecordingHost host = new RecordingHost();
        DocumentNavigationController controller = new DocumentNavigationController(host);
        controller.search(DocumentSearchQuery.from("target"));

        controller.resetForDocument();

        TestAssertions.assertEquals("sync-search", host.event,
                "opening another document must clear and synchronize search state");
    }

    @Test
    void selectedHeadingDispatchesItsAnchorAsTheNavigationDestination() {
        RecordingHost host = new RecordingHost();
        DocumentNavigationController controller = new DocumentNavigationController(host);

        controller.jumpTo(host.headings.at(1));

        TestAssertions.assertEquals("heading:second", host.event,
                "a selected table-of-contents item must open its modeled anchor");
    }

    @Test
    void nextHeadingUsesTheCurrentScrollPositionBeforeAdvancing() {
        RecordingHost host = new RecordingHost();
        DocumentNavigationController controller = new DocumentNavigationController(host);

        controller.jumpToNextHeading();

        TestAssertions.assertEquals("heading:second", host.event,
                "next-heading navigation must advance from the currently visible heading");
    }

    private static final class RecordingHost implements DocumentNavigationController.Host {
        private final MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# First\n# Second");
        private String event = "none";

        @Override
        public void showSearchBar() {
            event = "show-search";
        }

        @Override
        public void findAll(String query) {
            event = "find:" + query;
        }

        @Override
        public void findNext(boolean forward) {
            event = "next:" + forward;
        }

        @Override
        public void clearSearchMatches() {
            event = "clear-search";
        }

        @Override
        public void synchronizeSearchBar() {
            event = "sync-search";
        }

        @Override
        public MarkdownHeadings activeHeadings() {
            return headings;
        }

        @Override
        public HeadingScrollPosition headingScrollPosition() {
            return HeadingScrollPosition.from(0, 100);
        }

        @Override
        public void openHeading(MarkdownHeading heading) {
            event = "heading:" + heading.anchorId();
        }
    }
}
