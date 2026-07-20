package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class DirectionalGesturePathTest {

    @Test
    void leftChevronShapeIsSwipeLeft() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 220f, 150f, 80f, 150f, 220f },
                new float[] { 60f, 95f, 140f, 185f, 220f }).trigger();

        TestAssertions.assertTrue(trigger.isSwipeLeft(), "< shaped gesture must be recognized as swipe left trigger");
    }

    @Test
    void rightChevronShapeIsSwipeRight() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 80f, 150f, 220f, 150f, 80f },
                new float[] { 60f, 95f, 140f, 185f, 220f }).trigger();

        TestAssertions.assertTrue(trigger.isSwipeRight(), "> shaped gesture must be recognized as swipe right trigger");
    }

    @Test
    void fastThreePointRightChevronShapeIsSwipeRight() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 80f, 220f, 80f },
                new float[] { 60f, 140f, 220f }).trigger();

        TestAssertions.assertTrue(trigger.isSwipeRight(), "fast three-point > shaped gesture must be recognized as swipe right trigger");
    }

    @Test
    void upChevronShapeIsSwipeUp() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 80f, 120f, 160f, 200f, 240f },
                new float[] { 220f, 150f, 80f, 150f, 220f }).trigger();

        TestAssertions.assertTrue(trigger.isSwipeUp(), "^ shaped gesture must be recognized as swipe up trigger");
    }

    @Test
    void downChevronShapeIsSwipeDown() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 80f, 120f, 160f, 200f, 240f },
                new float[] { 80f, 150f, 220f, 150f, 80f }).trigger();

        TestAssertions.assertTrue(trigger.isSwipeDown(), "v shaped gesture must be recognized as swipe down trigger");
    }

    @Test
    void fastThreePointDownChevronShapeIsSwipeDown() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 80f, 160f, 240f },
                new float[] { 80f, 220f, 80f }).trigger();

        TestAssertions.assertTrue(trigger.isSwipeDown(), "fast three-point v shaped gesture must be recognized as swipe down trigger");
    }

    @Test
    void wideDownChevronShapeIsSwipeDown() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 40f, 120f, 220f, 320f, 400f },
                new float[] { 80f, 135f, 190f, 135f, 80f }).trigger();

        TestAssertions.assertTrue(trigger.isSwipeDown(), "wide v shaped gesture must be recognized as swipe down trigger");
    }

    @Test
    void straightLeftSwipeHasNoDirection() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 220f, 160f, 90f },
                new float[] { 100f, 104f, 96f }).trigger();

        TestAssertions.assertSame(null, trigger, "straight left swipe must not trigger a chevron gesture");
    }

    @Test
    void straightVerticalScrollHasNoDirection() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 100f, 104f, 96f },
                new float[] { 40f, 120f, 190f }).trigger();

        TestAssertions.assertSame(null, trigger, "straight vertical scroll must not trigger a chevron gesture");
    }

    @Test
    void diagonalLineHasNoDirection() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 20f, 160f },
                new float[] { 20f, 130f }).trigger();

        TestAssertions.assertSame(null, trigger, "diagonal line must not trigger a chevron gesture");
    }

    @Test
    void tinyChevronHasNoDirection() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 30f, 20f, 30f },
                new float[] { 20f, 30f, 40f }).trigger();

        TestAssertions.assertSame(null, trigger, "tiny chevron must not trigger a direction gesture");
    }

    @Test
    void chevronAtMinimumSizeTriggersAndJustBelowDoesNot() {
        GestureShortcutTrigger atMinimum = DirectionalGesturePath.fromPoints(
                new float[] { 28f, 0f, 28f },
                new float[] { 0f, 14f, 28f }).trigger();
        GestureShortcutTrigger justBelow = DirectionalGesturePath.fromPoints(
                new float[] { 27.9f, 0f, 27.9f },
                new float[] { 0f, 13.95f, 27.9f }).trigger();

        TestAssertions.assertTrue(atMinimum != null && atMinimum.isSwipeLeft(),
                "a chevron at exactly the 28dp minimum physical size must trigger");
        TestAssertions.assertSame(null, justBelow,
                "a chevron just below the 28dp minimum physical size must not trigger");
    }

    @Test
    void chevronWithShortStartLegHasNoDirection() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 400f, 200f, 320f },
                new float[] { 185f, 200f, 300f }).trigger();

        TestAssertions.assertSame(null, trigger,
                "a < stroke whose first leg barely moves must not count as a chevron, wherever it is drawn on screen");
    }

    @Test
    void chevronWithShortEndLegHasNoDirection() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 400f, 200f, 320f },
                new float[] { 100f, 200f, 215f }).trigger();

        TestAssertions.assertSame(null, trigger,
                "a < stroke whose returning leg barely moves must not count as a chevron, wherever it is drawn on screen");
    }

    @Test
    void upChevronWithShortStartLegHasNoDirection() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 185f, 200f, 300f },
                new float[] { 400f, 200f, 320f }).trigger();

        TestAssertions.assertSame(null, trigger,
                "a ^ stroke whose first leg barely moves must not count as a chevron, wherever it is drawn on screen");
    }

    @Test
    void upChevronWithShortEndLegHasNoDirection() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 100f, 200f, 215f },
                new float[] { 400f, 200f, 320f }).trigger();

        TestAssertions.assertSame(null, trigger,
                "a ^ stroke whose returning leg barely moves must not count as a chevron, wherever it is drawn on screen");
    }

    @Test
    void missingPointsHaveNoDirection() {
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(null, null).trigger();

        TestAssertions.assertSame(null, trigger, "missing points must not trigger a direction gesture");
    }

    @Test
    void chevronAtExactlyTheMinimumSizeStillTriggers() {
        // The preserved endpoints fix the height at exactly 72 (the minimum size),
        // so the size gate must accept it (size < 72 is false at the boundary).
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 100f, 160f, 100f },
                new float[] { 64f, 100f, 136f }).trigger();

        TestAssertions.assertTrue(trigger.isSwipeRight(), "a > chevron whose size is exactly the 72px minimum must still trigger");
    }

    @Test
    void chevronSmallerThanTheMinimumSizeFarFromOriginHasNoDirection() {
        // A > chevron with only 20dp extent: below the 28dp minimum even though its
        // coordinates are large, so the gesture must be ignored.
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 500f, 520f, 500f },
                new float[] { 300f, 310f, 320f }).trigger();

        TestAssertions.assertSame(null, trigger, "a chevron below the minimum size must not trigger, regardless of its position");
    }

    @Test
    void horizontalChevronWithTooShortLegsHasNoDirection() {
        // Apex Y sits next to the start Y, so the top leg is far shorter than the
        // required share of the height: not a clean > chevron.
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 100f, 200f, 100f },
                new float[] { 100f, 102f, 200f }).trigger();

        TestAssertions.assertSame(null, trigger, "a > chevron with one leg too short must not trigger");
    }

    @Test
    void verticalChevronWithTooShortLegsHasNoDirection() {
        // Apex X sits next to the start X, so the left leg is far shorter than the
        // required share of the width: not a clean ^ chevron.
        GestureShortcutTrigger trigger = DirectionalGesturePath.fromPoints(
                new float[] { 100f, 102f, 200f },
                new float[] { 100f, 200f, 100f }).trigger();

        TestAssertions.assertSame(null, trigger, "a ^ chevron with one leg too short must not trigger");
    }

    // Note: there is deliberately no "base drift" test. For these chevrons the apex sits at
    // the far edge, so apex depth + base drift == the span and the two ratios are
    // complementary (0.55 + 0.45 == 1.0). A skewed base therefore always fails the apex-depth
    // rule first, so the base-drift predicate cannot be isolated without a brittle on-threshold
    // input — it is effectively an equivalent mutation. See docs/harness/mutation-analysis-rule.md.
}
