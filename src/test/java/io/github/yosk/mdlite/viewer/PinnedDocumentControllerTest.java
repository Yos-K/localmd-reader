package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.testing.TestAssertions;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

public final class PinnedDocumentControllerTest {
    @Test
    void pinningAFilePersistsItAndRefreshesEveryProjection() {
        FakePinnedDocumentRepository repository = new FakePinnedDocumentRepository();
        FakePinnedDocumentHost host = new FakePinnedDocumentHost(true);
        PinnedDocumentController controller = new PinnedDocumentController(repository, host);
        controller.pin(fileTab());
        TestAssertions.assertEquals("content://guide|Pinned current file.|1",
                repository.onlyUri() + "|" + host.message + "|" + host.refreshCount,
                "pinning must persist the file and refresh tab and menu projections together");
    }

    @Test
    void unpinningAFileRemovesOnlyThatBookmarkAndRefreshesUi() {
        FakePinnedDocumentRepository repository = new FakePinnedDocumentRepository();
        repository.pinDocument("Guide", "content://guide");
        FakePinnedDocumentHost host = new FakePinnedDocumentHost(true);
        PinnedDocumentController controller = new PinnedDocumentController(repository, host);
        controller.unpin(fileTab());
        TestAssertions.assertEquals("0|Unpinned current file.|1",
                repository.size() + "|" + host.message + "|" + host.refreshCount,
                "unpinning must remove the selected bookmark and refresh every projection");
    }

    @Test
    void freeEntitlementMakesFileTabPinningUnavailable() {
        PinnedDocumentController controller = new PinnedDocumentController(
                new FakePinnedDocumentRepository(), new FakePinnedDocumentHost(false));
        TestAssertions.assertTrue(controller.decision(fileTab()) instanceof TabPinningDecision.Unavailable,
                "pinning interaction must remain unavailable without its entitlement");
    }

    @Test
    void clearingPinsUsesTheSameUiCompletionBoundary() {
        FakePinnedDocumentRepository repository = new FakePinnedDocumentRepository();
        repository.pinDocument("Guide", "content://guide");
        FakePinnedDocumentHost host = new FakePinnedDocumentHost(true);
        PinnedDocumentController controller = new PinnedDocumentController(repository, host);
        controller.clear();
        TestAssertions.assertEquals("0|Pinned files cleared.|1",
                repository.size() + "|" + host.message + "|" + host.refreshCount,
                "clearing pins must update persistence and every visible projection once");
    }

    private static OpenDocumentTab.FileDocumentTab fileTab() {
        return OpenDocumentTab.fileDocument("Guide", "content://guide",
                SafeHtml.fromTrustedRendererOutput("guide"));
    }

    private static final class FakePinnedDocumentHost implements PinnedDocumentController.Host {
        private final boolean available;
        private int refreshCount;
        private String message = "";
        private FakePinnedDocumentHost(boolean available) { this.available = available; }
        @Override public boolean pinnedDocumentsAvailable() { return available; }
        @Override public ViewerText viewerText() {
            return ViewerText.fromLanguage(ViewerLanguage.english());
        }
        @Override public void refreshPinnedDocuments(String message) {
            refreshCount++;
            this.message = message;
        }
    }

    private static final class FakePinnedDocumentRepository implements PinnedDocumentRepository {
        private final Set<String> uris = new HashSet<String>();
        @Override public void pinDocument(String displayName, String uri) { uris.add(uri); }
        @Override public void unpinDocument(String uri) { uris.remove(uri); }
        @Override public boolean isPinnedDocument(String uri) { return uris.contains(uri); }
        @Override public void clearPinnedDocuments() { uris.clear(); }
        private int size() { return uris.size(); }
        private String onlyUri() { return uris.iterator().next(); }
    }
}
