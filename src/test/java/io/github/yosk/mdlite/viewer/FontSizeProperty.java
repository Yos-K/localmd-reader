package io.github.yosk.mdlite.viewer;

import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.FloatRange;
import net.jqwik.api.constraints.IntRange;

/**
 * Property-based tests for FontSize invariants.
 *
 * Example-based tests in {@link FontSizeTest} cover boundary cases; these
 * properties verify that the invariants hold for *any* input the user (or AI
 * agent) could throw at the type.
 */
public final class FontSizeProperty {

    @Property
    boolean pinchScaleAlwaysProducesValueWithinBounds(
            @ForAll @IntRange(min = FontSize.MIN_SP, max = FontSize.MAX_SP) int initialSp,
            @ForAll @FloatRange(min = 0.01f, max = 100f) float scaleFactor) {
        FontSize result = FontSize.of(initialSp).changedByPinchScale(scaleFactor);
        return result.sp() >= FontSize.MIN_SP && result.sp() <= FontSize.MAX_SP;
    }

    @Property
    boolean repeatedIncreasesNeverExceedMaximum(
            @ForAll @IntRange(min = FontSize.MIN_SP, max = FontSize.MAX_SP) int initialSp,
            @ForAll @IntRange(min = 0, max = 200) int times) {
        FontSize size = applyRepeatedly(FontSize.of(initialSp), times, FontSize::increased);
        return size.sp() <= FontSize.MAX_SP;
    }

    @Property
    boolean repeatedDecreasesNeverGoBelowMinimum(
            @ForAll @IntRange(min = FontSize.MIN_SP, max = FontSize.MAX_SP) int initialSp,
            @ForAll @IntRange(min = 0, max = 200) int times) {
        FontSize size = applyRepeatedly(FontSize.of(initialSp), times, FontSize::decreased);
        return size.sp() >= FontSize.MIN_SP;
    }

    @Property
    boolean ofIsTotalAcrossAllIntegers(@ForAll int sp) {
        boolean inRange = sp >= FontSize.MIN_SP && sp <= FontSize.MAX_SP;
        try {
            FontSize result = FontSize.of(sp);
            return inRange && result.sp() == sp;
        } catch (IllegalArgumentException e) {
            return !inRange;
        }
    }

    @Property
    boolean increaseThenDecreaseIsIdempotentInTheMiddleOfTheRange(
            @ForAll @IntRange(min = FontSize.MIN_SP + 1, max = FontSize.MAX_SP - 1) int sp) {
        FontSize size = FontSize.of(sp);
        return size.increased().decreased().sp() == sp
                && size.decreased().increased().sp() == sp;
    }

    @Property
    boolean pinchScaleOfOneIsIdentity(
            @ForAll @IntRange(min = FontSize.MIN_SP, max = FontSize.MAX_SP) int sp) {
        return FontSize.of(sp).changedByPinchScale(1.0f).sp() == sp;
    }

    /**
     * Applies {@code step} to {@code start} exactly {@code times} times.
     *
     * Expressed as a stream fold rather than an imperative loop so the test
     * sources stay free of conditional/looping constructs (enforced by
     * scripts/check-test-smells.sh).
     */
    private static FontSize applyRepeatedly(FontSize start, int times, UnaryOperator<FontSize> step) {
        return IntStream.range(0, times)
                .boxed()
                .reduce(start, (size, ignoredIndex) -> step.apply(size), (left, right) -> right);
    }
}
