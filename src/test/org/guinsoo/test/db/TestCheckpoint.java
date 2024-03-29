/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;

/**
 * Tests the CHECKPOINT SQL statement.
 */
public class TestCheckpoint extends TestDb {

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
        // TODO test checkpoint with rollback, not only just run the command
        deleteDb("checkpoint");
        Connection c0 = getConnection("checkpoint");
        Statement s0 = c0.createStatement();
        Connection c1 = getConnection("checkpoint");
        Statement s1 = c1.createStatement();
        s1.execute("CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255))");
        s1.execute("INSERT INTO TEST VALUES(1, 'Hello')");
        s0.execute("CHECKPOINT");

        s1.execute("INSERT INTO TEST VALUES(2, 'World')");
        c1.setAutoCommit(false);
        s1.execute("INSERT INTO TEST VALUES(3, 'Maybe')");
        s0.execute("CHECKPOINT");

        s1.execute("INSERT INTO TEST VALUES(4, 'Or not')");
        s0.execute("CHECKPOINT");

        s1.execute("INSERT INTO TEST VALUES(5, 'ok yes')");
        s1.execute("COMMIT");
        s0.execute("CHECKPOINT");

        c0.close();
        c1.close();
        deleteDb("checkpoint");
    }

}
