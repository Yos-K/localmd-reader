package io.github.yosk.mdlite.presentation;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import io.github.yosk.mdlite.domain.RecentDocumentLimit;
import io.github.yosk.mdlite.file.PinnedDocuments;
import io.github.yosk.mdlite.file.RecentDocument;
import io.github.yosk.mdlite.file.RecentDocuments;
import io.github.yosk.mdlite.file.RestorableOpenTab;
import io.github.yosk.mdlite.file.RestorableOpenTabs;
import io.github.yosk.mdlite.viewer.OpenDocumentTab;
import io.github.yosk.mdlite.viewer.OpenDocumentTabs;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class TabPersistence {
    static final int MAX_PINNED_DOCUMENTS = 20;
    private static final String RECENT_PREFS = "recent_documents";
    private static final String PINNED_PREFS = "pinned_documents";
    private static final String RECENT_ITEMS = "items";
    private static final String OPEN_TABS_PREFS = "open_tabs";
    private static final String OPEN_TABS_ITEMS = "items";
    private static final String OPEN_TABS_ACTIVE_INDEX = "active_index";

    private final Context context;
    private final RecentDocumentLimit recentDocumentLimit;

    TabPersistence(Context context, RecentDocumentLimit recentDocumentLimit) {
        this.context = context;
        this.recentDocumentLimit = recentDocumentLimit == null
                ? RecentDocumentLimit.fromEntitlement(null)
                : recentDocumentLimit;
    }

    RestorableOpenTabs loadRestorableOpenTabs() {
        SharedPreferences prefs = context.getSharedPreferences(OPEN_TABS_PREFS, Context.MODE_PRIVATE);
        String raw = prefs.getString(OPEN_TABS_ITEMS, "");
        ArrayList<RestorableOpenTab> items = new ArrayList<RestorableOpenTab>();
        if (raw != null && raw.length() > 0) {
            String[] lines = raw.split("\\n", -1);
            for (int i = 0; i < lines.length; i++) {
                RestorableOpenTab tab = decodeRestorableOpenTab(lines[i]);
                if (tab != null) {
                    items.add(tab);
                }
            }
        }
        return RestorableOpenTabs.from(items, prefs.getInt(OPEN_TABS_ACTIVE_INDEX, 0));
    }

    void saveOpenTabs(OpenDocumentTabs openTabs) {
        StringBuilder raw = new StringBuilder();
        int savedCount = 0;
        int savedActiveIndex = 0;
        for (int i = 0; i < openTabs.tabs().size(); i++) {
            OpenDocumentTab tab = openTabs.tabs().get(i);
            if (!(tab instanceof OpenDocumentTab.FileDocumentTab)) {
                continue;
            }
            if (i == openTabs.activeIndex()) {
                savedActiveIndex = savedCount;
            }
            if (savedCount > 0) {
                raw.append('\n');
            }
            raw.append(encode(tab.title()))
                    .append('\t')
                    .append(encode(tab.uri()));
            savedCount++;
        }

        SharedPreferences.Editor editor = context.getSharedPreferences(OPEN_TABS_PREFS, Context.MODE_PRIVATE).edit();
        if (savedCount == 0) {
            editor.clear().apply();
            return;
        }
        editor.putString(OPEN_TABS_ITEMS, raw.toString())
                .putInt(OPEN_TABS_ACTIVE_INDEX, savedActiveIndex)
                .apply();
    }

    void recordRecentDocument(String displayName, String uriString) {
        RecentDocuments documents = loadRecentDocuments()
                .recordOpened(RecentDocument.of(displayName, uriString));
        saveRecentDocuments(documents);
    }

    RecentDocuments loadRecentDocuments() {
        SharedPreferences prefs = context.getSharedPreferences(RECENT_PREFS, Context.MODE_PRIVATE);
        String raw = prefs.getString(RECENT_ITEMS, "");
        ArrayList<RecentDocument> items = new ArrayList<RecentDocument>();
        if (raw != null && raw.length() > 0) {
            String[] lines = raw.split("\\n", -1);
            for (int i = 0; i < lines.length; i++) {
                RecentDocument document = decodeRecentDocument(lines[i]);
                if (document != null) {
                    items.add(document);
                }
            }
        }
        return RecentDocuments.from(recentDocumentLimit.maxItems(), items);
    }

    void clearRecentDocuments() {
        context.getSharedPreferences(RECENT_PREFS, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

    void pinDocument(String displayName, String uriString) {
        PinnedDocuments documents = loadPinnedDocuments()
                .pin(RecentDocument.of(displayName, uriString));
        saveDocuments(PINNED_PREFS, documents.items());
    }

    void unpinDocument(String uriString) {
        PinnedDocuments documents = loadPinnedDocuments().unpin(uriString);
        saveDocuments(PINNED_PREFS, documents.items());
    }

    boolean isPinnedDocument(String uriString) {
        return loadPinnedDocuments().containsUri(uriString);
    }

    PinnedDocuments loadPinnedDocuments() {
        return PinnedDocuments.from(MAX_PINNED_DOCUMENTS, loadDocumentList(PINNED_PREFS));
    }

    void clearPinnedDocuments() {
        context.getSharedPreferences(PINNED_PREFS, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

    private void saveRecentDocuments(RecentDocuments documents) {
        saveDocuments(RECENT_PREFS, documents.items());
    }

    private void saveDocuments(String prefsName, List<RecentDocument> items) {
        StringBuilder raw = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                raw.append('\n');
            }
            raw.append(encode(items.get(i).displayName()))
                    .append('\t')
                    .append(encode(items.get(i).uri()));
        }
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                .edit()
                .putString(RECENT_ITEMS, raw.toString())
                .apply();
    }

    private List<RecentDocument> loadDocumentList(String prefsName) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        String raw = prefs.getString(RECENT_ITEMS, "");
        ArrayList<RecentDocument> items = new ArrayList<RecentDocument>();
        if (raw != null && raw.length() > 0) {
            String[] lines = raw.split("\\n", -1);
            for (int i = 0; i < lines.length; i++) {
                RecentDocument document = decodeRecentDocument(lines[i]);
                if (document != null) {
                    items.add(document);
                }
            }
        }
        return items;
    }

    private static RestorableOpenTab decodeRestorableOpenTab(String line) {
        if (line == null || line.length() == 0) {
            return null;
        }
        int separator = line.indexOf('\t');
        if (separator < 0) {
            return null;
        }
        try {
            return RestorableOpenTab.of(
                    decode(line.substring(0, separator)),
                    decode(line.substring(separator + 1)));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static RecentDocument decodeRecentDocument(String line) {
        if (line == null || line.length() == 0) {
            return null;
        }
        int separator = line.indexOf('\t');
        if (separator < 0) {
            return null;
        }
        try {
            return RecentDocument.of(
                    decode(line.substring(0, separator)),
                    decode(line.substring(separator + 1)));
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
