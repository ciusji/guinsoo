/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.synth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;

/**
 * Tests lock releasing for concurrent select statements
 */
public class TestReleaseSelectLock extends TestDb {

    private static final String TEST_DB_NAME = "releaseSelectLock";

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestBase test = TestBase.createCaller().init();
        test.config.mvStore = false;
        test.testFromMain();
    }

    @Override
    public void test() throws Exception {
        deleteDb(TEST_DB_NAME);

        Connection conn = getConnection(TEST_DB_NAME);
        final Statement statement = conn.createStatement();
        statement.execute("create table test(id int primary key)");

        runConcurrentSelects();

        // check that all locks have been released by dropping the test table
        statement.execute("drop table test");

        statement.close();
        conn.close();
        deleteDb(TEST_DB_NAME);
    }

    private void runConcurrentSelects() throws InterruptedException {
        int tryCount = 500;
        int threadsCount = getSize(2, 4);
        for (int tryNumber = 0; tryNumber < tryCount; tryNumber++) {
            CountDownLatch allFinished = new CountDownLatch(threadsCount);

            for (int i = 0; i < threadsCount; i++) {
                new Thread(() -> {
                    try {
                        Connection conn = getConnection(TEST_DB_NAME);
                        PreparedStatement stmt = conn.prepareStatement("select id from test");
                        ResultSet rs = stmt.executeQuery();
                        while (rs.next()) {
                            rs.getInt(1);
                        }
                        stmt.close();
                        conn.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        allFinished.countDown();
                    }
                }).start();
            }

            allFinished.await();
        }
    }
}
