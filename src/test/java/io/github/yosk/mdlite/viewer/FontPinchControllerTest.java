package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class FontPinchControllerTest {
    @Nested
    final class OngoingPinch {
        @Test
        void pinchStartsFromTheCurrentlyRenderedFontSize() {
            RecordingOutput output = new RecordingOutput();
            FontPinchController controller = new FontPinchController(FontSize.of(16), output);

            controller.begin();
            controller.changeBy(1.20f);

            TestAssertions.assertEquals(19, controller.currentFontSize().sp(),
                    "pinch must calculate the committed font size from the gesture start size");
        }

        @Test
        void ongoingPinchAppliesTemporaryZoomWithoutReloadingTheDocument() {
            RecordingOutput output = new RecordingOutput();
            FontPinchController controller = new FontPinchController(FontSize.of(16), output);

            controller.begin();
            controller.changeBy(1.20f);

            TestAssertions.assertEquals(
                    120, output.zoomPercent, "ongoing pinch must immediately reflect the visible scale");
        }

        @Test
        void invalidPinchScaleDoesNotIssueAVisualUpdate() {
            RecordingOutput output = new RecordingOutput();
            FontPinchController controller = new FontPinchController(FontSize.of(16), output);

            controller.begin();
            controller.changeBy(Float.NaN);

            TestAssertions.assertEquals(
                    0, output.zoomCalls, "invalid pinch input must not mutate the visible document");
        }
    }

    @Nested
    final class PinchCompletion {
        @Test
        void completedFontChangeRequestsOneDocumentRender() {
            RecordingOutput output = new RecordingOutput();
            FontPinchController controller = new FontPinchController(FontSize.of(16), output);

            controller.begin();
            controller.changeBy(1.25f);
            controller.finish();

            TestAssertions.assertEquals(
                    1, output.renderCalls, "completed font change must commit through one document render");
        }

        @Test
        void completedFontChangePreservesTheVisibleViewportCenter() {
            RecordingOutput output = new RecordingOutput();
            output.scrollY = 100;
            output.viewportHeight = 400;
            FontPinchController controller = new FontPinchController(FontSize.of(16), output);

            controller.begin();
            controller.changeBy(1.25f);
            controller.finish();

            TestAssertions.assertEquals(175, output.requestedScrollY,
                    "font change must restore the scaled viewport center after rendering");
        }

        @Test
        void completedPinchWithoutFontChangeResetsTemporaryZoom() {
            RecordingOutput output = new RecordingOutput();
            FontPinchController controller = new FontPinchController(FontSize.of(16), output);

            controller.begin();
            controller.changeBy(1.01f);
            controller.finish();

            TestAssertions.assertEquals(
                    100, output.zoomPercent, "pinch without a committed size change must return to normal zoom");
        }
    }

    @Nested
    final class ScrollRestoration {
        @Test
        void loadedPageRestoresThePendingScrollPosition() {
            RecordingOutput output = new RecordingOutput();
            output.scrollY = 100;
            output.viewportHeight = 400;
            FontPinchController controller = new FontPinchController(FontSize.of(16), output);

            controller.begin();
            controller.changeBy(1.25f);
            controller.finish();
            controller.documentRendered();
            controller.pageLoaded();

            TestAssertions.assertEquals(
                    175, output.restoredScrollY, "loaded page must restore the position requested when pinch finished");
        }

        @Test
        void pendingScrollPositionIsConsumedAfterOnePageLoad() {
            RecordingOutput output = new RecordingOutput();
            FontPinchController controller = new FontPinchController(FontSize.of(16), output);

            controller.begin();
            controller.changeBy(1.25f);
            controller.finish();
            controller.documentRendered();
            controller.pageLoaded();
            controller.pageLoaded();

            TestAssertions.assertEquals(
                    1, output.restoreCalls, "pending scroll restoration must not leak into later page loads");
        }
    }

    private static final class RecordingOutput implements FontPinchController.Output {
        int zoomPercent;
        int zoomCalls;
        int renderCalls;
        int scrollY;
        int viewportHeight;
        int requestedScrollY;
        int restoredScrollY;
        int restoreCalls;

        @Override
        public void applyTextZoom(int percent) {
            zoomPercent = percent;
            zoomCalls++;
        }

        @Override
        public int currentScrollY() {
            return scrollY;
        }

        @Override
        public int currentViewportHeight() {
            return viewportHeight;
        }

        @Override
        public void requestDocumentRender(int scrollYAfterRender) {
            requestedScrollY = scrollYAfterRender;
            renderCalls++;
        }

        @Override
        public void restoreScrollPosition(int scrollY) {
            restoredScrollY = scrollY;
            restoreCalls++;
        }
    }
}
