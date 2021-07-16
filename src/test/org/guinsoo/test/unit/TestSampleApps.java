/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.unit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import org.guinsoo.store.fs.FileUtils;
import org.guinsoo.tools.ChangeFileEncryption;
import org.guinsoo.tools.RunScript;
import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;
import org.guinsoo.tools.DeleteDbFiles;
import org.guinsoo.util.IOUtils;
import org.guinsoo.util.StringUtils;
import org.guinsoo.util.Utils10;

/**
 * Tests the sample apps.
 */
public class TestSampleApps extends TestDb {

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }

    @Override
    public boolean isEnabled() {
        if (!getBaseDir().startsWith(TestBase.BASE_TEST_DIR)) {
            return false;
        }
        return true;
    }

    @Override
    public void test() throws Exception {
        deleteDb(getTestName());
        InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/guinsoo/samples/optimizations.sql");
        new File(getBaseDir()).mkdirs();
        FileOutputStream out = new FileOutputStream(getBaseDir() +
                "/optimizations.sql");
        IOUtils.copyAndClose(in, out);
        String url = "jdbc:guinsoo:" + getBaseDir() + "/" + getTestName();
        testApp("", RunScript.class, "-url", url, "-user", "sa",
                "-password", "sa", "-script", getBaseDir() +
                        "/optimizations.sql", "-checkResults");
        deleteDb(getTestName());
        testApp("Compacting...\nDone.", org.guinsoo.samples.Compact.class);
        testApp("NAME: Bob Meier\n" +
                "EMAIL: bob.meier@abcde.abc\n" +
                "PHONE: +41123456789\n\n" +
                "NAME: John Jones\n" +
                "EMAIL: john.jones@abcde.abc\n" +
                "PHONE: +41976543210\n",
                org.guinsoo.samples.CsvSample.class);
        testApp("",
                org.guinsoo.samples.CachedPreparedStatements.class);
        testApp("2 is prime\n" +
                "3 is prime\n" +
                "5 is prime\n" +
                "7 is prime\n" +
                "11 is prime\n" +
                "13 is prime\n" +
                "17 is prime\n" +
                "19 is prime\n" +
                "30\n" +
                "20\n" +
                "0/0\n" +
                "0/1\n" +
                "1/0\n" +
                "1/1\n" +
                "10",
                org.guinsoo.samples.Function.class);
        // Not compatible with PostgreSQL JDBC driver (throws a
        // NullPointerException):
        // testApp(org.guinsoo.samples.SecurePassword.class, null, "Joe");
        // TODO test ShowProgress (percent numbers are hardware specific)
        // TODO test ShutdownServer (server needs to be started in a separate
        // process)
        testApp("The sum is 20.00", org.guinsoo.samples.TriggerSample.class);
        testApp("Hello: 1\nWorld: 2", org.guinsoo.samples.TriggerPassData.class);
        testApp("Key 1 was generated\n" +
                "Key 2 was generated\n\n" +
                "TEST_TABLE:\n" +
                "1 Hallo\n\n" +
                "TEST_VIEW:\n" +
                "1 Hallo",
                org.guinsoo.samples.UpdatableView.class);
        testApp(
                "adding test data...\n" +
                "defrag to reduce random access...\n" +
                "create the zip file...\n" +
                "open the database from the zip file...",
                org.guinsoo.samples.ReadOnlyDatabaseInZip.class);
        testApp(
                "a: 1/Hello!\n" +
                "b: 1/Hallo!\n" +
                "1/A/Hello!\n" +
                "1/B/Hallo!",
                org.guinsoo.samples.RowAccessRights.class);

        // tools
        testApp("Allows changing the database file encryption password or algorithm*",
                ChangeFileEncryption.class, "-help");
        testApp("Deletes all files belonging to a database.*",
                DeleteDbFiles.class, "-help");
        FileUtils.delete(getBaseDir() + "/optimizations.sql");
    }

    private void testApp(String expected, Class<?> clazz, String... args)
            throws Exception {
        DeleteDbFiles.execute("data", "test", true);
        Method m = clazz.getMethod("main", String[].class);
        PrintStream oldOut = System.out, oldErr = System.err;
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buff, false, "UTF-8");
        System.setOut(out);
        System.setErr(out);
        try {
            m.invoke(null, new Object[] { args });
        } catch (InvocationTargetException e) {
            TestBase.logError("error", e.getTargetException());
        } catch (Throwable e) {
            TestBase.logError("error", e);
        }
        out.flush();
        System.setOut(oldOut);
        System.setErr(oldErr);
        String s = Utils10.byteArrayOutputStreamToString(buff, StandardCharsets.UTF_8);
        s = StringUtils.replaceAll(s, "\r\n", "\n");
        s = s.trim();
        expected = expected.trim();
        if (expected.endsWith("*")) {
            expected = expected.substring(0, expected.length() - 1);
            if (!s.startsWith(expected)) {
                assertEquals(expected.trim(), s.trim());
            }
        } else {
            assertEquals(expected.trim(), s.trim());
        }
    }
}
