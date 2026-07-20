package io.github.yosk.mdlite.viewer;

public final class DocumentSearchSession {
    private static final DocumentSearchSession EMPTY = new DocumentSearchSession(DocumentSearchQuery.from(""));

    private final DocumentSearchQuery query;

    private DocumentSearchSession(DocumentSearchQuery query) {
        this.query = query;
    }

    public static DocumentSearchSession empty() {
        return EMPTY;
    }

    public DocumentSearchSession search(DocumentSearchQuery nextQuery) {
        DocumentSearchQuery safeQuery = nextQuery == null ? DocumentSearchQuery.from("") : nextQuery;
        return safeQuery.isActive() ? new DocumentSearchSession(safeQuery) : empty();
    }

    public DocumentSearchSession clear() {
        return empty();
    }

    public boolean hasActiveQuery() {
        return query.isActive();
    }

    public String queryText() {
        return query.text();
    }
}
