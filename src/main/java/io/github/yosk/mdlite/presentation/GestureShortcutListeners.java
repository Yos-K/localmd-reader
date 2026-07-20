package io.github.yosk.mdlite.presentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import io.github.yosk.mdlite.viewer.GestureShortcutAction;
import io.github.yosk.mdlite.viewer.GestureShortcutTrigger;

final class GestureShortcutListeners {
    private GestureShortcutListeners() {
    }

    static final class RowClickListener implements View.OnClickListener {
        private final GestureShortcutDialogs dialogs;
        private AlertDialog dialog;

        RowClickListener(GestureShortcutDialogs dialogs) {
            this.dialogs = dialogs;
        }

        void attach(AlertDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onClick(View view) {
            if (view instanceof GestureShortcutRow) {
                if (dialog != null) { dialog.dismiss(); }
                dialogs.selectGestureTarget(((GestureShortcutRow) view).targetIndex());
            }
        }
    }

    static final class DoubleTapClickListener implements DialogInterface.OnClickListener {
        private final GestureShortcutDialogs dialogs;
        private final GestureShortcutAction[] actions;

        DoubleTapClickListener(GestureShortcutDialogs dialogs, GestureShortcutAction[] actions) {
            this.dialogs = dialogs;
            this.actions = actions;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialogs.applyDoubleTapShortcut(actions[which]);
        }
    }

    static final class CircleClickListener implements DialogInterface.OnClickListener {
        private final GestureShortcutDialogs dialogs;
        private final GestureShortcutAction[] actions;

        CircleClickListener(GestureShortcutDialogs dialogs, GestureShortcutAction[] actions) {
            this.dialogs = dialogs;
            this.actions = actions;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialogs.applyCircleGestureShortcut(actions[which]);
        }
    }

    static final class DirectionalClickListener implements DialogInterface.OnClickListener {
        private final GestureShortcutDialogs dialogs;
        private final GestureShortcutTrigger trigger;
        private final GestureShortcutAction[] actions;

        DirectionalClickListener(GestureShortcutDialogs dialogs,
                GestureShortcutTrigger trigger, GestureShortcutAction[] actions) {
            this.dialogs = dialogs;
            this.trigger = trigger;
            this.actions = actions;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialogs.applyDirectionalGestureShortcut(trigger, actions[which]);
        }
    }

    static final class CustomMenuClickListener implements DialogInterface.OnClickListener {
        private final GestureShortcutDialogs dialogs;

        CustomMenuClickListener(GestureShortcutDialogs dialogs) {
            this.dialogs = dialogs;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == 0) { dialogs.startCustomGestureRegistration(); return; }
            if (which == 1 && dialogs.hasCustomGestureShortcut()) {
                dialogs.showChangeCustomGestureActionDialog();
                return;
            }
            if (which == 1 || which == 2) { dialogs.clearCustomGestureShortcut(); }
        }
    }

    static final class CustomActionClickListener implements DialogInterface.OnClickListener {
        private final GestureShortcutDialogs dialogs;
        private final GestureShortcutAction[] actions;

        CustomActionClickListener(GestureShortcutDialogs dialogs, GestureShortcutAction[] actions) {
            this.dialogs = dialogs;
            this.actions = actions;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialogs.saveCustomGestureShortcut(actions[which]);
        }
    }
}
