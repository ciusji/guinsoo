/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;

/**
 * Test a BLOB larger than Integer.MAX_VALUE
 */
public class TestLargeBlob extends TestDb {

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
        if (!config.big || config.memory || config.networked) {
            return false;
        }
        return true;
    }

    @Override
    public void test() throws Exception {
        deleteDb("largeBlob");
        String url = getURL("largeBlob;TRACE_LEVEL_FILE=0", true);
        Connection conn = getConnection(url);
        final long testLength = Integer.MAX_VALUE + 110L;
        Statement stat = conn.createStatement();
        stat.execute("set COMPRESS_LOB LZF");
        stat.execute("create table test(x blob)");
        PreparedStatement prep = conn.prepareStatement(
                "insert into test values(?)");
        prep.setBinaryStream(1, new InputStream() {
            long remaining = testLength;
            int p;
            byte[] oneByte = { 0 };
            @Override
            public void close() {
                // ignore
            }
            @Override
            public int read(byte[] buff, int off, int len) {
                len = (int) Math.min(remaining, len);
                remaining -= len;
                if (p++ % 5000 == 0) {
                    println("" + remaining);
                }
                return len == 0 ? -1 : len;
            }
            @Override
            public int read() {
                return read(oneByte, 0, 1) < 0 ? -1 : oneByte[0];
            }
        }, -1);
        prep.executeUpdate();
        ResultSet rs = stat.executeQuery(
                "select length(x) from test");
        rs.next();
        assertEquals(testLength, rs.getLong(1));
        rs = stat.executeQuery("select x from test");
        rs.next();
        InputStream in = rs.getBinaryStream(1);
        byte[] buff = new byte[4 * 1024];
        long length = 0;
        while (true) {
            int len = in.read(buff);
            if (len < 0) {
                break;
            }
            length += len;
        }
        assertEquals(testLength, length);
        conn.close();
    }

}
