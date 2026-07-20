package io.github.yosk.mdlite.presentation;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Expands every tab close control's hit area to the 48dp minimum touch target
 * (#73) without changing its drawn size.
 *
 * The delegate is installed on the tab row, not on each tab group: a
 * TouchDelegate only receives touches inside its host view's own bounds, and a
 * tab group (about 33dp tall) is shorter than 48dp, so vertical expansion
 * beyond it would be dead. The row (tab height plus its 8dp paddings) does
 * cover the expanded area. A row hosts many close controls but allows only
 * one delegate, so the per-control delegates are multiplexed.
 */
final class CloseTabTouchTargets implements Runnable {
    private final ViewGroup tabRow;
    private final int minimumPx;

    CloseTabTouchTargets(ViewGroup tabRow, int minimumPx) {
        this.tabRow = tabRow;
        this.minimumPx = minimumPx;
    }

    @Override
    public void run() {
        List<TouchDelegate> delegates = new ArrayList<TouchDelegate>();
        for (int g = 0; g < tabRow.getChildCount(); g++) {
            View child = tabRow.getChildAt(g);
            if (child instanceof ViewGroup) {
                collectCloseDelegates((ViewGroup) child, delegates);
            }
        }
        tabRow.setTouchDelegate(delegates.isEmpty()
                ? null
                : new CompositeTouchDelegate(tabRow, delegates));
    }

    private void collectCloseDelegates(ViewGroup tabGroup, List<TouchDelegate> delegates) {
        for (int i = 0; i < tabGroup.getChildCount(); i++) {
            View view = tabGroup.getChildAt(i);
            if (view instanceof CloseTabText) {
                Rect hitArea = new Rect();
                view.getHitRect(hitArea);
                hitArea.offset(tabGroup.getLeft(), tabGroup.getTop());
                expandToMinimum(hitArea);
                delegates.add(new TouchDelegate(hitArea, view));
            }
        }
    }

    private void expandToMinimum(Rect hitArea) {
        int widthShortfall = minimumPx - hitArea.width();
        if (widthShortfall > 0) {
            hitArea.left -= widthShortfall / 2;
            hitArea.right += widthShortfall - widthShortfall / 2;
        }
        int heightShortfall = minimumPx - hitArea.height();
        if (heightShortfall > 0) {
            hitArea.top -= heightShortfall / 2;
            hitArea.bottom += heightShortfall - heightShortfall / 2;
        }
    }

    /** Tries each close control's delegate; TouchDelegate mutates the event it
     * forwards, so every attempt gets its own copy. */
    static final class CompositeTouchDelegate extends TouchDelegate {
        private final List<TouchDelegate> delegates;

        CompositeTouchDelegate(View host, List<TouchDelegate> delegates) {
            super(new Rect(), host);
            this.delegates = delegates;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            for (int i = 0; i < delegates.size(); i++) {
                MotionEvent attempt = MotionEvent.obtain(event);
                boolean handled = delegates.get(i).onTouchEvent(attempt);
                attempt.recycle();
                if (handled) {
                    return true;
                }
            }
            return false;
        }
    }
}
