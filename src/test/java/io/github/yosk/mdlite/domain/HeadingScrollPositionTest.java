package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class HeadingScrollPositionTest {
    @Test
    void zeroProgressSelectsTheFirstHeading() {
        HeadingScrollPosition position = HeadingScrollPosition.from(0, 1000);

        TestAssertions.assertEquals(0, position.estimatedHeadingIndex(4),
                "top scroll position must estimate the first heading");
    }

    @Test
    void middleProgressSelectsTheMiddleHeading() {
        HeadingScrollPosition position = HeadingScrollPosition.from(500, 1000);

        TestAssertions.assertEquals(2, position.estimatedHeadingIndex(4),
                "middle scroll position must estimate a middle heading");
    }

    @Test
    void endProgressSelectsTheLastHeading() {
        HeadingScrollPosition position = HeadingScrollPosition.from(1000, 1000);

        TestAssertions.assertEquals(3, position.estimatedHeadingIndex(4),
                "bottom scroll position must estimate the last heading");
    }

    @Test
    void negativeScrollIsClampedToTheFirstHeading() {
        HeadingScrollPosition position = HeadingScrollPosition.from(-10, 1000);

        TestAssertions.assertEquals(0, position.estimatedHeadingIndex(4),
                "negative scroll positions must be clamped to the first heading");
    }

    @Test
    void overScrollIsClampedToTheLastHeading() {
        HeadingScrollPosition position = HeadingScrollPosition.from(1200, 1000);

        TestAssertions.assertEquals(3, position.estimatedHeadingIndex(4),
                "over-scroll positions must be clamped to the last heading");
    }

    @Test
    void webViewMetricsScaleContentHeightBeforeEstimatingTheVisibleHeading() {
        HeadingScrollPosition position = HeadingScrollPosition.fromWebViewMetrics(300, 1000, 600, 2f);

        TestAssertions.assertEquals(1, position.estimatedHeadingIndex(4),
                "WebView content height must be scaled before comparing it with scroll pixels");
    }

    @Test
    void noHeadingKeepsNoActiveHeading() {
        HeadingScrollPosition position = HeadingScrollPosition.from(500, 1000);

        TestAssertions.assertEquals(-1, position.estimatedHeadingIndex(0),
                "documents without headings must have no estimated heading");
    }
}
