package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public final class ReaderAppearanceStructureTest {
    @Test
    void activityDelegatesCompleteThemeApplication() throws IOException {
        TestAssertions.assertContains(sourceFile("MainActivity.java"),
                "readerAppearance.applyCurrentTheme()",
                "the lifecycle coordinator must delegate native theme rendering");
    }

    @Test
    void appearanceOwnerAppliesSystemBarColorsFromTheCurrentPalette() throws IOException {
        TestAssertions.assertContains(sourceFile("ReaderAppearance.java"),
                "SystemBarsTheme.apply(activity.getWindow(), activity.viewerPalette)",
                "system bars and reader surfaces must share the same complete palette");
    }

    @Test
    void activityDoesNotRetainIndividualRootBackgroundStyling() throws IOException {
        TestAssertions.assertNotContains(sourceFile("MainActivity.java"),
                "root.setBackgroundColor(backgroundColor())",
                "individual native surface styling belongs to the appearance owner");
    }

    private static String sourceFile(String fileName) throws IOException {
        String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
        return new String(Files.readAllBytes(Paths.get(projectRoot,
                "src/main/java/io/github/yosk/mdlite/presentation/" + fileName)),
                StandardCharsets.UTF_8);
    }
}
