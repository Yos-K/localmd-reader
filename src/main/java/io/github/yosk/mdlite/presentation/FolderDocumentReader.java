package io.github.yosk.mdlite.presentation;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import io.github.yosk.mdlite.file.FileTypeDetector;
import io.github.yosk.mdlite.file.FolderDocumentEntry;
import io.github.yosk.mdlite.file.MarkdownLibraryLocation;
import java.util.ArrayList;
import java.util.List;

final class FolderDocumentReader {
    private final ContentResolver contentResolver;

    FolderDocumentReader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    List<FolderDocumentEntry> entries(Uri treeUri) {
        return entries(treeUri, DocumentsContract.getTreeDocumentId(treeUri));
    }

    String rootDisplayName(Uri treeUri) {
        String rootId = DocumentsContract.getTreeDocumentId(treeUri);
        Uri rootUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, rootId);
        Cursor cursor = contentResolver.query(rootUri,
                new String[] { DocumentsContract.Document.COLUMN_DISPLAY_NAME },
                null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            return cursor.moveToFirst() ? cursor.getString(0) : null;
        } finally {
            cursor.close();
        }
    }

    List<FolderDocumentEntry> entries(MarkdownLibraryLocation location) {
        Uri treeUri = Uri.parse(location.treeUri());
        String directoryId = location instanceof MarkdownLibraryLocation.RootLocation
                ? DocumentsContract.getTreeDocumentId(treeUri)
                : DocumentsContract.getDocumentId(Uri.parse(location.directoryUri()));
        return entries(treeUri, directoryId);
    }

    private List<FolderDocumentEntry> entries(Uri treeUri, String directoryId) {
        ArrayList<FolderDocumentEntry> entries = new ArrayList<FolderDocumentEntry>();
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                treeUri, directoryId);
        Cursor cursor = contentResolver.query(childrenUri,
                new String[] {
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                        DocumentsContract.Document.COLUMN_MIME_TYPE,
                        DocumentsContract.Document.COLUMN_DOCUMENT_ID
                },
                null, null, null);
        if (cursor == null) {
            return entries;
        }
        try {
            while (cursor.moveToNext()) {
                FolderDocumentEntry entry = entry(treeUri, cursor);
                if (entry != null) {
                    entries.add(entry);
                }
            }
        } finally {
            cursor.close();
        }
        return entries;
    }

    private FolderDocumentEntry entry(Uri treeUri, Cursor cursor) {
        String displayName = cursor.getString(0);
        String mimeType = cursor.getString(1);
        String documentId = cursor.getString(2);
        if (displayName == null || displayName.trim().length() == 0
                || documentId == null || documentId.trim().length() == 0) {
            return null;
        }
        Uri documentUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId);
        if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType)) {
            return FolderDocumentEntry.directory(displayName, documentUri.toString());
        }
        if (FileTypeDetector.isMarkdownDisplayName(displayName)) {
            return FolderDocumentEntry.markdownFile(displayName, documentUri.toString());
        }
        return FolderDocumentEntry.unsupportedFile(displayName, documentUri.toString());
    }
}
