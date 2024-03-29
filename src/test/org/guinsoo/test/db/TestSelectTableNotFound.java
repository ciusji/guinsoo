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

public class TestSelectTableNotFound extends TestDb {

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
        testWithoutAnyCandidate();
        testWithOneCandidate();
        testWithTwoCandidates();
        testWithSchema();
        testWithSchemaSearchPath();
        testWhenSchemaIsEmpty();
        testWithSchemaWhenSchemaIsEmpty();
        testWithSchemaSearchPathWhenSchemaIsEmpty();
    }

    private void testWithoutAnyCandidate() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnection();
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE T2 ( ID INT IDENTITY )");
        try {
            stat.executeQuery("SELECT 1 FROM t1");
            fail("Table `t1` was accessible but should not have been.");
        } catch (SQLException e) {
            String message = e.getMessage();
            assertContains(message, "Table \"t1\" not found;");
        }

        conn.close();
        deleteDb(getTestName());
    }

    private void testWithOneCandidate() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnection();
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE T1 ( ID INT IDENTITY )");
        try {
            stat.executeQuery("SELECT 1 FROM t1");
            fail("Table `t1` was accessible but should not have been.");
        } catch (SQLException e) {
            String message = e.getMessage();
            assertContains(message, "Table \"t1\" not found (candidates are: \"T1\")");
        }

        conn.close();
        deleteDb(getTestName());
    }

    private void testWithTwoCandidates() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnection();
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE Toast ( ID INT IDENTITY )");
        stat.execute("CREATE TABLE TOAST ( ID INT IDENTITY )");
        try {
            stat.executeQuery("SELECT 1 FROM toast");
            fail("Table `toast` was accessible but should not have been.");
        } catch (SQLException e) {
            String message = e.getMessage();
            assertContains(message, "Table \"toast\" not found (candidates are: \"TOAST, Toast\")");
        }

        conn.close();
        deleteDb(getTestName());
    }

    private void testWithSchema() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnection();
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE T1 ( ID INT IDENTITY )");
        try {
            stat.executeQuery("SELECT 1 FROM PUBLIC.t1");
            fail("Table `t1` was accessible but should not have been.");
        } catch (SQLException e) {
            String message = e.getMessage();
            assertContains(message, "Table \"t1\" not found (candidates are: \"T1\")");
        }

        conn.close();
        deleteDb(getTestName());
    }

    private void testWithSchemaSearchPath() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnection();
        Statement stat = conn.createStatement();
        stat.execute("SET SCHEMA_SEARCH_PATH PUBLIC");
        stat.execute("CREATE TABLE T1 ( ID INT IDENTITY )");
        try {
            stat.executeQuery("SELECT 1 FROM t1");
            fail("Table `t1` was accessible but should not have been.");
        } catch (SQLException e) {
            String message = e.getMessage();
            assertContains(message, "Table \"t1\" not found (candidates are: \"T1\")");
        }

        conn.close();
        deleteDb(getTestName());
    }

    private void testWhenSchemaIsEmpty() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnection();
        Statement stat = conn.createStatement();
        try {
            stat.executeQuery("SELECT 1 FROM t1");
            fail("Table `t1` was accessible but should not have been.");
        } catch (SQLException e) {
            String message = e.getMessage();
            assertContains(message, "Table \"t1\" not found (this database is empty)");
        }

        conn.close();
        deleteDb(getTestName());
    }

    private void testWithSchemaWhenSchemaIsEmpty() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnection();
        Statement stat = conn.createStatement();
        try {
            stat.executeQuery("SELECT 1 FROM PUBLIC.t1");
            fail("Table `t1` was accessible but should not have been.");
        } catch (SQLException e) {
            String message = e.getMessage();
            assertContains(message, "Table \"t1\" not found (this database is empty)");
        }

        conn.close();
        deleteDb(getTestName());
    }

    private void testWithSchemaSearchPathWhenSchemaIsEmpty() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnection();
        Statement stat = conn.createStatement();
        stat.execute("SET SCHEMA_SEARCH_PATH PUBLIC");
        try {
            stat.executeQuery("SELECT 1 FROM t1");
            fail("Table `t1` was accessible but should not have been.");
        } catch (SQLException e) {
            String message = e.getMessage();
            assertContains(message, "Table \"t1\" not found (this database is empty)");
        }

        conn.close();
        deleteDb(getTestName());
    }

    private Connection getConnection() throws SQLException {
        return getConnection(getTestName() + ";DATABASE_TO_UPPER=FALSE");
    }
}
