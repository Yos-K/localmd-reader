package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.CodeHighlighting;
import io.github.yosk.mdlite.domain.DocumentRenderingProfile;
import io.github.yosk.mdlite.domain.MarkdownHeadingAnchors;
import io.github.yosk.mdlite.domain.MermaidRendering;
import io.github.yosk.mdlite.domain.RelativeImageRendering;
import io.github.yosk.mdlite.domain.RelativeLinkRendering;
import io.github.yosk.mdlite.domain.SafeHtml;
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
        CodeFenceInfo codeFence = CodeFenceInfo.plain();
        StringBuilder codeBlock = new StringBuilder();
        StringBuilder paragraph = new StringBuilder();
        int openList = LIST_NONE;
        int mermaidIndex = 0;
        int previewCodeBlockIndex = 0;
        MarkdownHeadingAnchors headingAnchors = new MarkdownHeadingAnchors();

        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];
            if (MarkdownCodeBlockRenderer.isFenceLine(line)) {
                if (inCodeBlock) {
                    if (safeMermaidRendering.isEnabled() && codeFence.isMermaid()) {
                        html.append(MarkdownCodeBlockRenderer.renderMermaid(mermaidIndex, codeBlock.toString(), renderedMermaidDiagrams));
                        mermaidIndex++;
                    } else if (codeFence.isPreviewable()) {
                        html.append(MarkdownCodeBlockRenderer.renderPreviewable(previewCodeBlockIndex, codeBlock.toString(),
                                codeFence, safeCodeHighlighting));
                        previewCodeBlockIndex++;
                    } else {
                        html.append(MarkdownCodeBlockRenderer.renderLines(codeBlock.toString(), codeFence, safeCodeHighlighting));
                    }
                    inCodeBlock = false;
                    codeFence = CodeFenceInfo.plain();
                    codeBlock.setLength(0);
                } else {
                    flushParagraph(html, paragraph, safeRelativeLinkRendering, safeRelativeImageRendering);
                    openList = closeList(html, openList);
                    inCodeBlock = true;
                    codeFence = CodeFenceInfo.fromFenceLine(line);
                    if (!(safeMermaidRendering.isEnabled() && codeFence.isMermaid())
                            && !codeFence.isPreviewable()) {
                        html.append(MarkdownCodeBlockRenderer.openingHtml(codeFence));
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

            if (lineIndex + 1 < lines.length
                    && MarkdownTableRenderer.isRow(line)
                    && MarkdownTableRenderer.isSeparator(lines[lineIndex + 1])) {
                flushParagraph(html, paragraph, safeRelativeLinkRendering, safeRelativeImageRendering);
                openList = closeList(html, openList);
                html.append("<div class=\"table-scroll\"><table><thead><tr>");
                html.append(MarkdownTableRenderer.renderCells(
                        line, "th", safeRelativeLinkRendering, safeRelativeImageRendering));
                html.append("</tr></thead><tbody>");
                lineIndex += 2;
                while (lineIndex < lines.length && MarkdownTableRenderer.isRow(lines[lineIndex])) {
                    html.append("<tr>");
                    html.append(MarkdownTableRenderer.renderCells(
                            lines[lineIndex], "td", safeRelativeLinkRendering, safeRelativeImageRendering));
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
                        .append(MarkdownInlineRenderer.render(headingText, safeRelativeLinkRendering, safeRelativeImageRendering))
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
                html.append("<blockquote>").append(MarkdownInlineRenderer.render(line.substring(2).trim(), safeRelativeLinkRendering, safeRelativeImageRendering)).append("</blockquote>");
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
                            .append(MarkdownInlineRenderer.render(line.substring(6).trim(), safeRelativeLinkRendering, safeRelativeImageRendering)).append("</li>");
                    continue;
                }
                if (openList != LIST_UNORDERED) {
                    openList = closeList(html, openList);
                    html.append("<ul>");
                    openList = LIST_UNORDERED;
                }
                html.append("<li>").append(MarkdownInlineRenderer.render(line.substring(2).trim(), safeRelativeLinkRendering, safeRelativeImageRendering)).append("</li>");
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
                html.append("<li>").append(MarkdownInlineRenderer.render(line.substring(orderedMarkerLength).trim(), safeRelativeLinkRendering, safeRelativeImageRendering)).append("</li>");
                continue;
            }

            openList = closeList(html, openList);
            if (paragraph.length() > 0) {
                paragraph.append(' ');
            }
            paragraph.append(line.trim());
        }

        if (inCodeBlock) {
            if (safeMermaidRendering.isEnabled() && codeFence.isMermaid()) {
                html.append(MarkdownCodeBlockRenderer.renderMermaid(mermaidIndex, codeBlock.toString(), renderedMermaidDiagrams));
            } else if (codeFence.isPreviewable()) {
                html.append(MarkdownCodeBlockRenderer.renderPreviewable(previewCodeBlockIndex, codeBlock.toString(),
                        codeFence, safeCodeHighlighting));
            } else {
                html.append(MarkdownCodeBlockRenderer.renderLines(codeBlock.toString(), codeFence, safeCodeHighlighting));
            }
        }
        closeList(html, openList);
        flushParagraph(html, paragraph, safeRelativeLinkRendering, safeRelativeImageRendering);

        return SafeHtml.fromTrustedRendererOutput(html.toString());
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
        html.append("<p>").append(MarkdownInlineRenderer.render(paragraph.toString(), relativeLinkRendering, relativeImageRendering)).append("</p>");
        paragraph.setLength(0);
    }

}
