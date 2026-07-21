package io.github.yosk.mdlite.domain;

public abstract class DocumentRenderingCompletion {
    private final DocumentRenderingSession session;

    private DocumentRenderingCompletion(DocumentRenderingSession session) {
        if (session == null) {
            throw new IllegalArgumentException("rendering completion requires a session");
        }
        this.session = session;
    }

    static DocumentRenderingCompletion rendered(
            DocumentRenderingSession session,
            DocumentRenderInput renderInput) {
        return new Rendered(session, renderInput);
    }

    static DocumentRenderingCompletion unchanged(DocumentRenderingSession session) {
        return new Unchanged(session);
    }

    public final DocumentRenderingSession session() {
        return session;
    }

    public abstract void dispatch(Handler handler);

    public interface Handler {
        void rendered(DocumentRenderInput input);
        void unchanged();
    }

    private static final class Rendered extends DocumentRenderingCompletion {
        private final DocumentRenderInput renderInput;

        private Rendered(DocumentRenderingSession session, DocumentRenderInput renderInput) {
            super(session);
            if (renderInput == null) {
                throw new IllegalArgumentException("rendered completion requires its input");
            }
            this.renderInput = renderInput;
        }

        @Override
        public void dispatch(Handler handler) {
            requireHandler(handler);
            handler.rendered(renderInput);
        }
    }

    private static final class Unchanged extends DocumentRenderingCompletion {
        private Unchanged(DocumentRenderingSession session) {
            super(session);
        }

        @Override
        public void dispatch(Handler handler) {
            requireHandler(handler);
            handler.unchanged();
        }
    }

    private static void requireHandler(Handler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("rendering completion requires a handler");
        }
    }
}
