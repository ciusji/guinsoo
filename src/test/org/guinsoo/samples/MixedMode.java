/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.samples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.guinsoo.tools.Server;

/**
 * This sample program opens the same database once in embedded mode,
 * and once in the server mode. The embedded mode is faster, but only
 * the server mode supports remote connections.
 */
public class MixedMode {

    /**
     * This method is called when executing this sample application from the
     * command line.
     *
     * @param args the command line parameters
     */
    public static void main(String... args) throws Exception {

        // start the server, allows to access the database remotely
        Server server = Server.createTcpServer("-tcpPort", "9081");
        server.start();
        System.out.println(
                "You can access the database remotely now, using the URL:");
        System.out.println(
                "jdbc:guinsoo:tcp://localhost:9081/~/test (user: sa, password: sa)");

        // now use the database in your application in embedded mode
        Class.forName("org.guinsoo.Driver");
        Connection conn = DriverManager.getConnection(
                "jdbc:guinsoo:~/test", "sa", "sa");

        // some simple 'business usage'
        Statement stat = conn.createStatement();
        stat.execute("DROP TABLE TIMER IF EXISTS");
        stat.execute("CREATE TABLE TIMER(ID INT PRIMARY KEY, TIME VARCHAR)");
        System.out.println("Execute this a few times: " +
                "SELECT TIME FROM TIMER");
        System.out.println("To stop this application " +
                "(and the server), run: DROP TABLE TIMER");
        try {
            while (true) {
                // runs forever, except if you drop the table remotely
                stat.execute("MERGE INTO TIMER VALUES(1, LOCALTIME)");
                Thread.sleep(1000);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString());
        }
        conn.close();

        // stop the server
        server.stop();
    }
}
