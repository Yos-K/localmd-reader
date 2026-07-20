package io.github.yosk.mdlite.presentation;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

final class GestureShortcutRow extends LinearLayout {
    private final int targetIndex;

    GestureShortcutRow(
            Activity activity,
            int targetIndex,
            int previewKind,
            String actionLabel,
            boolean assigned,
            int backgroundColor,
            int borderColor,
            int lineColor,
            int textColor,
            int mutedColor,
            View.OnClickListener listener) {
        super(activity);
        this.targetIndex = targetIndex;
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(dp(activity, 8), dp(activity, 8), dp(activity, 8), dp(activity, 8));
        setBackground(roundedBackground(activity, backgroundColor, borderColor));
        setClickable(true);
        setOnClickListener(listener);

        View status = new View(activity);
        status.setBackground(roundedStatus(activity, assigned ? lineColor : mutedColor));
        addView(status, new LinearLayout.LayoutParams(dp(activity, 5), dp(activity, 38)));

        GesturePreviewView preview = new GesturePreviewView(activity, previewKind, lineColor, lineColor);
        preview.setAlpha(assigned ? 1.0f : 0.56f);
        LinearLayout.LayoutParams previewParams = new LinearLayout.LayoutParams(dp(activity, 58), dp(activity, 48));
        previewParams.leftMargin = dp(activity, 10);
        addView(preview, previewParams);

        TextView action = new TextView(activity);
        action.setText(actionLabel);
        action.setTextColor(assigned ? textColor : mutedColor);
        action.setTextSize(16);
        action.setTypeface(Typeface.DEFAULT_BOLD);
        action.setGravity(Gravity.CENTER_VERTICAL);
        action.setPadding(dp(activity, 14), 0, dp(activity, 8), 0);
        addView(action, new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));

        TextView chevron = new TextView(activity);
        chevron.setText(">");
        chevron.setTextColor(mutedColor);
        chevron.setTextSize(18);
        addView(chevron, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    int targetIndex() {
        return targetIndex;
    }

    private static int dp(Activity activity, int value) {
        return (int) (value * activity.getResources().getDisplayMetrics().density + 0.5f);
    }

    private static GradientDrawable roundedBackground(Activity activity, int fillColor, int strokeColor) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(fillColor);
        background.setCornerRadius(dp(activity, 8));
        background.setStroke(1, strokeColor);
        return background;
    }

    private static GradientDrawable roundedStatus(Activity activity, int fillColor) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(fillColor);
        background.setCornerRadius(dp(activity, 3));
        return background;
    }
}
