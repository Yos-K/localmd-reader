package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public final class MenuTransitionsD8CompatibilityTest {

    @Test
    void hideWhenClosedEndActionIsStaticToAvoidSyntheticOuterConstructorParameter() throws IOException {
        String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
        String source = new String(
                Files.readAllBytes(Paths.get(
                        projectRoot,
                        "src/main/java/io/github/yosk/mdlite/presentation/MenuTransitions.java")),
                StandardCharsets.UTF_8);

        TestAssertions.assertContains(
                source,
                "private static final class HideWhenClosed",
                "menu transition end action must be static so Termux D8 does not receive a named inner class with an unnamed synthetic outer constructor parameter");
    }
}
