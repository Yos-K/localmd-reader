package io.github.yosk.mdlite.presentation;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import io.github.yosk.mdlite.R;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.OpenDocumentTabs;
import io.github.yosk.mdlite.viewer.TabPinningDecision;

final class DocumentTabBar {
    private final MainActivity activity;
    private final HorizontalScrollView scroller;
    private final LinearLayout row;

    DocumentTabBar(MainActivity activity, HorizontalScrollView scroller, LinearLayout row) {
        this.activity = activity;
        this.scroller = scroller;
        this.row = row;
    }

    void render(OpenDocumentTabs tabs) {
        row.removeAllViews();
        for (int index = 0; index < tabs.tabs().size(); index++) {
            row.addView(tabGroup(tabs, index), wrapContent());
        }
        row.post(new CloseTabTouchTargets(row, activity.dp(48)));
        scroller.post(new ScrollToActiveTab(scroller, row, tabs.activeIndex()));
    }

    private LinearLayout tabGroup(OpenDocumentTabs tabs, int index) {
        OpenDocumentTab tab = tabs.tabs().get(index);
        LinearLayout group = new LinearLayout(activity);
        group.setOrientation(LinearLayout.HORIZONTAL);
        group.setGravity(Gravity.CENTER_VERTICAL);
        group.setPadding(0, 0, activity.dp(6), 0);
        group.addView(tabButton(tabs, tab, index), wrapContent());
        if (!MainActivity.WELCOME_URI.equals(tab.uri())) {
            group.addView(closeButton(tab, index), new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
        }
        return group;
    }

    private TabButton tabButton(OpenDocumentTabs tabs, OpenDocumentTab tab, int index) {
        boolean active = index == tabs.activeIndex();
        TabPinningDecision pinning = activity.pinnedDocumentController.decision(tab);
        TabButton button = new TabButton(activity, index);
        button.setText(tab.title());
        button.setContentDescription(pinning.tabDescription(activity.viewerText, tab.title()));
        button.setAllCaps(false);
        button.setOnClickListener(activity);
        button.setOnLongClickListener(activity);
        button.setLongClickable(true);
        button.setSingleLine(true);
        button.setEllipsize(TextUtils.TruncateAt.END);
        button.setMaxWidth(activity.dp(220));
        button.setTextSize(14);
        button.setTypeface(active ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        button.setTextColor(active ? activity.onPrimaryColor() : activity.textColor());
        button.setPadding(activity.dp(16), activity.dp(8), activity.dp(16), activity.dp(8));
        renderPinMark(button, pinning, active);
        button.setBackground(activity.makeTonalBackground(
                active ? activity.primaryColor() : activity.surfaceAltColor(),
                MainActivity.PILL_RADIUS_DP));
        return button;
    }

    private void renderPinMark(TabButton button, TabPinningDecision pinning, boolean active) {
        if (!(pinning instanceof TabPinningDecision.Unpin)) {
            return;
        }
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(
                activity.themedIcon(R.drawable.ic_push_pin_18,
                        active ? activity.onPrimaryColor() : activity.textColor()),
                null, null, null);
        button.setCompoundDrawablePadding(activity.dp(6));
    }

    private CloseTabText closeButton(OpenDocumentTab tab, int index) {
        CloseTabText close = new CloseTabText(activity, index);
        close.setCompoundDrawablesRelativeWithIntrinsicBounds(
                activity.themedIcon(R.drawable.ic_close_20, activity.mutedColor()),
                null, null, null);
        close.setGravity(Gravity.CENTER);
        close.setPadding(activity.dp(6), 0, activity.dp(14), 0);
        close.setContentDescription(activity.viewerText.closeTabDescription(tab.title()));
        close.setOnClickListener(activity);
        return close;
    }

    private LinearLayout.LayoutParams wrapContent() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
