package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.RelativeImageRendering;
import io.github.yosk.mdlite.domain.RelativeLinkRendering;

final class MarkdownTableRenderer {
    private MarkdownTableRenderer() {
    }

    static boolean isRow(String line) {
        String trimmed = line.trim();
        return trimmed.indexOf('|') >= 0 && splitCells(trimmed).length > 1;
    }

    static boolean isSeparator(String line) {
        String[] cells = splitCells(line);
        if (cells.length < 2) {
            return false;
        }
        for (int index = 0; index < cells.length; index++) {
            if (!isSeparatorCell(cells[index])) {
                return false;
            }
        }
        return true;
    }

    static String renderCells(
            String line,
            String tag,
            RelativeLinkRendering relativeLinkRendering,
            RelativeImageRendering relativeImageRendering) {
        StringBuilder html = new StringBuilder();
        String[] cells = splitCells(line);
        for (int index = 0; index < cells.length; index++) {
            html.append('<').append(tag).append('>')
                    .append(MarkdownInlineRenderer.render(
                            cells[index], relativeLinkRendering, relativeImageRendering))
                    .append("</").append(tag).append('>');
        }
        return html.toString();
    }

    private static boolean isSeparatorCell(String cell) {
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
        for (int index = 0; index < trimmed.length(); index++) {
            if (trimmed.charAt(index) != '-') {
                return false;
            }
        }
        return true;
    }

    private static String[] splitCells(String line) {
        String trimmed = line.trim();
        if (trimmed.startsWith("|")) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.endsWith("|")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        String[] cells = trimmed.split("\\|", -1);
        for (int index = 0; index < cells.length; index++) {
            cells[index] = cells[index].trim();
        }
        return cells;
    }
}
