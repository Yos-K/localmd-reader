package io.github.yosk.mdlite.presentation;

import android.view.ScaleGestureDetector;

final class FontScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    private final MainActivity activity;

    FontScaleGestureListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        activity.beginFontSizePinch();
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        activity.changeFontSizeByPinch(detector.getScaleFactor());
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        activity.finishFontSizePinch();
    }
}
