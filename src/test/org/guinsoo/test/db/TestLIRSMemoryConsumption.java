/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.db;

import java.util.Random;
import org.guinsoo.mvstore.cache.CacheLongKeyLIRS;
import org.guinsoo.test.TestBase;
import org.guinsoo.test.TestDb;
import org.guinsoo.util.Utils;

/**
 * Class TestLIRSMemoryConsumption.
 * <UL>
 * <LI> 8/5/18 10:57 PM initial creation
 * </UL>
 *
 * @author <a href='mailto:andrei.tokar@gmail.com'>Andrei Tokar</a>
 */
public class TestLIRSMemoryConsumption extends TestDb {

    /**
     * Run just this test.
     *
     * @param a
     *              ignored
     */
    public static void main(String... a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }

    @Override
    public void test() {
        testMemoryConsumption();
        System.out.println("-----------------------");
        testMemoryConsumption();
        System.out.println("-----------------------");
        testMemoryConsumption();
    }

    private static void testMemoryConsumption() {
        int size = 1_000_000;
        Random rng = new Random();
        CacheLongKeyLIRS.Config config = new CacheLongKeyLIRS.Config();
        for (int mb = 1; mb <= 16; mb *= 2) {
            config.maxMemory = mb * 1024 * 1024;
            CacheLongKeyLIRS<Object> cache = new CacheLongKeyLIRS<>(config);
            long memoryUsedInitial = Utils.getMemoryUsed();
            for (int i = 0; i < size; i++) {
                cache.put(i, createValue(i), getValueSize(i));
            }
            for (int i = 0; i < size; i++) {
                int key;
                int mode = rng.nextInt(4);
                switch(mode) {
                    default:
                    case 0:
                        key = rng.nextInt(10);
                        break;
                    case 1:
                        key = rng.nextInt(100);
                        break;
                    case 2:
                        key = rng.nextInt(10_000);
                        break;
                    case 3:
                        key = rng.nextInt(1_000_000);
                        break;
                }
                Object val = cache.get(key);
                if (val == null) {
                    cache.put(key, createValue(key), getValueSize(key));
                }
            }
            Utils.collectGarbage();
            cache.trimNonResidentQueue();
            long memoryUsed = Utils.getMemoryUsed();

            int sizeHot = cache.sizeHot();
            int sizeResident = cache.size();
            int sizeNonResident = cache.sizeNonResident();
            long hits = cache.getHits();
            long misses = cache.getMisses();
            System.out.println(mb + " | " +
                    (memoryUsed - memoryUsedInitial + 512) / 1024 + " | " +
                    (sizeResident+sizeNonResident) + " | " +
                    sizeHot + " | " + (sizeResident - sizeHot) + " | " + sizeNonResident +
                    " | " + (hits * 100 / (hits + misses)) );
        }
    }

    private static Object createValue(long key) {
//        return new Object();
        return new byte[2540];
    }

    private static int getValueSize(long key) {
//        return 16;
        return 2560;
    }
}
