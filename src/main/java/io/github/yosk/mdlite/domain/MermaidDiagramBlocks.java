package io.github.yosk.mdlite.domain;

public final class MermaidDiagramBlocks {
    private final MermaidDiagramBlock[] items;

    private MermaidDiagramBlocks(MermaidDiagramBlock[] items) {
        this.items = items;
    }

    public static MermaidDiagramBlocks fromMarkdown(String markdown) {
        String source = markdown == null ? "" : markdown;
        String[] lines = source.split("\\r?\\n", -1);
        MermaidDiagramBlock[] found = new MermaidDiagramBlock[lines.length];
        int count = 0;
        boolean inMermaidFence = false;
        boolean inOtherFence = false;
        StringBuilder diagram = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (isFenceLine(line)) {
                if (inMermaidFence) {
                    count = appendDiagram(found, count, diagram.toString());
                    diagram.setLength(0);
                    inMermaidFence = false;
                    continue;
                }
                if (inOtherFence) {
                    inOtherFence = false;
                    continue;
                }
                if (isMermaidFenceLine(line)) {
                    inMermaidFence = true;
                    diagram.setLength(0);
                    continue;
                }
                inOtherFence = true;
                continue;
            }
            if (inMermaidFence) {
                diagram.append(line).append('\n');
            }
        }

        MermaidDiagramBlock[] compact = new MermaidDiagramBlock[count];
        System.arraycopy(found, 0, compact, 0, count);
        return new MermaidDiagramBlocks(compact);
    }

    public MermaidDiagramBlock[] items() {
        MermaidDiagramBlock[] copy = new MermaidDiagramBlock[items.length];
        System.arraycopy(items, 0, copy, 0, items.length);
        return copy;
    }

    public boolean isEmpty() {
        return items.length == 0;
    }

    private static int appendDiagram(MermaidDiagramBlock[] found, int count, String source) {
        String normalized = source == null ? "" : source.trim();
        if (normalized.length() == 0) {
            return count;
        }
        found[count] = MermaidDiagramBlock.fromSource(normalized);
        return count + 1;
    }

    private static boolean isFenceLine(String line) {
        String trimmed = line == null ? "" : line.trim();
        return trimmed.equals("```") || trimmed.startsWith("```");
    }

    private static boolean isMermaidFenceLine(String line) {
        return "```mermaid".equalsIgnoreCase((line == null ? "" : line.trim()));
    }
}
