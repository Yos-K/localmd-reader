package io.github.yosk.mdlite.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PinnedDocuments {
    private final int maxItems;
    private final List<RecentDocument> items;

    private PinnedDocuments(int maxItems, List<RecentDocument> items) {
        if (maxItems < 1) {
            throw new IllegalArgumentException("pinned documents max items must be positive");
        }
        this.maxItems = maxItems;
        this.items = Collections.unmodifiableList(new ArrayList<RecentDocument>(items));
    }

    public static PinnedDocuments empty(int maxItems) {
        return new PinnedDocuments(maxItems, new ArrayList<RecentDocument>());
    }

    public static PinnedDocuments from(int maxItems, List<RecentDocument> items) {
        PinnedDocuments documents = empty(maxItems);
        if (items == null) {
            return documents;
        }
        for (int i = items.size() - 1; i >= 0; i--) {
            RecentDocument item = items.get(i);
            if (item != null) {
                documents = documents.pin(item);
            }
        }
        return documents;
    }

    public PinnedDocuments pin(RecentDocument pinned) {
        ArrayList<RecentDocument> next = new ArrayList<RecentDocument>();
        next.add(pinned);
        for (int i = 0; i < items.size(); i++) {
            RecentDocument item = items.get(i);
            if (!item.uri().equals(pinned.uri()) && next.size() < maxItems) {
                next.add(item);
            }
        }
        return new PinnedDocuments(maxItems, next);
    }

    public PinnedDocuments unpin(String uri) {
        String safeUri = uri == null ? "" : uri.trim();
        ArrayList<RecentDocument> next = new ArrayList<RecentDocument>();
        for (int i = 0; i < items.size(); i++) {
            RecentDocument item = items.get(i);
            if (!item.uri().equals(safeUri)) {
                next.add(item);
            }
        }
        return new PinnedDocuments(maxItems, next);
    }

    public boolean containsUri(String uri) {
        String safeUri = uri == null ? "" : uri.trim();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).uri().equals(safeUri)) {
                return true;
            }
        }
        return false;
    }

    public List<RecentDocument> items() {
        return items;
    }
}
