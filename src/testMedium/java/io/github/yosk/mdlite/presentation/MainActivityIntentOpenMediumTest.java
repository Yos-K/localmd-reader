package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceResponse;

import io.github.yosk.mdlite.domain.DocumentRenderingProfile;
import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.OpenDocumentTabs;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Medium-tier test: assert that opening a Markdown document via an Intent works
 * as behaviour, on the JVM (Robolectric), without an emulator.
 *
 * The Intent-open path (a file handed to the app via ACTION_VIEW) is currently
 * exercised only by the large device smoke (docs/harness/test-strategy.md, L3/L4),
 * which is flaky, manual, and slow. This pushes that coverage down into the medium
 * tier: it builds the real MainActivity and drives the actual
 * MainActivity.documentOpener over a real file:// URI, so the round trip
 * (read file -> render Markdown -> open tab) is verified every PR in the fast
 * gradle `test` job.
 *
 * A file:// URI is used deliberately: DocumentOpener reads it through a plain
 * FileInputStream (DocumentOpener.openInputStream), so no ContentResolver stub is
 * needed and the assertion stays about behaviour, not mocks.
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivityIntentOpenMediumTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void viewIntentOpensTheMarkdownFileAsTheActiveTab() throws IOException {
        File markdown = writeMarkdown("note.md", "# MediumIntentHeading\n\nbody text\n");
        Uri uri = Uri.fromFile(markdown);

        MainActivity activity = Robolectric.buildActivity(
                MainActivity.class, viewIntent(uri)).setup().get();

        OpenDocumentTab active = activity.openTabs.activeTab();
        assertTrue("ACTION_VIEW must open a file document tab, not the welcome tab",
                active instanceof OpenDocumentTab.FileDocumentTab);
        assertEquals("opened tab title must be the file's display name",
                "note.md", active.title());
        assertEquals("opened tab uri must be the incoming file uri",
                uri.toString(), active.uri());
        assertTrue("opened tab must render the file's Markdown content",
                active.document().value().contains("MediumIntentHeading"));
    }

    @Test
    public void secondViewIntentWhileRunningOpensAnAdditionalActiveTab() throws IOException {
        File first = writeMarkdown("first.md", "# FirstDoc\n");
        ActivityController<MainActivity> controller = Robolectric.buildActivity(
                MainActivity.class, viewIntent(Uri.fromFile(first))).setup();
        MainActivity activity = controller.get();

        int tabsAfterFirst = activity.openTabs.tabs().size();

        File second = writeMarkdown("second.md", "# SecondDoc\n");
        Uri secondUri = Uri.fromFile(second);
        controller.newIntent(viewIntent(secondUri));

        OpenDocumentTabs tabs = activity.openTabs;
        assertEquals("delivering a second ACTION_VIEW must add one more tab",
                tabsAfterFirst + 1, tabs.tabs().size());
        assertEquals("the newly opened document must become the active tab",
                secondUri.toString(), tabs.activeTab().uri());
        assertTrue("the newly opened tab must render the second file's content",
                tabs.activeTab().document().value().contains("SecondDoc"));
    }

    @Test
    public void proRelativeMarkdownLinkOpensTheTargetFileAsTheActiveTab() throws IOException {
        File markdown = writeMarkdown("readme.md", "# Readme\n");
        File guideDirectory = folder.newFolder("guide");
        File target = new File(guideDirectory, "intro.md");
        Files.write(target.toPath(), "# Intro\n".getBytes(StandardCharsets.UTF_8));
        MainActivity activity = Robolectric.buildActivity(
                MainActivity.class, viewIntent(Uri.fromFile(markdown))).setup().get();
        activity.documentRenderingProfile = DocumentRenderingProfile.fromEntitlement(FeatureEntitlement.pro());
        int tabsBeforeLink = activity.openTabs.tabs().size();

        boolean opened = activity.openActiveRelativeMarkdownLink(
                "https://localmd.local/__relative_markdown__?path=guide%2Fintro.md");

        assertEquals("Pro entitlement must open safe relative Markdown links",
                true, opened);
        assertEquals("opening a relative Markdown link must append the target as a new tab",
                tabsBeforeLink + 1, activity.openTabs.tabs().size());
        assertEquals("opened relative Markdown link must become the active tab",
                Uri.fromFile(target).toString(), activity.openTabs.activeTab().uri());
        assertTrue("opened relative Markdown link must render the target file",
                activity.openTabs.activeTab().document().value().contains("Intro"));
    }

    @Test
    public void proRelativeImageRequestServesAnImageBesideTheActiveFile() throws IOException {
        File markdown = writeMarkdown("readme.md", "# Readme\n![Icon](images/icon.png)\n");
        File imagesDirectory = folder.newFolder("images");
        File image = new File(imagesDirectory, "icon.png");
        Files.write(image.toPath(), new byte[] {(byte) 0x89, 'P', 'N', 'G'});
        MainActivity activity = Robolectric.buildActivity(
                MainActivity.class, viewIntent(Uri.fromFile(markdown))).setup().get();
        activity.documentRenderingProfile = DocumentRenderingProfile.fromEntitlement(FeatureEntitlement.pro());

        WebResourceResponse response = activity.openActiveRelativeImage(
                "https://localmd.local/__relative_image__?path=images%2Ficon.png");

        assertTrue("Pro entitlement must serve safe relative image requests",
                response != null);
        assertEquals("served relative png images must keep the png MIME type",
                "image/png", response.getMimeType());
    }

    private File writeMarkdown(String name, String content) throws IOException {
        File file = folder.newFile(name);
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private Intent viewIntent(Uri uri) {
        return new Intent(Intent.ACTION_VIEW, uri);
    }
}
