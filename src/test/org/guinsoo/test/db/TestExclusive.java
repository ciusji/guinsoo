/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;
import org.guinsoo.util.Task;

/**
 * Test for the exclusive mode.
 */
public class TestExclusive extends TestDb {

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
        testSetExclusiveTrueFalse();
        testSetExclusiveGetExclusive();
    }

    private void testSetExclusiveTrueFalse() throws Exception {
        deleteDb("exclusive");
        Connection conn = getConnection("exclusive");
        Statement stat = conn.createStatement();
        stat.execute("set exclusive true");
        assertThrows(ErrorCode.DATABASE_IS_IN_EXCLUSIVE_MODE, () -> getConnection("exclusive"));

        stat.execute("set exclusive false");
        Connection conn2 = getConnection("exclusive");
        final Statement stat2 = conn2.createStatement();
        stat.execute("set exclusive true");
        final AtomicInteger state = new AtomicInteger();
        Task task = new Task() {
            @Override
            public void call() throws SQLException {
                stat2.execute("select * from dual");
                if (state.get() != 1) {
                    new Error("unexpected state: " + state.get()).printStackTrace();
                }
            }
        };
        task.execute();
        state.set(1);
        stat.execute("set exclusive false");
        task.get();
        stat.execute("set exclusive true");
        conn.close();

        // check that exclusive mode is off when disconnected
        stat2.execute("select * from dual");
        conn2.close();
        deleteDb("exclusive");
    }

    private void testSetExclusiveGetExclusive() throws SQLException {
        deleteDb("exclusive");
        try (Connection connection = getConnection("exclusive")) {
            assertFalse(getExclusiveMode(connection));

            setExclusiveMode(connection, 1);
            assertTrue(getExclusiveMode(connection));

            setExclusiveMode(connection, 0);
            assertFalse(getExclusiveMode(connection));

            // Setting to existing mode should not throws exception
            setExclusiveMode(connection, 0);
            assertFalse(getExclusiveMode(connection));

            setExclusiveMode(connection, 1);
            assertTrue(getExclusiveMode(connection));

            // Setting to existing mode throws exception
            setExclusiveMode(connection, 1);
            assertTrue(getExclusiveMode(connection));

            setExclusiveMode(connection, 2);
            assertTrue(getExclusiveMode(connection));

            setExclusiveMode(connection, 0);
            assertFalse(getExclusiveMode(connection));
        }
    }


    private static void setExclusiveMode(Connection connection, int exclusiveMode) throws SQLException {
        String sql = "SET EXCLUSIVE " + exclusiveMode;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        }
    }

    private static boolean getExclusiveMode(Connection connection) throws SQLException{
        boolean exclusiveMode = false;

        String sql = "SELECT SETTING_VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE SETTING_NAME = 'EXCLUSIVE'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                exclusiveMode = result.getBoolean(1);
            }
        }

        return exclusiveMode;
    }
}
