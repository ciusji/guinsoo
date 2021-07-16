/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.poweroff;

import java.io.InputStream;
import java.util.Random;
import org.guinsoo.store.fs.FileUtils;
import org.guinsoo.test.TestBase;
import org.guinsoo.test.synth.OutputCatcher;

/**
 * Run the TestRecover test case in a loop. The process is killed after 10
 * seconds.
 */
public class TestRecoverKillLoop extends TestBase {

    /**
     * This method is called when executing this application from the command
     * line.
     *
     * @param args the command line parameters
     */
    public static void main(String... args) throws Exception {
        new TestRecoverKillLoop().runTest(Integer.MAX_VALUE);
    }

    @Override
    public void test() throws Exception {
        runTest(3);
    }

    private void runTest(int count) throws Exception {
        FileUtils.deleteRecursive("data/db", false);
        Random random = new Random(1);
        for (int i = 0; i < count; i++) {
            String[] procDef = {
                    getJVM(), "-cp", getClassPath(),
                    "-Dtest.dir=data/db",
                    TestRecover.class.getName()
            };
            Process p = Runtime.getRuntime().exec(procDef);
            InputStream in = p.getInputStream();
            OutputCatcher catcher = new OutputCatcher(in);
            catcher.start();
            while (true) {
                String s = catcher.readLine(60 * 1000);
                // System.out.println("> " + s);
                if (s == null) {
                    fail("No reply from process");
                } else if (s.startsWith("testing...")) {
                    int sleep = random.nextInt(10000);
                    Thread.sleep(sleep);
                    printTime("killing");
                    p.destroy();
                    p.waitFor();
                    break;
                } else if (s.startsWith("error!")) {
                    fail("Failed: " + s);
                }
            }
        }
    }

}
