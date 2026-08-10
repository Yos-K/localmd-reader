package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class TermuxOpenIntentStructureTest {
    @Nested
    final class WhenTermuxCannotDeliverStringArrayExtras {
        @Test
        void activityPublishesAScalarBatchBase64IntentContract() throws IOException {
            String activity = source("MainActivity.java");

            TestAssertions.assertContains(activity, "ACTION_OPEN_TEXTS_BASE64",
                    "Termux must have an action that does not depend on unsupported string-array extras");
            TestAssertions.assertContains(activity, "EXTRA_MARKDOWN_DOCUMENTS_BASE64",
                    "the compatible action must transport Markdown without shell quoting loss");
        }

        @Test
        void documentOpenerDecodesTheCompatiblePayloadBeforeOpeningTheTab() throws IOException {
            String opener = source("DocumentOpener.java").replaceAll("\\s+", " ");

            TestAssertions.assertContains(opener,
                    "MainActivity.ACTION_OPEN_TEXTS_BASE64.equals(action)",
                    "incoming compatible actions must be handled explicitly");
            TestAssertions.assertContains(opener,
                    "openEncodedMarkdownDocuments( intent.getStringExtra(MainActivity.EXTRA_MARKDOWN_DOCUMENTS_BASE64))",
                    "the transported batch must be decoded before opening its documents");
        }
    }

    private static String source(String name) throws IOException {
        String projectRoot = System.getProperty("user.dir").replaceFirst("/app$", "");
        return new String(Files.readAllBytes(Paths.get(
                projectRoot, "src/main/java/io/github/yosk/mdlite/presentation", name)),
                StandardCharsets.UTF_8);
    }
}
