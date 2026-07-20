package io.github.yosk.mdlite.presentation;

import android.view.MotionEvent;
import android.widget.LinearLayout;

final class SwipeMenuLayout extends LinearLayout {
    private final MainActivity activity;

    SwipeMenuLayout(MainActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (activity.handleMenuSwipe(event)) {
            return true;
        }
        return super.dispatchTouchEvent(event);
    }
}
