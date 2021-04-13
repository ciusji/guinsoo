/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.unit;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;
import org.gunsioo.api.ErrorCode;
import org.gunsioo.test.TestBase;
import org.gunsioo.test.TestDb;
import org.gunsioo.tools.Server;

/**
 * Tests the compatibility with older versions
 */
public class TestOldVersion extends TestDb {

    private ClassLoader cl;
    private Driver driver;

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }

    @Override
    public boolean isEnabled() {
        if (config.mvStore) {
            return false;
        }
        return true;
    }

    @Override
    public void test() throws Exception {
        cl = getClassLoader("file:ext/h2-1.2.127.jar");
        driver = getDriver(cl);
        if (driver == null) {
            println("not found: ext/h2-1.2.127.jar - test skipped");
            return;
        }
        Connection conn = driver.connect("jdbc:gunsioo:mem:", null);
        assertEquals("1.2.127 (2010-01-15)", conn.getMetaData()
                .getDatabaseProductVersion());
        conn.close();
        testLobInFiles();
        testOldClientNewServer();
    }

    private void testLobInFiles() throws Exception {
        deleteDb("oldVersion");
        Connection conn;
        Statement stat;
        conn = driver.connect("jdbc:gunsioo:" + getBaseDir() + "/oldVersion", null);
        stat = conn.createStatement();
        stat.execute("create table test(id int primary key, b blob, c clob)");
        PreparedStatement prep = conn
                .prepareStatement("insert into test values(?, ?, ?)");
        prep.setInt(1, 0);
        prep.setNull(2, Types.BLOB);
        prep.setNull(3, Types.CLOB);
        prep.execute();
        prep.setInt(1, 1);
        prep.setBytes(2, new byte[0]);
        prep.setString(3, "");
        prep.execute();
        prep.setInt(1, 2);
        prep.setBytes(2, new byte[5]);
        prep.setString(3, "\u1234\u1234\u1234\u1234\u1234");
        prep.execute();
        prep.setInt(1, 3);
        prep.setBytes(2, new byte[100000]);
        prep.setString(3, new String(new char[100000]));
        prep.execute();
        conn.close();
        try {
            conn = DriverManager.getConnection("jdbc:gunsioo:" + getBaseDir() +
                    "/oldVersion", new Properties());
            conn.createStatement().executeQuery("select * from test");
        } catch (SQLException e) {
            assertEquals(ErrorCode.FILE_VERSION_ERROR_1, e.getErrorCode());
            return;
        }
        fail("Old 1.2 database isn't detected");
    }

    private void testOldClientNewServer() throws Exception {
        Server server = org.gunsioo.tools.Server.createTcpServer();
        server.start();
        int port = server.getPort();
        assertThrows(ErrorCode.DRIVER_VERSION_ERROR_2, driver).connect(
                "jdbc:gunsioo:tcp://localhost:" + port + "/mem:test", null);
        server.stop();

        Class<?> serverClass = cl.loadClass("org.gunsioo.tools.Server");
        Method m;
        m = serverClass.getMethod("createTcpServer", String[].class);
        Object serverOld = m.invoke(null, new Object[] { new String[] {
                "-tcpPort", "" + port } });
        m = serverOld.getClass().getMethod("start");
        m.invoke(serverOld);
        Connection conn;
        conn = org.gunsioo.Driver.load().connect("jdbc:gunsioo:mem:", null);
        Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery("call 1");
        rs.next();
        assertEquals(1, rs.getInt(1));
        conn.close();
        m = serverOld.getClass().getMethod("stop");
        m.invoke(serverOld);
    }

    private static ClassLoader getClassLoader(String jarFile) throws Exception {
        URL[] urls = { new URL(jarFile) };
        return new URLClassLoader(urls, null) {
            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if (name.startsWith("org.gunsioo."))
                    return super.loadClass(name, resolve);
                return TestOldVersion.class.getClassLoader().loadClass(name);
            }
        };
    }

    private static Driver getDriver(ClassLoader cl) throws Exception {
        Class<?> driverClass;
        try {
            driverClass = cl.loadClass("org.gunsioo.Driver");
        } catch (ClassNotFoundException e) {
            return null;
        }
        Method m = driverClass.getMethod("load");
        Driver driver = (Driver) m.invoke(null);
        return driver;
    }

}
