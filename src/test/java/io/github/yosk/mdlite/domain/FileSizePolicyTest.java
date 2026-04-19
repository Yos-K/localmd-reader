package io.github.yosk.mdlite.domain;

public final class FileSizePolicyTest {
    private static final long MAX_SIZE_BYTES = 10L * 1024L * 1024L;

    public static void main(String[] args) {
        FileSizePolicyTest test = new FileSizePolicyTest();
        test.acceptsZeroByteFile();
        test.acceptsFileAtMaximumSize();
        test.rejectsFileAboveMaximumSize();
        test.rejectsNegativeSize();
        test.acceptsUnknownSizeWhenProviderDoesNotReportSize();
    }

    public void acceptsZeroByteFile() {
        FileSizePolicy policy = new FileSizePolicy(MAX_SIZE_BYTES);

        assertTrue(policy.isReadableSize(0), "zero byte file must be accepted");
    }

    public void acceptsFileAtMaximumSize() {
        FileSizePolicy policy = new FileSizePolicy(MAX_SIZE_BYTES);

        assertTrue(policy.isReadableSize(MAX_SIZE_BYTES), "file at maximum size must be accepted");
    }

    public void rejectsFileAboveMaximumSize() {
        FileSizePolicy policy = new FileSizePolicy(MAX_SIZE_BYTES);

        assertFalse(policy.isReadableSize(MAX_SIZE_BYTES + 1), "file above maximum size must be rejected");
    }

    public void rejectsNegativeSize() {
        FileSizePolicy policy = new FileSizePolicy(MAX_SIZE_BYTES);

        assertFalse(policy.isReadableSize(-2), "negative size other than unknown sentinel must be rejected");
    }

    public void acceptsUnknownSizeWhenProviderDoesNotReportSize() {
        FileSizePolicy policy = new FileSizePolicy(MAX_SIZE_BYTES);

        assertTrue(policy.isReadableSize(FileSizePolicy.UNKNOWN_SIZE), "unknown provider size must be accepted before streaming limit check");
    }

    private static void assertTrue(boolean actual, String message) {
        if (!actual) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean actual, String message) {
        if (actual) {
            throw new AssertionError(message);
        }
    }
}
