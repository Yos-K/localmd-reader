package io.github.yosk.mdlite.domain;

import java.util.HashMap;
import java.util.Map;

public final class MarkdownHeadingAnchors {
    private final Map<String, Integer> counts = new HashMap<String, Integer>();

    public String nextAnchorId(String title) {
        String base = baseAnchorId(title);
        Integer previous = counts.get(base);
        int next = previous == null ? 1 : previous.intValue() + 1;
        counts.put(base, Integer.valueOf(next));
        if (next == 1) {
            return base;
        }
        return base + "-" + next;
    }

    private static String baseAnchorId(String title) {
        String safeTitle = title == null ? "" : title.trim().toLowerCase();
        StringBuilder anchor = new StringBuilder();
        boolean pendingDash = false;
        for (int i = 0; i < safeTitle.length(); i++) {
            char c = safeTitle.charAt(i);
            if (isAnchorCharacter(c)) {
                if (pendingDash && anchor.length() > 0) {
                    anchor.append('-');
                }
                anchor.append(c);
                pendingDash = false;
            } else if (anchor.length() > 0) {
                pendingDash = true;
            }
        }
        if (anchor.length() == 0) {
            return "heading";
        }
        return anchor.toString();
    }

    private static boolean isAnchorCharacter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }
}
