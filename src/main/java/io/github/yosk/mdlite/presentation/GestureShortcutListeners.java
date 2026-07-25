package io.github.yosk.mdlite.presentation;

import android.view.View;

final class GestureShortcutListeners {
    private GestureShortcutListeners() {
    }

    static final class RowClickListener implements View.OnClickListener {
        private final GestureShortcutDialogs dialogs;

        RowClickListener(GestureShortcutDialogs dialogs) {
            this.dialogs = dialogs;
        }

        @Override
        public void onClick(View view) {
            if (view instanceof GestureShortcutRow) {
                dialogs.selectShortcutAction(((GestureShortcutRow) view).targetIndex());
            }
        }
    }
}
