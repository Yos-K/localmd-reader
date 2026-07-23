package io.github.yosk.mdlite.presentation;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.file.FileInfo;
import io.github.yosk.mdlite.file.MarkdownLibraryLocation;
import io.github.yosk.mdlite.file.MarkdownFileOpenResult;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.SavedDocumentPlacement;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class DocumentOpener {
    private final MainActivity activity;
    private final FolderDocumentReader folderDocumentReader;
    private final DocumentContentReader documentContentReader;
    private final ProjectLibraryOpener projectLibraryOpener;

    DocumentOpener(MainActivity activity) {
        this.activity = activity;
        this.folderDocumentReader = new FolderDocumentReader(activity.getContentResolver());
        this.documentContentReader = new DocumentContentReader(activity.getContentResolver());
        this.projectLibraryOpener = new ProjectLibraryOpener(activity, folderDocumentReader);
    }

    void openMarkdownPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("*/*");
        activity.startActivityForResult(intent, MainActivity.REQUEST_OPEN_DOCUMENT);
    }

    void openMarkdownLibrary() {
        projectLibraryOpener.open();
    }

    void chooseAnotherFolder() {
        projectLibraryOpener.chooseAnotherRoot();
    }

    void openSelectedFolder(Intent data) {
        Uri treeUri = data.getData();
        if (treeUri == null) {
            return;
        }
        persistReadPermission(data, treeUri);
        projectLibraryOpener.openSelectedRoot(treeUri.toString());
    }

    void openProjectLibrary(MarkdownLibraryLocation location) {
        projectLibraryOpener.open(location);
    }

    void openSelectedDocuments(Intent data) {
        ClipData clipData = data.getClipData();
        if (clipData != null && clipData.getItemCount() > 0) {
            openClipDataUris(clipData, true, data);
            return;
        }
        Uri uri = data.getData();
        if (uri != null) {
            persistReadPermission(data, uri);
            openUri(uri, true);
        }
    }

    void openClipDataUris(ClipData clipData, boolean remember, Intent permissionIntent) {
        for (int i = 0; i < clipData.getItemCount(); i++) {
            Uri uri = clipData.getItemAt(i).getUri();
            if (uri != null) {
                persistReadPermission(permissionIntent, uri);
                openUri(uri, remember);
            }
        }
    }

    void handleIncomingIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        activity.setIntent(new Intent());
        String action = intent.getAction();
        Uri uri = intent.getData();
        if (Intent.ACTION_VIEW.equals(action) && uri != null) {
            persistReadPermission(intent, uri);
            openUri(uri, true);
            return;
        }
        if (Intent.ACTION_SEND.equals(action)) {
            Uri sharedUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (sharedUri != null) {
                persistReadPermission(intent, sharedUri);
                openUri(sharedUri, true);
                return;
            }
            ClipData clipData = intent.getClipData();
            if (clipData != null) {
                openClipDataUris(clipData, true, intent);
            }
            return;
        }
        if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            ArrayList<Uri> sharedUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            if (sharedUris != null) {
                openUris(sharedUris, true, intent);
                return;
            }
            ClipData clipData = intent.getClipData();
            if (clipData != null) {
                openClipDataUris(clipData, true, intent);
            }
            return;
        }
        if (Intent.ACTION_PROCESS_TEXT.equals(action)) {
            activity.clipboardDocumentCreator.createMarkdownFromSelectedText(
                    intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT));
            return;
        }
        if (MainActivity.ACTION_OPEN_TEXT.equals(action)) {
            openMarkdownText(
                    intent.getStringExtra(MainActivity.EXTRA_MARKDOWN_TITLE),
                    intent.getStringExtra(MainActivity.EXTRA_MARKDOWN_SOURCE),
                    intent.getStringExtra(MainActivity.EXTRA_MARKDOWN_TEXT));
            return;
        }
        if (MainActivity.ACTION_OPEN_TEXTS.equals(action)) {
            openMarkdownTexts(
                    intent.getStringArrayExtra(MainActivity.EXTRA_MARKDOWN_TITLES),
                    intent.getStringArrayExtra(MainActivity.EXTRA_MARKDOWN_SOURCES),
                    intent.getStringArrayExtra(MainActivity.EXTRA_MARKDOWN_TEXTS_BASE64));
        }
    }

    void openUri(Uri uri, boolean remember) {
        openUri(uri, remember, "");
    }

    void openUri(Uri uri, boolean remember, String targetAnchorId) {
        openUri(uri, remember, targetAnchorId, SavedDocumentPlacement.openNormally());
    }

    void openSavedUri(Uri uri, SavedDocumentPlacement placement, boolean remember) {
        openUri(uri, remember, "", placement);
    }

    private void openUri(
            Uri uri,
            boolean remember,
            String targetAnchorId,
            SavedDocumentPlacement placement) {
        FileInfo fileInfo = readFileInfo(uri);
        MarkdownFileOpenResult openResult = MarkdownFileOpenResult.from(
                fileInfo.displayName, fileInfo.sizeBytes, activity.fileSizePolicy);
        if (openResult instanceof MarkdownFileOpenResult.UnsupportedMarkdownFile) {
            activity.showFileOpenError(activity.viewerText.unsupportedFile());
            return;
        }
        if (openResult instanceof MarkdownFileOpenResult.OversizedMarkdownFile) {
            activity.showFileOpenError(activity.viewerText.fileTooLarge());
            return;
        }

        MarkdownFileOpenResult.ReadableMarkdownFile readableFile =
                (MarkdownFileOpenResult.ReadableMarkdownFile) openResult;
        try {
            String markdown = readText(uri, MainActivity.MAX_FILE_SIZE_BYTES);
            String documentUri = uri.toString();
            SafeHtml rendered = activity.renderMarkdownForUri(documentUri, markdown);
            activity.documentTabSession.openSavedFile(
                    OpenDocumentTab.fileDocument(readableFile.displayName(), documentUri, rendered),
                    placement);
            activity.updateLocalizedText();
            activity.renderTabs();
            activity.renderCurrentDocument(targetAnchorId);
            activity.saveOpenTabs();
            if (remember) {
                activity.tabPersistence.recordRecentDocument(readableFile.displayName(), uri.toString());
            }
            activity.clearMessage();
        } catch (IOException e) {
            activity.showFileOpenError(activity.viewerText.unreadableFile());
        }
    }

    void openMarkdownText(String title, String source, String markdown) {
        String displayName = title == null || title.length() == 0 ? "Termux.md" : title;
        String sourceId = source == null || source.length() == 0 ? displayName : source;
        String text = markdown == null ? "" : markdown;
        long sizeBytes = text.getBytes(StandardCharsets.UTF_8).length;
        MarkdownFileOpenResult openResult = MarkdownFileOpenResult.from(
                displayName, sizeBytes, activity.fileSizePolicy);
        if (openResult instanceof MarkdownFileOpenResult.UnsupportedMarkdownFile) {
            activity.showFileOpenError(activity.viewerText.unsupportedFile());
            return;
        }
        if (openResult instanceof MarkdownFileOpenResult.OversizedMarkdownFile) {
            activity.showFileOpenError(activity.viewerText.fileTooLarge());
            return;
        }

        MarkdownFileOpenResult.ReadableMarkdownFile readableFile =
                (MarkdownFileOpenResult.ReadableMarkdownFile) openResult;
        String uri = "termux://open/" + Uri.encode(sourceId);
        SafeHtml rendered = activity.renderMarkdownForUri(uri, text);
        activity.documentTabSession.open(
                OpenDocumentTab.fileDocument(readableFile.displayName(), uri, rendered));
        activity.updateLocalizedText();
        activity.renderTabs();
        activity.renderCurrentDocument();
        activity.saveOpenTabs();
        activity.clearMessage();
    }

    FileInfo readFileInfo(Uri uri) {
        return documentContentReader.readFileInfo(uri);
    }

    String readText(Uri uri, long maxBytes) throws IOException {
        return documentContentReader.readText(uri, maxBytes);
    }

    private void openUris(List<Uri> uris, boolean remember, Intent permissionIntent) {
        for (Uri uri : uris) {
            if (uri != null) {
                persistReadPermission(permissionIntent, uri);
                openUri(uri, remember);
            }
        }
    }

    private void openMarkdownTexts(String[] titles, String[] sources, String[] textsBase64) {
        if (titles == null || sources == null || textsBase64 == null) {
            return;
        }
        int count = Math.min(titles.length, Math.min(sources.length, textsBase64.length));
        for (int i = 0; i < count; i++) {
            openMarkdownText(titles[i], sources[i], decodeBase64Text(textsBase64[i]));
        }
    }

    private String decodeBase64Text(String encoded) {
        if (encoded == null) {
            return "";
        }
        byte[] bytes = Base64.decode(encoded, Base64.DEFAULT);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private void persistReadPermission(Intent data, Uri uri) {
        if ((data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION) == 0) {
            return;
        }
        try {
            activity.getContentResolver().takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException e) {
        }
    }

}
