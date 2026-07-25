package io.github.yosk.mdlite.presentation;

import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import io.github.yosk.mdlite.viewer.ViewerTheme;

final class ThemeMenuPanel extends LinearLayout implements Runnable {
    private final MainActivity activity;

    ThemeMenuPanel(MainActivity activity) {
        super(activity);
        this.activity = activity;
        setOrientation(VERTICAL);
        setVisibility(View.GONE);
        setPadding(activity.dp(10), 0, 0, activity.dp(6));
    }

    @Override
    public void run() {
        removeAllViews();
        ViewerTheme[] themes = ViewerTheme.availableThemes(activity.featureEntitlement);
        for (int index = 0; index < themes.length; index++) {
            addView(themeButton(themes[index]),
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }

    void selectTheme(ViewerTheme theme) {
        activity.applySelectedTheme(theme);
        run();
    }

    private Button themeButton(final ViewerTheme theme) {
        Button button = new Button(activity);
        button.setText(activity.viewerText.themeLabel(theme));
        button.setAllCaps(false);
        button.setTextColor(activity.textColor());
        button.setTextSize(14);
        button.setTypeface(theme.equals(activity.currentTheme()) ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        button.setBackground(activity.makeTonalBackground(
                theme.equals(activity.currentTheme()) ? activity.surfaceAltColor() : activity.surfaceColor(), 8));
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTheme(theme);
            }
        });
        return button;
    }
}
