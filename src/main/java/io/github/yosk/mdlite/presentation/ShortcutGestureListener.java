package io.github.yosk.mdlite.presentation;

import android.view.GestureDetector;
import android.view.MotionEvent;

final class ShortcutGestureListener extends GestureDetector.SimpleOnGestureListener {
    private final MainActivity activity;

    ShortcutGestureListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        return activity.handleDoubleTapShortcut();
    }
}
