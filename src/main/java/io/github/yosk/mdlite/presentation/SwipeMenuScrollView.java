package io.github.yosk.mdlite.presentation;

import android.view.MotionEvent;
import android.widget.ScrollView;

final class SwipeMenuScrollView extends ScrollView {
    private final MainActivity activity;

    SwipeMenuScrollView(MainActivity activity) {
        super(activity);
        this.activity = activity;
        setFillViewport(true);
        setVerticalScrollBarEnabled(true);
    }

    @Override
    // interaction-command: scroll_menu
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (activity.handleMenuSwipe(event)) {
            return true;
        }
        return super.dispatchTouchEvent(event);
    }
}
