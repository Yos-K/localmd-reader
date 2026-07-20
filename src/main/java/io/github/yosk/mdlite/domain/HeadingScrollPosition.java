package io.github.yosk.mdlite.domain;

public final class HeadingScrollPosition {
    private final int scrollY;
    private final int scrollRange;

    private HeadingScrollPosition(int scrollY, int scrollRange) {
        this.scrollY = Math.max(0, scrollY);
        this.scrollRange = Math.max(0, scrollRange);
    }

    public static HeadingScrollPosition from(int scrollY, int scrollRange) {
        return new HeadingScrollPosition(scrollY, scrollRange);
    }

    public static HeadingScrollPosition fromWebViewMetrics(
            int scrollY,
            int contentHeight,
            int viewportHeight,
            float scale) {
        int scaledContentHeight = Math.round(Math.max(0, contentHeight) * Math.max(0f, scale));
        int scrollRange = Math.max(0, scaledContentHeight - Math.max(0, viewportHeight));
        return new HeadingScrollPosition(scrollY, scrollRange);
    }

    public int estimatedHeadingIndex(int headingCount) {
        if (headingCount <= 0) {
            return -1;
        }
        if (headingCount == 1 || scrollRange == 0) {
            return 0;
        }
        float progress = Math.min(1f, scrollY / (float) scrollRange);
        int index = Math.round(progress * (headingCount - 1));
        return Math.max(0, Math.min(headingCount - 1, index));
    }
}
