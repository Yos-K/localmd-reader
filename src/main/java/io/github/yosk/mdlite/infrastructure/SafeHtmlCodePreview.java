package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;
import java.util.Locale;

final class SafeHtmlCodePreview {
    private SafeHtmlCodePreview() {
    }

    static SafeHtml from(String source) {
        String safeSource = source == null ? "" : source;
        StringBuilder rendered = new StringBuilder();
        int index = 0;
        while (index < safeSource.length()) {
            char current = safeSource.charAt(index);
            if (current != '<') {
                rendered.append(escape(current));
                index++;
                continue;
            }
            int tagEnd = safeSource.indexOf('>', index + 1);
            if (tagEnd < 0) {
                rendered.append("&lt;");
                index++;
                continue;
            }
            rendered.append(safeTag(safeSource.substring(index + 1, tagEnd)));
            index = tagEnd + 1;
        }
        return SafeHtml.fromTrustedRendererOutput(rendered.toString());
    }

    private static String safeTag(String rawTag) {
        String trimmed = rawTag.trim();
        boolean closing = trimmed.startsWith("/");
        String name = tagName(trimmed, closing);
        if (!isAllowed(name)) {
            return escape("<" + rawTag + ">");
        }
        return closing ? "</" + name + ">" : "<" + name + ">";
    }

    private static String tagName(String tag, boolean closing) {
        int index = closing ? 1 : 0;
        while (index < tag.length() && Character.isWhitespace(tag.charAt(index))) { index++; }
        int start = index;
        while (index < tag.length() && Character.isLetterOrDigit(tag.charAt(index))) { index++; }
        return tag.substring(start, index).toLowerCase(Locale.US);
    }

    private static boolean isAllowed(String name) {
        return "p".equals(name) || "strong".equals(name) || "em".equals(name)
                || "b".equals(name) || "i".equals(name) || "ul".equals(name)
                || "ol".equals(name) || "li".equals(name) || "blockquote".equals(name)
                || "code".equals(name) || "pre".equals(name) || "br".equals(name)
                || "hr".equals(name) || "h1".equals(name) || "h2".equals(name)
                || "h3".equals(name) || "h4".equals(name) || "h5".equals(name)
                || "h6".equals(name);
    }

    private static String escape(String text) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < text.length(); i++) { escaped.append(escape(text.charAt(i))); }
        return escaped.toString();
    }

    private static String escape(char value) {
        if (value == '&') { return "&amp;"; }
        if (value == '<') { return "&lt;"; }
        if (value == '>') { return "&gt;"; }
        if (value == '"') { return "&quot;"; }
        if (value == '\'') { return "&#39;"; }
        return String.valueOf(value);
    }
}
