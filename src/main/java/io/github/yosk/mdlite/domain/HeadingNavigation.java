package io.github.yosk.mdlite.domain;

public abstract class HeadingNavigation {
    private static final Unavailable UNAVAILABLE = new Unavailable();

    private HeadingNavigation() {
    }

    public static HeadingNavigation from(
            TableOfContentsItems items,
            HeadingScrollPosition visiblePosition) {
        requireInputs(items, visiblePosition);
        if (items.count() == 0) {
            return UNAVAILABLE;
        }
        return new Destination(items, visiblePosition.estimatedHeadingIndex(items.count()));
    }

    public static HeadingNavigation selected(
            TableOfContentsItems items,
            MarkdownHeading selectedHeading) {
        requireInputs(items, selectedHeading);
        for (int index = 0; index < items.count(); index++) {
            if (sameHeading(items.at(index).heading(), selectedHeading)) {
                return new Destination(items, index);
            }
        }
        return UNAVAILABLE;
    }

    public abstract HeadingNavigation next();

    public abstract HeadingNavigation previous();

    public abstract void handle(Handler handler);

    private static boolean sameHeading(MarkdownHeading left, MarkdownHeading right) {
        return left.anchorId().equals(right.anchorId());
    }

    private static void requireInputs(Object first, Object second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("heading navigation requires valid inputs");
        }
    }

    public interface Handler {
        void unavailable();

        void destination(MarkdownHeading heading);
    }

    public static final class Unavailable extends HeadingNavigation {
        private Unavailable() {
        }

        @Override
        public Unavailable next() {
            return this;
        }

        @Override
        public Unavailable previous() {
            return this;
        }

        @Override
        public void handle(Handler handler) {
            handler.unavailable();
        }
    }

    public static final class Destination extends HeadingNavigation {
        private final TableOfContentsItems items;
        private final int index;

        private Destination(TableOfContentsItems items, int index) {
            this.items = items;
            this.index = index;
        }

        @Override
        public Destination next() {
            return new Destination(items, (index + 1) % items.count());
        }

        @Override
        public Destination previous() {
            return new Destination(items, (index + items.count() - 1) % items.count());
        }

        @Override
        public void handle(Handler handler) {
            handler.destination(items.at(index).heading());
        }
    }
}
