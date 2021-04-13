/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.store;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.gunsioo.mvstore.MVMap;
import org.gunsioo.mvstore.MVStore;
import org.gunsioo.store.fs.FileUtils;
import org.gunsioo.test.TestBase;
import org.gunsioo.util.Task;

/**
 * Tests the MVStoreUsage cache.
 */
public class TestMVStoreCachePerformance extends TestBase {

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestBase test = TestBase.createCaller().init();
        test.test();
    }

    @Override
    public void test() throws Exception {
        testCache(1, "");
        testCache(1, "cache:");
        testCache(10, "");
        testCache(10, "cache:");
        testCache(100, "");
        testCache(100, "cache:");
    }

    private void testCache(int threadCount, String fileNamePrefix) {
        String fileName = getBaseDir() + "/" + getTestName();
        fileName = fileNamePrefix  + fileName;
        FileUtils.delete(fileName);
        MVStore store = new MVStore.Builder().
                fileName(fileName).
                // cacheSize(1024).
                open();
        final MVMap<Integer, byte[]> map = store.openMap("test");
        final AtomicInteger counter = new AtomicInteger();
        byte[] data = new byte[8 * 1024];
        final int count = 10000;
        for (int i = 0; i < count; i++) {
            map.put(i, data);
            store.commit();
            if (i % 1000 == 0) {
                // System.out.println("add " + i);
            }
        }
        Task[] tasks = new Task[threadCount];
        for (int i = 0; i < threadCount; i++) {
            tasks[i] = new Task() {

                @Override
                public void call() throws Exception {
                    Random r = new Random();
                    do {
                        int id = r.nextInt(count);
                        map.get(id);
                        counter.incrementAndGet();
                    } while (!stop);
                }

            };
            tasks[i].execute();
        }
        for (int i = 0; i < 4; i++) {
            // Profiler prof = new Profiler().startCollecting();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // ignore
            }
            // System.out.println(prof.getTop(5));
            // System.out.println("  " + counter.get() / (i + 1) + " op/s");
        }
        // long time = System.nanoTime();
        for (Task t : tasks) {
            t.get();
        }
        store.close();
        System.out.println(counter.get() / 10000 + " ops/ms; " +
                threadCount + " thread(s); " + fileNamePrefix);
    }

}
