package io.github.yosk.mdlite.presentation;

import android.view.MotionEvent;
import android.widget.FrameLayout;

final class EdgeSwipeFrameLayout extends FrameLayout {
    private final MainActivity activity;

    EdgeSwipeFrameLayout(MainActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (activity.handleEdgeSwipe(event)) {
            return true;
        }
        return super.dispatchTouchEvent(event);
    }
}
