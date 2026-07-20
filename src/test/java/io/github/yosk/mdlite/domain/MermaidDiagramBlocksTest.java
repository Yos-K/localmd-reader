package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MermaidDiagramBlocksTest {

    @Test
    void markdownWithoutMermaidFenceCreatesEmptyDiagramBlocks() {
        MermaidDiagramBlocks blocks = MermaidDiagramBlocks.fromMarkdown("# Title\nplain text");

        TestAssertions.assertTrue(blocks.isEmpty(), "Markdown without Mermaid fences must create no diagram blocks");
        TestAssertions.assertEquals(0, blocks.items().length, "Empty Mermaid blocks must expose no items");
    }

    @Test
    void mermaidFenceCreatesOneDiagramBlock() {
        MermaidDiagramBlocks blocks = MermaidDiagramBlocks.fromMarkdown("```mermaid\ngraph TD\nA-->B\n```");

        TestAssertions.assertFalse(blocks.isEmpty(), "Mermaid fence must create a diagram block");
        TestAssertions.assertEquals(1, blocks.items().length, "One Mermaid fence must create one diagram block");
        TestAssertions.assertEquals("graph TD\nA-->B", blocks.items()[0].source(), "Mermaid diagram source must preserve diagram lines");
    }

    @Test
    void multipleMermaidFencesCreateMultipleDiagramBlocksInOrder() {
        MermaidDiagramBlocks blocks = MermaidDiagramBlocks.fromMarkdown("```mermaid\ngraph TD\nA-->B\n```\n```mermaid\nsequenceDiagram\nA->>B: Hi\n```");

        TestAssertions.assertEquals(2, blocks.items().length, "Two Mermaid fences must create two diagram blocks");
        TestAssertions.assertEquals("graph TD\nA-->B", blocks.items()[0].source(), "First Mermaid block must stay first");
        TestAssertions.assertEquals("sequenceDiagram\nA->>B: Hi", blocks.items()[1].source(), "Second Mermaid block must stay second");
    }

    @Test
    void nonMermaidFenceDoesNotCreateDiagramBlock() {
        MermaidDiagramBlocks blocks = MermaidDiagramBlocks.fromMarkdown("```java\nclass Note {}\n```");

        TestAssertions.assertTrue(blocks.isEmpty(), "Non-Mermaid fences must not create diagram blocks");
    }

    @Test
    void emptyMermaidFenceIsIgnored() {
        MermaidDiagramBlocks blocks = MermaidDiagramBlocks.fromMarkdown("```mermaid\n   \n```");

        TestAssertions.assertTrue(blocks.isEmpty(), "Empty Mermaid fences must not create invalid diagram blocks");
    }

    @Test
    void itemsReturnsCopyToProtectTheCollection() {
        MermaidDiagramBlocks blocks = MermaidDiagramBlocks.fromMarkdown("```mermaid\ngraph TD\nA-->B\n```");
        MermaidDiagramBlock[] items = blocks.items();
        items[0] = MermaidDiagramBlock.fromSource("graph LR\nX-->Y");

        TestAssertions.assertEquals("graph TD\nA-->B", blocks.items()[0].source(), "Mermaid blocks must protect stored items from caller mutation");
    }
}
