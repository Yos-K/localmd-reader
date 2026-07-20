package io.github.yosk.mdlite.presentation;

import java.util.ArrayList;
import java.util.List;
import io.github.yosk.mdlite.viewer.ClipboardMarkdownItem;

final class ClipboardMarkdownItems {
    private final List<ClipboardMarkdownItem> items;

    private ClipboardMarkdownItems(List<ClipboardMarkdownItem> items) {
        this.items = items;
    }

    static ClipboardMarkdownItems empty() {
        return new ClipboardMarkdownItems(new ArrayList<ClipboardMarkdownItem>());
    }

    ClipboardMarkdownItems appendCurrent(String title, CharSequence text) {
        ClipboardMarkdownItem item = itemFromText(title, text);
        if (item != null) {
            items.add(item);
        }
        return this;
    }

    ClipboardMarkdownItems appendHistory(String title, String markdown) {
        ClipboardMarkdownItem item = itemFromMarkdown(title, markdown);
        if (item != null && !containsMarkdown(item.markdown())) {
            items.add(item);
        }
        return this;
    }

    List<ClipboardMarkdownItem> asList() {
        return new ArrayList<ClipboardMarkdownItem>(items);
    }

    private static ClipboardMarkdownItem itemFromText(String title, CharSequence text) {
        if (text == null || text.length() == 0) {
            return null;
        }
        return itemFromMarkdown(title, AndroidStyledTextMarkdown.from(text));
    }

    private static ClipboardMarkdownItem itemFromMarkdown(String title, String markdown) {
        if (markdown == null || markdown.length() == 0) {
            return null;
        }
        return new ClipboardMarkdownItem(title, markdown);
    }

    private boolean containsMarkdown(String markdown) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).markdown().equals(markdown)) {
                return true;
            }
        }
        return false;
    }
}
