package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.file.FileSizePolicy;
import io.github.yosk.mdlite.file.RestorableOpenTab;
import io.github.yosk.mdlite.testing.TestAssertions;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.OpenDocumentTabs;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public final class RestorableOpenDocumentLoaderTest {
    @Test
    void readableStoredMarkdownBecomesAFileTab() {
        RestorableOpenDocumentLoader loader = new RestorableOpenDocumentLoader(
                new Source("README.md", 12, "# readme"), new Renderer(), new FileSizePolicy(2L * 1024L * 1024L),
                2L * 1024L * 1024L);

        RestoredOpenDocumentTab result = loader.load(RestorableOpenTab.of("old.md", "content://readme"));
        OpenDocumentTab tab = restoredTabs(result).tabs().get(0);

        TestAssertions.assertEquals("README.md", tab.title(),
                "restoration must prefer the current provider display name");
    }

    @Test
    void unsupportedStoredFileIsNotRestored() {
        RestorableOpenDocumentLoader loader = new RestorableOpenDocumentLoader(
                new Source("notes.txt", 12, "text"), new Renderer(), new FileSizePolicy(2L * 1024L * 1024L),
                2L * 1024L * 1024L);

        RestoredOpenDocumentTab result = loader.load(RestorableOpenTab.of("notes.txt", "content://notes"));

        TestAssertions.assertEquals("welcome", restoredTabs(result).activeTab().title(),
                "unsupported stored files must fall back to the welcome tab");
    }

    @Test
    void oversizedStoredFileIsNotReadOrRestored() {
        Source source = new Source("large.md", 2L * 1024L * 1024L + 1L, "# not read");
        RestorableOpenDocumentLoader loader = new RestorableOpenDocumentLoader(
                source, new Renderer(), new FileSizePolicy(2L * 1024L * 1024L), 2L * 1024L * 1024L);

        RestoredOpenDocumentTab result = loader.load(RestorableOpenTab.of("large.md", "content://large"));

        TestAssertions.assertEquals("welcome", restoredTabs(result).activeTab().title(),
                "oversized stored files must fall back to the welcome tab");
        TestAssertions.assertFalse(source.readCalled,
                "oversized stored files must be rejected before content reading");
    }

    @Test
    void unreadableStoredFileIsNotRestored() {
        RestorableOpenDocumentLoader loader = new RestorableOpenDocumentLoader(
                new Source("broken.md", 12, "", true), new Renderer(), new FileSizePolicy(2L * 1024L * 1024L),
                2L * 1024L * 1024L);

        RestoredOpenDocumentTab result = loader.load(RestorableOpenTab.of("broken.md", "content://broken"));

        TestAssertions.assertEquals("welcome", restoredTabs(result).activeTab().title(),
                "unreadable stored files must fall back to the welcome tab");
    }

    private static OpenDocumentTabs restoredTabs(RestoredOpenDocumentTab result) {
        return RestoredOpenDocumentTabs.restore(
                io.github.yosk.mdlite.file.RestorableOpenTabs.from(
                        java.util.Collections.singletonList(RestorableOpenTab.of("stored.md", "content://stored")), 0),
                OpenDocumentTab.welcome("welcome", "welcome:", SafeHtml.fromTrustedRendererOutput("welcome")),
                new RestoredOpenDocumentTabs.Loader() {
                    @Override
                    public RestoredOpenDocumentTab load(RestorableOpenTab ignored) {
                        return result;
                    }
                });
    }

    private static final class Renderer implements RestorableOpenDocumentLoader.Renderer {
        @Override
        public SafeHtml render(String uri, String markdown) {
            return SafeHtml.fromTrustedRendererOutput(markdown);
        }
    }

    private static final class Source implements RestorableOpenDocumentLoader.Source {
        private final String displayName;
        private final long size;
        private final String markdown;
        private final boolean fail;
        private boolean readCalled;

        Source(String displayName, long size, String markdown) {
            this(displayName, size, markdown, false);
        }

        Source(String displayName, long size, String markdown, boolean fail) {
            this.displayName = displayName;
            this.size = size;
            this.markdown = markdown;
            this.fail = fail;
        }

        @Override
        public String displayName(String uri) {
            return displayName;
        }

        @Override
        public long sizeBytes(String uri) {
            return size;
        }

        @Override
        public String readMarkdown(String uri, long maximumBytes) throws IOException {
            readCalled = true;
            if (fail) {
                throw new IOException("test failure");
            }
            return markdown;
        }
    }
}
