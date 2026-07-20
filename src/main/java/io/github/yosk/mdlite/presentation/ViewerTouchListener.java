package io.github.yosk.mdlite.presentation;

import android.view.MotionEvent;
import android.view.View;

final class ViewerTouchListener implements View.OnTouchListener {
    private final MainActivity activity;

    ViewerTouchListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return activity.handleViewerTouch(event);
    }
}
