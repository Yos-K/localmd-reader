package io.github.yosk.mdlite.presentation;

import android.graphics.Typeface;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BulletSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import io.github.yosk.mdlite.domain.MarkdownStyle;
import io.github.yosk.mdlite.domain.StyledMarkdownText;

final class AndroidStyledTextMarkdown {
    private AndroidStyledTextMarkdown() {
    }

    static String from(CharSequence text) {
        if (!(text instanceof Spanned)) {
            return text == null ? "" : text.toString();
        }
        Spanned spanned = (Spanned) text;
        StyledMarkdownText markdown = new StyledMarkdownText();
        int start = 0;
        while (start < spanned.length()) {
            int end = spanned.nextSpanTransition(start, spanned.length(), Object.class);
            markdown.append(spanned.subSequence(start, end).toString(), styleAt(spanned, start, end));
            start = end;
        }
        return markdown.value();
    }

    private static MarkdownStyle styleAt(Spanned spanned, int start, int end) {
        MarkdownStyle style = MarkdownStyle.plain();
        StyleSpan[] styleSpans = spanned.getSpans(start, end, StyleSpan.class);
        for (int i = 0; i < styleSpans.length; i++) {
            int spanStyle = styleSpans[i].getStyle();
            if (spanStyle == Typeface.BOLD || spanStyle == Typeface.BOLD_ITALIC) {
                style = style.withBold();
            }
            if (spanStyle == Typeface.ITALIC || spanStyle == Typeface.BOLD_ITALIC) {
                style = style.withItalic();
            }
        }
        UnderlineSpan[] underlineSpans = spanned.getSpans(start, end, UnderlineSpan.class);
        if (underlineSpans.length > 0) {
            style = style.withUnderline();
        }
        URLSpan[] urlSpans = spanned.getSpans(start, end, URLSpan.class);
        if (urlSpans.length > 0) {
            style = style.withLink(urlSpans[0].getURL());
        }
        int headingLevel = headingLevelAt(spanned, start, end);
        if (headingLevel > 0) {
            style = style.withHeadingLevel(headingLevel);
        }
        BulletSpan[] bulletSpans = spanned.getSpans(start, end, BulletSpan.class);
        if (bulletSpans.length > 0) {
            style = style.withBulletListItem();
        }
        return style;
    }

    private static int headingLevelAt(Spanned spanned, int start, int end) {
        RelativeSizeSpan[] relativeSpans = spanned.getSpans(start, end, RelativeSizeSpan.class);
        for (int i = 0; i < relativeSpans.length; i++) {
            float sizeChange = relativeSpans[i].getSizeChange();
            if (sizeChange >= 1.5f) {
                return 1;
            }
            if (sizeChange >= 1.25f) {
                return 2;
            }
        }
        AbsoluteSizeSpan[] absoluteSpans = spanned.getSpans(start, end, AbsoluteSizeSpan.class);
        for (int i = 0; i < absoluteSpans.length; i++) {
            int size = absoluteSpans[i].getSize();
            if (size >= 24) {
                return 1;
            }
            if (size >= 20) {
                return 2;
            }
        }
        return 0;
    }
}
