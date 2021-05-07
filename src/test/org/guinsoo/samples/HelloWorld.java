/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.samples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A very simple class that shows how to load the driver, create a database,
 * create a table, and insert some data.
 */
public class HelloWorld {

    /**
     * Called when ran from command line.
     *
     * @param args ignored
     */
    public static void main(String... args) throws Exception {
        // delete the database named 'test' in the user home directory
        // DeleteDbFiles.execute("~", "test", true);

        Class.forName("org.guinsoo.Driver");
        Connection conn = DriverManager.getConnection("jdbc:guinsoo:mem:");
        Statement stat = conn.createStatement();

        // this line would initialize the database
        // from the SQL script file 'init.sql'
        // stat.execute("runscript from 'init.sql'");

        stat.execute("create table test(id int primary key, name varchar(255))");
        stat.execute("insert into test values(1, 'Hello')");
        stat.execute("insert into test values(2, 'Hello')");
        stat.execute("insert into test values(3, 'Hello')");
        ResultSet rs;
        rs = stat.executeQuery("select * from test");
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
        stat.close();
        conn.close();
    }

}
