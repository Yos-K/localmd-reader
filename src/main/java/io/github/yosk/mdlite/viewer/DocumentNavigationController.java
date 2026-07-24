package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.HeadingNavigation;
import io.github.yosk.mdlite.domain.HeadingScrollPosition;
import io.github.yosk.mdlite.domain.MarkdownHeading;
import io.github.yosk.mdlite.domain.MarkdownHeadings;
import io.github.yosk.mdlite.domain.TableOfContentsItems;

public final class DocumentNavigationController implements HeadingNavigation.Handler {
    public interface Host {
        void showSearchBar();

        void findAll(String query);

        void findNext(boolean forward);

        void clearSearchMatches();

        void synchronizeSearchBar();

        MarkdownHeadings activeHeadings();

        HeadingScrollPosition headingScrollPosition();

        void openHeading(MarkdownHeading heading);
    }

    private final Host host;
    private DocumentSearchSession searchSession = DocumentSearchSession.empty();

    public DocumentNavigationController(Host host) {
        if (host == null) {
            throw new IllegalArgumentException("document navigation host must not be null");
        }
        this.host = host;
    }

    public void showSearchBar() {
        host.showSearchBar();
    }

    public void search(DocumentSearchQuery query) {
        searchSession = searchSession.search(query);
        if (searchSession.hasActiveQuery()) {
            host.findAll(searchSession.queryText());
        }
    }

    public boolean hasActiveSearch() {
        return searchSession.hasActiveQuery();
    }

    public String searchQueryText() {
        return searchSession.queryText();
    }

    public void nextSearchResult() {
        if (searchSession.hasActiveQuery()) {
            host.findNext(true);
        }
    }

    public void previousSearchResult() {
        if (searchSession.hasActiveQuery()) {
            host.findNext(false);
        }
    }

    public void clearSearchMatches() {
        host.clearSearchMatches();
    }

    public void resetForDocument() {
        searchSession = searchSession.clear();
        host.synchronizeSearchBar();
    }

    public MarkdownHeadings activeHeadings() {
        return host.activeHeadings();
    }

    public void jumpTo(MarkdownHeading heading) {
        HeadingNavigation.selected(items(), heading).handle(this);
    }

    public void jumpToNextHeading() {
        activeHeadingNavigation().next().handle(this);
    }

    public void jumpToPreviousHeading() {
        activeHeadingNavigation().previous().handle(this);
    }

    @Override
    public void unavailable() {
    }

    @Override
    public void destination(MarkdownHeading heading) {
        host.openHeading(heading);
    }

    private HeadingNavigation activeHeadingNavigation() {
        return HeadingNavigation.from(items(), host.headingScrollPosition());
    }

    private TableOfContentsItems items() {
        return TableOfContentsItems.from(host.activeHeadings());
    }
}
