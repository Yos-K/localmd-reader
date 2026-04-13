package io.github.yosk.mdlite.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RestorableOpenTabs {
    private final List<RestorableOpenTab> tabs;
    private final int activeIndex;

    private RestorableOpenTabs(List<RestorableOpenTab> tabs, int activeIndex) {
        this.tabs = Collections.unmodifiableList(new ArrayList<RestorableOpenTab>(tabs));
        this.activeIndex = activeIndex;
    }

    public static RestorableOpenTabs empty() {
        return new RestorableOpenTabs(new ArrayList<RestorableOpenTab>(), 0);
    }

    public static RestorableOpenTabs from(List<RestorableOpenTab> storedTabs, int storedActiveIndex) {
        if (storedTabs == null || storedTabs.isEmpty()) {
            return empty();
        }

        ArrayList<RestorableOpenTab> restored = new ArrayList<RestorableOpenTab>();
        for (int i = 0; i < storedTabs.size(); i++) {
            RestorableOpenTab tab = storedTabs.get(i);
            if (tab != null && !containsUri(restored, tab.uri())) {
                restored.add(tab);
            }
        }

        if (restored.isEmpty()) {
            return empty();
        }

        int safeActiveIndex = storedActiveIndex;
        if (safeActiveIndex < 0) {
            safeActiveIndex = 0;
        } else if (safeActiveIndex >= restored.size()) {
            safeActiveIndex = restored.size() - 1;
        }
        return new RestorableOpenTabs(restored, safeActiveIndex);
    }

    public boolean isEmpty() {
        return tabs.isEmpty();
    }

    public List<RestorableOpenTab> tabs() {
        return tabs;
    }

    public int activeIndex() {
        return activeIndex;
    }

    private static boolean containsUri(List<RestorableOpenTab> tabs, String uri) {
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).uri().equals(uri)) {
                return true;
            }
        }
        return false;
    }
}
