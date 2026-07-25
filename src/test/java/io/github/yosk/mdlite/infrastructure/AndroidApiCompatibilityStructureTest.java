package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class AndroidApiCompatibilityStructureTest {
    @Nested
    final class GivenTheAppSupportsAndroidApi23 {
        @Test
        void javaLibraryApisAreDesugaredForDevicesBelowTheirPlatformIntroductionLevel() throws IOException {
            String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
            String build = new String(Files.readAllBytes(Paths.get(projectRoot, "app/build.gradle")),
                    StandardCharsets.UTF_8);

            TestAssertions.assertContains(build, "coreLibraryDesugaringEnabled true",
                    "API 23 builds must rewrite newer Java library APIs for older devices");
            TestAssertions.assertContains(build, "coreLibraryDesugaring \"com.android.tools:desugar_jdk_libs:",
                    "API 23 builds must package the Java library API compatibility implementation");
        }
    }
}
