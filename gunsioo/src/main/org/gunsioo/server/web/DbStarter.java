/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.server.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.gunsioo.tools.Server;
import org.gunsioo.util.StringUtils;

/**
 * This class can be used to start the Gunsioo TCP server (or other Gunsioo servers, for
 * example the PG server) inside a web application container such as Tomcat or
 * Jetty. It can also open a database connection.
 */
public class DbStarter implements ServletContextListener {

    private Connection conn;
    private Server server;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            org.gunsioo.Driver.load();

            // This will get the setting from a context-param in web.xml if
            // defined:
            ServletContext servletContext = servletContextEvent.getServletContext();
            String url = getParameter(servletContext, "db.url", "jdbc:gunsioo:~/test");
            String user = getParameter(servletContext, "db.user", "sa");
            String password = getParameter(servletContext, "db.password", "sa");

            // Start the server if configured to do so
            String serverParams = getParameter(servletContext, "db.tcpServer", null);
            if (serverParams != null) {
                String[] params = StringUtils.arraySplit(serverParams, ' ', true);
                server = Server.createTcpServer(params);
                server.start();
            }

            // To access the database in server mode, use the database URL:
            // jdbc:gunsioo:tcp://localhost/~/test
            conn = DriverManager.getConnection(url, user, password);
            servletContext.setAttribute("connection", conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getParameter(ServletContext servletContext,
            String key, String defaultValue) {
        String value = servletContext.getInitParameter(key);
        return value == null ? defaultValue : value;
    }

    /**
     * Get the connection.
     *
     * @return the connection
     */
    public Connection getConnection() {
        return conn;
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            Statement stat = conn.createStatement();
            stat.execute("SHUTDOWN");
            stat.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (server != null) {
            server.stop();
            server = null;
        }
    }

}
