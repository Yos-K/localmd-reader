package io.github.yosk.mdlite.presentation;

import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

final class ScrollToActiveTab implements Runnable {
    private final HorizontalScrollView tabScroller;
    private final LinearLayout tabRow;
    private final int activeIndex;

    ScrollToActiveTab(HorizontalScrollView tabScroller, LinearLayout tabRow, int activeIndex) {
        this.tabScroller = tabScroller;
        this.tabRow = tabRow;
        this.activeIndex = activeIndex;
    }

    @Override
    public void run() {
        if (activeIndex < 0 || activeIndex >= tabRow.getChildCount()) {
            return;
        }
        View activeTab = tabRow.getChildAt(activeIndex);
        int targetLeft = activeTab.getLeft() - tabScroller.getPaddingLeft();
        int targetRight = activeTab.getRight() - tabScroller.getWidth() + tabScroller.getPaddingRight();
        if (targetLeft < tabScroller.getScrollX()) {
            tabScroller.smoothScrollTo(targetLeft, 0);
        } else if (targetRight > tabScroller.getScrollX()) {
            tabScroller.smoothScrollTo(targetRight, 0);
        }
    }
}
