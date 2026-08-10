package io.github.yosk.mdlite.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public final class EncodedMarkdownDocuments {
    private static final Pattern BASE64 = Pattern.compile(
            "(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?");
    private final List<EncodedMarkdownDocument> documents;

    private EncodedMarkdownDocuments(List<EncodedMarkdownDocument> documents) {
        this.documents = Collections.unmodifiableList(
                new ArrayList<EncodedMarkdownDocument>(documents));
    }

    public static EncodedMarkdownDocuments parse(String payload) {
        List<EncodedMarkdownDocument> parsed = new ArrayList<EncodedMarkdownDocument>();
        if (payload == null || payload.length() == 0) {
            return new EncodedMarkdownDocuments(parsed);
        }
        String[] records = payload.split(",", -1);
        for (String record : records) {
            String[] fields = record.split(":", -1);
            if (fields.length == 3
                    && BASE64.matcher(fields[0]).matches()
                    && BASE64.matcher(fields[1]).matches()
                    && BASE64.matcher(fields[2]).matches()) {
                parsed.add(new EncodedMarkdownDocument(fields[0], fields[1], fields[2]));
            }
        }
        return new EncodedMarkdownDocuments(parsed);
    }

    public int size() {
        return documents.size();
    }

    public EncodedMarkdownDocument get(int index) {
        return documents.get(index);
    }

    public static final class EncodedMarkdownDocument {
        private final String encodedTitle;
        private final String encodedSource;
        private final String encodedText;

        private EncodedMarkdownDocument(String encodedTitle, String encodedSource, String encodedText) {
            this.encodedTitle = encodedTitle;
            this.encodedSource = encodedSource;
            this.encodedText = encodedText;
        }

        public String encodedTitle() {
            return encodedTitle;
        }

        public String encodedSource() {
            return encodedSource;
        }

        public String encodedText() {
            return encodedText;
        }
    }
}
