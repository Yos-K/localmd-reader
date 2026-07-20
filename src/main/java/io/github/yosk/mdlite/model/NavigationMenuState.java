package io.github.yosk.mdlite.model;

public abstract class NavigationMenuState {
    private static final Closed CLOSED = new Closed();
    private static final Open OPEN = new Open();

    private NavigationMenuState() {
    }

    public static Closed closed() {
        return CLOSED;
    }

    public static Open opened() {
        return OPEN;
    }

    public abstract Open open();

    public abstract Closed close();

    public abstract void handle(Handler handler);

    public interface Handler {
        void closed();

        void open();
    }

    public static final class Closed extends NavigationMenuState {
        private Closed() {
        }

        @Override
        public Open open() {
            return OPEN;
        }

        @Override
        public Closed close() {
            return this;
        }

        @Override
        public void handle(Handler handler) {
            handler.closed();
        }
    }

    public static final class Open extends NavigationMenuState {
        private Open() {
        }

        @Override
        public Open open() {
            return this;
        }

        @Override
        public Closed close() {
            return CLOSED;
        }

        @Override
        public void handle(Handler handler) {
            handler.open();
        }
    }
}
