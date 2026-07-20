package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MarkdownLibraryPathTest {
    @Test
    void rootLocationPathContainsOnlyTheRootName() {
        MarkdownLibraryLocation location =
                MarkdownLibraryLocation.root("content://tree/project", "Project");

        MarkdownLibraryPath path = location.path();

        TestAssertions.assertEquals(1, path.segments().size(),
                "project root path must contain exactly one segment");
        TestAssertions.assertEquals("Project", path.segments().get(0),
                "project root path must retain the root display name");
        TestAssertions.assertEquals("Project", path.join(" / "),
                "project root breadcrumb must not add a trailing separator");
    }

    @Test
    void nestedLocationPathContainsEveryAncestorInNavigationOrder() {
        MarkdownLibraryLocation location = MarkdownLibraryLocation
                .root("content://tree/project", "Project")
                .enter(MarkdownLibraryItem.directory("notes", "content://tree/project/notes"))
                .enter(MarkdownLibraryItem.directory("archive", "content://tree/project/notes/archive"));

        MarkdownLibraryPath path = location.path();

        TestAssertions.assertEquals(3, path.segments().size(),
                "nested project path must retain root and every entered directory");
        TestAssertions.assertEquals("Project / notes / archive", path.join(" / "),
                "breadcrumb must display navigation order from root to current directory");
    }

    @Test
    void returningToParentRestoresTheParentBreadcrumb() {
        MarkdownLibraryLocation notes = MarkdownLibraryLocation
                .root("content://tree/project", "Project")
                .enter(MarkdownLibraryItem.directory("notes", "content://tree/project/notes"));
        MarkdownLibraryLocation archive = notes.enter(
                MarkdownLibraryItem.directory("archive", "content://tree/project/notes/archive"));

        MarkdownLibraryPath parentPath = archive.back().path();

        TestAssertions.assertEquals("Project / notes", parentPath.join(" / "),
                "back navigation must remove only the current breadcrumb segment");
    }

    @Test
    void breadcrumbRejectsEmptySeparatorBeforeRendering() {
        final MarkdownLibraryPath path =
                MarkdownLibraryLocation.root("content://tree/project", "Project").path();

        TestAssertions.assertThrows(IllegalArgumentException.class,
                new TestAssertions.ThrowingRunnable() {
                    @Override
                    public void run() {
                        path.join("");
                    }
                });
    }

    @Test
    void compactBreadcrumbKeepsRootAndNearestDirectoriesOnDeepPaths() {
        MarkdownLibraryPath path = MarkdownLibraryLocation
                .root("content://tree/project", "Project")
                .enter(MarkdownLibraryItem.directory("docs", "content://tree/project/docs"))
                .enter(MarkdownLibraryItem.directory("design", "content://tree/project/docs/design"))
                .enter(MarkdownLibraryItem.directory("api", "content://tree/project/docs/design/api"))
                .enter(MarkdownLibraryItem.directory("reference", "content://tree/project/docs/design/api/reference"))
                .path();

        TestAssertions.assertEquals("Project / ... / api / reference",
                path.compactJoin(" / ", "...", 4),
                "deep breadcrumb must preserve project context and nearest navigation levels");
    }

    @Test
    void compactBreadcrumbKeepsEverySegmentWhenPathFitsTheLimit() {
        MarkdownLibraryPath path = MarkdownLibraryLocation
                .root("content://tree/project", "Project")
                .enter(MarkdownLibraryItem.directory("notes", "content://tree/project/notes"))
                .path();

        TestAssertions.assertEquals("Project / notes", path.compactJoin(" / ", "...", 4),
                "short breadcrumb must not show a false omission marker");
    }
}
