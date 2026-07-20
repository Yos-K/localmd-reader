package io.github.yosk.mdlite.presentation;

import android.widget.Button;
import io.github.yosk.mdlite.domain.MarkdownHeading;

final class TableOfContentsItemButton extends Button {
    private final MarkdownHeading heading;

    TableOfContentsItemButton(MainActivity activity, MarkdownHeading heading) {
        super(activity);
        this.heading = heading;
        setAllCaps(false);
        setOnClickListener(activity);
    }

    MarkdownHeading heading() {
        return heading;
    }
}
