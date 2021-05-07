/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.jdbc;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;
import org.guinsoo.util.Task;

/**
 * Test concurrent usage of the same connection.
 */
public class TestConcurrentConnectionUsage extends TestDb {

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }

    @Override
    public void test() throws SQLException {
        testAutoCommit();
    }

    private void testAutoCommit() throws SQLException {
        deleteDb(getTestName());
        final Connection conn = getConnection(getTestName());
        final PreparedStatement p1 = conn.prepareStatement("select 1 from dual");
        Task t = new Task() {
            @Override
            public void call() throws Exception {
                while (!stop) {
                    p1.executeQuery();
                    conn.setAutoCommit(true);
                    conn.setAutoCommit(false);
                }
            }
        }.execute();
        PreparedStatement prep = conn.prepareStatement("select ? from dual");
        for (int i = 0; i < 10; i++) {
            prep.setBinaryStream(1, new ByteArrayInputStream(new byte[1024]));
            prep.executeQuery();
        }
        t.get();
        conn.close();
    }

}
