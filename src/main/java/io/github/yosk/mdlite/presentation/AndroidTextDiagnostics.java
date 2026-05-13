package io.github.yosk.mdlite.presentation;

import android.text.Spanned;

final class AndroidTextDiagnostics {
    private static final int PREVIEW_LIMIT = 600;

    private AndroidTextDiagnostics() {
    }

    static String describe(CharSequence text) {
        if (text == null) {
            return "Text: null";
        }

        StringBuilder report = new StringBuilder();
        report.append("Class: ").append(text.getClass().getName()).append('\n');
        report.append("Length: ").append(text.length()).append('\n');
        report.append("Spanned: ").append(text instanceof Spanned).append('\n');
        if (text instanceof Spanned) {
            appendSpans(report, (Spanned) text);
        }
        report.append('\n').append("Preview:").append('\n');
        report.append(preview(text.toString()));
        return report.toString();
    }

    private static void appendSpans(StringBuilder report, Spanned text) {
        Object[] spans = text.getSpans(0, text.length(), Object.class);
        report.append("Span count: ").append(spans.length).append('\n');
        for (int i = 0; i < spans.length; i++) {
            Object span = spans[i];
            report.append("- ")
                    .append(span.getClass().getName())
                    .append(" [")
                    .append(text.getSpanStart(span))
                    .append(',')
                    .append(text.getSpanEnd(span))
                    .append("] flags=")
                    .append(text.getSpanFlags(span))
                    .append('\n');
        }
    }

    private static String preview(String text) {
        String value = text.length() <= PREVIEW_LIMIT ? text : text.substring(0, PREVIEW_LIMIT) + "\n...";
        return value.replace("\r", "\\r").replace("\t", "\\t");
    }
}
