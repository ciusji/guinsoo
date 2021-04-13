/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.samples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.gunsioo.store.fs.FileUtils;
import org.gunsioo.tools.DeleteDbFiles;
import org.gunsioo.tools.RunScript;
import org.gunsioo.tools.Script;

/**
 * This sample application shows how to compact the database files.
 * This is done by creating a SQL script, and then re-creating the database
 * using this script.
 */
public class Compact {

    /**
     * This method is called when executing this sample application from the
     * command line.
     *
     * @param args the command line parameters
     */
    public static void main(String... args) throws Exception {
        DeleteDbFiles.execute("./data", "test", true);
        Class.forName("org.gunsioo.Driver");
        Connection conn = DriverManager.getConnection("jdbc:gunsioo:./data/test", "sa", "");
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR)");
        stat.execute("INSERT INTO TEST VALUES(1, 'Hello'), (2, 'World');");
        stat.close();
        conn.close();
        System.out.println("Compacting...");
        compact("./data", "test", "sa", "");
        System.out.println("Done.");
    }

    /**
     * Utility method to compact a database.
     *
     * @param dir the directory
     * @param dbName the database name
     * @param user the user name
     * @param password the password
     */
    public static void compact(String dir, String dbName,
            String user, String password) throws SQLException {
        String url = "jdbc:gunsioo:" + dir + "/" + dbName;
        String file = "data/test.sql";
        Script.process(url, user, password, file, "", "");
        DeleteDbFiles.execute(dir, dbName, true);
        RunScript.execute(url, user, password, file, null, false);
        FileUtils.delete(file);
    }

}
