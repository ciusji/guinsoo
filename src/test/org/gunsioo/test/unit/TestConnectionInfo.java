/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.unit;

import java.io.File;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.engine.ConnectionInfo;
import org.gunsioo.test.TestBase;
import org.gunsioo.test.TestDb;
import org.gunsioo.tools.DeleteDbFiles;

/**
 * Test the ConnectionInfo class.
 *
 * @author Kerry Sainsbury
 * @author Thomas Mueller Graf
 */
public class TestConnectionInfo extends TestDb {

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String[] a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }

    @Override
    public void test() throws Exception {
        testImplicitRelativePath();
        testConnectInitError();
        testConnectionInfo();
        testName();
    }

    private void testImplicitRelativePath() throws Exception {
        assertThrows(ErrorCode.URL_RELATIVE_TO_CWD, () -> getConnection("jdbc:gunsioo:" + getTestName()));
        assertThrows(ErrorCode.URL_RELATIVE_TO_CWD, () -> getConnection("jdbc:gunsioo:data/" + getTestName()));

        getConnection("jdbc:gunsioo:./data/" + getTestName()).close();
        DeleteDbFiles.execute("data", getTestName(), true);
    }

    private void testConnectInitError() throws Exception {
        assertThrows(ErrorCode.SYNTAX_ERROR_2, () -> getConnection("jdbc:gunsioo:mem:;init=error"));
        assertThrows(ErrorCode.IO_EXCEPTION_2, () -> getConnection("jdbc:gunsioo:mem:;init=runscript from 'wrong.file'"));
    }

    private void testConnectionInfo() {
        ConnectionInfo connectionInfo = new ConnectionInfo(
                "jdbc:gunsioo:mem:" + getTestName() +
                        ";LOG=2" +
                        ";ACCESS_MODE_DATA=rws" +
                        ";INIT=CREATE this...\\;INSERT that..." +
                        ";IFEXISTS=TRUE",
                null, null, null);

        assertEquals("jdbc:gunsioo:mem:" + getTestName(),
                connectionInfo.getURL());

        assertEquals("2",
                connectionInfo.getProperty("LOG", ""));
        assertEquals("rws",
                connectionInfo.getProperty("ACCESS_MODE_DATA", ""));
        assertEquals("CREATE this...;INSERT that...",
                connectionInfo.getProperty("INIT", ""));
        assertEquals("TRUE",
                connectionInfo.getProperty("IFEXISTS", ""));
        assertEquals("undefined",
                connectionInfo.getProperty("CACHE_TYPE", "undefined"));
    }

    private void testName() throws Exception {
        char differentFileSeparator = File.separatorChar == '/' ? '\\' : '/';
        ConnectionInfo connectionInfo = new ConnectionInfo("./test" +
                differentFileSeparator + "subDir");
        File file = new File("test" + File.separatorChar + "subDir");
        assertEquals(file.getCanonicalPath().replace('\\', '/'),
                connectionInfo.getName());
    }

}
