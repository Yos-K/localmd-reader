package io.github.yosk.mdlite.viewer;

public final class CustomGestureDrawingLayout {
    private static final int INSTRUCTION_TOP_SPACING_PX = 72;
    private static final int CANCEL_END_MARGIN_PX = 36;
    private static final int CANCEL_TOUCH_PADDING_PX = 24;

    private CustomGestureDrawingLayout() {
    }

    public static int instructionBaseline(int systemTopInset) {
        return Math.max(0, systemTopInset) + INSTRUCTION_TOP_SPACING_PX;
    }

    public static float cancelLabelStartX(int width, float labelWidth) {
        return width - CANCEL_END_MARGIN_PX - Math.max(0f, labelWidth);
    }

    public static boolean isCancelTarget(int width, int systemTopInset, float labelWidth,
            float x, float y) {
        int safeTop = Math.max(0, systemTopInset);
        return x >= cancelLabelStartX(width, labelWidth) - CANCEL_TOUCH_PADDING_PX && x <= width
                && y >= safeTop && y <= safeTop + 120;
    }
}
