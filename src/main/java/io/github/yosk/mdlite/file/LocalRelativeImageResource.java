package io.github.yosk.mdlite.file;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Locale;

public final class LocalRelativeImageResource {
    private static final String RELATIVE_IMAGE_REQUEST_PATH = "/__relative_image__";
    private static final LocalRelativeImageResource UNAVAILABLE =
            new LocalRelativeImageResource(false, "", "");

    private final boolean available;
    private final String filePath;
    private final String mimeType;

    private LocalRelativeImageResource(boolean available, String filePath, String mimeType) {
        this.available = available;
        this.filePath = filePath;
        this.mimeType = mimeType;
    }

    public static LocalRelativeImageResource unavailable() {
        return UNAVAILABLE;
    }

    public static LocalRelativeImageResource resolve(String markdownDocumentUri, String requestUrl) {
        return resolve(markdownDocumentUri, requestUrl, null);
    }

    public static LocalRelativeImageResource resolve(String markdownDocumentUri, String requestUrl, String allowedRootPath) {
        URI document = parseUri(markdownDocumentUri);
        URI request = parseUri(requestUrl);
        if (document == null || request == null) {
            return unavailable();
        }
        if (!"file".equals(document.getScheme())) {
            return unavailable();
        }
        if (!"https".equals(request.getScheme()) || !"localmd.local".equals(request.getHost())) {
            return unavailable();
        }
        String requestPath = decodedRequestPath(request);
        if (containsEncodedTraversal(requestPath)) {
            return unavailable();
        }
        String mimeType = mimeTypeFor(requestPath);
        if (mimeType.length() == 0) {
            return unavailable();
        }
        File documentFile = new File(document.getPath() == null ? "" : document.getPath());
        File documentDirectory = documentFile.getParentFile();
        if (documentDirectory == null) {
            return unavailable();
        }
        File allowedRoot = allowedRootPath == null || allowedRootPath.length() == 0
                ? documentDirectory
                : new File(allowedRootPath);
        File resolved = new File(documentDirectory, requestPath);
        if (!isInside(allowedRoot, resolved)) {
            return unavailable();
        }
        String resolvedPath = normalizedAbsolutePath(resolved);
        if (resolvedPath.length() == 0) {
            return unavailable();
        }
        return new LocalRelativeImageResource(true, resolvedPath, mimeType);
    }

    public boolean isAvailable() {
        return available;
    }

    public String filePath() {
        return filePath;
    }

    public String mimeType() {
        return mimeType;
    }

    private static URI parseUri(String value) {
        try {
            return value == null ? null : URI.create(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String decodedRequestPath(URI request) {
        String path = request.getRawPath();
        if (RELATIVE_IMAGE_REQUEST_PATH.equals(path)) {
            return decodedRelativeImageQueryPath(request.getRawQuery());
        }
        if (path == null || path.length() == 0 || "/".equals(path)) {
            return "";
        }
        String relative = path.startsWith("/") ? path.substring(1) : path;
        try {
            return URLDecoder.decode(relative, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private static String decodedRelativeImageQueryPath(String rawQuery) {
        if (rawQuery == null || !rawQuery.startsWith("path=")) {
            return "";
        }
        String encodedPath = rawQuery.substring("path=".length());
        int separator = encodedPath.indexOf('&');
        if (separator >= 0) {
            encodedPath = encodedPath.substring(0, separator);
        }
        try {
            return URLDecoder.decode(encodedPath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private static boolean isInside(File root, File candidate) {
        try {
            String rootPath = root.getCanonicalPath();
            String candidatePath = candidate.getCanonicalPath();
            return candidatePath.equals(rootPath) || candidatePath.startsWith(rootPath + File.separator);
        } catch (java.io.IOException e) {
            return false;
        }
    }

    private static String normalizedAbsolutePath(File file) {
        return new File(file.getAbsolutePath()).toURI().normalize().getPath();
    }

    private static boolean containsEncodedTraversal(String path) {
        String lower = path.toLowerCase(Locale.ROOT);
        return lower.contains("%2e") || lower.contains("%2f") || lower.contains("%5c");
    }

    private static String mimeTypeFor(String path) {
        String lower = path.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "";
    }
}
