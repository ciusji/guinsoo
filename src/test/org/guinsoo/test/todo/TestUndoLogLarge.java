/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.todo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import org.guinsoo.tools.DeleteDbFiles;

/**
 * A test with an undo log size of 2 GB.
 */
public class TestUndoLogLarge {

    /**
     * Run just this test.
     *
     * @param args ignored
     */
    public static void main(String... args) throws Exception {
        // System.setProperty("guinsoo.largeTransactions", "true");
        TestUndoLogLarge.test();
    }

    private static void test() throws SQLException {
        DeleteDbFiles.execute("data", "test", true);
        Connection conn = DriverManager.getConnection("jdbc:guinsoo:data/test");
        Statement stat = conn.createStatement();
        stat.execute("set max_operation_memory 100");
        stat.execute("set max_memory_undo 100");
        stat.execute("create table test(id identity, name varchar)");
        conn.setAutoCommit(false);
        PreparedStatement prep = conn.prepareStatement(
                "insert into test(name) values(space(1024*1024))");
        long time = System.nanoTime();
        for (int i = 0; i < 2500; i++) {
            prep.execute();
            long now = System.nanoTime();
            if (now > time + TimeUnit.SECONDS.toNanos(5)) {
                System.out.println(i);
                time = now + TimeUnit.SECONDS.toNanos(5);
            }
        }
        conn.rollback();
        conn.close();
    }

}
