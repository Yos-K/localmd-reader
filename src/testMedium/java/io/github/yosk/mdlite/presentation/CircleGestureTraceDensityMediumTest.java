package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.view.MotionEvent;

import io.github.yosk.mdlite.viewer.GestureShortcutTrigger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Medium-tier test: gesture recognition must depend on the PHYSICAL stroke
 * size (dp), not raw pixels, so the same finger movement is recognized the
 * same way on every screen density (issue #147, owner-approved defect).
 * Uses real MotionEvents on the JVM (Robolectric).
 */
@RunWith(RobolectricTestRunner.class)
public class CircleGestureTraceDensityMediumTest {

    private static final float HIGH_DENSITY = 3.0f;
    private static final float LOW_DENSITY = 1.0f;

    @Test
    public void chevronOfSamePhysicalSizeIsRecognizedOnEveryDensity() {
        GestureShortcutTrigger onHighDensity =
                leftChevronTrace(HIGH_DENSITY, 30f).directionalGestureTrigger();
        GestureShortcutTrigger onLowDensity =
                leftChevronTrace(LOW_DENSITY, 30f).directionalGestureTrigger();

        assertTrue("a 30dp left chevron must be recognized as swipe_left on a high-density screen",
                onHighDensity != null && onHighDensity.isSwipeLeft());
        assertTrue("the same 30dp left chevron must also be recognized on a low-density screen",
                onLowDensity != null && onLowDensity.isSwipeLeft());
    }

    @Test
    public void physicallyTinyChevronDoesNotTriggerOnHighDensityScreens() {
        GestureShortcutTrigger trigger =
                leftChevronTrace(HIGH_DENSITY, 25f).directionalGestureTrigger();

        assertNull("a chevron below the minimum physical size must not trigger, even when its pixel size is large on a high-density screen",
                trigger);
    }

    private static CircleGestureTrace leftChevronTrace(float density, float sizeDp) {
        CircleGestureTrace trace = new CircleGestureTrace(density);
        float sizePx = sizeDp * density;
        appendMove(trace, sizePx, 0f);
        appendMove(trace, 0f, sizePx / 2f);
        appendMove(trace, sizePx, sizePx);
        return trace;
    }

    private static void appendMove(CircleGestureTrace trace, float x, float y) {
        MotionEvent event = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_MOVE, x, y, 0);
        trace.append(event);
        event.recycle();
    }
}
