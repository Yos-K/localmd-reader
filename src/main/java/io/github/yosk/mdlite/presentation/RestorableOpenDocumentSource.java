package io.github.yosk.mdlite.presentation;

import android.net.Uri;
import io.github.yosk.mdlite.file.FileInfo;
import io.github.yosk.mdlite.model.RestorableOpenDocumentLoader;
import java.io.IOException;

final class RestorableOpenDocumentSource implements RestorableOpenDocumentLoader.Source {
    private final DocumentOpener documentOpener;

    RestorableOpenDocumentSource(DocumentOpener documentOpener) {
        this.documentOpener = documentOpener;
    }

    @Override
    public String displayName(String uri) {
        return fileInfo(uri).displayName;
    }

    @Override
    public long sizeBytes(String uri) {
        return fileInfo(uri).sizeBytes;
    }

    @Override
    public String readMarkdown(String uri, long maximumBytes) throws IOException {
        return documentOpener.readText(Uri.parse(uri), maximumBytes);
    }

    private FileInfo fileInfo(String uri) {
        return documentOpener.readFileInfo(Uri.parse(uri));
    }
}
