package io.github.yosk.mdlite.presentation;

import android.view.View;
import android.widget.TextView;
import io.github.yosk.mdlite.model.DisclosureState;

final class ExpandableMenuSection implements DisclosureState.Handler {
    private static final Runnable NO_OP = new Runnable() {
        @Override
        public void run() {
        }
    };
    private final View panel;
    private final Runnable renderExpandedContent;
    private DisclosureState state = DisclosureState.collapsed();

    ExpandableMenuSection(View panel, Runnable renderExpandedContent) {
        if (panel == null || renderExpandedContent == null) {
            throw new IllegalArgumentException("expandable menu section requires complete rendering");
        }
        this.panel = panel;
        this.renderExpandedContent = renderExpandedContent;
        state.handle(this);
    }

    static ExpandableMenuSection staticContent(View panel) {
        return new ExpandableMenuSection(panel, NO_OP);
    }

    void toggle() {
        state = state.toggled();
        state.handle(this);
    }

    void refreshExpandedContent() {
        state.handle(this);
    }

    void refreshChevron(MainActivity activity, TextView button) {
        state.handle(new ChevronHandler(activity, button));
    }

    @Override
    public void collapsed() {
        panel.setVisibility(View.GONE);
    }

    @Override
    public void expanded() {
        renderExpandedContent.run();
        panel.setVisibility(View.VISIBLE);
    }

    private static final class ChevronHandler implements DisclosureState.Handler {
        private final MainActivity activity;
        private final TextView button;

        private ChevronHandler(MainActivity activity, TextView button) {
            this.activity = activity;
            this.button = button;
        }

        @Override
        public void collapsed() {
            activity.applyExpandChevron(button, false);
        }

        @Override
        public void expanded() {
            activity.applyExpandChevron(button, true);
        }
    }
}
