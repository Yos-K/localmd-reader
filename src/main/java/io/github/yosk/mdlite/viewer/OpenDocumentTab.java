package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.SafeHtml;

public abstract class OpenDocumentTab {
    private final String title;
    private final String uri;
    private final SafeHtml document;

    private OpenDocumentTab(String title, String uri, SafeHtml document) {
        this.title = normalizedTitle(title);
        this.uri = normalizedUri(uri);
        if (document == null) {
            throw new IllegalArgumentException("open tab document must not be null");
        }
        this.document = document;
    }

    public static OpenDocumentTab welcome(String title, String uri, SafeHtml document) {
        return new WelcomeTab(title, uri, document);
    }

    public static OpenDocumentTab of(String title, String uri, SafeHtml document) {
        return fileDocument(title, uri, document);
    }

    public static OpenDocumentTab fileDocument(String title, String uri, SafeHtml document) {
        return new FileDocumentTab(title, uri, document);
    }

    public static OpenDocumentTab clipboardDraft(String title, String uri, SafeHtml document) {
        return new ClipboardDraftTab(title, uri, document);
    }

    public static OpenDocumentTab selectedTextDraft(String title, String uri, SafeHtml document) {
        return new SelectedTextDraftTab(title, uri, document);
    }

    private static String normalizedTitle(String title) {
        String safeTitle = title == null ? "" : title.trim();
        return safeTitle.length() == 0 ? "Untitled Markdown" : safeTitle;
    }

    private static String normalizedUri(String uri) {
        String safeUri = uri == null ? "" : uri.trim();
        if (safeUri.length() == 0) {
            throw new IllegalArgumentException("open tab URI must not be empty");
        }
        return safeUri;
    }

    public String title() {
        return title;
    }

    public String uri() {
        return uri;
    }

    public SafeHtml document() {
        return document;
    }

    public abstract TabStatusMessage statusMessage();

    public static final class WelcomeTab extends OpenDocumentTab {
        private WelcomeTab(String title, String uri, SafeHtml document) {
            super(title, uri, document);
        }

        @Override
        public TabStatusMessage statusMessage() {
            return TabStatusMessage.none();
        }
    }

    public static final class FileDocumentTab extends OpenDocumentTab {
        private FileDocumentTab(String title, String uri, SafeHtml document) {
            super(title, uri, document);
        }

        @Override
        public TabStatusMessage statusMessage() {
            return TabStatusMessage.none();
        }
    }

    public abstract static class DraftDocumentTab extends OpenDocumentTab {
        private DraftDocumentTab(String title, String uri, SafeHtml document) {
            super(title, uri, document);
        }
    }

    public static final class ClipboardDraftTab extends DraftDocumentTab {
        private ClipboardDraftTab(String title, String uri, SafeHtml document) {
            super(title, uri, document);
        }

        @Override
        public TabStatusMessage statusMessage() {
            return TabStatusMessage.temporaryMarkdown();
        }
    }

    public static final class SelectedTextDraftTab extends DraftDocumentTab {
        private SelectedTextDraftTab(String title, String uri, SafeHtml document) {
            super(title, uri, document);
        }

        @Override
        public TabStatusMessage statusMessage() {
            return TabStatusMessage.selectedTextMarkdown();
        }
    }
}
