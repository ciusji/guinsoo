/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.samples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This sample application shows how to cache prepared statements.
 */
public class CachedPreparedStatements {

    private Connection conn;
    private Statement stat;
    private final ConcurrentHashMap<String, PreparedStatement> prepared = new ConcurrentHashMap<>();

    /**
     * This method is called when executing this sample application from the
     * command line.
     *
     * @param args the command line parameters
     */
    public static void main(String... args) throws Exception {
        new CachedPreparedStatements().run();
    }

    private void run() throws Exception {
        Class.forName("org.guinsoo.Driver");
        conn = DriverManager.getConnection(
                "jdbc:guinsoo:mem:", "sa", "");
        stat = conn.createStatement();
        stat.execute(
                "create table test(id int primary key, name varchar)");
        PreparedStatement prep = prepare(
                "insert into test values(?, ?)");
        prep.setInt(1, 1);
        prep.setString(2, "Hello");
        prep.execute();
        conn.close();
    }

    private PreparedStatement prepare(String sql)
            throws SQLException {
        PreparedStatement prep = prepared.get(sql);
        if (prep == null) {
            prep = conn.prepareStatement(sql);
            prepared.put(sql, prep);
        }
        return prep;
    }

}
