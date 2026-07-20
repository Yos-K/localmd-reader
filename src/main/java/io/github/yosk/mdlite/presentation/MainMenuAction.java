package io.github.yosk.mdlite.presentation;

abstract class MainMenuAction {
    abstract String label(MainActivity activity);

    abstract void perform(MainActivity activity);

    boolean visible(MainActivity activity) {
        return true;
    }
}
