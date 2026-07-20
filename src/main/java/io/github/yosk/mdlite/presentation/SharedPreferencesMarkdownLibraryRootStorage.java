package io.github.yosk.mdlite.presentation;

import android.content.Context;
import android.content.SharedPreferences;
import io.github.yosk.mdlite.file.MarkdownLibraryRootStorage;

final class SharedPreferencesMarkdownLibraryRootStorage implements MarkdownLibraryRootStorage {
    private static final String PREFERENCES = "markdown_library";
    private static final String TREE_URI = "tree_uri";

    private final Context context;

    SharedPreferencesMarkdownLibraryRootStorage(Context context) {
        this.context = context;
    }

    @Override
    public String loadTreeUri() {
        return preferences().getString(TREE_URI, "");
    }

    @Override
    public void saveTreeUri(String treeUri) {
        preferences().edit().putString(TREE_URI, treeUri).apply();
    }

    @Override
    public void clearTreeUri() {
        preferences().edit().remove(TREE_URI).apply();
    }

    private SharedPreferences preferences() {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }
}
