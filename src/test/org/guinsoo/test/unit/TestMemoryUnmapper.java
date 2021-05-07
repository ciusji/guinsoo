/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.unit;

import java.lang.ProcessBuilder.Redirect;
import java.nio.ByteBuffer;

import org.guinsoo.test.TestBase;
import org.guinsoo.util.MemoryUnmapper;

/**
 * Tests memory unmapper.
 */
public class TestMemoryUnmapper extends TestBase {
    private static final int OK = 0, /* EXCEPTION = 1, */ UNAVAILABLE = 2;

    /**
     * May be used to run only this test and may be launched by this test in a
     * subprocess.
     *
     * @param a
     *            if empty run this test only
     */
    public static void main(String... a) throws Exception {
        if (a.length == 0) {
            TestBase.createCaller().init().testFromMain();
        } else {
            ByteBuffer buffer = ByteBuffer.allocateDirect(10);
            System.exit(MemoryUnmapper.unmap(buffer) ? OK : UNAVAILABLE);
        }
    }

    @Override
    public void test() throws Exception {
        ProcessBuilder pb = new ProcessBuilder().redirectError(Redirect.INHERIT);
        // Test that unsafe unmapping is disabled by default
        pb.command(getJVM(), "-cp", getClassPath(), "-ea", getClass().getName(), "dummy");
        assertEquals(UNAVAILABLE, pb.start().waitFor());
        // Test that it can be enabled
        pb.command(getJVM(), "-cp", getClassPath(), "-ea", "-Dguinsoo.nioCleanerHack=true", getClass().getName(), "dummy");
        assertEquals(OK, pb.start().waitFor());
        // Test that it will not be enabled with a security manager
        pb.command(getJVM(), "-cp", getClassPath(), "-ea", "-Djava.security.manager", "-Dguinsoo.nioCleanerHack=true",
                getClass().getName(), "dummy");
        assertEquals(UNAVAILABLE, pb.start().waitFor());
    }

}
