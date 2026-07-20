package io.github.yosk.mdlite.presentation;

import android.app.Activity;
import android.widget.Button;

final class TabButton extends Button {
    private final int tabIndex;

    TabButton(Activity activity, int tabIndex) {
        super(activity);
        this.tabIndex = tabIndex;
    }

    int tabIndex() {
        return tabIndex;
    }
}
