package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ReaderLinkNavigationTest {
    @Test
    void welcomeActionDispatchesTheMarkdownPickerCommand() {
        RecordingHandler handler = new RecordingHandler();

        ReaderLinkNavigation.from(WelcomeDocumentBuilder.OPEN_MARKDOWN_URL).perform(handler);

        TestAssertions.assertEquals("open-picker", handler.event,
                "the welcome action must open the Android document picker");
    }

    @Test
    void localRelativeDocumentDispatchesTheInReaderOpenCommand() {
        RecordingHandler handler = new RecordingHandler();

        ReaderLinkNavigation.from(
                "https://localmd.local/__relative_markdown__?path=guide.md").perform(handler);

        TestAssertions.assertEquals(
                "open-relative:https://localmd.local/__relative_markdown__?path=guide.md",
                handler.event,
                "a relative Markdown request must stay inside the reader");
    }

    @Test
    void publicHttpsLinkDispatchesTheExternalBrowserCommand() {
        RecordingHandler handler = new RecordingHandler();

        ReaderLinkNavigation.from("https://example.com/docs").perform(handler);

        TestAssertions.assertEquals("open-external:https://example.com/docs", handler.event,
                "a public web link must be delegated to the platform browser");
    }

    @Test
    void unsupportedSchemeIsConsumedWithoutDispatchingAnAction() {
        RecordingHandler handler = new RecordingHandler();

        ReaderLinkNavigation.from("javascript:alert(1)").perform(handler);

        TestAssertions.assertEquals("none", handler.event,
                "an unsupported scheme must not escape the reader WebView");
    }

    private static final class RecordingHandler implements ReaderLinkNavigation.Handler {
        private String event = "none";

        @Override
        public void openMarkdownPicker() {
            event = "open-picker";
        }

        @Override
        public void openRelativeMarkdown(String requestUrl) {
            event = "open-relative:" + requestUrl;
        }

        @Override
        public void openExternalUrl(String url) {
            event = "open-external:" + url;
        }
    }
}
