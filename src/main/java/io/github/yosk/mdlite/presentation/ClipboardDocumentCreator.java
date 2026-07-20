package io.github.yosk.mdlite.presentation;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import io.github.yosk.mdlite.domain.MarkdownDraftFileName;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.file.MarkdownFileOpenResult;
import io.github.yosk.mdlite.viewer.ClipboardMarkdownItem;
import io.github.yosk.mdlite.viewer.DraftMarkdownDocument;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import java.nio.charset.StandardCharsets;
import java.util.List;

final class ClipboardDocumentCreator {
    private final MainActivity activity;

    ClipboardDocumentCreator(MainActivity activity) {
        this.activity = activity;
    }

    void createMarkdownFromClipboard() {
        ClipData clip = clipboardClip();
        List<ClipboardMarkdownItem> items = clipboardMarkdownItems(clip);
        if (items.isEmpty()) {
            activity.showFileOpenError(activity.viewerText.noClipboardText());
            return;
        }
        if (items.size() == 1) {
            openClipboardMarkdownItem(items.get(0));
            return;
        }
        showClipboardItemPicker(items);
    }

    void createMarkdownFromSelectedText(CharSequence selectedText) {
        if (selectedText == null || selectedText.length() == 0) {
            activity.showFileOpenError(activity.viewerText.noTextToCreate());
            return;
        }
        openSelectedTextMarkdown("Selected text", AndroidStyledTextMarkdown.from(selectedText));
    }

    void openSelectedClipboardItems(List<ClipboardMarkdownItem> items, boolean[] selected) {
        boolean opened = false;
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                openClipboardMarkdownItem(items.get(i));
                opened = true;
            }
        }
        if (!opened) {
            activity.showFileOpenError(activity.viewerText.noClipboardItemSelected());
        }
    }

    private List<ClipboardMarkdownItem> clipboardMarkdownItems(ClipData clip) {
        ClipboardMarkdownItems items = ClipboardMarkdownItems.empty();
        appendCurrentClipboardItems(items, clip);
        appendClipboardHistoryItems(items);
        return items.asList();
    }

    private void appendCurrentClipboardItems(ClipboardMarkdownItems items, ClipData clip) {
        if (clip == null) {
            return;
        }
        for (int i = 0; i < clip.getItemCount(); i++) {
            CharSequence text = clip.getItemAt(i).coerceToStyledText(activity);
            String title = i == 0 ? "Clipboard" : "Clipboard " + (i + 1);
            items.appendCurrent(title, text);
        }
    }

    private void appendClipboardHistoryItems(ClipboardMarkdownItems items) {
        List<String> history = activity.clipboardHistoryStore.load();
        for (int i = 0; i < history.size(); i++) {
            items.appendHistory(activity.viewerText.historyClipboardTitle(i), history.get(i));
        }
    }

    private void showClipboardItemPicker(List<ClipboardMarkdownItem> items) {
        boolean[] selected = new boolean[items.size()];
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.clipboardItemsToOpen())
                // interaction-surface: clipboard-item-picker
                .setMultiChoiceItems(clipboardItemLabels(items), selected,
                        new ClipboardItemCheckedListener(selected))
                .setPositiveButton(activity.viewerText.openSelected(),
                        new ClipboardItemsOpenListener(this, items, selected))
                .setNegativeButton(activity.viewerText.cancel(), null)
                .show();
    }

    private String[] clipboardItemLabels(List<ClipboardMarkdownItem> items) {
        String[] labels = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            String preview = items.get(i).markdown().replace('\n', ' ').trim();
            String clipped = preview.length() > 48 ? preview.substring(0, 48) + "..." : preview;
            labels[i] = items.get(i).title() + ": " + clipped;
        }
        return labels;
    }

    private void openClipboardMarkdownItem(ClipboardMarkdownItem item) {
        activity.clipboardHistoryStore.record(item.markdown());
        openClipboardDraftMarkdown(item.title(), item.markdown());
    }

    private void openClipboardDraftMarkdown(String title, String markdown) {
        DraftMarkdownDocument draft = draftMarkdownDocument(title, markdown);
        if (draft == null) {
            return;
        }
        activity.draftMarkdownByUri.put(draft.uri(), draft.markdown());
        activity.openTabs = activity.openTabs.open(
                OpenDocumentTab.clipboardDraft(draft.displayName(), draft.uri(), draft.rendered()));
        activity.updateLocalizedText();
        activity.renderTabs();
        activity.renderCurrentDocument();
    }

    private void openSelectedTextMarkdown(String title, String markdown) {
        DraftMarkdownDocument draft = draftMarkdownDocument(title, markdown);
        if (draft == null) {
            return;
        }
        activity.draftMarkdownByUri.put(draft.uri(), draft.markdown());
        activity.openTabs = activity.openTabs.open(
                OpenDocumentTab.selectedTextDraft(draft.displayName(), draft.uri(), draft.rendered()));
        activity.updateLocalizedText();
        activity.renderTabs();
        activity.renderCurrentDocument();
    }

    private DraftMarkdownDocument draftMarkdownDocument(String title, String markdown) {
        String text = markdown == null ? "" : markdown;
        long sizeBytes = text.getBytes(StandardCharsets.UTF_8).length;
        MarkdownFileOpenResult openResult = MarkdownFileOpenResult.from(
                title + ".md", sizeBytes, activity.fileSizePolicy);
        if (openResult instanceof MarkdownFileOpenResult.OversizedMarkdownFile) {
            activity.showFileOpenError(activity.viewerText.fileTooLarge());
            return null;
        }

        String displayName = nextDraftDisplayName(title);
        String draftUri = MainActivity.DRAFT_URI_PREFIX + Uri.encode(displayName);
        SafeHtml rendered = activity.renderMarkdownForUri(draftUri, text);
        return new DraftMarkdownDocument(displayName, draftUri, text, rendered);
    }

    private String nextDraftDisplayName(String title) {
        int sequence = 1;
        String displayName = MarkdownDraftFileName.fromTitle(title, sequence).value();
        while (activity.draftMarkdownByUri.containsKey(
                MainActivity.DRAFT_URI_PREFIX + Uri.encode(displayName))) {
            sequence++;
            displayName = MarkdownDraftFileName.fromTitle(title, sequence).value();
        }
        return displayName;
    }

    private ClipData clipboardClip() {
        ClipboardManager clipboard =
                (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null || !clipboard.hasPrimaryClip()) {
            return null;
        }
        ClipData clip = clipboard.getPrimaryClip();
        if (clip == null || clip.getItemCount() == 0) {
            return null;
        }
        return clip;
    }

    private static final class ClipboardItemCheckedListener
            implements DialogInterface.OnMultiChoiceClickListener {
        private final boolean[] selected;

        private ClipboardItemCheckedListener(boolean[] selected) {
            this.selected = selected;
        }

        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            selected[which] = isChecked;
        }
    }

    private static final class ClipboardItemsOpenListener
            implements DialogInterface.OnClickListener {
        private final ClipboardDocumentCreator creator;
        private final List<ClipboardMarkdownItem> items;
        private final boolean[] selected;

        private ClipboardItemsOpenListener(ClipboardDocumentCreator creator,
                List<ClipboardMarkdownItem> items, boolean[] selected) {
            this.creator = creator;
            this.items = items;
            this.selected = selected;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            creator.openSelectedClipboardItems(items, selected);
        }
    }
}
