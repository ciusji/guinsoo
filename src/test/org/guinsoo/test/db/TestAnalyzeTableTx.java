/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;

public class TestAnalyzeTableTx extends TestDb {
    private static final int C = 10_000;

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }

    @Override
    public boolean isEnabled() {
        return !config.networked && !config.big;
    }

    @Override
    public void test() throws Exception {
        deleteDb(getTestName());
        Connection[] connections = new Connection[C];
        try (Connection shared = getConnection(getTestName())) {
            Statement statement = shared.createStatement();
            statement.executeUpdate("DROP TABLE IF EXISTS TEST");
            statement.executeUpdate("CREATE TABLE TEST(ID INT PRIMARY KEY)");
            for (int i = 0; i < C; i++) {
                Connection c = getConnection(getTestName());
                c.createStatement().executeUpdate("INSERT INTO TEST VALUES (" + i + ')');
                connections[i] = c;
            }
            try (ResultSet rs = statement.executeQuery("SELECT * FROM TEST")) {
                for (int i = 0; i < C; i++) {
                    if (!rs.next())
                        throw new Exception("next");
                    if (rs.getInt(1) != i)
                        throw new Exception(Integer.toString(i));
                }
            }
        } finally {
            for (Connection connection : connections) {
                if (connection != null) {
                    try { connection.close(); } catch (Throwable ignore) {/**/}
                }
            }
        }
    }
}
