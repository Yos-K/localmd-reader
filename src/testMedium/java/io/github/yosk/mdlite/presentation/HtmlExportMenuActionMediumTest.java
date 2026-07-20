package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;
import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;

@RunWith(RobolectricTestRunner.class)
public class HtmlExportMenuActionMediumTest {

    @Test
    public void freeReaderDoesNotOfferHtmlExportForAnOpenFile() {
        MainActivity activity = activityWithOpenFile();
        activity.featureEntitlement = FeatureEntitlement.free();

        boolean visible = MainMenuActions.exportAsHtml().visible(activity);

        assertFalse("Free must not expose the Pro HTML export action", visible);
    }

    @Test
    public void proReaderOffersHtmlExportForAnOpenFile() {
        MainActivity activity = activityWithOpenFile();
        activity.featureEntitlement = FeatureEntitlement.pro();

        boolean visible = MainMenuActions.exportAsHtml().visible(activity);

        assertTrue("Pro must expose HTML export for the active file", visible);
    }

    @Test
    public void proReaderDoesNotOfferHtmlExportForTheWelcomeDocument() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        activity.featureEntitlement = FeatureEntitlement.pro();

        boolean visible = MainMenuActions.exportAsHtml().visible(activity);

        assertFalse("The generated welcome document must not be exported", visible);
    }

    @Test
    public void proReaderOffersHtmlExportForAClipboardDraft() {
        MainActivity activity = activityWithClipboardDraft();
        activity.featureEntitlement = FeatureEntitlement.pro();

        boolean visible = MainMenuActions.exportAsHtml().visible(activity);

        assertTrue("Pro must expose HTML export for temporary user Markdown", visible);
    }

    @Test
    public void exportingAnOpenFileRequestsAPortableHtmlDocument() {
        MainActivity activity = activityWithOpenFile();
        activity.featureEntitlement = FeatureEntitlement.pro();

        MainMenuActions.exportAsHtml().perform(activity);

        ShadowActivity.IntentForResult request = shadowOf(activity).getNextStartedActivityForResult();
        assertEquals("HTML export must use Android document creation",
                Intent.ACTION_CREATE_DOCUMENT, request.intent.getAction());
        assertEquals("HTML export must declare its portable content type",
                "text/html", request.intent.getType());
        assertEquals("HTML export must replace the Markdown extension",
                "notes.html", request.intent.getStringExtra(Intent.EXTRA_TITLE));
        assertEquals("HTML export must use its own result route",
                MainActivity.REQUEST_EXPORT_HTML, request.requestCode);
    }

    @Test
    public void directExportRequestCannotBypassFreeEntitlement() {
        MainActivity activity = activityWithOpenFile();
        activity.featureEntitlement = FeatureEntitlement.free();

        activity.exportActiveDocumentAsHtml();

        assertNull("Free entitlement must not reach Android document creation",
                shadowOf(activity).getNextStartedActivityForResult());
    }

    @Test
    public void exportingAClipboardDraftUsesAnHtmlFileName() {
        MainActivity activity = activityWithClipboardDraft();
        activity.featureEntitlement = FeatureEntitlement.pro();

        MainMenuActions.exportAsHtml().perform(activity);

        ShadowActivity.IntentForResult request = shadowOf(activity).getNextStartedActivityForResult();
        assertEquals("Temporary Markdown export must use an HTML file name",
                "Clipboard.html", request.intent.getStringExtra(Intent.EXTRA_TITLE));
    }

    private static MainActivity activityWithOpenFile() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        activity.openTabs = activity.openTabs.open(OpenDocumentTab.fileDocument(
                "notes.md",
                "content://documents/notes.md",
                SafeHtml.fromTrustedRendererOutput("<h1>Notes</h1>")));
        return activity;
    }

    private static MainActivity activityWithClipboardDraft() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        activity.openTabs = activity.openTabs.open(OpenDocumentTab.clipboardDraft(
                "Clipboard.md",
                "draft://clipboard",
                SafeHtml.fromTrustedRendererOutput("<h1>Clipboard</h1>")));
        return activity;
    }
}
