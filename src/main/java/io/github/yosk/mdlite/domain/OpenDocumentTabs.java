package io.github.yosk.mdlite.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class OpenDocumentTabs {
    private final List<OpenDocumentTab> tabs;
    private final int activeIndex;

    private OpenDocumentTabs(List<OpenDocumentTab> tabs, int activeIndex) {
        if (tabs == null || tabs.isEmpty()) {
            throw new IllegalArgumentException("open tabs must not be empty");
        }
        if (activeIndex < 0 || activeIndex >= tabs.size()) {
            throw new IllegalArgumentException("active tab index must point to an open tab");
        }
        this.tabs = Collections.unmodifiableList(new ArrayList<OpenDocumentTab>(tabs));
        this.activeIndex = activeIndex;
    }

    public static OpenDocumentTabs withInitialTab(OpenDocumentTab tab) {
        ArrayList<OpenDocumentTab> tabs = new ArrayList<OpenDocumentTab>();
        tabs.add(tab);
        return new OpenDocumentTabs(tabs, 0);
    }

    public OpenDocumentTabs open(OpenDocumentTab tab) {
        ArrayList<OpenDocumentTab> next = new ArrayList<OpenDocumentTab>(tabs);
        for (int i = 0; i < next.size(); i++) {
            if (next.get(i).uri().equals(tab.uri())) {
                next.set(i, tab);
                return new OpenDocumentTabs(next, i);
            }
        }
        next.add(tab);
        return new OpenDocumentTabs(next, next.size() - 1);
    }

    public OpenDocumentTabs activate(int index) {
        return new OpenDocumentTabs(tabs, index);
    }

    public OpenDocumentTab activeTab() {
        return tabs.get(activeIndex);
    }

    public List<OpenDocumentTab> tabs() {
        return tabs;
    }

    public int activeIndex() {
        return activeIndex;
    }
}
