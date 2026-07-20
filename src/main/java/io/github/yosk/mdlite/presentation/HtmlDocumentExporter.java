package io.github.yosk.mdlite.presentation;

import android.content.Intent;
import android.net.Uri;

import io.github.yosk.mdlite.domain.TableReadingMode;
import io.github.yosk.mdlite.infrastructure.HtmlPageBuilder;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

final class HtmlDocumentExporter {
    private final MainActivity activity;

    HtmlDocumentExporter(MainActivity activity) {
        this.activity = activity;
    }

    void exportActiveDocument() {
        OpenDocumentTab tab = activity.openTabs.activeTab();
        activity.pendingExportHtml = HtmlPageBuilder.buildPage(
                tab.document(),
                activity.currentTheme,
                activity.currentFontSize,
                TableReadingMode.fromEntitlement(activity.featureEntitlement));

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_TITLE, htmlFileName(tab.title()));
        activity.startActivityForResult(intent, MainActivity.REQUEST_EXPORT_HTML);
    }

    void writePendingHtml(Uri uri) {
        try {
            OutputStream output = activity.getContentResolver().openOutputStream(uri);
            if (output == null) {
                activity.showFileOpenError(activity.viewerText.htmlExportFailed());
                return;
            }
            try {
                output.write(activity.pendingExportHtml.getBytes(StandardCharsets.UTF_8));
            } finally {
                output.close();
            }
            activity.pendingExportHtml = "";
            activity.showInfoDialog(activity.viewerText.exportAsHtml(), activity.viewerText.exportedHtml());
        } catch (IOException e) {
            activity.showFileOpenError(activity.viewerText.htmlExportFailed());
        }
    }

    private static String htmlFileName(String title) {
        String lowerTitle = title.toLowerCase(Locale.ROOT);
        if (lowerTitle.endsWith(".markdown")) {
            return title.substring(0, title.length() - ".markdown".length()) + ".html";
        }
        if (lowerTitle.endsWith(".md")) {
            return title.substring(0, title.length() - ".md".length()) + ".html";
        }
        return title + ".html";
    }
}
