/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.test.TestBase;
import org.gunsioo.test.TestDb;

/**
 * Tests if prepared statements are re-compiled when required.
 */
public class TestAutoRecompile extends TestDb {

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
        deleteDb("autoRecompile");
        Connection conn = getConnection("autoRecompile");
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE TEST(ID INT PRIMARY KEY)");
        PreparedStatement prep = conn.prepareStatement("SELECT * FROM TEST");
        assertEquals(1, prep.executeQuery().getMetaData().getColumnCount());
        stat.execute("ALTER TABLE TEST ADD COLUMN NAME VARCHAR(255)");
        assertEquals(2, prep.executeQuery().getMetaData().getColumnCount());
        stat.execute("DROP TABLE TEST");
        stat.execute("CREATE TABLE TEST(ID INT PRIMARY KEY, X INT, Y INT)");
        assertEquals(3, prep.executeQuery().getMetaData().getColumnCount());
        // TODO test auto-recompile with insert..select, views and so on

        prep = conn.prepareStatement("INSERT INTO TEST VALUES(1, 2, 3)");
        stat.execute("ALTER TABLE TEST ADD COLUMN Z INT");
        assertThrows(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH, prep).execute();
        assertThrows(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH, prep).execute();
        conn.close();
        deleteDb("autoRecompile");
    }

}
