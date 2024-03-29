/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.unit;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.guinsoo.message.TraceSystem;
import org.guinsoo.store.fs.FileUtils;
import org.guinsoo.test.TestBase;
import org.guinsoo.util.Utils10;

/**
 * Tests the trace system
 */
public class TestTraceSystem extends TestBase {

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }

    @Override
    public void test() throws Exception {
        testTraceDebug();
        testReadOnly();
        testAdapter();
    }

    private void testAdapter() {
        TraceSystem ts = new TraceSystem(null);
        ts.setName("test");
        ts.setLevelFile(TraceSystem.ADAPTER);
        ts.getTrace("test").debug("test");
        ts.getTrace("test").info("test");
        ts.getTrace("test").error(new Exception(), "test");

        // The used SLF4J-nop logger has all log levels disabled,
        // so this should be reflected in the trace system.
        assertFalse(ts.isEnabled(TraceSystem.INFO));
        assertFalse(ts.getTrace("test").isInfoEnabled());

        ts.close();
    }

    private void testTraceDebug() throws Exception {
        TraceSystem ts = new TraceSystem(null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ts.setSysOut(new PrintStream(out, false, "UTF-8"));
        ts.setLevelSystemOut(TraceSystem.DEBUG);
        ts.getTrace("test").debug(new Exception("error"), "test");
        ts.close();
        String outString = Utils10.byteArrayOutputStreamToString(out, StandardCharsets.UTF_8);
        assertContains(outString, "error");
        assertContains(outString, "Exception");
        assertContains(outString, "test");
    }

    private void testReadOnly() throws Exception {
        String readOnlyFile = getBaseDir() + "/readOnly.log";
        FileUtils.delete(readOnlyFile);
        FileUtils.newOutputStream(readOnlyFile, false).close();
        FileUtils.setReadOnly(readOnlyFile);
        TraceSystem ts = new TraceSystem(readOnlyFile);
        ts.setLevelFile(TraceSystem.INFO);
        ts.getTrace("test").info("test");
        FileUtils.delete(readOnlyFile);
        ts.close();
    }

}
