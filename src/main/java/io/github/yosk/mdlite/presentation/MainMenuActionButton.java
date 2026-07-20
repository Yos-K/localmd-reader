package io.github.yosk.mdlite.presentation;

import android.view.View;
import android.widget.Button;

final class MainMenuActionButton extends Button {
    private final MainMenuAction action;

    MainMenuActionButton(MainActivity activity, MainMenuAction action) {
        super(activity);
        this.action = action;
        setAllCaps(false);
        setOnClickListener(activity);
    }

    void refresh(MainActivity activity) {
        setText(action.label(activity));
        setVisibility(action.visible(activity) ? View.VISIBLE : View.GONE);
    }

    void perform(MainActivity activity) {
        action.perform(activity);
    }
}
