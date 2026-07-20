package io.github.yosk.mdlite.presentation;

import android.net.Uri;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.github.yosk.mdlite.file.MarkdownLibraryItem;
import io.github.yosk.mdlite.file.MarkdownLibraryListing;
import io.github.yosk.mdlite.file.MarkdownLibraryLocation;
import io.github.yosk.mdlite.file.MarkdownLibraryQuery;
import io.github.yosk.mdlite.model.MarkdownLibraryPanelState;
import java.util.List;

final class MarkdownLibraryMenuTree extends LinearLayout implements TextWatcher {
    private final MainActivity activity;
    private final TextView pathView;
    private final EditText filterInput;
    private final Button chooseAnotherFolderButton;
    private final LinearLayout entriesView;
    private MarkdownLibraryPanelState panelState = MarkdownLibraryPanelState.unselected();

    MarkdownLibraryMenuTree(MainActivity activity) {
        super(activity);
        this.activity = activity;
        setOrientation(VERTICAL);
        setVisibility(GONE);
        setPadding(activity.dp(10), activity.dp(4), 0, activity.dp(8));

        pathView = new TextView(activity);
        pathView.setTextSize(13);
        pathView.setPadding(activity.dp(12), activity.dp(8), activity.dp(12), activity.dp(6));
        addView(pathView, matchWidth());

        chooseAnotherFolderButton = rowButton(activity.viewerText.chooseAnotherFolder(),
                new ChooseAnotherFolderClickListener(activity));
        addView(chooseAnotherFolderButton, matchWidth());

        filterInput = new EditText(activity);
        filterInput.setSingleLine(true);
        filterInput.setInputType(InputType.TYPE_CLASS_TEXT);
        filterInput.setTextSize(14);
        filterInput.setPadding(activity.dp(12), activity.dp(8), activity.dp(12), activity.dp(8));
        filterInput.addTextChangedListener(this);
        LinearLayout.LayoutParams filterParams = matchWidth();
        filterParams.setMargins(activity.dp(8), activity.dp(4), activity.dp(8), activity.dp(6));
        addView(filterInput, filterParams);

        entriesView = new LinearLayout(activity);
        entriesView.setOrientation(VERTICAL);
        addView(entriesView, matchWidth());
        refreshStyle();
    }

    // interaction-surface: markdown-library-tree
    void show(MarkdownLibraryLocation nextLocation, MarkdownLibraryListing nextListing) {
        if (nextLocation == null || nextListing == null) {
            throw new IllegalArgumentException("Markdown library menu content must not be null");
        }
        MarkdownLibraryPanelState.Expanded expanded =
                MarkdownLibraryPanelState.expanded(nextLocation, nextListing);
        panelState = expanded;
        renderContent(expanded.content());
        filterInput.setHint(activity.viewerText.libraryFilterHint());
        chooseAnotherFolderButton.setText(activity.viewerText.chooseAnotherFolder());
        filterInput.setText("");
        renderEntries(nextLocation, nextListing, expanded.content().visibleListing());
        setVisibility(VISIBLE);
        refreshStyle();
    }

    // interaction-command: collapse_library_tree
    // interaction-command: expand_library_tree
    boolean toggleLoadedTree() {
        TogglePanelHandler handler = new TogglePanelHandler();
        panelState.handle(handler);
        return handler.handled;
    }

    boolean isExpanded() {
        ExpansionHandler handler = new ExpansionHandler();
        panelState.handle(handler);
        return handler.expanded;
    }

    void refreshStyle() {
        pathView.setTextColor(activity.mutedColor());
        chooseAnotherFolderButton.setTextColor(activity.textColor());
        chooseAnotherFolderButton.setBackground(activity.makeRowRippleBackground());
        filterInput.setTextColor(activity.textColor());
        filterInput.setHintTextColor(activity.mutedColor());
        filterInput.setBackground(activity.makePlainRoundedBackground(
                activity.surfaceColor(), activity.borderColor(), 8));
        styleEntryChildren();
    }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        panelState = panelState.withQuery(
                MarkdownLibraryQuery.from(text == null ? "" : text.toString()));
        panelState.handle(new FilterEntriesHandler());
    }

    @Override
    public void afterTextChanged(Editable text) {
    }

    private void renderContent(MarkdownLibraryPanelState.Content content) {
        pathView.setText(content.location().path().compactJoin(" / ", "...", 4));
    }

    private void renderEntries(MarkdownLibraryLocation currentLocation,
            MarkdownLibraryListing completeListing, MarkdownLibraryListing visibleListing) {
        entriesView.removeAllViews();
        if (currentLocation instanceof MarkdownLibraryLocation.NestedLocation) {
            entriesView.addView(rowButton(activity.viewerText.upOneFolder(),
                    new ParentFolderClickListener(activity, currentLocation)), matchWidth());
        }
        List<MarkdownLibraryItem> items = visibleListing.items();
        for (int i = 0; i < items.size(); i++) {
            MarkdownLibraryItem item = items.get(i);
            String label = item instanceof MarkdownLibraryItem.DirectoryItem
                    ? activity.viewerText.folderEntry(item.displayName())
                    : item.displayName();
            entriesView.addView(rowButton(label,
                    new LibraryItemClickListener(activity, currentLocation, item)), matchWidth());
        }
        if (items.isEmpty()) {
            TextView empty = new TextView(activity);
            empty.setText(completeListing.items().isEmpty()
                    ? activity.viewerText.noMarkdownFilesInFolder()
                    : activity.viewerText.noMatchingLibraryItems());
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(activity.dp(12), activity.dp(20), activity.dp(12), activity.dp(20));
            entriesView.addView(empty, matchWidth());
        }
        styleEntryChildren();
    }

    private final class FilterEntriesHandler implements MarkdownLibraryPanelState.Handler {
        @Override
        public void unselected() {
        }

        @Override
        public void expanded(MarkdownLibraryPanelState.Content content) {
            renderFiltered(content);
        }

        @Override
        public void collapsed(MarkdownLibraryPanelState.Content content) {
            renderFiltered(content);
        }

        private void renderFiltered(MarkdownLibraryPanelState.Content content) {
            renderEntries(content.location(), content.listing(), content.visibleListing());
        }
    }

    private final class TogglePanelHandler implements MarkdownLibraryPanelState.Handler {
        private boolean handled;

        @Override
        public void unselected() {
            handled = false;
        }

        @Override
        public void expanded(MarkdownLibraryPanelState.Content content) {
            panelState = panelState.toggled();
            setVisibility(GONE);
            handled = true;
        }

        @Override
        public void collapsed(MarkdownLibraryPanelState.Content content) {
            panelState = panelState.toggled();
            setVisibility(VISIBLE);
            handled = true;
        }
    }

    private static final class ExpansionHandler implements MarkdownLibraryPanelState.Handler {
        private boolean expanded;

        @Override
        public void unselected() {
            expanded = false;
        }

        @Override
        public void expanded(MarkdownLibraryPanelState.Content content) {
            expanded = true;
        }

        @Override
        public void collapsed(MarkdownLibraryPanelState.Content content) {
            expanded = false;
        }
    }

    private Button rowButton(String text, View.OnClickListener listener) {
        Button button = new Button(activity);
        button.setText(text);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        button.setPadding(activity.dp(14), activity.dp(9), activity.dp(14), activity.dp(9));
        button.setOnClickListener(listener);
        return button;
    }

    private void styleEntryChildren() {
        for (int i = 0; i < entriesView.getChildCount(); i++) {
            View child = entriesView.getChildAt(i);
            if (child instanceof TextView) {
                TextView text = (TextView) child;
                text.setTextColor(child instanceof Button
                        ? activity.textColor() : activity.mutedColor());
                if (child instanceof Button) {
                    child.setBackground(activity.makeRowRippleBackground());
                }
            }
        }
    }

    private static LinearLayout.LayoutParams matchWidth() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private static final class ChooseAnotherFolderClickListener implements View.OnClickListener {
        private final MainActivity activity;

        private ChooseAnotherFolderClickListener(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(View view) {
            activity.chooseAnotherFolder();
        }
    }

    private static final class ParentFolderClickListener implements View.OnClickListener {
        private final MainActivity activity;
        private final MarkdownLibraryLocation location;

        private ParentFolderClickListener(MainActivity activity, MarkdownLibraryLocation location) {
            this.activity = activity;
            this.location = location;
        }

        @Override
        public void onClick(View view) {
            activity.documentOpener.openProjectLibrary(location.back());
        }
    }

    private static final class LibraryItemClickListener implements View.OnClickListener {
        private final MainActivity activity;
        private final MarkdownLibraryLocation location;
        private final MarkdownLibraryItem item;

        private LibraryItemClickListener(MainActivity activity, MarkdownLibraryLocation location,
                MarkdownLibraryItem item) {
            this.activity = activity;
            this.location = location;
            this.item = item;
        }

        @Override
        public void onClick(View view) {
            if (item instanceof MarkdownLibraryItem.DirectoryItem) {
                activity.documentOpener.openProjectLibrary(location.enter(
                        (MarkdownLibraryItem.DirectoryItem) item));
                return;
            }
            activity.documentOpener.openUri(Uri.parse(item.uri()), true);
            activity.closeMenu();
        }
    }
}
