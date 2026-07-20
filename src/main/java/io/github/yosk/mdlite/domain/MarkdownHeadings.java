package io.github.yosk.mdlite.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MarkdownHeadings {
    private final List<MarkdownHeading> headings;

    private MarkdownHeadings(List<MarkdownHeading> headings) {
        this.headings = Collections.unmodifiableList(new ArrayList<MarkdownHeading>(headings));
    }

    public static MarkdownHeadings fromMarkdown(String markdown) {
        String source = markdown == null ? "" : markdown;
        String[] lines = source.split("\\r?\\n", -1);
        ArrayList<MarkdownHeading> headings = new ArrayList<MarkdownHeading>();
        MarkdownHeadingAnchors anchors = new MarkdownHeadingAnchors();
        boolean inCodeBlock = false;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (isFenceLine(line)) {
                inCodeBlock = !inCodeBlock;
            } else if (!inCodeBlock) {
                int level = headingLevel(line);
                if (level > 0) {
                    String title = line.substring(level + 1).trim();
                    headings.add(new MarkdownHeading(level, title, anchors.nextAnchorId(title)));
                }
            }
        }
        return new MarkdownHeadings(headings);
    }

    public int count() {
        return headings.size();
    }

    public MarkdownHeading at(int index) {
        return headings.get(index);
    }

    public List<MarkdownHeading> items() {
        return headings;
    }

    private static boolean isFenceLine(String line) {
        // CommonMark fenced code blocks: up to 3 spaces of leading indentation, then a run of
        // 3+ backticks. This recognizes indented fences and closing fences that carry trailing
        // whitespace; both toggle the code block so hash lines inside it stay out of the TOC.
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

    private static int headingLevel(String line) {
        int hashes = 0;
        while (hashes < line.length() && line.charAt(hashes) == '#') {
            hashes++;
        }
        if (hashes < 1 || hashes > 6) {
            return 0;
        }
        if (line.length() <= hashes || line.charAt(hashes) != ' ') {
            return 0;
        }
        if (line.substring(hashes + 1).trim().length() == 0) {
            return 0;
        }
        return hashes;
    }
}
