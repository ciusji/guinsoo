/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.unit;

import java.io.File;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.engine.ConnectionInfo;
import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;
import org.guinsoo.tools.DeleteDbFiles;

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
        assertThrows(ErrorCode.URL_RELATIVE_TO_CWD, () -> getConnection("jdbc:guinsoo:" + getTestName()));
        assertThrows(ErrorCode.URL_RELATIVE_TO_CWD, () -> getConnection("jdbc:guinsoo:data/" + getTestName()));

        getConnection("jdbc:guinsoo:./data/" + getTestName()).close();
        DeleteDbFiles.execute("data", getTestName(), true);
    }

    private void testConnectInitError() throws Exception {
        assertThrows(ErrorCode.SYNTAX_ERROR_2, () -> getConnection("jdbc:guinsoo:mem:;init=error"));
        assertThrows(ErrorCode.IO_EXCEPTION_2, () -> getConnection("jdbc:guinsoo:mem:;init=runscript from 'wrong.file'"));
    }

    private void testConnectionInfo() {
        ConnectionInfo connectionInfo = new ConnectionInfo(
                "jdbc:guinsoo:mem:" + getTestName() +
                        ";LOG=2" +
                        ";ACCESS_MODE_DATA=rws" +
                        ";INIT=CREATE this...\\;INSERT that..." +
                        ";IFEXISTS=TRUE",
                null, null, null);

        assertEquals("jdbc:guinsoo:mem:" + getTestName(),
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
