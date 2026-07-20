package io.github.yosk.mdlite.presentation;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import io.github.yosk.mdlite.R;
import io.github.yosk.mdlite.viewer.DocumentSearchQuery;

final class DocumentSearchBar extends LinearLayout implements TextWatcher, View.OnClickListener {
    private final MainActivity activity;
    private final EditText input;
    private final Button previousButton;
    private final Button nextButton;
    private final Button closeButton;
    private boolean syncingFromSession;

    DocumentSearchBar(MainActivity activity) {
        super(activity);
        this.activity = activity;
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setVisibility(View.GONE);

        input = new EditText(activity);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setTextSize(14);
        input.setMinHeight(0);
        input.setPadding(activity.dp(10), activity.dp(4), activity.dp(10), activity.dp(4));
        input.addTextChangedListener(this);
        addView(input, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        previousButton = button("");
        nextButton = button("");
        closeButton = button("");
        addView(previousButton, compactButtonParams());
        addView(nextButton, compactButtonParams());
        addView(closeButton, compactButtonParams());
        refreshText();
        refreshActions();
    }

    void showBar() {
        setVisibility(View.VISIBLE);
        syncFromSession();
        input.requestFocus();
    }

    void hideBar() {
        setVisibility(View.GONE);
        activity.clearWebViewSearch();
    }

    void syncFromSession() {
        syncingFromSession = true;
        input.setText(activity.currentSearchQueryText());
        input.setSelection(input.getText().length());
        syncingFromSession = false;
        refreshActions();
    }

    void refreshText() {
        input.setHint(activity.viewerText().searchHint());
        previousButton.setContentDescription(activity.viewerText().previousSearchResult());
        nextButton.setContentDescription(activity.viewerText().nextSearchResult());
        closeButton.setContentDescription(activity.viewerText().cancel());
    }

    void refreshStyle() {
        setBackgroundColor(activity.backgroundColor());
        setPadding(activity.dp(10), activity.dp(4), activity.dp(10), activity.dp(4));
        input.setTextColor(activity.textColor());
        input.setHintTextColor(activity.mutedColor());
        input.setBackground(activity.makePlainRoundedBackground(activity.surfaceColor(), activity.borderColor(), 8));
        activity.styleCompactButton(previousButton);
        activity.styleCompactButton(nextButton);
        activity.styleCompactButton(closeButton);
        // Vector icons replace the old arrow/close glyphs (#73); re-tinted here
        // because refreshStyle runs on every theme switch.
        previousButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                activity.themedIcon(R.drawable.ic_arrow_upward_18, activity.primaryStrongColor()), null, null, null);
        nextButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                activity.themedIcon(R.drawable.ic_arrow_downward_18, activity.primaryStrongColor()), null, null, null);
        closeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                activity.themedIcon(R.drawable.ic_close_20, activity.primaryStrongColor()), null, null, null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (syncingFromSession) { return; }
        activity.clearWebViewSearch();
        activity.searchTextInDocument(DocumentSearchQuery.from(s == null ? "" : s.toString()));
        refreshActions();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onClick(View view) {
        if (view == previousButton) {
            activity.findPreviousSearchResult();
            return;
        }
        if (view == nextButton) {
            activity.findNextSearchResult();
            return;
        }
        if (view == closeButton) {
            hideBar();
        }
    }

    private Button button(String label) {
        Button button = new Button(activity);
        button.setText(label);
        button.setAllCaps(false);
        button.setOnClickListener(this);
        return button;
    }

    private void refreshActions() {
        boolean active = activity.hasActiveDocumentSearch();
        previousButton.setEnabled(active);
        nextButton.setEnabled(active);
    }

    private LinearLayout.LayoutParams compactButtonParams() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
