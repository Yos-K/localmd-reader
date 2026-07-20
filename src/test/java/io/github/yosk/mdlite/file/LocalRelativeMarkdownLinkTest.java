package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class LocalRelativeMarkdownLinkTest {

    @Test
    void fileMarkdownDocumentResolvesSafeLocalMdRelativeMarkdownLink() {
        LocalRelativeMarkdownLink link = LocalRelativeMarkdownLink.resolve(
                "file:///docs/project/readme.md",
                "https://localmd.local/guide/intro.md",
                "/docs");

        TestAssertions.assertTrue(link.isAvailable(), "safe local Markdown links must resolve from the active document");
        TestAssertions.assertEquals(
                "/docs/project/guide/intro.md",
                link.filePath(),
                "safe local Markdown links must resolve relative to the active document directory");
    }

    @Test
    void markdownLinkWithFragmentResolvesTheTargetFilePath() {
        LocalRelativeMarkdownLink link = LocalRelativeMarkdownLink.resolve(
                "file:///docs/project/readme.md",
                "https://localmd.local/__relative_markdown__?path=guide%2Fintro.md%23section",
                "/docs");

        TestAssertions.assertTrue(link.isAvailable(), "safe local Markdown links with fragments must resolve the target file");
        TestAssertions.assertEquals(
                "/docs/project/guide/intro.md",
                link.filePath(),
                "fragment identifiers must not become part of the resolved file path");
        TestAssertions.assertEquals(
                "section",
                link.targetAnchorId(),
                "fragment identifiers must be preserved as the target heading anchor");
    }

    @Test
    void markdownLinkWithoutFragmentHasNoTargetAnchor() {
        LocalRelativeMarkdownLink link = LocalRelativeMarkdownLink.resolve(
                "file:///docs/project/readme.md",
                "https://localmd.local/__relative_markdown__?path=guide%2Fintro.md",
                "/docs");

        TestAssertions.assertTrue(link.isAvailable(), "safe local Markdown links without fragments must resolve");
        TestAssertions.assertEquals(
                "",
                link.targetAnchorId(),
                "links without fragments must not request a heading jump");
    }

    @Test
    void relativeMarkdownLinkCanUseMarkdownExtension() {
        LocalRelativeMarkdownLink link = LocalRelativeMarkdownLink.resolve(
                "file:///docs/project/readme.md",
                "https://localmd.local/__relative_markdown__?path=guide%2Fintro.markdown",
                "/docs");

        TestAssertions.assertTrue(link.isAvailable(), ".markdown links must be accepted as readable Markdown documents");
    }

    @Test
    void parentDirectoryMarkdownLinkResolvesWhenItStaysInsideTheAllowedRoot() {
        LocalRelativeMarkdownLink link = LocalRelativeMarkdownLink.resolve(
                "file:///docs/project/chapter/readme.md",
                "https://localmd.local/__relative_markdown__?path=..%2Ftoc.md",
                "/docs/project");

        TestAssertions.assertTrue(link.isAvailable(), "parent directory Markdown links inside the allowed root must resolve");
        TestAssertions.assertEquals(
                "/docs/project/toc.md",
                link.filePath(),
                "encoded relative Markdown paths must preserve parent directory navigation");
    }

    @Test
    void relativeMarkdownLinkCannotEscapeTheAllowedRoot() {
        LocalRelativeMarkdownLink link = LocalRelativeMarkdownLink.resolve(
                "file:///docs/project/readme.md",
                "https://localmd.local/__relative_markdown__?path=..%2F..%2Fsecret.md",
                "/docs");

        TestAssertions.assertFalse(link.isAvailable(), "relative Markdown links must not escape the allowed document set root");
    }

    @Test
    void markdownFileNameCanContainLiteralPlusSigns() {
        LocalRelativeMarkdownLink link = LocalRelativeMarkdownLink.resolve(
                "file:///docs/project/readme.md",
                "https://localmd.local/__relative_markdown__?path=C%2B%2B+notes.md",
                "/docs");

        TestAssertions.assertTrue(link.isAvailable(), "literal plus signs in Markdown file names must resolve");
        TestAssertions.assertEquals(
                "/docs/project/C++ notes.md",
                link.filePath(),
                "encoded plus signs must decode as plus signs while encoded spaces decode as spaces");
    }

    @Test
    void relativeMarkdownLinkRejectsNonMarkdownTarget() {
        LocalRelativeMarkdownLink link = LocalRelativeMarkdownLink.resolve(
                "file:///docs/project/readme.md",
                "https://localmd.local/guide/image.png",
                "/docs");

        TestAssertions.assertFalse(link.isAvailable(), "relative Markdown navigation must reject non-Markdown targets");
    }

    @Test
    void relativeMarkdownLinkRejectsRequestsOutsideTheLocalMdHost() {
        LocalRelativeMarkdownLink link = LocalRelativeMarkdownLink.resolve(
                "file:///docs/project/readme.md",
                "https://example.com/guide/intro.md",
                "/docs");

        TestAssertions.assertFalse(link.isAvailable(), "relative Markdown navigation must only handle app-local WebView links");
    }

    @Test
    void nonFileMarkdownDocumentCannotResolveRelativeMarkdownLinks() {
        LocalRelativeMarkdownLink link = LocalRelativeMarkdownLink.resolve(
                "content://provider/readme.md",
                "https://localmd.local/guide/intro.md",
                "/docs");

        TestAssertions.assertFalse(link.isAvailable(), "relative Markdown navigation requires a file-backed active document");
    }
}
