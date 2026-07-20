package io.github.yosk.mdlite.presentation;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import io.github.yosk.mdlite.model.NavigationMenuState;

/**
 * Slide/scrim transition for the menu panel (#76).
 *
 * Deliberately tiny state machine: the only state is the target (open or
 * closed). A transition always animates from the current property values
 * toward the target, so rapid toggling cannot corrupt state — a new call
 * cancels the running animation and retargets from wherever the panel is.
 * ViewPropertyAnimator durations follow the system animator duration scale,
 * so devices with animations disabled switch instantly; canceled animations
 * never run their end actions, and the end actions re-check the target as a
 * second guard.
 */
final class MenuTransitions {
    private static final long DURATION_MS = 220;

    private final View menuPanel;
    private final View scrim;
    private final Runnable hidePanelWhenClosed;
    private final Runnable hideScrimWhenClosed;
    private NavigationMenuState state = NavigationMenuState.closed();

    MenuTransitions(View menuPanel, View scrim) {
        this.menuPanel = menuPanel;
        this.scrim = scrim;
        this.hidePanelWhenClosed = new HideWhenClosed(menuPanel, this);
        this.hideScrimWhenClosed = new HideWhenClosed(scrim, this);
    }

    boolean isOpenTargeted() {
        OpenStateHandler handler = new OpenStateHandler();
        state.handle(handler);
        return handler.open;
    }

    void open() {
        state = state.open();
        menuPanel.animate().cancel();
        scrim.animate().cancel();
        if (menuPanel.getVisibility() != View.VISIBLE) {
            menuPanel.setTranslationX(-panelWidth());
            menuPanel.setVisibility(View.VISIBLE);
        }
        if (scrim.getVisibility() != View.VISIBLE) {
            scrim.setAlpha(0f);
            scrim.setVisibility(View.VISIBLE);
        }
        menuPanel.animate().translationX(0f)
                .setDuration(DURATION_MS)
                .setInterpolator(new DecelerateInterpolator());
        scrim.animate().alpha(1f).setDuration(DURATION_MS);
    }

    void close() {
        state = state.close();
        menuPanel.animate().cancel();
        scrim.animate().cancel();
        menuPanel.animate().translationX(-panelWidth())
                .setDuration(DURATION_MS)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(hidePanelWhenClosed);
        scrim.animate().alpha(0f)
                .setDuration(DURATION_MS)
                .withEndAction(hideScrimWhenClosed);
    }

    private float panelWidth() {
        if (menuPanel.getWidth() > 0) {
            return menuPanel.getWidth();
        }
        return menuPanel.getLayoutParams() == null ? 0f : menuPanel.getLayoutParams().width;
    }

    private static final class HideWhenClosed implements Runnable {
        private final View view;
        private final MenuTransitions transitions;

        HideWhenClosed(View view, MenuTransitions transitions) {
            this.view = view;
            this.transitions = transitions;
        }

        @Override
        public void run() {
            if (!transitions.isOpenTargeted()) {
                view.setVisibility(View.GONE);
            }
        }
    }

    private static final class OpenStateHandler implements NavigationMenuState.Handler {
        private boolean open;

        @Override
        public void closed() {
            open = false;
        }

        @Override
        public void open() {
            open = true;
        }
    }
}
