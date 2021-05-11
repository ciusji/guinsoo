/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;

/**
 * Test for limit updates.
 */
public class TestLimitUpdates extends TestDb {

    private static final String DATABASE_NAME = "limitUpdates";

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
        testLimitUpdates();
        deleteDb(DATABASE_NAME);
    }

    private void testLimitUpdates() throws SQLException {
        deleteDb(DATABASE_NAME);
        Connection conn = null;
        PreparedStatement prep = null;

        try {
            conn = getConnection(DATABASE_NAME);
            prep = conn.prepareStatement(
                    "CREATE TABLE TEST(KEY_ID INT PRIMARY KEY, VALUE_ID INT)");
            prep.executeUpdate();

            prep.close();
            prep = conn.prepareStatement("INSERT INTO TEST VALUES(?, ?)");
            int numRows = 10;
            for (int i = 0; i < numRows; ++i) {
                prep.setInt(1, i);
                prep.setInt(2, 0);
                prep.execute();
            }
            assertEquals(numRows, countWhere(conn, 0));

            // update all elements than available
            prep.close();
            prep = conn.prepareStatement("UPDATE TEST SET VALUE_ID = ?");
            prep.setInt(1, 1);
            prep.execute();
            assertEquals(numRows, countWhere(conn, 1));

            // update less elements than available
            updateLimit(conn, 2, numRows / 2);
            assertEquals(numRows / 2, countWhere(conn, 2));

            // update more elements than available
            updateLimit(conn, 3, numRows * 2);
            assertEquals(numRows, countWhere(conn, 3));

            // update no elements
            updateLimit(conn, 4, 0);
            assertEquals(0, countWhere(conn, 4));
        } finally {
            if (prep != null) {
                prep.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    private static int countWhere(final Connection conn, final int where)
            throws SQLException {
        PreparedStatement prep = null;
        ResultSet rs = null;
        try {
            prep = conn.prepareStatement(
                    "SELECT COUNT(*) FROM TEST WHERE VALUE_ID = ?");
            prep.setInt(1, where);
            rs = prep.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (prep != null) {
                prep.close();
            }
        }
    }

    private static void updateLimit(final Connection conn, final int value,
            final int limit) throws SQLException {
        try (PreparedStatement prep = conn.prepareStatement(
                    "UPDATE TEST SET VALUE_ID = ? LIMIT ?")) {
            prep.setInt(1, value);
            prep.setInt(2, limit);
            prep.execute();
        }
    }
}