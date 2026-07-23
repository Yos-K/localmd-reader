package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.DocumentUri;

public abstract class SavedDocumentPlacement {
    private static final OpenNormally OPEN_NORMALLY = new OpenNormally();

    private SavedDocumentPlacement() {
    }

    public static SavedDocumentPlacement from(OpenDocumentTab source) {
        if (source instanceof OpenDocumentTab.DraftDocumentTab) {
            return new ReplaceDraft(source.documentUri());
        }
        return OPEN_NORMALLY;
    }

    public static SavedDocumentPlacement openNormally() {
        return OPEN_NORMALLY;
    }

    public abstract OpenDocumentTabs place(
            OpenDocumentTabs tabs,
            OpenDocumentTab.FileDocumentTab savedFile);

    public static final class OpenNormally extends SavedDocumentPlacement {
        private OpenNormally() {
        }

        @Override
        public OpenDocumentTabs place(
                OpenDocumentTabs tabs,
                OpenDocumentTab.FileDocumentTab savedFile) {
            return tabs.open(savedFile);
        }
    }

    public static final class ReplaceDraft extends SavedDocumentPlacement {
        private final DocumentUri draftUri;

        private ReplaceDraft(DocumentUri draftUri) {
            this.draftUri = draftUri;
        }

        @Override
        public OpenDocumentTabs place(
                OpenDocumentTabs tabs,
                OpenDocumentTab.FileDocumentTab savedFile) {
            return tabs.replaceDraftWithFile(draftUri, savedFile);
        }
    }
}
