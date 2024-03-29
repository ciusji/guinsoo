/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.store;

import java.util.TreeMap;

import org.guinsoo.dev.cluster.ShardedMap;
import org.guinsoo.test.TestBase;

/**
 * Test sharded maps.
 */
public class TestShardedMap extends TestBase {

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestBase.createCaller().init().testFromMain();
    }

    @Override
    public void test() {
        testLinearSplit();
        testReplication();
        testOverlap();
    }

    private void testLinearSplit() {
        ShardedMap<Integer, Integer> map = new ShardedMap<>();
        TreeMap<Integer, Integer> a = new TreeMap<>();
        TreeMap<Integer, Integer> b = new TreeMap<>();
        map.addMap(a, null, 5);
        map.addMap(b, 5, null);
        for (int i = 0; i < 10; i++) {
            map.put(i, i * 10);
        }
        assertEquals(10, map.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(i * 10, map.get(i).intValue());
        }
        assertEquals("[0, 1, 2, 3, 4]",
                a.keySet().toString());
        assertEquals("[5, 6, 7, 8, 9]",
                b.keySet().toString());
        assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]",
                map.keySet().toString());
        assertEquals(10, map.sizeAsLong());
    }

    private void testReplication() {
        ShardedMap<Integer, Integer> map = new ShardedMap<>();
        TreeMap<Integer, Integer> a = new TreeMap<>();
        TreeMap<Integer, Integer> b = new TreeMap<>();
        map.addMap(a, null, null);
        map.addMap(b, null, null);
        for (int i = 0; i < 10; i++) {
            map.put(i, i * 10);
        }
        assertEquals(10, map.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(i * 10, map.get(i).intValue());
        }
        assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]",
                a.keySet().toString());
        assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]",
                b.keySet().toString());
        assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]",
                map.keySet().toString());
        assertEquals(10, map.sizeAsLong());
    }

    private void testOverlap() {
        ShardedMap<Integer, Integer> map = new ShardedMap<>();
        TreeMap<Integer, Integer> a = new TreeMap<>();
        TreeMap<Integer, Integer> b = new TreeMap<>();
        map.addMap(a, null, 10);
        map.addMap(b, 5, null);
        for (int i = 0; i < 20; i++) {
            map.put(i, i * 10);
        }
        // overlap: size is unknown
        assertEquals(-1, map.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i * 10, map.get(i).intValue());
        }
        assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]",
                a.keySet().toString());
        assertEquals("[5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]",
                b.keySet().toString());
        assertEquals(-1, map.sizeAsLong());
    }

}