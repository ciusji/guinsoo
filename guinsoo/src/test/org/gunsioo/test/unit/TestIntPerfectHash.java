/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.unit;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.gunsioo.dev.hash.IntPerfectHash;
import org.gunsioo.dev.hash.IntPerfectHash.BitArray;
import org.gunsioo.test.TestBase;

/**
 * Tests the perfect hash tool.
 */
public class TestIntPerfectHash extends TestBase {

    /**
     * Run just this test.
     *
     * @param a ignored
     */
    public static void main(String... a) throws Exception {
        TestIntPerfectHash test = (TestIntPerfectHash) TestBase.createCaller().init();
        test.measure();
        test.test();
        test.measure();
    }

    /**
     * Measure the hash functions.
     */
    public void measure() {
        int size = 10000;
        test(size / 10);
        int s;
        long time = System.nanoTime();
        s = test(size);
        time = System.nanoTime() - time;
        System.out.println((double) s / size + " bits/key in " +
                TimeUnit.NANOSECONDS.toMillis(time) + " ms");

    }

    @Override
    public void test() {
        testBitArray();
        for (int i = 0; i < 100; i++) {
            test(i);
        }
        for (int i = 100; i <= 10000; i *= 10) {
            test(i);
        }
    }

    private void testBitArray() {
        byte[] data = new byte[0];
        BitSet set = new BitSet();
        for (int i = 100; i >= 0; i--) {
            data = BitArray.setBit(data, i, true);
            set.set(i);
        }
        Random r = new Random(1);
        for (int i = 0; i < 10000; i++) {
            int pos = r.nextInt(100);
            boolean s = r.nextBoolean();
            data = BitArray.setBit(data, pos, s);
            set.set(pos, s);
            pos = r.nextInt(100);
            assertTrue(BitArray.getBit(data, pos) == set.get(pos));
        }
        assertTrue(BitArray.countBits(data) == set.cardinality());
    }

    private int test(int size) {
        Random r = new Random(size);
        HashSet<Integer> set = new HashSet<>();
        while (set.size() < size) {
            set.add(r.nextInt());
        }
        ArrayList<Integer> list = new ArrayList<>(set);
        byte[] desc = IntPerfectHash.generate(list);
        int max = test(desc, set);
        assertEquals(size - 1, max);
        return desc.length * 8;
    }

    private int test(byte[] desc, Set<Integer> set) {
        int max = -1;
        HashSet<Integer> test = new HashSet<>();
        IntPerfectHash hash = new IntPerfectHash(desc);
        for (int x : set) {
            int h = hash.get(x);
            assertTrue(h >= 0);
            assertTrue(h <= set.size() * 3);
            max = Math.max(max, h);
            assertFalse(test.contains(h));
            test.add(h);
        }
        return max;
    }

}
