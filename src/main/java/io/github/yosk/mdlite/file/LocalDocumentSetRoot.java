package io.github.yosk.mdlite.file;

import java.io.File;
import java.net.URI;

public final class LocalDocumentSetRoot {
    private static final LocalDocumentSetRoot UNAVAILABLE = new LocalDocumentSetRoot("");

    private final String path;

    private LocalDocumentSetRoot(String path) {
        this.path = path;
    }

    public static LocalDocumentSetRoot fromDocumentUri(String documentUri) {
        try {
            URI document = URI.create(documentUri == null ? "" : documentUri);
            if (!"file".equals(document.getScheme())) {
                return UNAVAILABLE;
            }
            File documentFile = new File(document.getPath() == null ? "" : document.getPath());
            File documentDirectory = documentFile.getParentFile();
            File documentSetRoot = documentDirectory == null ? null : documentDirectory.getParentFile();
            return documentSetRoot == null
                    ? UNAVAILABLE
                    : new LocalDocumentSetRoot(documentSetRoot.getAbsolutePath());
        } catch (IllegalArgumentException e) {
            return UNAVAILABLE;
        }
    }

    public String path() {
        return path;
    }
}
