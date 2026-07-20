package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MarkdownLibraryRootNameTest {
    @Test
    void providerDisplayNameBecomesTheProjectRootName() {
        MarkdownLibraryRootName name =
                MarkdownLibraryRootName.fromProviderValue(" Project ", "Markdown library");

        TestAssertions.assertEquals("Project", name.value(),
                "provider root display name must identify the selected project");
    }

    @Test
    void missingProviderDisplayNameUsesTheLocalizedFallback() {
        MarkdownLibraryRootName name =
                MarkdownLibraryRootName.fromProviderValue(null, "Markdown library");

        TestAssertions.assertEquals("Markdown library", name.value(),
                "missing provider name must keep the library location readable");
    }

    @Test
    void blankProviderDisplayNameUsesTheLocalizedFallback() {
        MarkdownLibraryRootName name =
                MarkdownLibraryRootName.fromProviderValue("   ", "Markdownライブラリ");

        TestAssertions.assertEquals("Markdownライブラリ", name.value(),
                "blank provider name must use the current viewer language fallback");
    }

    @Test
    void emptyFallbackIsRejectedBeforeReadingProviderData() {
        TestAssertions.assertThrows(IllegalArgumentException.class,
                new TestAssertions.ThrowingRunnable() {
                    @Override
                    public void run() {
                        MarkdownLibraryRootName.fromProviderValue("Project", "");
                    }
                });
    }
}
