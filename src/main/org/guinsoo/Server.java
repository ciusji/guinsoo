/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo;

import java.sql.Connection;
import java.sql.SQLException;

import org.guinsoo.message.DbException;
import org.guinsoo.server.Service;
import org.guinsoo.server.ShutdownHandler;
import org.guinsoo.server.TcpServer;
import org.guinsoo.server.pg.PgServer;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.util.Tool;

/**
 * Starts the Guinsoo service, TCP, and PG server.
 *
 * @author ciusji
 */
public class Server extends Tool implements Runnable, ShutdownHandler {

    private final Service service;
    private Server tcp, pg;
    private ShutdownHandler shutdownHandler;
    private boolean started;

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public Server() {
        // echo logo
        this.service = null;
        System.out.println(ANSI_GREEN + "\n" +
                "                _                      \n" +
                "   ____ ___  __(_)___  _________  ____ \n" +
                "  / __ `/ / / / / __ \\/ ___/ __ \\/ __ \\\n" +
                " / /_/ / /_/ / / / / (__  ) /_/ / /_/ /\n" +
                " \\__, /\\__,_/_/_/ /_/____/\\____/\\____/ \n" +
                "/____/                               \n" +
                "  \n" +
                "version 0.2.2, redefine your data.  \n"
                + ANSI_RESET
        );
    }

    /**
     * Create a new server for the given service.
     *
     * @param service the service
     * @param args the command line arguments
     */
    public Server(Service service, String... args) throws SQLException {
        verifyArgs(args);
        this.service = service;
        try {
            service.init(args);
        } catch (Exception e) {
            throw DbException.toSQLException(e);
        }
    }

    /**
     * When running without options, -tcp and -pg are started.
     * <br />
     * Options are case sensitive. Supported options are:
     * <table>
     * <tr><td>[-help] or [-?]</td>
     * <td>Print the list of options</td></tr>
     * <tr><td>[-tcp]</td>
     * <td>Start the TCP server</td></tr>
     * <tr><td>[-tcpAllowOthers]</td>
     * <td>Allow other computers to connect - see below</td></tr>
     * <tr><td>[-tcpDaemon]</td>
     * <td>Use a daemon thread</td></tr>
     * <tr><td>[-tcpPort &lt;port&gt;]</td>
     * <td>The port (default: 9092)</td></tr>
     * <tr><td>[-tcpSSL]</td>
     * <td>Use encrypted (SSL) connections</td></tr>
     * <tr><td>[-tcpPassword &lt;pwd&gt;]</td>
     * <td>The password for shutting down a TCP server</td></tr>
     * <tr><td>[-tcpShutdown "&lt;url&gt;"]</td>
     * <td>Stop the TCP server; example: tcp://localhost</td></tr>
     * <tr><td>[-tcpShutdownForce]</td>
     * <td>Do not wait until all connections are closed</td></tr>
     * <tr><td>[-pg]</td>
     * <td>Start the PG server</td></tr>
     * <tr><td>[-pgAllowOthers]</td>
     * <td>Allow other computers to connect - see below</td></tr>
     * <tr><td>[-pgDaemon]</td>
     * <td>Use a daemon thread</td></tr>
     * <tr><td>[-pgPort &lt;port&gt;]</td>
     * <td>The port (default: 5435)</td></tr>
     * <tr><td>[-properties "&lt;dir&gt;"]</td>
     * <td>Server properties (default: ~, disable: null)</td></tr>
     * <tr><td>[-baseDir &lt;dir&gt;]</td>
     * <td>The base directory for Guinsoo databases (all servers)</td></tr>
     * <tr><td>[-ifExists]</td>
     * <td>Only existing databases may be opened (all servers)</td></tr>
     * <tr><td>[-ifNotExists]</td>
     * <td>Databases are created when accessed</td></tr>
     * <tr><td>[-trace]</td>
     * <td>Print additional trace information (all servers)</td></tr>
     * <tr><td>[-key &lt;from&gt; &lt;to&gt;]</td>
     * <td>Allows to map a database name to another (all servers)</td></tr>
     * </table>
     * The options -xAllowOthers are potentially risky.
     * <br />
     * For details, see Advanced Topics / Protection against Remote Access.
     *
     * @param args the command line arguments
     */
    public static void main(String... args) throws SQLException {
        new Server().runTool(args);
    }

    private void verifyArgs(String... args) throws SQLException {
        for (int i = 0; args != null && i < args.length; i++) {
            String arg = args[i];
            if (arg == null) {
            } else if ("-?".equals(arg) || "-help".equals(arg)) {
                // ok
            } else if (arg.startsWith("-tcp")) {
                if ("-tcp".equals(arg)) {
                    // ok
                } else if ("-tcpAllowOthers".equals(arg)) {
                    // no parameters
                } else if ("-tcpDaemon".equals(arg)) {
                    // no parameters
                } else if ("-tcpSSL".equals(arg)) {
                    // no parameters
                } else if ("-tcpPort".equals(arg)) {
                    i++;
                } else if ("-tcpPassword".equals(arg)) {
                    i++;
                } else if ("-tcpShutdown".equals(arg)) {
                    i++;
                } else if ("-tcpShutdownForce".equals(arg)) {
                    // ok
                } else {
                    throwUnsupportedOption(arg);
                }
            } else if (arg.startsWith("-pg")) {
                if ("-pg".equals(arg)) {
                    // ok
                } else if ("-pgAllowOthers".equals(arg)) {
                    // no parameters
                } else if ("-pgDaemon".equals(arg)) {
                    // no parameters
                } else if ("-pgPort".equals(arg)) {
                    i++;
                } else {
                    throwUnsupportedOption(arg);
                }
            } else if (arg.startsWith("-ftp")) {
                if ("-ftpPort".equals(arg)) {
                    i++;
                } else if ("-ftpDir".equals(arg)) {
                    i++;
                } else if ("-ftpRead".equals(arg)) {
                    i++;
                } else if ("-ftpWrite".equals(arg)) {
                    i++;
                } else if ("-ftpWritePassword".equals(arg)) {
                    i++;
                } else if ("-ftpTask".equals(arg)) {
                    // no parameters
                } else {
                    throwUnsupportedOption(arg);
                }
            } else if ("-properties".equals(arg)) {
                i++;
            } else if ("-trace".equals(arg)) {
                // no parameters
            } else if ("-ifExists".equals(arg)) {
                // no parameters
            } else if ("-ifNotExists".equals(arg)) {
                // no parameters
            } else if ("-baseDir".equals(arg)) {
                i++;
            } else if ("-key".equals(arg)) {
                i += 2;
            } else if ("-tool".equals(arg)) {
                // no parameters
            } else {
                throwUnsupportedOption(arg);
            }
        }
    }

    @Override
    public void runTool(String... args) throws SQLException {
        boolean tcpStart = false, pgStart = false;
        /// boolean browserStart = false;
        boolean tcpShutdown = false, tcpShutdownForce = false;
        String tcpPassword = "";
        String tcpShutdownServer = "";
        boolean startDefaultServers = true;
        for (int i = 0; args != null && i < args.length; i++) {
            String arg = args[i];
            if (arg == null) {
            } else if ("-?".equals(arg) || "-help".equals(arg)) {
                showUsage();
                return;
            } else if (arg.startsWith("-tcp")) {
                if ("-tcp".equals(arg)) {
                    startDefaultServers = false;
                    tcpStart = true;
                } else if ("-tcpAllowOthers".equals(arg)) {
                    // no parameters
                } else if ("-tcpDaemon".equals(arg)) {
                    // no parameters
                } else if ("-tcpSSL".equals(arg)) {
                    // no parameters
                } else if ("-tcpPort".equals(arg)) {
                    i++;
                } else if ("-tcpPassword".equals(arg)) {
                    tcpPassword = args[++i];
                } else if ("-tcpShutdown".equals(arg)) {
                    startDefaultServers = false;
                    tcpShutdown = true;
                    tcpShutdownServer = args[++i];
                } else if ("-tcpShutdownForce".equals(arg)) {
                    tcpShutdownForce = true;
                } else {
                    showUsageAndThrowUnsupportedOption(arg);
                }
            } else if (arg.startsWith("-pg")) {
                if ("-pg".equals(arg)) {
                    startDefaultServers = false;
                    pgStart = true;
                } else if ("-pgAllowOthers".equals(arg)) {
                    // no parameters
                } else if ("-pgDaemon".equals(arg)) {
                    // no parameters
                } else if ("-pgPort".equals(arg)) {
                    i++;
                } else {
                    showUsageAndThrowUnsupportedOption(arg);
                }
            } else if ("-properties".equals(arg)) {
                i++;
            } else if ("-trace".equals(arg)) {
                // no parameters
            } else if ("-ifExists".equals(arg)) {
                // no parameters
            } else if ("-ifNotExists".equals(arg)) {
                // no parameters
            } else if ("-baseDir".equals(arg)) {
                i++;
            } else if ("-key".equals(arg)) {
                i += 2;
            } else {
                showUsageAndThrowUnsupportedOption(arg);
            }
        }
        verifyArgs(args);
        if (startDefaultServers) {
            tcpStart = true;
            pgStart = true;
        }
        // TODO server: maybe use one single properties file?
        if (tcpShutdown) {
            out.println("Shutting down TCP Server at " + tcpShutdownServer);
            shutdownTcpServer(tcpShutdownServer, tcpPassword,
                    tcpShutdownForce, false);
        }
        try {
            if (tcpStart) {
                tcp = createTcpServer(args);
                tcp.start();
                out.println(tcp.getStatus());
                tcp.setShutdownHandler(this);
            }
            if (pgStart) {
                pg = createPgServer(args);
                pg.start();
                out.println(pg.getStatus());
            }
        } catch (SQLException e) {
            stopAll();
            throw e;
        }
    }

    /**
     * Shutdown one or all TCP server. If force is set to false, the server will
     * not allow new connections, but not kill existing connections, instead it
     * will stop if the last connection is closed. If force is set to true,
     * existing connections are killed. After calling the method with
     * force=false, it is not possible to call it again with force=true because
     * new connections are not allowed. Example:
     *
     * <pre>
     * Server.shutdownTcpServer(&quot;tcp://localhost:9094&quot;,
     *         password, true, false);
     * </pre>
     *
     * @param url example: tcp://localhost:9094
     * @param password the password to use ("" for no password)
     * @param force the shutdown (don't wait)
     * @param all whether all TCP servers that are running in the JVM should be
     *            stopped
     */
    public static void shutdownTcpServer(String url, String password,
            boolean force, boolean all) throws SQLException {
        TcpServer.shutdown(url, password, force, all);
    }

    /**
     * Get the status of this server.
     *
     * @return the status
     */
    public String getStatus() {
        StringBuilder buff = new StringBuilder();
        if (!started) {
            buff.append("Not started");
        } else if (isRunning(false)) {
            buff.append(service.getType()).
                append(" server running at ").
                append(service.getURL()).
                append(" (");
            if (service.getAllowOthers()) {
                buff.append("others can connect");
            } else {
                buff.append("only local connections");
            }
            buff.append(')');
        } else {
            buff.append("The ").
                append(service.getType()).
                append(" server could not be started. " +
                        "Possible cause: another server is already running at ").
                append(service.getURL());
        }
        return buff.toString();
    }

    /**
     * Create a new web server, but does not start it yet. Example:
     *
     * <pre>
     * Server server = Server.createWebServer("-trace").start();
     * </pre>
     * Supported options are:
     * -webPort, -webSSL, -webAllowOthers, -webDaemon,
     * -trace, -ifExists, -ifNotExists, -baseDir, -properties.
     * See the main method for details.
     *
     * @param args the argument list
     * @return the server
     */
    @Deprecated
    public static Server createWebServer(String... args) throws SQLException {
        return null;
    }

    /**
     * Create a new TCP server, but does not start it yet. Example:
     *
     * <pre>
     * Server server = Server.createTcpServer("-tcpPort", "9123", "-tcpAllowOthers").start();
     * </pre>
     * Supported options are:
     * -tcpPort, -tcpSSL, -tcpPassword, -tcpAllowOthers, -tcpDaemon,
     * -trace, -ifExists, -ifNotExists, -baseDir, -key.
     * See the main method for details.
     * <p>
     * If no port is specified, the default port is used if possible,
     * and if this port is already used, a random port is used.
     * Use getPort() or getURL() after starting to retrieve the port.
     * </p>
     *
     * @param args the argument list
     * @return the server
     */
    public static Server createTcpServer(String... args) throws SQLException {
        TcpServer service = new TcpServer();
        Server server = new Server(service, args);
        service.setShutdownHandler(server);
        return server;
    }

    /**
     * Create a new PG server, but does not start it yet.
     * Example:
     * <pre>
     * Server server = Server.createPgServer("-pgAllowOthers").start();
     * </pre>
     * Supported options are:
     * -pgPort, -pgAllowOthers, -pgDaemon,
     * -trace, -ifExists, -ifNotExists, -baseDir, -key.
     * See the main method for details.
     * <p>
     * If no port is specified, the default port is used if possible,
     * and if this port is already used, a random port is used.
     * Use getPort() or getURL() after starting to retrieve the port.
     * </p>
     *
     * @param args the argument list
     * @return the server
     */
    public static Server createPgServer(String... args) throws SQLException {
        return new Server(new PgServer(), args);
    }

    /**
     * Tries to start the server.
     * @return the server if successful
     * @throws SQLException if the server could not be started
     */
    public Server start() throws SQLException {
        int tryTimes = 64;
        try {
            started = true;
            service.start();
            String url = service.getURL();
            int idx = url.indexOf('?');
            if (idx >= 0) {
                url = url.substring(0, idx);
            }
            String name = service.getName() + " (" + url + ')';
            Thread t = new Thread(this, name);
            t.setDaemon(service.isDaemon());
            t.start();
            for (int i = 1; i < tryTimes; i += i) {
                wait(i);
                if (isRunning(false)) {
                    return this;
                }
            }
            if (isRunning(true)) {
                return this;
            }
            throw DbException.get(ErrorCode.EXCEPTION_OPENING_PORT_2,
                    name, "timeout; " +
                    "please check your network configuration, specially the file /etc/hosts");
        } catch (DbException e) {
            throw DbException.toSQLException(e);
        }
    }

    private static void wait(int i) {
        try {
            // sleep at most 4096 ms
            long sleep = (long) i * (long) i;
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private void stopAll() {
        Server s = tcp;
        if (s != null && s.isRunning(false)) {
            s.stop();
        }
        s = tcp;
        if (s != null && s.isRunning(false)) {
            s.stop();
            tcp = null;
        }
        s = pg;
        if (s != null && s.isRunning(false)) {
            s.stop();
            pg = null;
        }
    }

    /**
     * Checks if the server is running.
     *
     * @param traceError if errors should be written
     * @return if the server is running
     */
    public boolean isRunning(boolean traceError) {
        return service.isRunning(traceError);
    }

    /**
     * Stops the server.
     */
    public void stop() {
        started = false;
        if (service != null) {
            service.stop();
        }
    }

    /**
     * Gets the URL of this server.
     *
     * @return the url
     */
    public String getURL() {
        return service.getURL();
    }

    /**
     * Gets the port this server is listening on.
     *
     * @return the port
     */
    public int getPort() {
        return service.getPort();
    }

    /**
     * INTERNAL
     */
    @Override
    public void run() {
        try {
            service.listen();
        } catch (Exception e) {
            DbException.traceThrowable(e);
        }
    }

    /**
     * INTERNAL
     */
    public void setShutdownHandler(ShutdownHandler shutdownHandler) {
        this.shutdownHandler = shutdownHandler;
    }

    /**
     * INTERNAL
     */
    @Override
    public void shutdown() {
        if (shutdownHandler != null) {
            shutdownHandler.shutdown();
        } else {
            stopAll();
        }
    }

    /**
     * Get the service attached to this server.
     *
     * @return the service
     */
    public Service getService() {
        return service;
    }

    /**
     * Open a new browser tab or window with the given URL.
     *
     * @param url the URL to open
     */
    @Deprecated
    public static void openBrowser(String url) throws Exception {
        ///
    }

    /**
     * Start a web server and a browser that uses the given connection. The
     * current transaction is preserved. This is specially useful to manually
     * inspect the database when debugging. This method return as soon as the
     * user has disconnected.
     *
     * @param conn the database connection (the database must be open)
     */
    @Deprecated
    public static void startWebServer(Connection conn) throws SQLException {
        /// startWebServer(conn, false);
    }

    /**
     * Start a web server and a browser that uses the given connection. The
     * current transaction is preserved. This is specially useful to manually
     * inspect the database when debugging. This method return as soon as the
     * user has disconnected.
     *
     * @param conn the database connection (the database must be open)
     * @param ignoreProperties if {@code true} properties from
     *         {@code .guinsoo.server.properties} will be ignored
     */
    @Deprecated
    public static void startWebServer(Connection conn, boolean ignoreProperties) {
        ///
    }

}
