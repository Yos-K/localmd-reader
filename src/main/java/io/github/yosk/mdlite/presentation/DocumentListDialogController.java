package io.github.yosk.mdlite.presentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import io.github.yosk.mdlite.file.PinnedDocuments;
import io.github.yosk.mdlite.file.RecentDocument;
import io.github.yosk.mdlite.file.RecentDocuments;
import io.github.yosk.mdlite.model.DocumentListCommand;
import io.github.yosk.mdlite.model.DocumentListDialogState;
import java.util.List;

final class DocumentListDialogController implements DialogInterface.OnClickListener,
        DialogInterface.OnDismissListener, DocumentListCommand.Handler {
    private final MainActivity activity;
    private DocumentListDialogState state = DocumentListDialogState.closed();

    DocumentListDialogController(MainActivity activity) {
        this.activity = activity;
    }

    // interaction-surface: recent-files-dialog
    void showRecentDocuments() {
        RecentDocuments documents = activity.tabPersistence.loadRecentDocuments();
        if (documents.items().isEmpty()) {
            showEmptyDialog(activity.viewerText.recentFiles(), activity.viewerText.noRecentFiles());
            return;
        }
        state = DocumentListDialogState.recent(documents);
        showDialog(dialogBuilder(activity.viewerText.recentFiles())
                // interaction-surface: recent-files-dialog
                .setItems(labels(documents.items()), this)
                .setNegativeButton(activity.viewerText.clearHistory(), this)
                .setPositiveButton(activity.viewerText.close(), null));
    }

    // interaction-surface: pinned-files-dialog
    void showPinnedDocuments() {
        PinnedDocuments documents = activity.tabPersistence.loadPinnedDocuments();
        if (documents.items().isEmpty()) {
            showEmptyDialog(activity.viewerText.pinnedFiles(), activity.viewerText.noPinnedFiles());
            return;
        }
        state = DocumentListDialogState.pinned(documents);
        showDialog(dialogBuilder(activity.viewerText.pinnedFiles())
                // interaction-surface: pinned-files-dialog
                .setItems(labels(documents.items()), this)
                .setNegativeButton(activity.viewerText.clearPinnedFiles(), this)
                .setPositiveButton(activity.viewerText.close(), null));
    }

    @Override
    // interaction-command: open_recent_file
    // interaction-command: clear_recent_files
    // interaction-command: open_pinned_file
    // interaction-command: clear_all_pins
    public void onClick(DialogInterface dialog, int which) {
        DocumentListCommand command = which == DialogInterface.BUTTON_NEGATIVE
                ? state.secondaryAction() : state.select(which);
        state = state.close();
        command.execute(this);
    }

    @Override
    // interaction-command: close_dialog
    public void onDismiss(DialogInterface dialog) {
        state = state.close();
    }

    @Override
    public void none() {
    }

    @Override
    public void openDocument(RecentDocument document) {
        activity.documentOpener.openUri(Uri.parse(document.uri()), true);
    }

    @Override
    public void clearRecent() {
        activity.tabPersistence.clearRecentDocuments();
        activity.showInfoDialog(activity.viewerText.recentFiles(),
                activity.viewerText.recentFilesCleared());
    }

    @Override
    public void clearPinned() {
        activity.tabPersistence.clearPinnedDocuments();
        activity.showInfoDialog(activity.viewerText.pinnedFiles(),
                activity.viewerText.pinnedFilesCleared());
    }

    private void showEmptyDialog(String title, String message) {
        state = state.close();
        showDialog(dialogBuilder(title)
                .setMessage(message)
                .setPositiveButton(activity.viewerText.close(), null));
    }

    private AlertDialog.Builder dialogBuilder(String title) {
        return new AlertDialog.Builder(activity).setTitle(title);
    }

    private void showDialog(AlertDialog.Builder builder) {
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    private static String[] labels(List<RecentDocument> documents) {
        String[] labels = new String[documents.size()];
        for (int i = 0; i < documents.size(); i++) {
            labels[i] = documents.get(i).displayName();
        }
        return labels;
    }

}
