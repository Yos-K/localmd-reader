package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.CodeHighlighting;
import io.github.yosk.mdlite.domain.DocumentRenderingProfile;
import io.github.yosk.mdlite.domain.MarkdownHeadingAnchors;
import io.github.yosk.mdlite.domain.MermaidRendering;
import io.github.yosk.mdlite.domain.RelativeImageRendering;
import io.github.yosk.mdlite.domain.RelativeLinkRendering;
import io.github.yosk.mdlite.domain.SafeHtml;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public final class JavaSimpleMarkdownRenderer {
    private static final int LIST_NONE = 0;
    private static final int LIST_UNORDERED = 1;
    private static final int LIST_ORDERED = 2;
    private static final int LIST_CHECKLIST = 3;

    public SafeHtml render(String markdown) {
        return render(markdown, CodeHighlighting.plain());
    }

    public SafeHtml render(String markdown, CodeHighlighting codeHighlighting) {
        return render(markdown, codeHighlighting, MermaidRendering.plainCode(), null);
    }

    public SafeHtml render(
            String markdown,
            CodeHighlighting codeHighlighting,
            MermaidRendering mermaidRendering,
            Map<Integer, SafeHtml> renderedMermaidDiagrams) {
        return render(markdown, codeHighlighting, mermaidRendering,
                RelativeLinkRendering.disabled(), RelativeImageRendering.disabled(), renderedMermaidDiagrams);
    }

    public SafeHtml render(
            String markdown,
            CodeHighlighting codeHighlighting,
            MermaidRendering mermaidRendering,
            RelativeLinkRendering relativeLinkRendering,
            Map<Integer, SafeHtml> renderedMermaidDiagrams) {
        return render(markdown, codeHighlighting, mermaidRendering,
                relativeLinkRendering, RelativeImageRendering.disabled(), renderedMermaidDiagrams);
    }

    public SafeHtml render(
            String markdown,
            DocumentRenderingProfile profile,
            Map<Integer, SafeHtml> renderedMermaidDiagrams) {
        DocumentRenderingProfile safeProfile = profile == null
                ? DocumentRenderingProfile.fromEntitlement(null)
                : profile;
        return render(
                markdown,
                safeProfile.codeHighlighting(),
                safeProfile.mermaidRendering(),
                safeProfile.relativeLinkRendering(),
                safeProfile.relativeImageRendering(),
                renderedMermaidDiagrams);
    }

    public SafeHtml render(
            String markdown,
            CodeHighlighting codeHighlighting,
            MermaidRendering mermaidRendering,
            RelativeLinkRendering relativeLinkRendering,
            RelativeImageRendering relativeImageRendering,
            Map<Integer, SafeHtml> renderedMermaidDiagrams) {
        CodeHighlighting safeCodeHighlighting = codeHighlighting == null ? CodeHighlighting.plain() : codeHighlighting;
        MermaidRendering safeMermaidRendering = mermaidRendering == null ? MermaidRendering.plainCode() : mermaidRendering;
        RelativeLinkRendering safeRelativeLinkRendering =
                relativeLinkRendering == null ? RelativeLinkRendering.disabled() : relativeLinkRendering;
        RelativeImageRendering safeRelativeImageRendering =
                relativeImageRendering == null ? RelativeImageRendering.disabled() : relativeImageRendering;
        String source = markdown == null ? "" : markdown;
        StringBuilder html = new StringBuilder();
        String[] lines = source.split("\\r?\\n", -1);
        boolean inCodeBlock = false;
        String codeBlockLanguage = "";
        StringBuilder codeBlock = new StringBuilder();
        StringBuilder paragraph = new StringBuilder();
        int openList = LIST_NONE;
        int mermaidIndex = 0;
        MarkdownHeadingAnchors headingAnchors = new MarkdownHeadingAnchors();

        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];
            if (isFenceLine(line)) {
                if (inCodeBlock) {
                    if (safeMermaidRendering.isEnabled() && "mermaid".equals(codeBlockLanguage)) {
                        html.append(renderMermaidBlock(mermaidIndex, codeBlock.toString(), renderedMermaidDiagrams));
                        mermaidIndex++;
                    } else {
                        html.append(renderCodeBlockLines(codeBlock.toString(), codeBlockLanguage, safeCodeHighlighting));
                    }
                    inCodeBlock = false;
                    codeBlockLanguage = "";
                    codeBlock.setLength(0);
                } else {
                    flushParagraph(html, paragraph, safeRelativeLinkRendering, safeRelativeImageRendering);
                    openList = closeList(html, openList);
                    inCodeBlock = true;
                    codeBlockLanguage = codeFenceLanguage(line);
                    if (!(safeMermaidRendering.isEnabled() && "mermaid".equals(codeBlockLanguage))) {
                        html.append(openCodeBlockHtml(line));
                    }
                }
                continue;
            }

            if (inCodeBlock) {
                codeBlock.append(line).append('\n');
                continue;
            }

            if (line.trim().isEmpty()) {
                flushParagraph(html, paragraph, safeRelativeLinkRendering, safeRelativeImageRendering);
                openList = closeList(html, openList);
                continue;
            }

            if (lineIndex + 1 < lines.length && isTableHeaderLine(line) && isTableSeparatorLine(lines[lineIndex + 1])) {
                flushParagraph(html, paragraph, safeRelativeLinkRendering, safeRelativeImageRendering);
                openList = closeList(html, openList);
                String[] headerCells = splitTableCells(line);
                html.append("<div class=\"table-scroll\"><table><thead><tr>");
                appendTableCells(html, headerCells, "th", safeRelativeLinkRendering, safeRelativeImageRendering);
                html.append("</tr></thead><tbody>");
                lineIndex += 2;
                while (lineIndex < lines.length && isTableHeaderLine(lines[lineIndex])) {
                    html.append("<tr>");
                    appendTableCells(html, splitTableCells(lines[lineIndex]), "td", safeRelativeLinkRendering, safeRelativeImageRendering);
                    html.append("</tr>");
                    lineIndex++;
                }
                lineIndex--;
                html.append("</tbody></table></div>");
                continue;
            }

            int headingLevel = headingLevel(line);
            if (headingLevel > 0) {
                flushParagraph(html, paragraph, safeRelativeLinkRendering, safeRelativeImageRendering);
                openList = closeList(html, openList);
                String headingText = line.substring(headingLevel + 1).trim();
                html.append("<h").append(headingLevel)
                        .append(" id=\"").append(headingAnchors.nextAnchorId(headingText)).append("\">")
                        .append(renderInline(headingText, safeRelativeLinkRendering, safeRelativeImageRendering))
                        .append("</h").append(headingLevel).append(">");
                continue;
            }

            if (line.equals("---")) {
                flushParagraph(html, paragraph, safeRelativeLinkRendering, safeRelativeImageRendering);
                openList = closeList(html, openList);
                html.append("<hr>");
                continue;
            }

            if (line.startsWith("> ")) {
                flushParagraph(html, paragraph, safeRelativeLinkRendering, safeRelativeImageRendering);
                openList = closeList(html, openList);
                html.append("<blockquote>").append(renderInline(line.substring(2).trim(), safeRelativeLinkRendering, safeRelativeImageRendering)).append("</blockquote>");
                continue;
            }

            if (line.startsWith("- ")) {
                flushParagraph(html, paragraph, safeRelativeLinkRendering, safeRelativeImageRendering);
                String checkbox = checklistCheckboxHtml(line);
                if (checkbox != null) {
                    if (openList != LIST_CHECKLIST) {
                        openList = closeList(html, openList);
                        html.append("<ul class=\"checklist\">");
                        openList = LIST_CHECKLIST;
                    }
                    html.append("<li>").append(checkbox).append(' ')
                            .append(renderInline(line.substring(6).trim(), safeRelativeLinkRendering, safeRelativeImageRendering)).append("</li>");
                    continue;
                }
                if (openList != LIST_UNORDERED) {
                    openList = closeList(html, openList);
                    html.append("<ul>");
                    openList = LIST_UNORDERED;
                }
                html.append("<li>").append(renderInline(line.substring(2).trim(), safeRelativeLinkRendering, safeRelativeImageRendering)).append("</li>");
                continue;
            }

            int orderedMarkerLength = orderedMarkerLength(line);
            if (orderedMarkerLength > 0) {
                flushParagraph(html, paragraph, safeRelativeLinkRendering, safeRelativeImageRendering);
                if (openList != LIST_ORDERED) {
                    openList = closeList(html, openList);
                    html.append("<ol>");
                    openList = LIST_ORDERED;
                }
                html.append("<li>").append(renderInline(line.substring(orderedMarkerLength).trim(), safeRelativeLinkRendering, safeRelativeImageRendering)).append("</li>");
                continue;
            }

            openList = closeList(html, openList);
            if (paragraph.length() > 0) {
                paragraph.append(' ');
            }
            paragraph.append(line.trim());
        }

        if (inCodeBlock) {
            if (safeMermaidRendering.isEnabled() && "mermaid".equals(codeBlockLanguage)) {
                html.append(renderMermaidBlock(mermaidIndex, codeBlock.toString(), renderedMermaidDiagrams));
            } else {
                html.append(renderCodeBlockLines(codeBlock.toString(), codeBlockLanguage, safeCodeHighlighting));
            }
        }
        closeList(html, openList);
        flushParagraph(html, paragraph, safeRelativeLinkRendering, safeRelativeImageRendering);

        return SafeHtml.fromTrustedRendererOutput(html.toString());
    }

    private static String renderCodeBlockLines(String codeBlock, String language, CodeHighlighting codeHighlighting) {
        String[] codeLines = codeBlock.split("\\n", -1);
        StringBuilder rendered = new StringBuilder();
        for (int i = 0; i < codeLines.length; i++) {
            if (i == codeLines.length - 1 && codeLines[i].length() == 0) {
                continue;
            }
            rendered.append(renderCodeBlockLine(codeLines[i], language, codeHighlighting)).append('\n');
        }
        rendered.append("</code></pre>");
        return rendered.toString();
    }

    private static String renderMermaidBlock(int index, String source, Map<Integer, SafeHtml> renderedMermaidDiagrams) {
        SafeHtml rendered = renderedMermaidDiagrams == null ? null : renderedMermaidDiagrams.get(Integer.valueOf(index));
        if (rendered != null) {
            return "<div class=\"mermaid-diagram\"><div class=\"mermaid-diagram-scale\">" + rendered.value() + "</div></div>";
        }
        return "<div class=\"mermaid-placeholder\" data-mermaid-index=\"" + index + "\">"
                + "<strong>Mermaid diagram</strong>"
                + "<span>Rendering in background...</span>"
                + "<pre><code class=\"language-mermaid\">" + escapeHtml(source.trim()) + "</code></pre>"
                + "</div>";
    }

    private static String renderCodeBlockLine(String line, String language, CodeHighlighting codeHighlighting) {
        if (!codeHighlighting.isEnabled()) {
            return escapeHtml(line);
        }
        if ("java".equals(language)) {
            return JavaCodeHighlighter.highlightLine(line);
        }
        if ("kt".equals(language) || "kotlin".equals(language)) {
            return KotlinCodeHighlighter.highlightLine(line);
        }
        if ("js".equals(language) || "jsx".equals(language) || "javascript".equals(language)
                || "ts".equals(language) || "tsx".equals(language) || "typescript".equals(language)) {
            return JavaScriptCodeHighlighter.highlightLine(line);
        }
        if ("py".equals(language) || "python".equals(language)) {
            return PythonCodeHighlighter.highlightLine(line);
        }
        if ("html".equals(language) || "xml".equals(language)) {
            return MarkupCodeHighlighter.highlightLine(line);
        }
        if ("css".equals(language)) {
            return CssCodeHighlighter.highlightLine(line);
        }
        if ("yaml".equals(language) || "yml".equals(language)) {
            return YamlCodeHighlighter.highlightLine(line);
        }
        if ("json".equals(language)) {
            return JsonCodeHighlighter.highlightLine(line);
        }
        if ("sh".equals(language) || "bash".equals(language) || "shell".equals(language)) {
            return ShellCodeHighlighter.highlightLine(line);
        }
        return escapeHtml(line);
    }

    private static boolean isFenceLine(String line) {
        // Match the heading extractor (MarkdownHeadings.isFenceLine): up to 3 spaces of
        // indentation then a run of 3+ backticks. Keeping the two in lockstep prevents a
        // hash line inside an indented/trailing-space fence from rendering as a heading on
        // the page while being absent from the TOC/jump list (or vice versa).
        int indent = 0;
        while (indent < line.length() && line.charAt(indent) == ' ') {
            indent++;
        }
        if (indent > 3) {
            return false;
        }
        int ticks = 0;
        while (indent + ticks < line.length() && line.charAt(indent + ticks) == '`') {
            ticks++;
        }
        return ticks >= 3;
    }

    private static String openCodeBlockHtml(String fenceLine) {
        String language = codeFenceLanguage(fenceLine);
        if (language.length() == 0) {
            return "<pre><code>";
        }
        return "<pre><code class=\"language-" + language + "\">";
    }

    private static String codeFenceLanguage(String fenceLine) {
        String trimmed = fenceLine.trim();
        if (trimmed.length() <= 3) {
            return "";
        }
        String language = trimmed.substring(3).trim();
        if (!isSafeLanguageName(language)) {
            return "";
        }
        return language;
    }

    private static boolean isSafeLanguageName(String language) {
        if (language.length() == 0) {
            return false;
        }
        for (int i = 0; i < language.length(); i++) {
            char c = language.charAt(i);
            boolean safe = (c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9')
                    || c == '_'
                    || c == '-';
            if (!safe) {
                return false;
            }
        }
        return true;
    }

    private static int headingLevel(String line) {
        int level = 0;
        while (level < line.length() && line.charAt(level) == '#') {
            level++;
        }
        if (level < 1 || level > 6) {
            return 0;
        }
        if (level >= line.length() || line.charAt(level) != ' ') {
            return 0;
        }
        return level;
    }

    private static int orderedMarkerLength(String line) {
        int index = 0;
        while (index < line.length() && Character.isDigit(line.charAt(index))) {
            index++;
        }
        if (index == 0 || index + 1 >= line.length()) {
            return 0;
        }
        if (line.charAt(index) == '.' && line.charAt(index + 1) == ' ') {
            return index + 2;
        }
        return 0;
    }

    private static boolean isTableHeaderLine(String line) {
        String trimmed = line.trim();
        return trimmed.indexOf('|') >= 0 && splitTableCells(trimmed).length > 1;
    }

    private static boolean isTableSeparatorLine(String line) {
        String[] cells = splitTableCells(line);
        if (cells.length < 2) {
            return false;
        }
        for (int i = 0; i < cells.length; i++) {
            if (!isTableSeparatorCell(cells[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isTableSeparatorCell(String cell) {
        String trimmed = cell.trim();
        if (trimmed.startsWith(":")) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.endsWith(":")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        if (trimmed.length() < 3) {
            return false;
        }
        for (int i = 0; i < trimmed.length(); i++) {
            if (trimmed.charAt(i) != '-') {
                return false;
            }
        }
        return true;
    }

    private static String[] splitTableCells(String line) {
        String trimmed = line.trim();
        if (trimmed.startsWith("|")) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.endsWith("|")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        String[] rawCells = trimmed.split("\\|", -1);
        for (int i = 0; i < rawCells.length; i++) {
            rawCells[i] = rawCells[i].trim();
        }
        return rawCells;
    }

    private static void appendTableCells(
            StringBuilder html,
            String[] cells,
            String tag,
            RelativeLinkRendering relativeLinkRendering,
            RelativeImageRendering relativeImageRendering) {
        for (int i = 0; i < cells.length; i++) {
            html.append('<').append(tag).append('>')
                    .append(renderInline(cells[i], relativeLinkRendering, relativeImageRendering))
                    .append("</").append(tag).append('>');
        }
    }

    private static int closeList(StringBuilder html, int openList) {
        if (openList == LIST_UNORDERED) {
            html.append("</ul>");
        } else if (openList == LIST_ORDERED) {
            html.append("</ol>");
        } else if (openList == LIST_CHECKLIST) {
            html.append("</ul>");
        }
        return LIST_NONE;
    }

    private static String checklistCheckboxHtml(String line) {
        if (line.length() < 6) {
            return null;
        }
        if (!line.startsWith("- [") || line.charAt(4) != ']' || line.charAt(5) != ' ') {
            return null;
        }
        char marker = line.charAt(3);
        if (marker == ' ') {
            return "<input type=\"checkbox\" disabled>";
        }
        if (marker == 'x' || marker == 'X') {
            return "<input type=\"checkbox\" checked disabled>";
        }
        return null;
    }

    private static void flushParagraph(
            StringBuilder html,
            StringBuilder paragraph,
            RelativeLinkRendering relativeLinkRendering,
            RelativeImageRendering relativeImageRendering) {
        if (paragraph.length() == 0) {
            return;
        }
        html.append("<p>").append(renderInline(paragraph.toString(), relativeLinkRendering, relativeImageRendering)).append("</p>");
        paragraph.setLength(0);
    }

    private static String renderInline(
            String text,
            RelativeLinkRendering relativeLinkRendering,
            RelativeImageRendering relativeImageRendering) {
        StringBuilder out = new StringBuilder();
        StringBuilder code = null;

        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current == '`') {
                if (code == null) {
                    code = new StringBuilder();
                } else {
                    out.append("<code>").append(escapeHtml(code.toString())).append("</code>");
                    code = null;
                }
                continue;
            }

            if (code == null) {
                int imageEnd = appendMarkdownImageIfPresent(out, text, i, relativeLinkRendering, relativeImageRendering);
                if (imageEnd >= i) {
                    i = imageEnd;
                    continue;
                }
                int linkEnd = appendMarkdownLinkIfPresent(out, text, i, relativeLinkRendering, relativeImageRendering);
                if (linkEnd >= i) {
                    i = linkEnd;
                    continue;
                }
                out.append(escapeHtmlChar(current));
            } else {
                code.append(current);
            }
        }

        if (code != null) {
            out.append('`').append(escapeHtml(code.toString()));
        }

        return out.toString();
    }

    private static int appendMarkdownImageIfPresent(
            StringBuilder out,
            String text,
            int index,
            RelativeLinkRendering relativeLinkRendering,
            RelativeImageRendering relativeImageRendering) {
        if (text.charAt(index) != '!' || index + 1 >= text.length() || text.charAt(index + 1) != '[') {
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
                    .append(escapeHtml(renderInline(alt, relativeLinkRendering, RelativeImageRendering.disabled())))
                    .append("\">");
        } else {
            out.append(renderInline(alt, relativeLinkRendering, RelativeImageRendering.disabled()));
        }
        return urlEnd;
    }

    private static int appendMarkdownLinkIfPresent(
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
            out.append("<a href=\"").append(escapeHtml(linkHref(url, relativeLinkRendering))).append("\">")
                    .append(renderInline(label, relativeLinkRendering, relativeImageRendering))
                    .append("</a>");
        } else {
            out.append(renderInline(label, relativeLinkRendering, relativeImageRendering));
        }
        return urlEnd;
    }

    private static boolean isSafeImageUrl(String url, RelativeImageRendering relativeImageRendering) {
        String lower = url.toLowerCase();
        return relativeImageRendering != null
                && relativeImageRendering.isEnabled()
                && isSafeRelativeLinkUrl(url, lower);
    }

    private static String localRelativeImageRequestUrl(String url) {
        try {
            return "https://localmd.local/__relative_image__?path=" + URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "https://localmd.local/__relative_image__?path=";
        }
    }

    private static boolean isSafeLinkUrl(String url, RelativeLinkRendering relativeLinkRendering) {
        String lower = url.toLowerCase();
        if (lower.startsWith("https://") || lower.startsWith("http://")) {
            return true;
        }
        return relativeLinkRendering != null
                && relativeLinkRendering.isEnabled()
                && isSafeRelativeLinkUrl(url, lower);
    }

    private static String linkHref(String url, RelativeLinkRendering relativeLinkRendering) {
        String lower = url.toLowerCase();
        if (lower.startsWith("https://") || lower.startsWith("http://")) {
            return url;
        }
        return localRelativeMarkdownRequestUrl(url);
    }

    private static String localRelativeMarkdownRequestUrl(String url) {
        try {
            return "https://localmd.local/__relative_markdown__?path=" + URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "https://localmd.local/__relative_markdown__?path=";
        }
    }

    private static boolean isSafeRelativeLinkUrl(String url, String lower) {
        return url.length() > 0
                && !url.startsWith("/")
                && !url.startsWith("\\")
                && !url.startsWith("//")
                && lower.indexOf(':') < 0;
    }

    private static String escapeHtml(String text) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            escaped.append(escapeHtmlChar(text.charAt(i)));
        }
        return escaped.toString();
    }

    private static String escapeHtmlChar(char value) {
        switch (value) {
            case '&':
                return "&amp;";
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            case '"':
                return "&quot;";
            case '\'':
                return "&#39;";
            default:
                return String.valueOf(value);
        }
    }

}
