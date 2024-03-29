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

public class TestAlterTableNotFound extends TestDb {

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
        testWithoutAnyCandidateWhenDatabaseToLower();
        testWithoutAnyCandidateWhenDatabaseToUpper();
        testWithoutAnyCandidateWhenCaseInsensitiveIdentifiers();
        testWithOneCandidate();
        testWithOneCandidateWhenDatabaseToLower();
        testWithOneCandidateWhenDatabaseToUpper();
        testWithOneCandidateWhenCaseInsensitiveIdentifiers();
        testWithTwoCandidates();
    }

    private void testWithoutAnyCandidate() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnectionWithSettings("DATABASE_TO_UPPER=FALSE");
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE T2 ( ID INT IDENTITY )");
        try {
            stat.execute("ALTER TABLE t1 DROP COLUMN ID");
            fail("Table `t1` was accessible but should not have been.");
        } catch (SQLException e) {
            String message = e.getMessage();
            assertContains(message, "Table \"t1\" not found;");
        }

        conn.close();
        deleteDb(getTestName());
    }

    private void testWithoutAnyCandidateWhenDatabaseToLower() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnectionWithSettings("DATABASE_TO_LOWER=TRUE");
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE T2 ( ID INT IDENTITY )");
        try {
            stat.execute("ALTER TABLE T1 DROP COLUMN ID");
            fail("Table `t1` was accessible but should not have been.");
        } catch (SQLException e) {
            String message = e.getMessage();
            assertContains(message, "Table \"t1\" not found;");
        }

        conn.close();
        deleteDb(getTestName());
    }

    private void testWithoutAnyCandidateWhenDatabaseToUpper() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnectionWithSettings("DATABASE_TO_LOWER=FALSE;DATABASE_TO_UPPER=TRUE");
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE T2 ( ID INT IDENTITY )");
        try {
            stat.execute("ALTER TABLE t1 DROP COLUMN ID");
            fail("Table `T1` was accessible but should not have been.");
        } catch (SQLException e) {
            String message = e.getMessage();
            assertContains(message, "Table \"T1\" not found;");
        }

        conn.close();
        deleteDb(getTestName());
    }

    private void testWithoutAnyCandidateWhenCaseInsensitiveIdentifiers() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnectionWithSettings("DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE");
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE T2 ( ID INT IDENTITY )");
        try {
            stat.execute("ALTER TABLE t1 DROP COLUMN ID");
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
        Connection conn = getConnectionWithSettings("DATABASE_TO_UPPER=FALSE");
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE T1 ( ID INT IDENTITY )");
        try {
            stat.execute("ALTER TABLE t1 DROP COLUMN ID");
            fail("Table `t1` was accessible but should not have been.");
        } catch (SQLException e) {
            String message = e.getMessage();
            assertContains(message, "Table \"t1\" not found (candidates are: \"T1\")");
        }

        conn.close();
        deleteDb(getTestName());
    }

    private void testWithOneCandidateWhenDatabaseToLower() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnectionWithSettings("DATABASE_TO_LOWER=TRUE");
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE t1 ( ID INT IDENTITY, PAYLOAD INT )");
        stat.execute("ALTER TABLE T1 DROP COLUMN PAYLOAD");
        conn.close();
        deleteDb(getTestName());
    }

    private void testWithOneCandidateWhenDatabaseToUpper() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnectionWithSettings("DATABASE_TO_UPPER=TRUE");
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE T1 ( ID INT IDENTITY, PAYLOAD INT )");
        stat.execute("ALTER TABLE t1 DROP COLUMN PAYLOAD");
        conn.close();
        deleteDb(getTestName());
    }

    private void testWithOneCandidateWhenCaseInsensitiveIdentifiers() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnectionWithSettings("DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE");
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE T1 ( ID INT IDENTITY, PAYLOAD INT )");
        stat.execute("ALTER TABLE t1 DROP COLUMN PAYLOAD");
        conn.close();
        deleteDb(getTestName());
    }

    private void testWithTwoCandidates() throws SQLException {
        deleteDb(getTestName());
        Connection conn = getConnectionWithSettings("DATABASE_TO_UPPER=FALSE");
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE Toast ( ID INT IDENTITY )");
        stat.execute("CREATE TABLE TOAST ( ID INT IDENTITY )");
        try {
            stat.execute("ALTER TABLE toast DROP COLUMN ID");
            fail("Table `toast` was accessible but should not have been.");
        } catch (SQLException e) {
            String message = e.getMessage();
            assertContains(message, "Table \"toast\" not found (candidates are: \"TOAST, Toast\")");
        }

        conn.close();
        deleteDb(getTestName());
    }

    private Connection getConnectionWithSettings(String settings) throws SQLException {
        return getConnection(getTestName() + ";" + settings);
    }
}
