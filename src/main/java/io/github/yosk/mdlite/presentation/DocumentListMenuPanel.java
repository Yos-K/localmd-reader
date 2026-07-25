package io.github.yosk.mdlite.presentation;

import android.graphics.Typeface;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.github.yosk.mdlite.file.RecentDocument;
import java.util.List;

final class DocumentListMenuPanel extends LinearLayout implements Runnable {
    private final MainActivity activity;
    private final boolean pinned;

    DocumentListMenuPanel(MainActivity activity, boolean pinned) {
        super(activity);
        this.activity = activity;
        this.pinned = pinned;
        setOrientation(VERTICAL);
        setVisibility(View.GONE);
        setPadding(activity.dp(10), 0, 0, activity.dp(6));
    }

    @Override
    public void run() {
        removeAllViews();
        List<RecentDocument> documents = pinned ? activity.tabPersistence.loadPinnedDocuments().items()
                                                : activity.tabPersistence.loadRecentDocuments().items();
        if (documents.isEmpty()) {
            addView(emptyState(), wrapParams());
            return;
        }
        for (RecentDocument document : documents) {
            addView(documentRow(document), wrapParams());
        }
        addView(clearButton(), wrapParams());
    }

    void refreshStyle() {
        run();
    }

    private View documentRow(final RecentDocument document) {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        Button open = compactButton(document.displayName());
        open.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        open.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openDocument(document);
            }
        });
        row.addView(open, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        if (pinned) {
            Button unpin = compactButton(activity.viewerText.unpinPinnedDocumentAction());
            unpin.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    unpinDocument(document);
                }
            });
            row.addView(unpin, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
        return row;
    }

    private Button clearButton() {
        Button clear =
                compactButton(pinned ? activity.viewerText.clearPinnedFiles() : activity.viewerText.clearHistory());
        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clearDocuments();
            }
        });
        return clear;
    }

    void openDocument(RecentDocument document) {
        activity.closeMenu();
        activity.documentOpener.openUri(Uri.parse(document.uri()), true);
    }

    void unpinDocument(RecentDocument document) {
        activity.unpinPinnedDocument(document);
        run();
    }

    void clearDocuments() {
        if (pinned) {
            activity.clearPinnedDocuments();
        } else {
            activity.clearRecentDocuments();
        }
        run();
    }

    private TextView emptyState() {
        TextView empty = new TextView(activity);
        empty.setText(pinned ? activity.viewerText.noPinnedFiles() : activity.viewerText.noRecentFiles());
        empty.setTextColor(activity.mutedColor());
        empty.setTextSize(14);
        empty.setPadding(activity.dp(14), activity.dp(8), activity.dp(14), activity.dp(8));
        return empty;
    }

    private Button compactButton(String label) {
        Button button = new Button(activity);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextColor(activity.textColor());
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setPadding(activity.dp(12), activity.dp(7), activity.dp(12), activity.dp(7));
        button.setBackground(activity.makeTonalBackground(activity.surfaceAltColor(), 8));
        return button;
    }

    private static LayoutParams wrapParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }
}
