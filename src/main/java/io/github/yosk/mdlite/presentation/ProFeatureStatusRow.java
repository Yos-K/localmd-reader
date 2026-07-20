package io.github.yosk.mdlite.presentation;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

final class ProFeatureStatusRow extends LinearLayout {
    ProFeatureStatusRow(
            Activity activity,
            String title,
            String description,
            String status,
            boolean available,
            int backgroundColor,
            int borderColor,
            int textColor,
            int mutedColor,
            int primaryColor) {
        super(activity);
        setOrientation(LinearLayout.VERTICAL);
        setPadding(dp(activity, 14), dp(activity, 12), dp(activity, 14), dp(activity, 12));
        setBackground(roundedBackground(activity, backgroundColor, borderColor));

        LinearLayout header = new LinearLayout(activity);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView titleView = new TextView(activity);
        titleView.setText(title);
        titleView.setTextColor(textColor);
        titleView.setTextSize(16);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        header.addView(titleView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView statusView = new TextView(activity);
        statusView.setText(status);
        statusView.setTextColor(available ? primaryColor : mutedColor);
        statusView.setTextSize(12);
        statusView.setTypeface(Typeface.DEFAULT_BOLD);
        statusView.setPadding(dp(activity, 10), dp(activity, 4), dp(activity, 10), dp(activity, 4));
        statusView.setBackground(roundedBackground(activity, available ? tint(primaryColor) : backgroundColor, borderColor));
        header.addView(statusView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        addView(header, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView descriptionView = new TextView(activity);
        descriptionView.setText(description);
        descriptionView.setTextColor(mutedColor);
        descriptionView.setTextSize(13);
        descriptionView.setPadding(0, dp(activity, 8), 0, 0);
        addView(descriptionView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private static GradientDrawable roundedBackground(Activity activity, int fillColor, int strokeColor) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(fillColor);
        background.setCornerRadius(dp(activity, 8));
        background.setStroke(1, strokeColor);
        return background;
    }

    private static int tint(int color) {
        return (0x22 << 24) | (color & 0x00ffffff);
    }

    private static int dp(Activity activity, int value) {
        return (int) (value * activity.getResources().getDisplayMetrics().density + 0.5f);
    }
}
