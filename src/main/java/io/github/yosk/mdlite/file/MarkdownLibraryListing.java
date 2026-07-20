package io.github.yosk.mdlite.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class MarkdownLibraryListing {
    private final List<MarkdownLibraryItem> items;

    private MarkdownLibraryListing(List<MarkdownLibraryItem> items) {
        this.items = Collections.unmodifiableList(new ArrayList<MarkdownLibraryItem>(items));
    }

    public static MarkdownLibraryListing from(List<FolderDocumentEntry> entries) {
        ArrayList<MarkdownLibraryItem> items = new ArrayList<MarkdownLibraryItem>();
        Set<String> seenUris = new HashSet<String>();
        if (entries == null) {
            return new MarkdownLibraryListing(items);
        }
        for (int i = 0; i < entries.size(); i++) {
            FolderDocumentEntry entry = entries.get(i);
            if (entry instanceof FolderDocumentEntry.DirectoryEntry && seenUris.add(entry.uri())) {
                items.add(MarkdownLibraryItem.directory(entry.displayName(), entry.uri()));
            } else if (entry instanceof FolderDocumentEntry.MarkdownFileEntry && seenUris.add(entry.uri())) {
                items.add(MarkdownLibraryItem.document(entry.displayName(), entry.uri()));
            }
        }
        sort(items);
        return new MarkdownLibraryListing(items);
    }

    private static void sort(ArrayList<MarkdownLibraryItem> items) {
        for (int i = 1; i < items.size(); i++) {
            MarkdownLibraryItem current = items.get(i);
            int insertAt = i;
            while (insertAt > 0 && compare(items.get(insertAt - 1), current) > 0) {
                items.set(insertAt, items.get(insertAt - 1));
                insertAt--;
            }
            items.set(insertAt, current);
        }
    }

    private static int compare(MarkdownLibraryItem left, MarkdownLibraryItem right) {
        int leftType = left instanceof MarkdownLibraryItem.DirectoryItem ? 0 : 1;
        int rightType = right instanceof MarkdownLibraryItem.DirectoryItem ? 0 : 1;
        int byType = leftType - rightType;
        if (byType != 0) {
            return byType;
        }
        int byName = left.displayName().toLowerCase(Locale.ROOT)
                .compareTo(right.displayName().toLowerCase(Locale.ROOT));
        if (byName != 0) {
            return byName;
        }
        return left.uri().compareTo(right.uri());
    }

    public List<MarkdownLibraryItem> items() {
        return items;
    }

    public MarkdownLibraryListing matching(MarkdownLibraryQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Markdown library query must not be null");
        }
        ArrayList<MarkdownLibraryItem> matching = new ArrayList<MarkdownLibraryItem>();
        for (int i = 0; i < items.size(); i++) {
            MarkdownLibraryItem item = items.get(i);
            if (query.matches(item)) {
                matching.add(item);
            }
        }
        return new MarkdownLibraryListing(matching);
    }
}
