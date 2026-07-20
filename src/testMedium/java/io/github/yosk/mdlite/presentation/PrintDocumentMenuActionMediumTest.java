package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.webkit.WebView;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PrintDocumentMenuActionMediumTest {

    @Test
    public void freeReaderDoesNotOfferPrintingForAnOpenFile() {
        MainActivity activity = activityWithOpenFile();
        activity.featureEntitlement = FeatureEntitlement.free();

        boolean visible = PrintDocumentMenuActions.printOrSavePdf().visible(activity);

        assertFalse("Free must not expose the Pro print action", visible);
    }

    @Test
    public void proReaderOffersPrintingForAnOpenFile() {
        MainActivity activity = activityWithOpenFile();
        activity.featureEntitlement = FeatureEntitlement.pro();

        boolean visible = PrintDocumentMenuActions.printOrSavePdf().visible(activity);

        assertTrue("Pro must expose printing for the active file", visible);
    }

    @Test
    public void proReaderDoesNotOfferPrintingForTheWelcomeDocument() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        activity.featureEntitlement = FeatureEntitlement.pro();

        boolean visible = PrintDocumentMenuActions.printOrSavePdf().visible(activity);

        assertFalse("The generated welcome document must not be printed", visible);
    }

    @Test
    public void proReaderOffersPrintingForAClipboardDraft() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        activity.openTabs = activity.openTabs.open(OpenDocumentTab.clipboardDraft(
                "Clipboard.md",
                "draft://clipboard",
                SafeHtml.fromTrustedRendererOutput("<h1>Clipboard</h1>")));
        activity.featureEntitlement = FeatureEntitlement.pro();

        boolean visible = PrintDocumentMenuActions.printOrSavePdf().visible(activity);

        assertTrue("Pro must expose printing for temporary user Markdown", visible);
    }

    @Test
    public void printingAnOpenFileDelegatesTheRenderedDocumentAndTitle() {
        MainActivity activity = activityWithOpenFile();
        activity.featureEntitlement = FeatureEntitlement.pro();
        RecordingDocumentPrintLauncher launcher = new RecordingDocumentPrintLauncher();
        activity.documentPrintLauncher = launcher;

        PrintDocumentMenuActions.printOrSavePdf().perform(activity);

        assertSame("Printing must use the reader WebView", activity.webView, launcher.printedWebView);
        assertEquals("Printing must preserve the active document title", "notes.md", launcher.documentTitle);
    }

    private static MainActivity activityWithOpenFile() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        activity.openTabs = activity.openTabs.open(OpenDocumentTab.fileDocument(
                "notes.md",
                "content://documents/notes.md",
                SafeHtml.fromTrustedRendererOutput("<h1>Notes</h1>")));
        return activity;
    }

    private static final class RecordingDocumentPrintLauncher implements DocumentPrintLauncher {
        private WebView printedWebView;
        private String documentTitle;

        @Override
        public void print(WebView webView, String title) {
            printedWebView = webView;
            documentTitle = title;
        }
    }
}
