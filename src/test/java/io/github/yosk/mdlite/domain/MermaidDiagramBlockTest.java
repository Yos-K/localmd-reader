package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MermaidDiagramBlockTest {

    @Test
    void sourceIsTrimmedToKeepDiagramContentCanonical() {
        MermaidDiagramBlock block = MermaidDiagramBlock.fromSource("  graph TD\nA-->B  ");

        TestAssertions.assertEquals("graph TD\nA-->B", block.source(), "Mermaid source must be trimmed without changing inner lines");
    }

    @Test
    void emptySourceIsRejectedToKeepDiagramBlockAlwaysValid() {
        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                new TestAssertions.ThrowingRunnable() {
                    @Override
                    public void run() {
                        MermaidDiagramBlock.fromSource("   ");
                    }
                });
    }

    @Test
    void nullSourceIsRejectedToKeepDiagramBlockAlwaysValid() {
        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                new TestAssertions.ThrowingRunnable() {
                    @Override
                    public void run() {
                        MermaidDiagramBlock.fromSource(null);
                    }
                });
    }
}
