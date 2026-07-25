package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.RelativeImageRendering;
import io.github.yosk.mdlite.domain.RelativeLinkRendering;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

final class MarkdownInlineRenderer {
    private MarkdownInlineRenderer() {
    }

    static String render(
            String text,
            RelativeLinkRendering relativeLinkRendering,
            RelativeImageRendering relativeImageRendering) {
        StringBuilder out = new StringBuilder();
        StringBuilder code = null;
        for (int index = 0; index < text.length(); index++) {
            char current = text.charAt(index);
            if (current == '`') {
                code = toggleCode(out, code);
                continue;
            }
            if (code != null) {
                code.append(current);
                continue;
            }
            int renderedEnd = appendStrong(out, text, index, relativeLinkRendering, relativeImageRendering);
            if (renderedEnd < index) {
                renderedEnd = appendImage(out, text, index, relativeLinkRendering, relativeImageRendering);
            }
            if (renderedEnd < index) {
                renderedEnd = appendLink(out, text, index, relativeLinkRendering, relativeImageRendering);
            }
            if (renderedEnd >= index) {
                index = renderedEnd;
            } else {
                out.append(escapeHtmlChar(current));
            }
        }
        if (code != null) {
            out.append('`').append(escapeHtml(code.toString()));
        }
        return out.toString();
    }

    private static StringBuilder toggleCode(StringBuilder out, StringBuilder code) {
        if (code == null) {
            return new StringBuilder();
        }
        out.append("<code>").append(escapeHtml(code.toString())).append("</code>");
        return null;
    }

    private static int appendStrong(
            StringBuilder out,
            String text,
            int index,
            RelativeLinkRendering relativeLinkRendering,
            RelativeImageRendering relativeImageRendering) {
        if (text.charAt(index) != '*'
                || index + 1 >= text.length()
                || text.charAt(index + 1) != '*'
                || isEscaped(text, index)) {
            return -1;
        }
        int end = closingStrongMarker(text, index + 2);
        if (end < 0 || end == index + 2) {
            return -1;
        }
        out.append("<strong>")
                .append(render(text.substring(index + 2, end), relativeLinkRendering, relativeImageRendering))
                .append("</strong>");
        return end + 1;
    }

    private static int closingStrongMarker(String text, int start) {
        for (int index = start; index + 1 < text.length(); index++) {
            if (text.charAt(index) == '*'
                    && text.charAt(index + 1) == '*'
                    && !isEscaped(text, index)) {
                return index;
            }
        }
        return -1;
    }

    private static boolean isEscaped(String text, int index) {
        int slashCount = 0;
        for (int current = index - 1; current >= 0 && text.charAt(current) == '\\'; current--) {
            slashCount++;
        }
        return slashCount % 2 == 1;
    }

    private static int appendImage(
            StringBuilder out,
            String text,
            int index,
            RelativeLinkRendering relativeLinkRendering,
            RelativeImageRendering relativeImageRendering) {
        if (text.charAt(index) != '!'
                || index + 1 >= text.length()
                || text.charAt(index + 1) != '[') {
            return -1;
        }
        int labelEnd = text.indexOf(']', index + 2);
        if (labelEnd < 0 || labelEnd + 1 >= text.length() || text.charAt(labelEnd + 1) != '(') {
            return -1;
        }
        int urlEnd = text.indexOf(')', labelEnd + 2);
        if (urlEnd < 0) {
            return -1;
        }
        String alt = text.substring(index + 2, labelEnd);
        String url = text.substring(labelEnd + 2, urlEnd).trim();
        if (isSafeImageUrl(url, relativeImageRendering)) {
            out.append("<img src=\"").append(escapeHtml(localRelativeImageRequestUrl(url))).append("\" alt=\"")
                    .append(escapeHtml(render(alt, relativeLinkRendering, RelativeImageRendering.disabled())))
                    .append("\">");
        } else {
            out.append(render(alt, relativeLinkRendering, RelativeImageRendering.disabled()));
        }
        return urlEnd;
    }

    private static int appendLink(
            StringBuilder out,
            String text,
            int index,
            RelativeLinkRendering relativeLinkRendering,
            RelativeImageRendering relativeImageRendering) {
        if (text.charAt(index) != '[') {
            return -1;
        }
        int labelEnd = text.indexOf(']', index + 1);
        if (labelEnd < 0 || labelEnd + 1 >= text.length() || text.charAt(labelEnd + 1) != '(') {
            return -1;
        }
        int urlEnd = text.indexOf(')', labelEnd + 2);
        if (urlEnd < 0) {
            return -1;
        }
        String label = text.substring(index + 1, labelEnd);
        String url = text.substring(labelEnd + 2, urlEnd).trim();
        if (isSafeLinkUrl(url, relativeLinkRendering)) {
            out.append("<a href=\"").append(escapeHtml(linkHref(url))).append("\">")
                    .append(render(label, relativeLinkRendering, relativeImageRendering))
                    .append("</a>");
        } else {
            out.append(render(label, relativeLinkRendering, relativeImageRendering));
        }
        return urlEnd;
    }

    private static boolean isSafeImageUrl(String url, RelativeImageRendering relativeImageRendering) {
        return relativeImageRendering != null
                && relativeImageRendering.isEnabled()
                && isSafeRelativeUrl(url, url.toLowerCase());
    }

    private static boolean isSafeLinkUrl(String url, RelativeLinkRendering relativeLinkRendering) {
        String lower = url.toLowerCase();
        if (lower.startsWith("https://") || lower.startsWith("http://")) {
            return true;
        }
        return relativeLinkRendering != null
                && relativeLinkRendering.isEnabled()
                && isSafeRelativeUrl(url, lower);
    }

    private static String linkHref(String url) {
        String lower = url.toLowerCase();
        if (lower.startsWith("https://") || lower.startsWith("http://")) {
            return url;
        }
        return localRequestUrl("__relative_markdown__", url);
    }

    private static String localRelativeImageRequestUrl(String url) {
        return localRequestUrl("__relative_image__", url);
    }

    private static String localRequestUrl(String endpoint, String url) {
        try {
            return "https://localmd.local/" + endpoint + "?path=" + URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "https://localmd.local/" + endpoint + "?path=";
        }
    }

    private static boolean isSafeRelativeUrl(String url, String lower) {
        return url.length() > 0
                && !url.startsWith("/")
                && !url.startsWith("\\")
                && !url.startsWith("//")
                && lower.indexOf(':') < 0;
    }

    private static String escapeHtml(String text) {
        StringBuilder escaped = new StringBuilder();
        for (int index = 0; index < text.length(); index++) {
            escaped.append(escapeHtmlChar(text.charAt(index)));
        }
        return escaped.toString();
    }

    private static String escapeHtmlChar(char value) {
        switch (value) {
            case '&': return "&amp;";
            case '<': return "&lt;";
            case '>': return "&gt;";
            case '"': return "&quot;";
            case '\'': return "&#39;";
            default: return String.valueOf(value);
        }
    }
}
