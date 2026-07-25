package io.github.yosk.mdlite.presentation;

import android.view.View;
import android.widget.LinearLayout;

final class GestureShortcutMenuPanel extends LinearLayout implements Runnable {
    private final MainActivity activity;

    GestureShortcutMenuPanel(MainActivity activity) {
        super(activity);
        this.activity = activity;
        setOrientation(VERTICAL);
        setVisibility(View.GONE);
        setPadding(activity.dp(10), 0, 0, activity.dp(6));
    }

    @Override
    public void run() {
        showGestureList();
    }

    void showGestureList() {
        removeAllViews();
        addView(activity.gestureShortcutDialogs.gestureShortcutListView(),
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    void showActions(int targetIndex) {
        removeAllViews();
        addView(activity.gestureShortcutDialogs.gestureShortcutActionView(targetIndex, new Runnable() {
            @Override
            public void run() {
                showGestureList();
            }
        }), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }
}
