package io.github.yosk.mdlite.viewer;

public final class CustomGestureDrawingLayout {
    private static final int INSTRUCTION_TOP_SPACING_PX = 72;

    private CustomGestureDrawingLayout() {
    }

    public static int instructionBaseline(int systemTopInset) {
        return Math.max(0, systemTopInset) + INSTRUCTION_TOP_SPACING_PX;
    }
}
