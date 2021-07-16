/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.unit;

import java.sql.Connection;
import java.sql.Statement;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;

/**
 * Test the ICU4J collator.
 */
public class TestCollation extends TestDb {

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
        deleteDb("collation");
        Connection conn = getConnection("collation");
        Statement stat = conn.createStatement();
        assertThrows(ErrorCode.INVALID_VALUE_2, stat).
                execute("set collation xyz");
        stat.execute("set collation en");
        stat.execute("set collation default_en");
        assertThrows(ErrorCode.CLASS_NOT_FOUND_1, stat).
                execute("set collation icu4j_en");

        stat.execute("set collation ge");
        stat.execute("create table test(id int)");
        // the same as the current - ok
        stat.execute("set collation ge");
        // not allowed to change now
        assertThrows(ErrorCode.COLLATION_CHANGE_WITH_DATA_TABLE_1, stat).
            execute("set collation en");

        conn.close();
        deleteDb("collation");
    }

}
