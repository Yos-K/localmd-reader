package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.github.yosk.mdlite.file.FolderDocumentEntry;
import io.github.yosk.mdlite.file.MarkdownLibraryItem;
import io.github.yosk.mdlite.file.MarkdownLibraryListing;
import io.github.yosk.mdlite.file.MarkdownLibraryLocation;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public final class MarkdownLibraryMenuTreeMediumTest {
    @Test
    public void selectedRootRendersItsFolderBeforeItsMarkdownDocument() {
        MarkdownLibraryMenuTree tree = shownRootTree();

        assertEquals("notes/", entryText(tree, 0));
    }

    @Test
    public void nestedLocationRendersParentNavigationBeforeItsChildren() {
        MainActivity activity = activity();
        MarkdownLibraryLocation root = MarkdownLibraryLocation.root("content://tree/root", "Project");
        MarkdownLibraryLocation nested = root.enter(
                (MarkdownLibraryItem.DirectoryItem) MarkdownLibraryItem.directory(
                        "notes", "content://tree/root/document/notes"));
        MarkdownLibraryMenuTree tree = new MarkdownLibraryMenuTree(activity);

        tree.show(nested, MarkdownLibraryListing.from(Arrays.asList(
                FolderDocumentEntry.markdownFile("inside.md", "content://inside"))));

        assertEquals(activity.viewerText.upOneFolder(), entryText(tree, 0));
    }

    @Test
    public void filterKeepsOnlyItemsWhoseNamesMatchTheQuery() {
        MarkdownLibraryMenuTree tree = shownRootTree();
        EditText filter = (EditText) tree.getChildAt(2);

        filter.setText("readme");

        assertEquals(1, entries(tree).getChildCount());
    }

    @Test
    public void selectedLibraryTreeBecomesVisibleInsideTheMenu() {
        MarkdownLibraryMenuTree tree = shownRootTree();

        assertEquals(View.VISIBLE, tree.getVisibility());
    }

    @Test
    public void tappingAnExpandedLibraryActionCollapsesTheSelectedTree() {
        MarkdownLibraryMenuTree tree = shownRootTree();

        tree.toggleLoadedTree();

        assertEquals(View.GONE, tree.getVisibility());
    }

    @Test
    public void tappingACollapsedLibraryActionReopensTheSameSelectedTree() {
        MarkdownLibraryMenuTree tree = shownRootTree();
        tree.toggleLoadedTree();

        tree.toggleLoadedTree();

        assertEquals("notes/", entryText(tree, 0));
    }

    @Test
    public void libraryActionCannotToggleBeforeAFolderHasBeenSelected() {
        MarkdownLibraryMenuTree tree = new MarkdownLibraryMenuTree(activity());

        boolean toggled = tree.toggleLoadedTree();

        assertEquals(false, toggled);
    }

    private static MarkdownLibraryMenuTree shownRootTree() {
        MainActivity activity = activity();
        MarkdownLibraryMenuTree tree = new MarkdownLibraryMenuTree(activity);
        tree.show(MarkdownLibraryLocation.root("content://tree/root", "Project"),
                MarkdownLibraryListing.from(Arrays.asList(
                        FolderDocumentEntry.markdownFile("README.md", "content://readme"),
                        FolderDocumentEntry.directory("notes", "content://notes"))));
        return tree;
    }

    private static MainActivity activity() {
        return Robolectric.buildActivity(MainActivity.class).setup().get();
    }

    private static LinearLayout entries(MarkdownLibraryMenuTree tree) {
        return (LinearLayout) tree.getChildAt(3);
    }

    private static String entryText(MarkdownLibraryMenuTree tree, int index) {
        return ((TextView) entries(tree).getChildAt(index)).getText().toString();
    }
}
