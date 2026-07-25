package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public final class CustomGestureDrawingInsetsStructureTest {
    @Test
    void drawingViewReceivesTheInsetAlreadyObservedByTheActivity() throws IOException {
        String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
        String dialogs = new String(Files.readAllBytes(Paths.get(projectRoot,
                "src/main/java/io/github/yosk/mdlite/presentation/GestureShortcutDialogs.java")),
                StandardCharsets.UTF_8);
        String activity = new String(Files.readAllBytes(Paths.get(projectRoot,
                "src/main/java/io/github/yosk/mdlite/presentation/MainActivity.java")),
                StandardCharsets.UTF_8);

        TestAssertions.assertContains(dialogs, "activity.systemTopInsetPx()",
                "the drawing overlay must receive the Activity's already-dispatched system inset");
        TestAssertions.assertContains(activity, "int systemTopInsetPx()",
                "the Activity must expose its observed inset to focused presentation collaborators");
        TestAssertions.assertContains(activity, "registerOnBackInvokedCallback",
                "custom gesture drawing must intercept the modern Android back dispatcher");
        TestAssertions.assertContains(dialogs, "activity.registerCustomGestureBackCallback()",
                "starting custom gesture drawing must register its modern back callback");
        TestAssertions.assertContains(dialogs, "activity.unregisterCustomGestureBackCallback()",
                "finishing custom gesture drawing must remove its modern back callback");
    }

    @Test
    void drawingViewExposesItsCancellationActionToAssistiveTechnology() throws IOException {
        String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
        String drawingView = new String(Files.readAllBytes(Paths.get(projectRoot,
                "src/main/java/io/github/yosk/mdlite/presentation/CustomGestureDrawingView.java")),
                StandardCharsets.UTF_8);

        TestAssertions.assertContains(drawingView,
                "setContentDescription(instruction + \". \" + cancelLabel)",
                "assistive technology must identify both the drawing instruction and cancel action");
        TestAssertions.assertContains(drawingView, "public boolean performClick()",
                "assistive technology must be able to invoke the visible cancel action");
        TestAssertions.assertContains(drawingView, "performClick();",
                "touch and assistive actions must use the same cancellation behavior");
    }
}
