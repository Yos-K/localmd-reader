package io.github.yosk.mdlite.presentation;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.github.yosk.mdlite.domain.TableOfContentsItem;
import io.github.yosk.mdlite.domain.TableOfContentsItems;

final class TableOfContentsMenuPanel extends LinearLayout implements Runnable {
    private final MainActivity activity;

    TableOfContentsMenuPanel(MainActivity activity) {
        super(activity);
        this.activity = activity;
        setOrientation(VERTICAL);
        setVisibility(View.GONE);
        setPadding(activity.dp(10), 0, 0, activity.dp(6));
    }

    @Override
    public void run() {
        removeAllViews();
        TableOfContentsItems items = TableOfContentsItems.from(activity.activeMarkdownHeadings());
        if (items.count() == 0) {
            addView(emptyState(), wrapParams());
            return;
        }
        for (TableOfContentsItem item : items.items()) {
            TableOfContentsItemButton button = new TableOfContentsItemButton(activity, item.heading());
            button.setText(item.label());
            style(button);
            addView(button, wrapParams());
        }
    }

    void refreshStyle() {
        for (int index = 0; index < getChildCount(); index++) {
            View child = getChildAt(index);
            if (child instanceof TableOfContentsItemButton) {
                style((TableOfContentsItemButton) child);
            } else if (child instanceof TextView) {
                ((TextView) child).setTextColor(activity.mutedColor());
            }
        }
    }

    private TextView emptyState() {
        TextView empty = new TextView(activity);
        empty.setText(activity.viewerText.noHeadings());
        empty.setTextColor(activity.mutedColor());
        empty.setTextSize(14);
        empty.setPadding(activity.dp(16), activity.dp(10), activity.dp(16), activity.dp(10));
        return empty;
    }

    private void style(TableOfContentsItemButton button) {
        button.setTextColor(activity.textColor());
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT);
        button.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        button.setPadding(activity.dp(14), activity.dp(8), activity.dp(14), activity.dp(8));
        button.setBackground(activity.makeTonalBackground(activity.surfaceAltColor(), 8));
    }

    private static LayoutParams wrapParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }
}
