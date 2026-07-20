package io.github.yosk.mdlite.presentation;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import io.github.yosk.mdlite.file.FileInfo;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

final class DocumentContentReader {
    private final ContentResolver contentResolver;

    DocumentContentReader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    FileInfo readFileInfo(Uri uri) {
        if ("file".equals(uri.getScheme())) {
            File file = new File(uri.getPath() == null ? "" : uri.getPath());
            return new FileInfo(file.getName(), file.length());
        }

        String displayName = "";
        long size = -1;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    displayName = cursor.getString(nameIndex);
                }
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                    size = cursor.getLong(sizeIndex);
                }
            } finally {
                cursor.close();
            }
        }

        if (displayName == null || displayName.length() == 0) {
            String lastSegment = uri.getLastPathSegment();
            displayName = lastSegment == null ? "" : lastSegment;
        }
        return new FileInfo(displayName, size);
    }

    String readText(Uri uri, long maxBytes) throws IOException {
        InputStream input = openInputStream(uri);
        if (input == null) {
            throw new IOException("document input stream unavailable");
        }
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            long total = 0;
            int read;
            while ((read = input.read(buffer)) != -1) {
                total += read;
                if (total > maxBytes) {
                    throw new IOException("document too large");
                }
                output.write(buffer, 0, read);
            }
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        } finally {
            input.close();
        }
    }

    private InputStream openInputStream(Uri uri) throws IOException {
        if ("file".equals(uri.getScheme())) {
            return new FileInputStream(new File(uri.getPath() == null ? "" : uri.getPath()));
        }
        return contentResolver.openInputStream(uri);
    }
}
