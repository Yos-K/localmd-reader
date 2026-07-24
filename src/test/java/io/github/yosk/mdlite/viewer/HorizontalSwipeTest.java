package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class HorizontalSwipeTest {
    @Test
    void rightSwipeIsRecognizedAtTheMinimumDistance() {
        TestAssertions.assertSame(HorizontalSwipe.RIGHT, HorizontalSwipe.from(10f, 82f, 72f),
                "threshold right swipe must open the menu");
    }

    @Test
    void leftSwipeIsRecognizedAtTheMinimumDistance() {
        TestAssertions.assertSame(HorizontalSwipe.LEFT, HorizontalSwipe.from(82f, 10f, 72f),
                "threshold left swipe must close the menu");
    }

    @Test
    void movementShorterThanTheMinimumDistanceIsIgnored() {
        TestAssertions.assertSame(HorizontalSwipe.NONE, HorizontalSwipe.from(10f, 81f, 72f),
                "short horizontal movement must not toggle menu visibility");
    }

    @Test
    void invalidMinimumDistanceIsRejected() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                HorizontalSwipe.from(10f, 100f, 0f);
            }
        });
    }
}
