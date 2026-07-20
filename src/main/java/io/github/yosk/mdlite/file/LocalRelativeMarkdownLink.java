package io.github.yosk.mdlite.file;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Locale;

public final class LocalRelativeMarkdownLink {
    private static final String RELATIVE_MARKDOWN_REQUEST_PATH = "/__relative_markdown__";
    private static final LocalRelativeMarkdownLink UNAVAILABLE =
            new LocalRelativeMarkdownLink(false, "", "");

    private final boolean available;
    private final String filePath;
    private final String targetAnchorId;

    private LocalRelativeMarkdownLink(boolean available, String filePath, String targetAnchorId) {
        this.available = available;
        this.filePath = filePath;
        this.targetAnchorId = targetAnchorId == null ? "" : targetAnchorId;
    }

    public static LocalRelativeMarkdownLink unavailable() {
        return UNAVAILABLE;
    }

    public static LocalRelativeMarkdownLink resolve(
            String markdownDocumentUri,
            String requestUrl,
            String allowedRootPath) {
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
        String requestPath = markdownPathWithoutFragment(decodedRequestPath(request));
        if (!FileTypeDetector.isMarkdownDisplayName(requestPath)) {
            return unavailable();
        }
        if (containsEncodedTraversal(requestPath)) {
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
        return new LocalRelativeMarkdownLink(true, resolvedPath, decodedFragment(request));
    }

    public boolean isAvailable() {
        return available;
    }

    public String filePath() {
        return filePath;
    }

    public String targetAnchorId() {
        return targetAnchorId;
    }

    private static URI parseUri(String value) {
        try {
            return value == null ? null : URI.create(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String decodedRequestPath(URI request) {
        if (RELATIVE_MARKDOWN_REQUEST_PATH.equals(request.getRawPath())) {
            return decodedRelativeMarkdownQueryPath(request.getRawQuery());
        }
        String path = request.getRawPath();
        if (path == null || path.length() == 0 || "/".equals(path)) {
            return "";
        }
        String decodedPath = request.getPath();
        String relative = decodedPath.startsWith("/") ? decodedPath.substring(1) : decodedPath;
        return relative;
    }

    private static String decodedRelativeMarkdownQueryPath(String rawQuery) {
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

    private static String decodedFragment(URI request) {
        if (RELATIVE_MARKDOWN_REQUEST_PATH.equals(request.getRawPath())) {
            return fragmentFromDecodedPath(decodedRelativeMarkdownQueryPath(request.getRawQuery()));
        }
        String fragment = request.getRawFragment();
        if (fragment == null || fragment.length() == 0) {
            return "";
        }
        try {
            return URLDecoder.decode(fragment, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private static String markdownPathWithoutFragment(String path) {
        int fragmentStart = path.indexOf('#');
        return fragmentStart < 0 ? path : path.substring(0, fragmentStart);
    }

    private static String fragmentFromDecodedPath(String path) {
        int fragmentStart = path.indexOf('#');
        if (fragmentStart < 0 || fragmentStart + 1 >= path.length()) {
            return "";
        }
        return path.substring(fragmentStart + 1);
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
}
