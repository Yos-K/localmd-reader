package io.github.yosk.mdlite.presentation;

import android.content.Intent;
import android.net.Uri;
import io.github.yosk.mdlite.file.PersistableReadPermission;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.SavedDocumentPlacement;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

final class DocumentSaver {
    private final MainActivity activity;

    DocumentSaver(MainActivity activity) {
        this.activity = activity;
    }

    void saveActiveMarkdownAs() {
        OpenDocumentTab tab = activity.openTabs().activeTab();
        if (MainActivity.WELCOME_URI.equals(tab.uri())) {
            activity.showFileOpenError(activity.viewerText.noDocumentToSave());
            return;
        }
        String markdown = markdownForSave(tab);
        if (markdown.length() == 0) {
            activity.showFileOpenError(activity.viewerText.noDocumentToSave());
            return;
        }
        activity.pendingSaveMarkdown = markdown;
        activity.pendingSavePlacement = SavedDocumentPlacement.from(tab);
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.setType("text/markdown");
        intent.putExtra(Intent.EXTRA_TITLE, tab.title());
        activity.startActivityForResult(intent, MainActivity.REQUEST_SAVE_DOCUMENT);
    }

    void persistSavedDocumentReadPermission(Intent data, Uri uri) {
        PersistableReadPermission permission = PersistableReadPermission.fromResultFlags(
                data.getFlags(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (!permission.isGranted()) {
            return;
        }
        try {
            activity.getContentResolver().takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException ignored) {
            // Some document providers return a usable URI without persistable access.
        }
    }

    void writePendingMarkdown(Uri uri) {
        try {
            OutputStream output = activity.getContentResolver().openOutputStream(uri);
            if (output == null) {
                activity.showFileOpenError(activity.viewerText.createMarkdownFailed());
                return;
            }
            try {
                output.write(activity.pendingSaveMarkdown.getBytes(StandardCharsets.UTF_8));
            } finally {
                output.close();
            }
            SavedDocumentPlacement placement = activity.pendingSavePlacement;
            activity.pendingSaveMarkdown = "";
            activity.pendingSavePlacement = SavedDocumentPlacement.openNormally();
            activity.documentOpener.openSavedUri(uri, placement, true);
            activity.showSavedMarkdownMessage();
        } catch (IOException e) {
            activity.showFileOpenError(activity.viewerText.createMarkdownFailed());
        }
    }

    private String markdownForSave(OpenDocumentTab tab) {
        if (tab instanceof OpenDocumentTab.DraftDocumentTab) {
            String markdown = activity.draftMarkdownByUri.get(tab.uri());
            return markdown == null ? "" : markdown;
        }
        try {
            return activity.documentOpener.readText(Uri.parse(tab.uri()), MainActivity.MAX_FILE_SIZE_BYTES);
        } catch (IOException e) {
            return "";
        } catch (SecurityException e) {
            return "";
        }
    }

}
