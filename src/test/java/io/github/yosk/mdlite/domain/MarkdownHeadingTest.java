package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MarkdownHeadingTest {

    @Test
    void middleHeadingLevelIsValid() {
        MarkdownHeading heading = new MarkdownHeading(3, "Section", "section");
        TestAssertions.assertEquals(3, heading.level(), "level 3 is a valid intermediate heading level");
    }

    @Test
    void levelZeroIsRejected() {
        TestAssertions.assertThrows(IllegalArgumentException.class,
                () -> new MarkdownHeading(0, "Title", "title"));
    }

    @Test
    void levelSevenIsRejected() {
        TestAssertions.assertThrows(IllegalArgumentException.class,
                () -> new MarkdownHeading(7, "Title", "title"));
    }

    @Test
    void titleIsPreservedExactly() {
        MarkdownHeading heading = new MarkdownHeading(1, "My Title", "my-title");

        TestAssertions.assertEquals("My Title", heading.title(), "title must be preserved exactly");
    }

    @Test
    void anchorIdIsPreservedExactly() {
        MarkdownHeading heading = new MarkdownHeading(2, "Section", "section-2");

        TestAssertions.assertEquals("section-2", heading.anchorId(), "anchorId must be preserved exactly");
    }

    @Test
    void nullTitleIsRejected() {
        TestAssertions.assertThrows(IllegalArgumentException.class,
                () -> new MarkdownHeading(1, null, "title"));
    }

    @Test
    void blankTitleIsRejected() {
        TestAssertions.assertThrows(IllegalArgumentException.class,
                () -> new MarkdownHeading(1, "   ", "title"));
    }

    @Test
    void nullAnchorIdIsRejected() {
        TestAssertions.assertThrows(IllegalArgumentException.class,
                () -> new MarkdownHeading(1, "Title", null));
    }

    @Test
    void blankAnchorIdIsRejected() {
        TestAssertions.assertThrows(IllegalArgumentException.class,
                () -> new MarkdownHeading(1, "Title", "  "));
    }

    @Test
    void levelOneBoundaryIsValid() {
        MarkdownHeading heading = new MarkdownHeading(1, "Top", "top");
        TestAssertions.assertEquals(1, heading.level(), "level 1 is the minimum valid level");
    }

    @Test
    void levelSixBoundaryIsValid() {
        MarkdownHeading heading = new MarkdownHeading(6, "Deep", "deep");
        TestAssertions.assertEquals(6, heading.level(), "level 6 is the maximum valid level");
    }
}
