package io.github.yosk.mdlite.viewer;

public final class CustomGestureDrawingLayout {
    private static final int INSTRUCTION_TOP_SPACING_PX = 72;

    private CustomGestureDrawingLayout() {
    }

    public static int instructionBaseline(int systemTopInset) {
        return Math.max(0, systemTopInset) + INSTRUCTION_TOP_SPACING_PX;
    }

    public static boolean isCancelTarget(int width, int systemTopInset, float x, float y) {
        int safeTop = Math.max(0, systemTopInset);
        return x >= width - 160 && x <= width
                && y >= safeTop && y <= safeTop + 120;
    }
}
