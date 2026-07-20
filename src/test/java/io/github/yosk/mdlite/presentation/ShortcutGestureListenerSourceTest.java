package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public final class ShortcutGestureListenerSourceTest {

    @Test
    void shortcutGestureListenerMustAcceptDownEventsSoDoubleTapCanBeRecognized() throws Exception {
        String source = new String(Files.readAllBytes(shortcutGestureListenerSourcePath()));

        TestAssertions.assertContains(source,
                "public boolean onDown(MotionEvent event) {\n        return true;\n    }",
                "shortcut double-tap detection must keep receiving the gesture stream after ACTION_DOWN");
    }

    private static Path shortcutGestureListenerSourcePath() {
        return Arrays.asList(
                Paths.get("src/main/java/io/github/yosk/mdlite/presentation/ShortcutGestureListener.java"),
                Paths.get("../src/main/java/io/github/yosk/mdlite/presentation/ShortcutGestureListener.java"))
                .stream()
                .filter(Files::exists)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }
}
