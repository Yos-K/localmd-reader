package io.github.yosk.mdlite.presentation;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.github.yosk.mdlite.R;

final class ReaderAppearance {
    private final MainActivity activity;

    ReaderAppearance(MainActivity activity) {
        this.activity = activity;
    }

    void applyCurrentTheme() {
        SystemBarsTheme.apply(activity.getWindow(), activity.viewerPalette);
        activity.root.setBackgroundColor(activity.backgroundColor());
        activity.topBar.setBackgroundColor(activity.backgroundColor());
        activity.tabScroller.setBackgroundColor(activity.backgroundColor());
        activity.menuScrollContainer.setBackgroundColor(activity.backgroundColor());
        activity.menuPanel.setBackgroundColor(activity.backgroundColor());
        activity.appTitle.setTextColor(activity.textColor());
        activity.menuTitle.setTextColor(activity.textColor());
        activity.messageView.setTextColor(activity.textColor());
        activity.messageView.setBackgroundColor(activity.messageColor());
        styleToolbarButton(activity.menuButton);
        for (int index = 0; index < activity.menuActionButtons.length; index++) {
            styleMenuButton(activity.menuActionButtons[index]);
        }
        for (int index = 0; index < activity.menuCards.size(); index++) {
            styleMenuCard(activity.menuCards.get(index));
        }
        activity.markdownLibraryMenuTree.refreshStyle();
        activity.tableOfContentsPanel.refreshStyle();
        activity.documentSearchBar.refreshStyle();
        int sectionColor = activity.primaryStrongColor();
        activity.filesSection.setTextColor(sectionColor);
        activity.readingSection.setTextColor(sectionColor);
        activity.layoutSection.setTextColor(sectionColor);
        activity.infoSection.setTextColor(sectionColor);
    }

    Drawable themedIcon(int resourceId, int color) {
        Drawable icon = activity.getDrawable(resourceId).mutate();
        icon.setTint(color);
        return icon;
    }

    void applyExpandChevron(TextView button, boolean expanded) {
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                themedIcon(expanded ? R.drawable.ic_expand_less_18 : R.drawable.ic_expand_more_18,
                        activity.textColor()), null);
        button.setCompoundDrawablePadding(activity.dp(6));
    }

    Drawable roundedBackground(int fillColor, int strokeColor, int radiusDp) {
        return withRipple(plainRoundedBackground(fillColor, strokeColor, radiusDp), radiusDp);
    }

    Drawable tonalBackground(int fillColor, int radiusDp) {
        return withRipple(plainTonalBackground(fillColor, radiusDp), radiusDp);
    }

    GradientDrawable plainTonalBackground(int fillColor, int radiusDp) {
        GradientDrawable surface = new GradientDrawable();
        surface.setColor(fillColor);
        surface.setCornerRadius(activity.dp(radiusDp));
        return surface;
    }

    Drawable rowRippleBackground() {
        GradientDrawable mask = new GradientDrawable();
        mask.setColor(0xffffffff);
        return new RippleDrawable(ColorStateList.valueOf(rippleColor()), null, mask);
    }

    GradientDrawable plainRoundedBackground(int fillColor, int strokeColor, int radiusDp) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(fillColor);
        background.setCornerRadius(activity.dp(radiusDp));
        background.setStroke(1, strokeColor);
        return background;
    }

    void styleMenuCard(LinearLayout card) {
        card.setBackground(plainTonalBackground(activity.surfaceAltColor(), 12));
        card.setClipToOutline(true);
    }

    void styleToolbarButton(TextView view) {
        view.setTextColor(activity.primaryStrongColor());
        view.setTextSize(15);
        view.setTypeface(Typeface.DEFAULT);
        view.setPadding(activity.dp(16), activity.dp(9), activity.dp(16), activity.dp(9));
        view.setBackground(roundedBackground(
                activity.surfaceAltColor(), activity.borderColor(), 8));
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                themedIcon(R.drawable.ic_menu_20, activity.primaryStrongColor()), null, null, null);
        view.setCompoundDrawablePadding(activity.dp(8));
    }

    void styleCompactButton(TextView view) {
        view.setTextColor(activity.primaryStrongColor());
        view.setTextSize(14);
        view.setTypeface(Typeface.DEFAULT);
        view.setMinWidth(0);
        view.setMinHeight(0);
        view.setMinimumWidth(0);
        view.setMinimumHeight(0);
        view.setPadding(activity.dp(8), activity.dp(4), activity.dp(8), activity.dp(4));
        view.setBackground(roundedBackground(
                activity.surfaceAltColor(), activity.borderColor(), 8));
    }

    void styleMenuButton(TextView view) {
        view.setTextColor(activity.textColor());
        view.setTextSize(16);
        view.setTypeface(Typeface.DEFAULT);
        view.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        view.setPadding(activity.dp(16), activity.dp(12), activity.dp(16), activity.dp(12));
        view.setBackground(rowRippleBackground());
    }

    private Drawable withRipple(GradientDrawable content, int radiusDp) {
        GradientDrawable mask = new GradientDrawable();
        mask.setColor(0xffffffff);
        mask.setCornerRadius(activity.dp(radiusDp));
        return new RippleDrawable(ColorStateList.valueOf(rippleColor()), content, mask);
    }

    private int rippleColor() {
        return (activity.primaryColor() & 0x00ffffff) | 0x33000000;
    }
}
