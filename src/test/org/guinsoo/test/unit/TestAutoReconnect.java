/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.unit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.guinsoo.Driver;
import org.guinsoo.api.DatabaseEventListener;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;
import org.guinsoo.tools.Server;

/**
 * Tests automatic embedded/server mode.
 */
public class TestAutoReconnect extends TestDb {

    private String url;
    private boolean autoServer;
    private Server server;
    private Connection connServer;
    private Connection conn;

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }

    private void restart() throws SQLException, InterruptedException {
        if (autoServer) {
            if (connServer != null) {
                connServer.createStatement().execute("SHUTDOWN");
                connServer.close();
            }
            Driver.load();
            connServer = getConnection(url);
        } else {
            server.stop();
            Thread.sleep(100); // try to prevent "port may be in use" error
            server.start();
        }
    }

    @Override
    public void test() throws Exception {
        testWrongUrl();
        autoServer = true;
        testReconnect();
        autoServer = false;
        testReconnect();
        deleteDb(getTestName());
    }

    private void testWrongUrl() throws Exception {
        deleteDb(getTestName());
        Server tcp = Server.createTcpServer().start();
        try {
            conn = getConnection("jdbc:guinsoo:" + getBaseDir() + '/' + getTestName() + ";AUTO_SERVER=TRUE");
            assertThrows(ErrorCode.DATABASE_ALREADY_OPEN_1,
                    () -> getConnection("jdbc:guinsoo:" + getBaseDir() + '/' + getTestName() + ";OPEN_NEW=TRUE"));
            assertThrows(ErrorCode.DATABASE_ALREADY_OPEN_1,
                    () -> getConnection("jdbc:guinsoo:" + getBaseDir() + '/' + getTestName() + ";OPEN_NEW=TRUE"));
            conn.close();

            conn = getConnection("jdbc:guinsoo:tcp://localhost:" + tcp.getPort() + '/' + getBaseDir() + '/' //
                    + getTestName());
            assertThrows(ErrorCode.DATABASE_ALREADY_OPEN_1, () -> getConnection(
                    "jdbc:guinsoo:" + getBaseDir() + '/' + getTestName() + ";AUTO_SERVER=TRUE;OPEN_NEW=TRUE"));
            conn.close();
        } finally {
            tcp.stop();
        }
    }

    private void testReconnect() throws Exception {
        deleteDb(getTestName());
        if (autoServer) {
            url = "jdbc:guinsoo:" + getBaseDir() + "/" + getTestName() + ";" +
                "FILE_LOCK=SOCKET;" +
                "AUTO_SERVER=TRUE;OPEN_NEW=TRUE";
            restart();
        } else {
            server = Server.createTcpServer("-ifNotExists").start();
            int port = server.getPort();
            url = "jdbc:guinsoo:tcp://localhost:" + port + "/" + getBaseDir() + "/" + getTestName() + ";" +
                "FILE_LOCK=SOCKET;AUTO_RECONNECT=TRUE";
        }

        // test the database event listener
        conn = getConnection(url + ";DATABASE_EVENT_LISTENER='" +
        MyDatabaseEventListener.class.getName() + "'");
        conn.close();

        Statement stat;

        conn = getConnection(url);
        restart();
        stat = conn.createStatement();
        restart();
        stat.execute("create table test(id identity, name varchar)");
        restart();
        PreparedStatement prep = conn.prepareStatement(
                "insert into test(name) values(?)");
        restart();
        prep.setString(1, "Hello");
        restart();
        prep.execute();
        restart();
        prep.setString(1, "World");
        restart();
        prep.execute();
        restart();
        ResultSet rs = stat.executeQuery("select * from test order by id");
        restart();
        assertTrue(rs.next());
        restart();
        assertEquals(1, rs.getInt(1));
        restart();
        assertEquals("Hello", rs.getString(2));
        restart();
        assertTrue(rs.next());
        restart();
        assertEquals(2, rs.getInt(1));
        restart();
        assertEquals("World", rs.getString(2));
        restart();
        assertFalse(rs.next());
        restart();
        stat.execute("SET @TEST 10");
        restart();
        rs = stat.executeQuery("CALL @TEST");
        rs.next();
        assertEquals(10, rs.getInt(1));
        stat.setFetchSize(10);
        restart();
        rs = stat.executeQuery("select * from system_range(1, 20)");
        restart();
        for (int i = 0;; i++) {
            try {
                boolean more = rs.next();
                if (!more) {
                    assertEquals(i, 20);
                    break;
                }
                restart();
                int x = rs.getInt(1);
                assertEquals(x, i + 1);
                if (i > 10) {
                    fail();
                }
            } catch (SQLException e) {
                if (i < 10) {
                    throw e;
                }
                break;
            }
        }
        restart();
        rs.close();

        conn.setAutoCommit(false);
        restart();
        assertThrows(ErrorCode.CONNECTION_BROKEN_1, conn.createStatement()).
                execute("select * from test");

        conn.close();
        if (autoServer) {
            connServer.close();
        } else {
            server.stop();
        }
    }

    /**
     * A database event listener used in this test.
     */
    public static final class MyDatabaseEventListener implements DatabaseEventListener {
    }
}
