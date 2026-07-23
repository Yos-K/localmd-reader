package io.github.yosk.mdlite.infrastructure;

public abstract class ReaderLinkNavigation {
    public interface Handler {
        void openMarkdownPicker();

        void openRelativeMarkdown(String requestUrl);

        void openExternalUrl(String url);
    }

    private static final ReaderLinkNavigation OPEN_PICKER = new OpenPicker();
    private static final ReaderLinkNavigation CONSUMED = new Consumed();
    private static final String LOCAL_ORIGIN = "https://localmd.local/";
    private static final String RELATIVE_MARKDOWN_REQUEST =
            "https://localmd.local/__relative_markdown__";

    private ReaderLinkNavigation() {
    }

    public static ReaderLinkNavigation from(String url) {
        if (WelcomeDocumentBuilder.OPEN_MARKDOWN_URL.equals(url)) {
            return OPEN_PICKER;
        }
        if (url == null) {
            return CONSUMED;
        }
        String lower = url.toLowerCase();
        if (lower.startsWith(RELATIVE_MARKDOWN_REQUEST)) {
            return new OpenRelativeMarkdown(url);
        }
        if (lower.startsWith(LOCAL_ORIGIN)) {
            return CONSUMED;
        }
        if (lower.startsWith("https://") || lower.startsWith("http://")) {
            return new OpenExternalUrl(url);
        }
        return CONSUMED;
    }

    public abstract void perform(Handler handler);

    private static final class OpenPicker extends ReaderLinkNavigation {
        @Override
        public void perform(Handler handler) {
            handler.openMarkdownPicker();
        }
    }

    private static final class OpenRelativeMarkdown extends ReaderLinkNavigation {
        private final String requestUrl;

        private OpenRelativeMarkdown(String requestUrl) {
            this.requestUrl = requestUrl;
        }

        @Override
        public void perform(Handler handler) {
            handler.openRelativeMarkdown(requestUrl);
        }
    }

    private static final class OpenExternalUrl extends ReaderLinkNavigation {
        private final String url;

        private OpenExternalUrl(String url) {
            this.url = url;
        }

        @Override
        public void perform(Handler handler) {
            handler.openExternalUrl(url);
        }
    }

    private static final class Consumed extends ReaderLinkNavigation {
        @Override
        public void perform(Handler handler) {
        }
    }
}
