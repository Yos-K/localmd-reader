package io.github.yosk.mdlite.testing;

public final class TestAssertions {
    private TestAssertions() {
    }

    public static void assertTrue(boolean actual, String message) {
        require(actual, message);
    }

    public static void assertFalse(boolean actual, String message) {
        require(!actual, message);
    }

    public static void assertEquals(int expected, int actual, String message) {
        require(expected == actual, message + "\nExpected: " + expected + "\nActual: " + actual);
    }

    public static void assertEquals(long expected, long actual, String message) {
        require(expected == actual, message + "\nExpected: " + expected + "\nActual: " + actual);
    }

    public static void assertEquals(String expected, String actual, String message) {
        require(expected.equals(actual), message + "\nExpected: " + expected + "\nActual: " + actual);
    }

    public static void assertSame(Object expected, Object actual, String message) {
        require(expected == actual, message);
    }

    public static void assertNotEmpty(String actual, String message) {
        require(actual != null && actual.length() > 0, message + " must not be empty");
    }

    public static void assertContains(String actual, String expected, String message) {
        require(actual.contains(expected), message + "\nExpected to contain: " + expected + "\nActual: " + actual);
    }

    public static void assertNotContains(String actual, String forbidden, String message) {
        require(!actual.contains(forbidden), message + "\nForbidden content: " + forbidden + "\nActual: " + actual);
    }

    public static void assertNotNull(Object actual, String message) {
        require(actual != null, message);
    }

    public static void assertThrows(Class<? extends RuntimeException> expected, ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException actual) {
            require(expected.isInstance(actual), "Unexpected exception: " + actual);
            return;
        }
        throw new AssertionError("Expected exception: " + expected.getName());
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public interface ThrowingRunnable {
        void run();
    }
}
