package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class PersistableReadPermissionTest {
    private static final int READ = 1;
    private static final int WRITE = 2;
    private static final int PERSISTABLE = 64;

    @Test
    void readGrantProducesAReadablePersistablePermission() {
        PersistableReadPermission permission = PersistableReadPermission.fromResultFlags(READ, READ);

        TestAssertions.assertTrue(permission.isGranted(),
                "a returned read grant must be retained for restored tabs");
        TestAssertions.assertEquals(READ, permission.flags(),
                "only the supported read grant must be persisted");
    }

    @Test
    void unrelatedResultFlagsProduceNoPermission() {
        PersistableReadPermission permission = PersistableReadPermission.fromResultFlags(
                WRITE | PERSISTABLE, READ);

        TestAssertions.assertFalse(permission.isGranted(),
                "write and persistable flags without read access must not invent a read grant");
        TestAssertions.assertEquals(0, permission.flags(),
                "an absent read grant must persist no flags");
    }

    @Test
    void everyIntegerResultIsReducedToTheSupportedReadFlag() {
        PersistableReadPermission permission = PersistableReadPermission.fromResultFlags(-1, READ);

        TestAssertions.assertEquals(READ, permission.flags(),
                "unknown result bits must not escape the permission model");
    }
}
