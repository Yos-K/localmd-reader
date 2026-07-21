package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public final class TableOfContentsMenuPanelMediumTest {
    @Test
    public void renderingTwoHeadingsCreatesTwoSelectableRows() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        activity.documentRenderingSession = activity.documentRenderingSession.open(
                activity.openTabs.activeTab().documentUri(),
                "# Overview\n\n## Details",
                activity.documentRenderingProfile).session();
        TableOfContentsMenuPanel panel = new TableOfContentsMenuPanel(activity);

        panel.run();

        assertEquals("each modeled heading must produce one table-of-contents row",
                2, panel.getChildCount());
    }

    @Test
    public void renderingDocumentWithoutHeadingsCreatesOneEmptyStateRow() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        activity.documentRenderingSession = activity.documentRenderingSession.open(
                activity.openTabs.activeTab().documentUri(),
                "plain text",
                activity.documentRenderingProfile).session();
        TableOfContentsMenuPanel panel = new TableOfContentsMenuPanel(activity);

        panel.run();

        assertEquals("a heading-free document must keep an explicit empty state",
                1, panel.getChildCount());
    }
}
