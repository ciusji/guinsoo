/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.synth.thread;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.guinsoo.Driver;
import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;

/**
 * Starts multiple threads and performs random operations on each thread.
 */
public class TestMulti extends TestDb {

    /**
     * If set, the test should stop.
     */
    public volatile boolean stop;

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
        Driver.load();
        deleteDb("openClose");

        // int len = getSize(5, 100);
        int len = 10;
        TestMultiThread[] threads = new TestMultiThread[len];
        for (int i = 0; i < len; i++) {
            threads[i] = new TestMultiNews(this);
        }
        threads[0].first();
        for (int i = 0; i < len; i++) {
            threads[i].start();
        }
        Thread.sleep(10000);
        this.stop = true;
        for (int i = 0; i < len; i++) {
            threads[i].join();
        }
        threads[0].finalTest();
    }

    Connection getConnection() throws SQLException {
        final String url = "jdbc:guinsoo:" + getBaseDir() +
                "/openClose;LOCK_MODE=3;DB_CLOSE_DELAY=-1";
        Connection conn = DriverManager.getConnection(url, "sa", "");
        return conn;
    }

}
