package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import java.util.List;

public abstract class RestoredOpenDocumentTab {
    private RestoredOpenDocumentTab() {
    }

    public static RestoredOpenDocumentTab available(OpenDocumentTab tab) {
        if (tab == null) {
            throw new IllegalArgumentException("restored open tab must not be null");
        }
        return new Available(tab);
    }

    public static RestoredOpenDocumentTab unavailable() {
        return Unavailable.INSTANCE;
    }

    abstract boolean appendTo(List<OpenDocumentTab> tabs);

    private static final class Available extends RestoredOpenDocumentTab {
        private final OpenDocumentTab tab;

        private Available(OpenDocumentTab tab) {
            this.tab = tab;
        }

        @Override
        boolean appendTo(List<OpenDocumentTab> tabs) {
            tabs.add(tab);
            return true;
        }
    }

    private static final class Unavailable extends RestoredOpenDocumentTab {
        private static final Unavailable INSTANCE = new Unavailable();

        @Override
        boolean appendTo(List<OpenDocumentTab> tabs) {
            return false;
        }
    }
}
