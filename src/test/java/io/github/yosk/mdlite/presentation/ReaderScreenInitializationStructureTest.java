package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public final class ReaderScreenInitializationStructureTest {
    @Test
    void activityDelegatesReaderViewConstructionToOneInitializer() throws IOException {
        TestAssertions.assertContains(sourceFile("MainActivity.java"),
                "ReaderScreenInitializer.initialize(this)",
                "the Activity composition root must delegate detailed Android View construction");
    }

    @Test
    void initializerOwnsTopBarConstruction() throws IOException {
        TestAssertions.assertContains(sourceFile("ReaderScreenInitializer.java"),
                "private static void initTopBar(MainActivity activity)",
                "top-bar widget construction belongs to the focused screen initializer");
    }

    @Test
    void activityDoesNotRetainMenuPanelConstructionDetails() throws IOException {
        TestAssertions.assertNotContains(sourceFile("MainActivity.java"),
                "private void initMenuPanel()",
                "menu widget construction must not remain in the lifecycle coordinator");
    }

    private static String sourceFile(String fileName) throws IOException {
        String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
        return new String(Files.readAllBytes(Paths.get(projectRoot,
                "src/main/java/io/github/yosk/mdlite/presentation/" + fileName)),
                StandardCharsets.UTF_8);
    }
}
