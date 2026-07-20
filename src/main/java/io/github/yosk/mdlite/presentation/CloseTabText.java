package io.github.yosk.mdlite.presentation;

import android.app.Activity;
import android.widget.TextView;

final class CloseTabText extends TextView {
    private final int tabIndex;

    CloseTabText(Activity activity, int tabIndex) {
        super(activity);
        this.tabIndex = tabIndex;
    }

    int tabIndex() {
        return tabIndex;
    }
}
