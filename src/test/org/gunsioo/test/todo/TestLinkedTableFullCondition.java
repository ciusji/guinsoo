/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.todo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.gunsioo.tools.DeleteDbFiles;

/**
 * The complete condition should be sent to a linked table, not just the index
 * condition.
 */
public class TestLinkedTableFullCondition {

    /**
     * Run just this test.
     *
     * @param args ignored
     */
    public static void main(String... args) throws Exception {
        DeleteDbFiles.execute("data", null, true);
        Class.forName("org.gunsioo.Driver");
        Connection conn;
        conn = DriverManager.getConnection("jdbc:gunsioo:data/test");
        Statement stat = conn.createStatement();
        stat.execute("create table test(id int primary key, name varchar)");
        stat.execute("insert into test values(1, 'Hello')");
        stat.execute("insert into test values(2, 'World')");
        stat.execute("create linked table test_link" +
                "('', 'jdbc:gunsioo:data/test', '', '', 'TEST')");
        stat.execute("set trace_level_system_out 2");
        // the query sent to the linked database is
        // SELECT * FROM PUBLIC.TEST T WHERE ID>=? AND ID<=? {1: 1, 2: 1};
        // it should also include AND NAME='Hello'
        stat.execute("select * from test_link " +
                "where id = 1 and name = 'Hello'");
        conn.close();
    }

}
