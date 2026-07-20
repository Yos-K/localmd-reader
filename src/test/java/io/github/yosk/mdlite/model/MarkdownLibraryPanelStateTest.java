package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.file.FolderDocumentEntry;
import io.github.yosk.mdlite.file.MarkdownLibraryListing;
import io.github.yosk.mdlite.file.MarkdownLibraryLocation;
import io.github.yosk.mdlite.file.MarkdownLibraryQuery;
import io.github.yosk.mdlite.testing.TestAssertions;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public final class MarkdownLibraryPanelStateTest {
    @Test
    void togglingAnUnselectedLibraryPreservesItsSelectionRequiredState() {
        MarkdownLibraryPanelState.Unselected unselected = MarkdownLibraryPanelState.unselected();

        TestAssertions.assertSame(unselected, unselected.toggled(),
                "an unselected library cannot invent loaded content while toggling");
    }

    @Test
    void selectedLibraryStartsExpanded() {
        MarkdownLibraryPanelState state = expanded();

        TestAssertions.assertTrue(state instanceof MarkdownLibraryPanelState.Expanded,
                "valid selected content must start as an expanded library panel");
    }

    @Test
    void expandedLibraryCollapsesWithoutLosingItsContent() {
        MarkdownLibraryPanelState.Expanded expanded = expanded();

        MarkdownLibraryPanelState.Collapsed collapsed =
                (MarkdownLibraryPanelState.Collapsed) expanded.toggled();

        TestAssertions.assertSame(expanded.content(), collapsed.content(),
                "collapsing must preserve the exact selected library context");
    }

    @Test
    void collapsedLibraryExpandsWithoutLosingItsContent() {
        MarkdownLibraryPanelState.Collapsed collapsed =
                (MarkdownLibraryPanelState.Collapsed) expanded().toggled();

        MarkdownLibraryPanelState.Expanded reopened =
                (MarkdownLibraryPanelState.Expanded) collapsed.toggled();

        TestAssertions.assertSame(collapsed.content(), reopened.content(),
                "reopening must preserve the exact selected library context");
    }

    @Test
    void filteringAnExpandedLibraryPreservesTheOriginalListing() {
        MarkdownLibraryPanelState.Expanded expanded = expanded();

        MarkdownLibraryPanelState.Expanded filtered = (MarkdownLibraryPanelState.Expanded)
                expanded.withQuery(MarkdownLibraryQuery.from("missing"));

        TestAssertions.assertEquals(1, filtered.content().listing().items().size(),
                "filtering must retain the complete listing for a later query");
        TestAssertions.assertEquals(0, filtered.content().visibleListing().items().size(),
                "the panel state must derive visible entries from its current query");
    }

    @Test
    void collapsingAFilteredLibraryPreservesItsVisibleEntries() {
        MarkdownLibraryPanelState filtered = expanded()
                .withQuery(MarkdownLibraryQuery.from("missing"));

        MarkdownLibraryPanelState.Collapsed collapsed =
                (MarkdownLibraryPanelState.Collapsed) filtered.toggled();

        TestAssertions.assertEquals(0, collapsed.content().visibleListing().items().size(),
                "collapsing must preserve the active library query");
    }

    @Test
    void selectedLibraryRejectsMissingLocation() {
        TestAssertions.assertThrows(IllegalArgumentException.class,
                () -> MarkdownLibraryPanelState.expanded(null, listing()));
    }

    @Test
    void selectedLibraryRejectsMissingListing() {
        TestAssertions.assertThrows(IllegalArgumentException.class,
                () -> MarkdownLibraryPanelState.expanded(location(), null));
    }

    private static MarkdownLibraryPanelState.Expanded expanded() {
        return MarkdownLibraryPanelState.expanded(location(), listing());
    }

    private static MarkdownLibraryLocation location() {
        return MarkdownLibraryLocation.root("content://tree/project", "Project");
    }

    private static MarkdownLibraryListing listing() {
        return MarkdownLibraryListing.from(Collections.singletonList(
                FolderDocumentEntry.markdownFile("README.md", "content://readme")));
    }
}
