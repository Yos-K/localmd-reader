package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertTrue;

import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

@RunWith(RobolectricTestRunner.class)
public final class MenuPanelScrollabilityMediumTest {
    @Test
    public void verticalSwipeReachesLowerMenuActions() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        SwipeMenuScrollView menu = activity.menuScrollContainer;
        int width = activity.dp(310);
        int height = activity.dp(320);
        layout(menu, width, height);
        activity.openMenu();
        long downTime = 100L;

        menu.dispatchTouchEvent(event(downTime, 100L, MotionEvent.ACTION_DOWN,
                width / 2f, height - activity.dp(24)));
        menu.dispatchTouchEvent(event(downTime, 120L, MotionEvent.ACTION_MOVE,
                width / 2f, height / 2f));
        menu.dispatchTouchEvent(event(downTime, 140L, MotionEvent.ACTION_MOVE,
                width / 2f, activity.dp(24)));
        menu.dispatchTouchEvent(event(downTime, 160L, MotionEvent.ACTION_UP,
                width / 2f, activity.dp(24)));
        Shadows.shadowOf(Looper.getMainLooper()).idle();

        assertTrue("a vertical menu swipe must reveal actions below the viewport",
                menu.getScrollY() > 0);
    }

    private static void layout(View view, int width, int height) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, width, height);
    }

    private static MotionEvent event(long downTime, long eventTime, int action,
            float x, float y) {
        return MotionEvent.obtain(downTime, eventTime, action, x, y, 0);
    }
}
