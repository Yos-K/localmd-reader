package io.github.yosk.mdlite.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PersistentMarkdownTextStore {
    private final File root;

    public PersistentMarkdownTextStore(File root) {
        if (root == null) {
            throw new IllegalArgumentException("persistent Markdown root must not be null");
        }
        this.root = root;
    }

    public File store(String displayName, String sourceId, String markdown) throws IOException {
        File sourceDirectory = new File(root, sourceHash(required(sourceId, "source id")));
        ensureDirectory(sourceDirectory);
        File target = new File(sourceDirectory, safeFileName(displayName));
        File pending = new File(sourceDirectory, ".pending");
        write(pending, markdown == null ? "" : markdown);
        replace(pending, target);
        removeStaleFiles(sourceDirectory, target);
        return target;
    }

    private static String safeFileName(String displayName) {
        String candidate = displayName == null ? "" : displayName.trim();
        candidate = candidate.replace('\\', '/');
        candidate = candidate.substring(candidate.lastIndexOf('/') + 1).replaceAll("[\\p{Cntrl}]", "_");
        return candidate.length() == 0 ? "Termux.md" : candidate;
    }

    private static String sourceHash(String sourceId) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(sourceId.getBytes(StandardCharsets.UTF_8));
            StringBuilder encoded = new StringBuilder();
            for (byte value : digest) {
                encoded.append(String.format(java.util.Locale.US, "%02x", Integer.valueOf(value & 0xff)));
            }
            return encoded.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 must be available", e);
        }
    }

    private static String required(String value, String label) {
        if (value == null || value.trim().length() == 0) {
            throw new IllegalArgumentException(label + " must not be empty");
        }
        return value;
    }

    private static void ensureDirectory(File directory) throws IOException {
        if (!directory.isDirectory() && !directory.mkdirs()) {
            throw new IOException("persistent Markdown directory is unavailable");
        }
    }

    private static void write(File file, String markdown) throws IOException {
        FileOutputStream output = new FileOutputStream(file, false);
        try {
            output.write(markdown.getBytes(StandardCharsets.UTF_8));
            output.getFD().sync();
        } finally {
            output.close();
        }
    }

    private static void replace(File pending, File target) throws IOException {
        if (target.exists() && !target.delete()) {
            throw new IOException("previous persistent Markdown could not be replaced");
        }
        if (!pending.renameTo(target)) {
            throw new IOException("persistent Markdown could not be committed");
        }
    }

    private static void removeStaleFiles(File directory, File target) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (!file.equals(target)) {
                file.delete();
            }
        }
    }
}
