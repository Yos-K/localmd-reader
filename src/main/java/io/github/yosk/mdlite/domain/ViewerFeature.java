package io.github.yosk.mdlite.domain;

public final class ViewerFeature {
    private static final int FREE = 1;
    private static final int PRO = 2;

    public static final ViewerFeature OPEN_LOCAL_MARKDOWN =
            new ViewerFeature("open-local-markdown", FREE);
    public static final ViewerFeature OPEN_FROM_TERMUX =
            new ViewerFeature("open-from-termux", FREE);
    public static final ViewerFeature LIGHT_AND_DARK_THEME =
            new ViewerFeature("light-and-dark-theme", FREE);
    public static final ViewerFeature PINCH_FONT_SIZE =
            new ViewerFeature("pinch-font-size", FREE);
    public static final ViewerFeature TABS =
            new ViewerFeature("tabs", FREE);
    public static final ViewerFeature RESTORE_TABS =
            new ViewerFeature("restore-tabs", FREE);
    public static final ViewerFeature RECENT_FILES_LIMITED =
            new ViewerFeature("recent-files-limited", FREE);
    public static final ViewerFeature CLEAR_RECENT_FILES =
            new ViewerFeature("clear-recent-files", FREE);
    public static final ViewerFeature CONTROLS_PLACEMENT =
            new ViewerFeature("controls-placement", FREE);
    public static final ViewerFeature ENGLISH_AND_JAPANESE_UI =
            new ViewerFeature("english-and-japanese-ui", FREE);
    public static final ViewerFeature PRIVACY_DIALOG =
            new ViewerFeature("privacy-dialog", FREE);
    public static final ViewerFeature EXTRA_THEMES =
            new ViewerFeature("extra-themes", PRO);
    public static final ViewerFeature MERMAID_RENDERING =
            new ViewerFeature("mermaid-rendering", PRO);
    public static final ViewerFeature CODE_HIGHLIGHTING =
            new ViewerFeature("code-highlighting", PRO);
    public static final ViewerFeature CUSTOM_GESTURE_SHORTCUTS =
            new ViewerFeature("custom-gesture-shortcuts", PRO);
    public static final ViewerFeature TABLE_READING_ENHANCEMENTS =
            new ViewerFeature("table-reading-enhancements", PRO);
    public static final ViewerFeature EXTENDED_RECENT_FILES =
            new ViewerFeature("extended-recent-files", PRO);
    public static final ViewerFeature TABLE_OF_CONTENTS =
            new ViewerFeature("table-of-contents", PRO);
    public static final ViewerFeature HEADING_JUMP =
            new ViewerFeature("heading-jump", PRO);
    public static final ViewerFeature FOLDER_BROWSING =
            new ViewerFeature("folder-browsing", PRO);
    public static final ViewerFeature RELATIVE_LINKS =
            new ViewerFeature("relative-links", PRO);
    public static final ViewerFeature RELATIVE_IMAGES =
            new ViewerFeature("relative-images", PRO);
    public static final ViewerFeature EXPORT_OPTIONS =
            new ViewerFeature("export-options", PRO);

    private static final ViewerFeature[] VALUES = {
        OPEN_LOCAL_MARKDOWN,
        OPEN_FROM_TERMUX,
        LIGHT_AND_DARK_THEME,
        PINCH_FONT_SIZE,
        TABS,
        RESTORE_TABS,
        RECENT_FILES_LIMITED,
        CLEAR_RECENT_FILES,
        CONTROLS_PLACEMENT,
        ENGLISH_AND_JAPANESE_UI,
        PRIVACY_DIALOG,
        EXTRA_THEMES,
        MERMAID_RENDERING,
        CODE_HIGHLIGHTING,
        CUSTOM_GESTURE_SHORTCUTS,
        TABLE_READING_ENHANCEMENTS,
        EXTENDED_RECENT_FILES,
        TABLE_OF_CONTENTS,
        HEADING_JUMP,
        FOLDER_BROWSING,
        RELATIVE_LINKS,
        RELATIVE_IMAGES,
        EXPORT_OPTIONS
    };

    private final String id;
    private final int tier;

    private ViewerFeature(String id, int tier) {
        this.id = id;
        this.tier = tier;
    }

    public String id() {
        return id;
    }

    public boolean isFree() {
        return tier == FREE;
    }

    public static ViewerFeature[] values() {
        ViewerFeature[] copy = new ViewerFeature[VALUES.length];
        System.arraycopy(VALUES, 0, copy, 0, VALUES.length);
        return copy;
    }

    public static ViewerFeature fromId(String id) {
        for (int i = 0; i < VALUES.length; i++) {
            ViewerFeature feature = VALUES[i];
            if (feature.id.equals(id)) {
                return feature;
            }
        }
        throw new IllegalArgumentException("Unknown viewer feature: " + id);
    }
}
