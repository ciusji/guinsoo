/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.unit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import org.guinsoo.Driver;
import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;
import org.guinsoo.test.utils.SelfDestructor;

/**
 * Tests database file locking.
 * A new process is started.
 */
public class TestFileLockProcess extends TestDb {

    /**
     * This method is called when executing this application from the command
     * line.
     *
     * @param args the command line parameters
     */
    public static void main(String... args) throws Exception {
        SelfDestructor.startCountdown(60);
        if (args.length == 0) {
            TestBase.createCaller().init().testFromMain();
            return;
        }
        String url = args[0];
        execute(url);
    }

    private static void execute(String url) {
        Driver.load();
        try {
            Class.forName("org.guinsoo.Driver");
            Connection conn = DriverManager.getConnection(url);
            System.out.println("!");
            conn.close();
        } catch (Exception e) {
            // failed - expected
        }
    }

    @Override
    public boolean isEnabled() {
        if (config.codeCoverage || config.networked) {
            return false;
        }
        if (getBaseDir().indexOf(':') > 0) {
            return false;
        }
        return true;
    }

    @Override
    public void test() throws Exception {
        deleteDb("lock");
        String url = "jdbc:guinsoo:"+getBaseDir()+"/lock";

        println("socket");
        test(4, url + ";file_lock=socket");

        println("fs");
        test(4, url + ";file_lock=fs");

        println("default");
        test(50, url);

        deleteDb("lock");
    }

    private void test(int count, String url) throws Exception {
        url = getURL(url, true);
        Connection conn = getConnection(url);
        String selfDestruct = SelfDestructor.getPropertyString(60);
        String[] procDef = { getJVM(), selfDestruct,
                "-cp", getClassPath(),
                getClass().getName(), url };
        ArrayList<Process> processes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Thread.sleep(100);
            if (i % 10 == 0) {
                println(i + "/" + count);
            }
            Process proc = Runtime.getRuntime().exec(procDef);
            processes.add(proc);
        }
        for (int i = 0; i < count; i++) {
            Process proc = processes.get(i);
            StringBuilder buff = new StringBuilder();
            while (true) {
                int ch = proc.getErrorStream().read();
                if (ch < 0) {
                    break;
                }
                System.out.print((char) ch);
                buff.append((char) ch);
            }
            while (true) {
                int ch = proc.getInputStream().read();
                if (ch < 0) {
                    break;
                }
                System.out.print((char) ch);
                buff.append((char) ch);
            }
            proc.waitFor();

            // The travis build somehow generates messages like this from javac.
            // No idea where it is coming from.
            String processOutput = buff.toString();
            processOutput = processOutput.replaceAll("Picked up _JAVA_OPTIONS: -Xmx2048m -Xms512m", "").trim();

            assertEquals(0, proc.exitValue());
            assertTrue(i + ": " + buff.toString(), processOutput.isEmpty());
        }
        Thread.sleep(100);
        conn.close();
    }

}
