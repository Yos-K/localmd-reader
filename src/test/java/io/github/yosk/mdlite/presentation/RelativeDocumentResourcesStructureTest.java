package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public final class RelativeDocumentResourcesStructureTest {
    @Test
    void activityDelegatesRelativeImageRequests() throws IOException {
        TestAssertions.assertContains(sourceFile("MainActivity.java"),
                "relativeDocumentResources.openImage(requestUrl)",
                "the Activity must delegate relative image resolution and streaming");
    }

    @Test
    void activityDelegatesRelativeMarkdownRequests() throws IOException {
        TestAssertions.assertContains(sourceFile("MainActivity.java"),
                "relativeDocumentResources.openMarkdown(requestUrl)",
                "the Activity must delegate relative Markdown resolution and opening");
    }

    @Test
    void activityDoesNotOpenRelativeResourceStreamsDirectly() throws IOException {
        TestAssertions.assertNotContains(sourceFile("MainActivity.java"),
                "new FileInputStream",
                "resource streaming belongs to the relative-resource adapter");
    }

    private static String sourceFile(String fileName) throws IOException {
        String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
        return new String(Files.readAllBytes(Paths.get(projectRoot,
                "src/main/java/io/github/yosk/mdlite/presentation/" + fileName)),
                StandardCharsets.UTF_8);
    }
}
