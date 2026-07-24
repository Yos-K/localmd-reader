package io.github.yosk.mdlite.presentation;

import android.content.Intent;
import android.net.Uri;

final class ActivityResultRouter {
    private final MainActivity activity;

    ActivityResultRouter(MainActivity activity) {
        this.activity = activity;
    }

    void handle(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.REQUEST_OPEN_DOCUMENT
                && resultCode == MainActivity.RESULT_OK && data != null) {
            activity.documentOpener.openSelectedDocuments(data);
            return;
        }
        if (requestCode == MainActivity.REQUEST_OPEN_FOLDER
                && resultCode == MainActivity.RESULT_OK && data != null) {
            activity.documentOpener.openSelectedFolder(data);
            return;
        }
        if (requestCode == MainActivity.REQUEST_SAVE_DOCUMENT
                && resultCode == MainActivity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                activity.writePendingMarkdown(uri);
            }
            return;
        }
        if (requestCode == MainActivity.REQUEST_EXPORT_HTML
                && resultCode == MainActivity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                activity.writePendingHtml(uri);
            }
        }
    }
}
