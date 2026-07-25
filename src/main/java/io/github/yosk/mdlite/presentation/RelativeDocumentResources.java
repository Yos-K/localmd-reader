package io.github.yosk.mdlite.presentation;

import android.net.Uri;
import android.webkit.WebResourceResponse;
import io.github.yosk.mdlite.file.LocalDocumentSetRoot;
import io.github.yosk.mdlite.file.LocalRelativeImageResource;
import io.github.yosk.mdlite.file.LocalRelativeMarkdownLink;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

final class RelativeDocumentResources {
    private final MainActivity activity;

    RelativeDocumentResources(MainActivity activity) {
        this.activity = activity;
    }

    WebResourceResponse openImage(String requestUrl) {
        if (!activity.documentRenderingProfile.relativeImageRendering().isEnabled()) {
            return null;
        }
        OpenDocumentTab.FileDocumentTab activeFile = activeFileTab();
        if (activeFile == null) {
            return null;
        }
        LocalRelativeImageResource resource = LocalRelativeImageResource.resolve(
                activeFile.uri(),
                requestUrl,
                LocalDocumentSetRoot.fromDocumentUri(activeFile.uri()).path());
        if (!resource.isAvailable()) {
            return null;
        }
        try {
            return new WebResourceResponse(
                    resource.mimeType(), null, new FileInputStream(resource.filePath()));
        } catch (IOException e) {
            return null;
        }
    }

    boolean openMarkdown(String requestUrl) {
        if (!activity.documentRenderingProfile.relativeLinkRendering().isEnabled()) {
            return false;
        }
        OpenDocumentTab.FileDocumentTab activeFile = activeFileTab();
        if (activeFile == null) {
            return false;
        }
        LocalRelativeMarkdownLink link = LocalRelativeMarkdownLink.resolve(
                activeFile.uri(),
                requestUrl,
                LocalDocumentSetRoot.fromDocumentUri(activeFile.uri()).path());
        if (!link.isAvailable()) {
            return false;
        }
        activity.documentOpener.openUri(
                Uri.fromFile(new File(link.filePath())), true, link.targetAnchorId());
        return true;
    }

    private OpenDocumentTab.FileDocumentTab activeFileTab() {
        if (activity.documentTabSession == null
                || !(activity.openTabs().activeTab() instanceof OpenDocumentTab.FileDocumentTab)) {
            return null;
        }
        return (OpenDocumentTab.FileDocumentTab) activity.openTabs().activeTab();
    }
}
