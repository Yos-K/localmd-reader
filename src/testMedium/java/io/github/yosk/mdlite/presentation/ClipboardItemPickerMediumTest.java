package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;

import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.ClipboardMarkdownItem;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;

@RunWith(RobolectricTestRunner.class)
public class ClipboardItemPickerMediumTest {

    @Test
    public void twoClipboardItemsDisplayPickerWithTwoChoicesAndConfirmAction() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("first", "# First");
        clip.addItem(new ClipData.Item("# Second"));
        clipboard.setPrimaryClip(clip);

        activity.clipboardDocumentCreator.createMarkdownFromClipboard();

        AlertDialog picker = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull("multiple clipboard items must open the modeled picker surface", picker);
        assertEquals("the picker must expose every clipboard item",
                2, picker.getListView().getAdapter().getCount());
        assertNotNull("the picker must expose the modeled completion command",
                picker.getButton(DialogInterface.BUTTON_POSITIVE));
    }

    @Test
    public void confirmingTwoSelectedClipboardItemsOpensTwoTemporaryTabs() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        List<ClipboardMarkdownItem> items = Arrays.asList(
                new ClipboardMarkdownItem("First", "# First"),
                new ClipboardMarkdownItem("Second", "# Second"));
        int tabsBeforeSelection = activity.openTabs().tabs().size();

        activity.clipboardDocumentCreator.openSelectedClipboardItems(
                items, new boolean[] {true, true});

        assertEquals("confirming two selected items must append two tabs",
                tabsBeforeSelection + 2, activity.openTabs().tabs().size());
        assertTrue("the selected clipboard content must open as a temporary draft tab",
                activity.openTabs().activeTab() instanceof OpenDocumentTab.ClipboardDraftTab);
    }
}
