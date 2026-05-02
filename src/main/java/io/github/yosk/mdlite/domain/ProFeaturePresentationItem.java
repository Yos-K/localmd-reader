package io.github.yosk.mdlite.domain;

public final class ProFeaturePresentationItem {
    private final ViewerFeature feature;
    private final String title;
    private final String description;
    private final boolean available;

    public ProFeaturePresentationItem(
            ViewerFeature feature,
            String title,
            String description,
            boolean available) {
        if (feature == null) {
            throw new IllegalArgumentException("Feature must not be null.");
        }
        if (title == null || title.length() == 0) {
            throw new IllegalArgumentException("Title must not be empty.");
        }
        if (description == null || description.length() == 0) {
            throw new IllegalArgumentException("Description must not be empty.");
        }
        this.feature = feature;
        this.title = title;
        this.description = description;
        this.available = available;
    }

    public ViewerFeature feature() {
        return feature;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public boolean isAvailable() {
        return available;
    }

    public String statusLabel() {
        return available ? "Available" : "Locked";
    }
}
