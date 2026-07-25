package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

public final class PersistentMarkdownTextStoreTest {
    @Test
    void storedMarkdownCanBeReadFromTheReturnedFile() throws Exception {
        File root = temporaryRoot("read");
        PersistentMarkdownTextStore store = new PersistentMarkdownTextStore(root);

        File stored = store.store("notes.md", "/storage/notes.md", "# Notes");

        TestAssertions.assertEquals("# Notes", read(stored),
                "persisted direct text must remain readable for tab restoration");
        TestAssertions.assertEquals("notes.md", stored.getName(),
                "the stored file name must preserve the tab title");
    }

    @Test
    void storingTheSameSourceReplacesItsPreviousContent() throws Exception {
        File root = temporaryRoot("replace");
        PersistentMarkdownTextStore store = new PersistentMarkdownTextStore(root);

        store.store("notes.md", "/storage/notes.md", "old");
        File stored = store.store("notes.md", "/storage/notes.md", "new");

        TestAssertions.assertEquals("new", read(stored),
                "a reopened source must restore its latest content");
        TestAssertions.assertEquals(1, stored.getParentFile().listFiles().length,
                "one source must not accumulate stale stored files");
    }

    @Test
    void unsafeTitleCannotEscapeTheStoreRoot() throws Exception {
        File root = temporaryRoot("safe-name");
        PersistentMarkdownTextStore store = new PersistentMarkdownTextStore(root);

        File stored = store.store("../../outside.md", "source", "safe");

        TestAssertions.assertEquals("outside.md", stored.getName(),
                "stored titles must be reduced to a safe file name");
        TestAssertions.assertTrue(stored.getCanonicalPath().startsWith(root.getCanonicalPath()),
                "stored direct text must remain inside the app-owned root");
    }

    private static File temporaryRoot(String label) throws Exception {
        return Files.createTempDirectory("localmd-" + label).toFile();
    }

    private static String read(File file) throws Exception {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }
}
