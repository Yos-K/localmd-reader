package io.github.yosk.mdlite.model;

public abstract class DisclosureState {
    private static final Collapsed COLLAPSED = new Collapsed();
    private static final Expanded EXPANDED = new Expanded();

    private DisclosureState() {
    }

    public static Collapsed collapsed() {
        return COLLAPSED;
    }

    public static Expanded expanded() {
        return EXPANDED;
    }

    public abstract DisclosureState toggled();

    public abstract void handle(Handler handler);

    public interface Handler {
        void collapsed();

        void expanded();
    }

    public static final class Collapsed extends DisclosureState {
        private Collapsed() {
        }

        @Override
        public Expanded toggled() {
            return EXPANDED;
        }

        @Override
        public void handle(Handler handler) {
            handler.collapsed();
        }
    }

    public static final class Expanded extends DisclosureState {
        private Expanded() {
        }

        @Override
        public Collapsed toggled() {
            return COLLAPSED;
        }

        @Override
        public void handle(Handler handler) {
            handler.expanded();
        }
    }
}
