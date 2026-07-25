package io.github.yosk.mdlite.viewer;

public final class FontPinchController {
    private final Output output;
    private FontSize currentFontSize;
    private FontSize renderedFontSize;
    private FontSize pinchStartFontSize;
    private float accumulatedScale = 1f;
    private float temporaryScale = 1f;
    private PendingScrollRestore pendingScrollRestore = NoPendingScrollRestore.INSTANCE;

    public FontPinchController(FontSize initialFontSize, Output output) {
        if (initialFontSize == null) {
            throw new IllegalArgumentException("initial font size is required");
        }
        if (output == null) {
            throw new IllegalArgumentException("output is required");
        }
        this.currentFontSize = initialFontSize;
        this.renderedFontSize = initialFontSize;
        this.pinchStartFontSize = initialFontSize;
        this.output = output;
    }

    public FontSize currentFontSize() {
        return currentFontSize;
    }

    public void begin() {
        accumulatedScale = 1f;
        temporaryScale = 1f;
        pinchStartFontSize = currentFontSize;
    }

    public void changeBy(float scaleFactor) {
        if (!FontSize.canApplyPinchScale(scaleFactor)) {
            return;
        }
        accumulatedScale *= scaleFactor;
        temporaryScale = clampedTemporaryScale(temporaryScale * scaleFactor);
        currentFontSize = pinchStartFontSize.changedByPinchScale(accumulatedScale);
        output.applyTextZoom(Math.round(
                (pinchStartFontSize.sp() * temporaryScale * 100f) / renderedFontSize.sp()));
    }

    public void finish() {
        accumulatedScale = 1f;
        temporaryScale = 1f;
        if (currentFontSize.sp() == renderedFontSize.sp()) {
            output.applyTextZoom(100);
            return;
        }
        int scrollY = restoredScrollY();
        pendingScrollRestore = new WaitingScrollRestore(scrollY);
        output.requestDocumentRender(scrollY);
    }

    public void documentRendered() {
        renderedFontSize = currentFontSize;
        output.applyTextZoom(100);
    }

    public void pageLoaded() {
        pendingScrollRestore = pendingScrollRestore.restore(output);
    }

    private float clampedTemporaryScale(float scale) {
        float minScale = FontSize.MIN_SP / (float) pinchStartFontSize.sp();
        float maxScale = FontSize.MAX_SP / (float) pinchStartFontSize.sp();
        return Math.max(minScale, Math.min(maxScale, scale));
    }

    private int restoredScrollY() {
        float scale = currentFontSize.sp() / (float) renderedFontSize.sp();
        int viewportHeight = output.currentViewportHeight();
        float viewportCenter = output.currentScrollY() + (viewportHeight / 2f);
        return Math.max(0, Math.round((viewportCenter * scale) - (viewportHeight / 2f)));
    }

    public interface Output {
        void applyTextZoom(int percent);

        int currentScrollY();

        int currentViewportHeight();

        void requestDocumentRender(int scrollYAfterRender);

        void restoreScrollPosition(int scrollY);
    }

    private interface PendingScrollRestore {
        PendingScrollRestore restore(Output output);
    }

    private enum NoPendingScrollRestore implements PendingScrollRestore {
        INSTANCE;

        @Override
        public PendingScrollRestore restore(Output output) {
            return this;
        }
    }

    private static final class WaitingScrollRestore implements PendingScrollRestore {
        private final int scrollY;

        WaitingScrollRestore(int scrollY) {
            this.scrollY = scrollY;
        }

        @Override
        public PendingScrollRestore restore(Output output) {
            output.restoreScrollPosition(scrollY);
            return NoPendingScrollRestore.INSTANCE;
        }
    }
}
