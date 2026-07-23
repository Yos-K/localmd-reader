package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class CodeFenceInfoTest {
    @Test
    void metadataAfterLanguageDoesNotBecomePartOfTheLanguage() {
        TestAssertions.assertEquals("html",
                CodeFenceInfo.fromFenceLine("```HTML title=example").language(),
                "code-fence language must be its normalized first info-string token");
    }

    @Test
    void unsafeLanguageTokenProducesPlainCode() {
        TestAssertions.assertEquals("",
                CodeFenceInfo.fromFenceLine("```java\" onclick=bad").language(),
                "unsafe code-fence metadata must not enter an HTML class");
    }

    @Test
    void markdownAliasSelectsMarkdownPreview() {
        TestAssertions.assertTrue(CodeFenceInfo.fromFenceLine("```md").isMarkdownPreview(),
                "the md alias must select Markdown preview behavior");
    }

    @Test
    void ordinaryCodeLanguageDoesNotSelectPreview() {
        TestAssertions.assertFalse(CodeFenceInfo.fromFenceLine("```java").isPreviewable(),
                "ordinary source code must remain a raw highlighted block");
    }
}
