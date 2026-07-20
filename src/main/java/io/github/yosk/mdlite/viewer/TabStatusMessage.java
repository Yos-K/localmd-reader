package io.github.yosk.mdlite.viewer;

public abstract class TabStatusMessage {
    private static final TabStatusMessage NONE = new NoTabStatusMessage();
    private static final TabStatusMessage TEMPORARY_MARKDOWN = new TemporaryMarkdownMessage();
    private static final TabStatusMessage SELECTED_TEXT_MARKDOWN = new SelectedTextMarkdownMessage();

    public static TabStatusMessage none() {
        return NONE;
    }

    public static TabStatusMessage temporaryMarkdown() {
        return TEMPORARY_MARKDOWN;
    }

    public static TabStatusMessage selectedTextMarkdown() {
        return SELECTED_TEXT_MARKDOWN;
    }

    public abstract String localized(ViewerText text);

    private static final class NoTabStatusMessage extends TabStatusMessage {
        @Override
        public String localized(ViewerText text) {
            return "";
        }
    }

    private static final class TemporaryMarkdownMessage extends TabStatusMessage {
        @Override
        public String localized(ViewerText text) {
            return text.temporaryMarkdown();
        }
    }

    private static final class SelectedTextMarkdownMessage extends TabStatusMessage {
        @Override
        public String localized(ViewerText text) {
            return text.selectedTextMarkdown();
        }
    }
}
