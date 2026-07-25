package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.file.RestorableOpenTab;
import io.github.yosk.mdlite.file.RestorableOpenTabs;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.OpenDocumentTabs;
import java.util.ArrayList;
import java.util.List;

public final class RestoredOpenDocumentTabs {
    public interface Loader {
        RestoredOpenDocumentTab load(RestorableOpenTab storedTab);
    }

    private RestoredOpenDocumentTabs() {
    }

    public static OpenDocumentTabs restore(
            RestorableOpenTabs storedTabs,
            OpenDocumentTab initialTab,
            Loader loader) {
        if (storedTabs == null || initialTab == null || loader == null) {
            throw new IllegalArgumentException("tab restoration inputs must not be null");
        }
        if (storedTabs.isEmpty()) {
            return OpenDocumentTabs.withInitialTab(initialTab);
        }

        List<OpenDocumentTab> restored = new ArrayList<OpenDocumentTab>();
        int selectedRestoredIndex = -1;
        for (int index = 0; index < storedTabs.tabs().size(); index++) {
            int candidateIndex = restored.size();
            RestoredOpenDocumentTab loaded = loader.load(storedTabs.tabs().get(index));
            if (loaded == null) {
                throw new IllegalStateException("tab loader must return a restoration result");
            }
            boolean available = loaded.appendTo(restored);
            if (available && index == storedTabs.activeIndex()) {
                selectedRestoredIndex = candidateIndex;
            }
        }
        if (restored.isEmpty()) {
            return OpenDocumentTabs.withInitialTab(initialTab);
        }
        if (selectedRestoredIndex < 0) {
            selectedRestoredIndex = Math.min(storedTabs.activeIndex(), restored.size() - 1);
        }
        return openTabs(restored, selectedRestoredIndex);
    }

    private static OpenDocumentTabs openTabs(List<OpenDocumentTab> restored, int activeIndex) {
        OpenDocumentTabs tabs = OpenDocumentTabs.withInitialTab(restored.get(0));
        for (int index = 1; index < restored.size(); index++) {
            tabs = tabs.open(restored.get(index));
        }
        return tabs.activate(activeIndex);
    }
}
