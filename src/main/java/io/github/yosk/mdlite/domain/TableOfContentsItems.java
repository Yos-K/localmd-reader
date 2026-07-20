package io.github.yosk.mdlite.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TableOfContentsItems {
    private final List<TableOfContentsItem> items;

    private TableOfContentsItems(List<TableOfContentsItem> items) {
        this.items = Collections.unmodifiableList(new ArrayList<TableOfContentsItem>(items));
    }

    public static TableOfContentsItems from(MarkdownHeadings headings) {
        MarkdownHeadings safeHeadings = headings == null ? MarkdownHeadings.fromMarkdown("") : headings;
        ArrayList<TableOfContentsItem> items = new ArrayList<TableOfContentsItem>();
        for (int i = 0; i < safeHeadings.count(); i++) {
            MarkdownHeading heading = safeHeadings.at(i);
            items.add(new TableOfContentsItem(heading, labelFor(heading)));
        }
        return new TableOfContentsItems(items);
    }

    public int count() {
        return items.size();
    }

    public TableOfContentsItem at(int index) {
        return items.get(index);
    }

    public List<TableOfContentsItem> items() {
        return items;
    }

    private static String labelFor(MarkdownHeading heading) {
        return indentFor(heading.level()) + heading.title();
    }

    private static String indentFor(int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 1; i < level; i++) {
            indent.append("  ");
        }
        return indent.toString();
    }
}
