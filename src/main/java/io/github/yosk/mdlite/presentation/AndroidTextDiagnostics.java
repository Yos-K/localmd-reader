package io.github.yosk.mdlite.presentation;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
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

    static String describe(Context context, ClipData clip) {
        if (clip == null) {
            return "ClipData: null";
        }

        StringBuilder report = new StringBuilder();
        appendDescription(report, clip.getDescription());
        report.append("Item count: ").append(clip.getItemCount()).append('\n');
        for (int i = 0; i < clip.getItemCount(); i++) {
            appendItem(report, context, clip.getItemAt(i), i);
        }
        return report.toString();
    }

    private static void appendDescription(StringBuilder report, ClipDescription description) {
        if (description == null) {
            report.append("Description: null\n");
            return;
        }
        report.append("Label: ").append(description.getLabel()).append('\n');
        report.append("MIME types:").append('\n');
        for (int i = 0; i < description.getMimeTypeCount(); i++) {
            report.append("- ").append(description.getMimeType(i)).append('\n');
        }
    }

    private static void appendItem(StringBuilder report, Context context, ClipData.Item item, int index) {
        report.append('\n').append("Item ").append(index).append(':').append('\n');
        CharSequence text = item.getText();
        CharSequence styledText = item.coerceToStyledText(context);
        String htmlText = item.getHtmlText();
        report.append("Raw text: ").append(text == null ? "null" : text.getClass().getName()).append('\n');
        report.append("Styled text: ").append(styledText == null ? "null" : styledText.getClass().getName()).append('\n');
        report.append("HTML: ").append(htmlText == null ? "null" : "present length=" + htmlText.length()).append('\n');
        report.append(describe(styledText)).append('\n');
        if (htmlText != null) {
            report.append('\n').append("HTML preview:").append('\n').append(preview(htmlText)).append('\n');
        }
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
