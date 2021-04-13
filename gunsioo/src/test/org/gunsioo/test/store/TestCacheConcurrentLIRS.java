/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.store;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.gunsioo.mvstore.cache.CacheLongKeyLIRS;
import org.gunsioo.test.TestBase;
import org.gunsioo.util.Task;

/**
 * Tests the cache algorithm.
 */
public class TestCacheConcurrentLIRS extends TestBase {

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }

    @Override
    public void test() throws Exception {
        testConcurrent();
    }

    private void testConcurrent() {
        CacheLongKeyLIRS.Config cc = new CacheLongKeyLIRS.Config();
        cc.maxMemory = 100;
        final CacheLongKeyLIRS<Integer> test = new CacheLongKeyLIRS<>(cc);
        int threadCount = 8;
        final CountDownLatch wait = new CountDownLatch(1);
        final AtomicBoolean stopped = new AtomicBoolean();
        Task[] tasks = new Task[threadCount];
        final int[] getCounts = new int[threadCount];
        final int offset = 1000000;
        for (int i = 0; i < 100; i++) {
            test.put(offset + i, i);
        }
        final int[] keys = new int[1000];
        Random random = new Random(1);
        for (int i = 0; i < keys.length; i++) {
            int key;
            do {
                key = (int) Math.abs(random.nextGaussian() * 50);
            } while (key > 100);
            keys[i] = key;
        }
        for (int i = 0; i < threadCount; i++) {
            final int x = i;
            Task t = new Task() {
                @Override
                public void call() throws Exception {
                    Random random = new Random(x);
                    wait.await();
                    int i = 0;
                    for (; !stopped.get(); i++) {
                        int key = keys[random.nextInt(keys.length)];
                        test.get(offset + key);
                        if ((i & 127) == 0) {
                            test.put(offset + random.nextInt(100), random.nextInt());
                        }
                    }
                    getCounts[x] = i;
                }
            };
            t.execute("t" + i);
            tasks[i] = t;
        }
        wait.countDown();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopped.set(true);
        for (Task t : tasks) {
            t.get();
        }
        int totalCount = 0;
        for (int x : getCounts) {
            totalCount += x;
        }
        trace("requests: " + totalCount);
    }

}
