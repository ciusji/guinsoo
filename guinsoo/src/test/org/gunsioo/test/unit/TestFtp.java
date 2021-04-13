/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.unit;

import org.gunsioo.dev.ftp.FtpClient;
import org.gunsioo.dev.ftp.server.FtpEvent;
import org.gunsioo.dev.ftp.server.FtpEventListener;
import org.gunsioo.dev.ftp.server.FtpServer;
import org.gunsioo.store.fs.FileUtils;
import org.gunsioo.test.TestBase;
import org.gunsioo.tools.Server;

/**
 * Tests the FTP server tool.
 */
public class TestFtp extends TestBase implements FtpEventListener {

    private FtpEvent lastEvent;

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
        if (getBaseDir().indexOf(':') > 0) {
            return false;
        }
        return true;
    }

    @Override
    public void test() throws Exception {
        FileUtils.delete(getBaseDir() + "/ftp");
        test(getBaseDir());
        FileUtils.delete(getBaseDir() + "/ftp");
    }

    private void test(String dir) throws Exception {
        Server server = FtpServer.createFtpServer(
                "-ftpDir", dir, "-ftpPort", "8121").start();
        FtpServer ftp = (FtpServer) server.getService();
        ftp.setEventListener(this);
        FtpClient client = FtpClient.open("localhost:8121");
        client.login("sa", "sa");
        client.makeDirectory("ftp");
        client.changeWorkingDirectory("ftp");
        assertEquals("CWD", lastEvent.getCommand());
        client.makeDirectory("hello");
        client.changeWorkingDirectory("hello");
        client.changeDirectoryUp();
        assertEquals("CDUP", lastEvent.getCommand());
        client.nameList("hello");
        client.removeDirectory("hello");
        client.close();
        server.stop();
    }

    @Override
    public void beforeCommand(FtpEvent event) {
        lastEvent = event;
    }

    @Override
    public void afterCommand(FtpEvent event) {
        lastEvent = event;
    }

    @Override
    public void onUnsupportedCommand(FtpEvent event) {
        lastEvent = event;
    }

}
