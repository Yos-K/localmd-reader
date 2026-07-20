package io.github.yosk.mdlite.presentation;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class ClipboardHistoryStore {
    private static final String PREFS = "clipboard_history";
    private static final String ITEMS = "items";
    private static final int MAX_ITEMS = 10;

    private final Context context;

    ClipboardHistoryStore(Context context) {
        this.context = context;
    }

    List<String> load() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String raw = prefs.getString(ITEMS, "");
        ArrayList<String> items = new ArrayList<String>();
        if (raw != null && raw.length() > 0) {
            String[] lines = raw.split("\\n", -1);
            for (int i = 0; i < lines.length; i++) {
                String markdown = decodeItem(lines[i]);
                if (markdown != null) {
                    items.add(markdown);
                }
            }
        }
        return items;
    }

    void record(String markdown) {
        if (markdown == null || markdown.length() == 0) {
            return;
        }
        ArrayList<String> items = new ArrayList<String>();
        items.add(markdown);
        List<String> existing = load();
        for (int i = 0; i < existing.size(); i++) {
            String item = existing.get(i);
            if (!markdown.equals(item) && items.size() < MAX_ITEMS) {
                items.add(item);
            }
        }
        save(items);
    }

    private void save(List<String> items) {
        StringBuilder raw = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                raw.append('\n');
            }
            raw.append(encode(items.get(i)));
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(ITEMS, raw.toString())
                .apply();
    }

    private static String decodeItem(String line) {
        if (line == null || line.length() == 0) {
            return null;
        }
        try {
            return decode(line);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String encode(String value) {
        return Base64.encodeToString(value.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP | Base64.URL_SAFE);
    }

    private static String decode(String value) {
        return new String(Base64.decode(value, Base64.NO_WRAP | Base64.URL_SAFE), StandardCharsets.UTF_8);
    }
}
