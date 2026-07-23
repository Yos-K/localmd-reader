package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.file.RestorableOpenTab;
import io.github.yosk.mdlite.file.RestorableOpenTabs;
import io.github.yosk.mdlite.testing.TestAssertions;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.OpenDocumentTabs;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public final class RestoredOpenDocumentTabsTest {
    private static final OpenDocumentTab WELCOME = OpenDocumentTab.welcome(
            "Welcome", "app://welcome", SafeHtml.fromTrustedRendererOutput("welcome"));

    @Test
    void emptyPersistenceRestoresTheWelcomeDocument() {
        OpenDocumentTabs restored = RestoredOpenDocumentTabs.restore(
                RestorableOpenTabs.empty(), WELCOME, new ConfiguredStoredTabs());

        TestAssertions.assertEquals("app://welcome", restored.activeTab().uri(),
                "an empty saved session must have a useful initial document");
    }

    @Test
    void missingStoredDocumentIsRemovedFromTheRestoredSession() {
        RestorableOpenTabs stored = RestorableOpenTabs.from(Arrays.asList(
                RestorableOpenTab.of("missing", "file://missing"),
                RestorableOpenTab.of("selected", "file://selected")), 1);

        OpenDocumentTabs restored = RestoredOpenDocumentTabs.restore(
                stored, WELCOME, loaderWithMissingAndReadable("file://missing", "file://selected"));

        TestAssertions.assertEquals(1, restored.tabs().size(),
                "an unavailable saved document must not leave an invalid tab");
    }

    @Test
    void removingAnEarlierMissingTabPreservesTheSelectedReadableDocument() {
        RestorableOpenTabs stored = RestorableOpenTabs.from(Arrays.asList(
                RestorableOpenTab.of("missing", "file://missing"),
                RestorableOpenTab.of("selected", "file://selected")), 1);

        OpenDocumentTabs restored = RestoredOpenDocumentTabs.restore(
                stored, WELCOME, loaderWithMissingAndReadable("file://missing", "file://selected"));

        TestAssertions.assertEquals("file://selected", restored.activeTab().uri(),
                "removing an earlier tab must preserve the selected readable document");
    }

    @Test
    void missingSelectedDocumentFallsBackToTheNearestReadableDocument() {
        RestorableOpenTabs stored = RestorableOpenTabs.from(Arrays.asList(
                RestorableOpenTab.of("readable", "file://readable"),
                RestorableOpenTab.of("missing", "file://missing")), 1);

        OpenDocumentTabs restored = RestoredOpenDocumentTabs.restore(
                stored, WELCOME, loaderWithMissingAndReadable("file://missing", "file://readable"));

        TestAssertions.assertEquals("file://readable", restored.activeTab().uri(),
                "an unavailable selected document must fall back to an existing tab");
    }

    private static RestoredOpenDocumentTabs.Loader loaderWithMissingAndReadable(
            String missingUri, String readableUri) {
        Map<String, RestoredOpenDocumentTab> configured = new HashMap<String, RestoredOpenDocumentTab>();
        configured.put(missingUri, RestoredOpenDocumentTab.unavailable());
        configured.put(readableUri, RestoredOpenDocumentTab.available(OpenDocumentTab.fileDocument(
                "readable", readableUri, SafeHtml.fromTrustedRendererOutput("readable"))));
        return new ConfiguredStoredTabs(configured);
    }

    private static final class ConfiguredStoredTabs implements RestoredOpenDocumentTabs.Loader {
        private final Map<String, RestoredOpenDocumentTab> configured;

        private ConfiguredStoredTabs() {
            this(new HashMap<String, RestoredOpenDocumentTab>());
        }

        private ConfiguredStoredTabs(Map<String, RestoredOpenDocumentTab> configured) {
            this.configured = configured;
        }

        @Override
        public RestoredOpenDocumentTab load(RestorableOpenTab storedTab) {
            return configured.get(storedTab.uri());
        }
    }
}
