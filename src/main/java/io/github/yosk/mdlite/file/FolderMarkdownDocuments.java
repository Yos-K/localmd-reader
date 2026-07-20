package io.github.yosk.mdlite.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class FolderMarkdownDocuments {
    private final List<RecentDocument> items;

    private FolderMarkdownDocuments(List<RecentDocument> items) {
        this.items = Collections.unmodifiableList(new ArrayList<RecentDocument>(items));
    }

    public static FolderMarkdownDocuments from(List<FolderDocumentEntry> entries) {
        ArrayList<RecentDocument> markdownFiles = new ArrayList<RecentDocument>();
        Set<String> seenUris = new HashSet<String>();
        if (entries == null) {
            return new FolderMarkdownDocuments(markdownFiles);
        }
        for (int i = 0; i < entries.size(); i++) {
            FolderDocumentEntry entry = entries.get(i);
            if (entry instanceof FolderDocumentEntry.MarkdownFileEntry && seenUris.add(entry.uri())) {
                markdownFiles.add(RecentDocument.of(entry.displayName(), entry.uri()));
            }
        }
        sortByDisplayName(markdownFiles);
        return new FolderMarkdownDocuments(markdownFiles);
    }

    private static void sortByDisplayName(ArrayList<RecentDocument> documents) {
        for (int i = 1; i < documents.size(); i++) {
            RecentDocument current = documents.get(i);
            int insertAt = i;
            while (insertAt > 0 && compare(documents.get(insertAt - 1), current) > 0) {
                documents.set(insertAt, documents.get(insertAt - 1));
                insertAt--;
            }
            documents.set(insertAt, current);
        }
    }

    private static int compare(RecentDocument left, RecentDocument right) {
        String leftName = left.displayName().toLowerCase(Locale.ROOT);
        String rightName = right.displayName().toLowerCase(Locale.ROOT);
        int byName = leftName.compareTo(rightName);
        if (byName != 0) {
            return byName;
        }
        return left.uri().compareTo(right.uri());
    }

    public List<RecentDocument> items() {
        return items;
    }
}
