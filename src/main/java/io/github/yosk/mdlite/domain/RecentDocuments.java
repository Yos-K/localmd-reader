package io.github.yosk.mdlite.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RecentDocuments {
    private final int maxItems;
    private final List<RecentDocument> items;

    private RecentDocuments(int maxItems, List<RecentDocument> items) {
        if (maxItems < 1) {
            throw new IllegalArgumentException("recent documents max items must be positive");
        }
        this.maxItems = maxItems;
        this.items = Collections.unmodifiableList(new ArrayList<RecentDocument>(items));
    }

    public static RecentDocuments empty(int maxItems) {
        return new RecentDocuments(maxItems, new ArrayList<RecentDocument>());
    }

    public static RecentDocuments from(int maxItems, List<RecentDocument> items) {
        RecentDocuments documents = empty(maxItems);
        if (items == null) {
            return documents;
        }
        for (int i = items.size() - 1; i >= 0; i--) {
            RecentDocument item = items.get(i);
            if (item != null) {
                documents = documents.recordOpened(item);
            }
        }
        return documents;
    }

    public RecentDocuments recordOpened(RecentDocument opened) {
        ArrayList<RecentDocument> next = new ArrayList<RecentDocument>();
        next.add(opened);
        for (int i = 0; i < items.size(); i++) {
            RecentDocument item = items.get(i);
            if (!item.uri().equals(opened.uri()) && next.size() < maxItems) {
                next.add(item);
            }
        }
        return new RecentDocuments(maxItems, next);
    }

    public List<RecentDocument> items() {
        return items;
    }
}
