package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class MutationScopeStructureTest {
    @Nested
    final class GivenTestsUseNestedSpecificationContexts {
        @Test
        void mutationTargetsExcludeBothTestContainersAndTheirNestedClasses() throws IOException {
            String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
            String config = new String(Files.readAllBytes(Paths.get(projectRoot, "harness.config.sh")),
                    StandardCharsets.UTF_8);

            TestAssertions.assertContains(config, "EXCLUDED_CLASSES=\"*Test*,*Tests*,*Property*,*Properties*",
                    "mutation scoring must measure production classes rather than nested test classes");
        }
    }
}
