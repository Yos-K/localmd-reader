package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class FileSizePolicyTest {
    private static final long MAX_SIZE_BYTES = 10L * 1024L * 1024L;


    @Test
    void acceptsZeroByteFile() {
        FileSizePolicy policy = new FileSizePolicy(MAX_SIZE_BYTES);

        TestAssertions.assertTrue(policy.isReadableSize(0), "zero byte file must be accepted");
    }

    @Test
    void acceptsFileAtMaximumSize() {
        FileSizePolicy policy = new FileSizePolicy(MAX_SIZE_BYTES);

        TestAssertions.assertTrue(policy.isReadableSize(MAX_SIZE_BYTES), "file at maximum size must be accepted");
    }

    @Test
    void rejectsFileAboveMaximumSize() {
        FileSizePolicy policy = new FileSizePolicy(MAX_SIZE_BYTES);

        TestAssertions.assertFalse(policy.isReadableSize(MAX_SIZE_BYTES + 1), "file above maximum size must be rejected");
    }

    @Test
    void rejectsNegativeSize() {
        FileSizePolicy policy = new FileSizePolicy(MAX_SIZE_BYTES);

        TestAssertions.assertFalse(policy.isReadableSize(-2), "negative size other than unknown sentinel must be rejected");
    }

    @Test
    void acceptsUnknownSizeWhenProviderDoesNotReportSize() {
        FileSizePolicy policy = new FileSizePolicy(MAX_SIZE_BYTES);

        TestAssertions.assertTrue(policy.isReadableSize(FileSizePolicy.UNKNOWN_SIZE), "unknown provider size must be accepted before streaming limit check");
    }
}
