package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public final class ActivityResultRoutingStructureTest {
    @Test
    void activityDelegatesActivityResultsToTheLifecycleRouter() throws IOException {
        String source = sourceFile();

        TestAssertions.assertContains(source, "activityResultRouter.handle(requestCode, resultCode, data)",
                "activity result callback must delegate routing to one lifecycle adapter");
    }

    @Test
    void activityDoesNotRetainRequestSpecificResultBranches() throws IOException {
        String source = sourceFile();

        TestAssertions.assertNotContains(source, "REQUEST_OPEN_DOCUMENT && resultCode",
                "request-specific activity result branches must live in the lifecycle adapter");
        TestAssertions.assertNotContains(source, "REQUEST_EXPORT_HTML && resultCode",
                "export result routing must live in the lifecycle adapter");
    }

    private static String sourceFile() throws IOException {
        String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
        return new String(Files.readAllBytes(Paths.get(projectRoot,
                "src/main/java/io/github/yosk/mdlite/presentation/MainActivity.java")),
                StandardCharsets.UTF_8);
    }
}
