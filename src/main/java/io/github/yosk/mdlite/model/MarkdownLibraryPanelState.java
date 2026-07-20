package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.file.MarkdownLibraryListing;
import io.github.yosk.mdlite.file.MarkdownLibraryLocation;
import io.github.yosk.mdlite.file.MarkdownLibraryQuery;

public abstract class MarkdownLibraryPanelState {
    private static final Unselected UNSELECTED = new Unselected();

    private MarkdownLibraryPanelState() {
    }

    public static Unselected unselected() {
        return UNSELECTED;
    }

    public static Expanded expanded(MarkdownLibraryLocation location,
            MarkdownLibraryListing listing) {
        return new Expanded(new Content(location, listing, MarkdownLibraryQuery.from("")));
    }

    public abstract MarkdownLibraryPanelState toggled();

    public abstract MarkdownLibraryPanelState withQuery(MarkdownLibraryQuery query);

    public abstract void handle(Handler handler);

    public interface Handler {
        void unselected();

        void expanded(Content content);

        void collapsed(Content content);
    }

    public static final class Content {
        private final MarkdownLibraryLocation location;
        private final MarkdownLibraryListing listing;
        private final MarkdownLibraryQuery query;

        private Content(MarkdownLibraryLocation location, MarkdownLibraryListing listing,
                MarkdownLibraryQuery query) {
            if (location == null || listing == null || query == null) {
                throw new IllegalArgumentException("Markdown library panel content must be complete");
            }
            this.location = location;
            this.listing = listing;
            this.query = query;
        }

        public MarkdownLibraryLocation location() {
            return location;
        }

        public MarkdownLibraryListing listing() {
            return listing;
        }

        public MarkdownLibraryListing visibleListing() {
            return listing.matching(query);
        }

        private Content withQuery(MarkdownLibraryQuery nextQuery) {
            return new Content(location, listing, nextQuery);
        }
    }

    public static final class Unselected extends MarkdownLibraryPanelState {
        private Unselected() {
        }

        @Override
        public Unselected toggled() {
            return this;
        }

        @Override
        public Unselected withQuery(MarkdownLibraryQuery query) {
            return this;
        }

        @Override
        public void handle(Handler handler) {
            handler.unselected();
        }
    }

    public abstract static class Loaded extends MarkdownLibraryPanelState {
        private final Content content;

        private Loaded(Content content) {
            this.content = content;
        }

        public final Content content() {
            return content;
        }

    }

    public static final class Expanded extends Loaded {
        private Expanded(Content content) {
            super(content);
        }

        @Override
        public Collapsed toggled() {
            return new Collapsed(content());
        }

        @Override
        public Expanded withQuery(MarkdownLibraryQuery query) {
            return new Expanded(content().withQuery(query));
        }

        @Override
        public void handle(Handler handler) {
            handler.expanded(content());
        }
    }

    public static final class Collapsed extends Loaded {
        private Collapsed(Content content) {
            super(content);
        }

        @Override
        public Expanded toggled() {
            return new Expanded(content());
        }

        @Override
        public Collapsed withQuery(MarkdownLibraryQuery query) {
            return new Collapsed(content().withQuery(query));
        }

        @Override
        public void handle(Handler handler) {
            handler.collapsed(content());
        }
    }
}
