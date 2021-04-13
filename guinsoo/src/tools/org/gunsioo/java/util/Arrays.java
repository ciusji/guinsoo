/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.java.util;

/**
 * An simple implementation of java.util.Arrays
 */
public class Arrays {

    /**
     * Fill an array with the given value.
     *
     * @param array the array
     * @param x the value
     */
    public static void fill(char[] array, char x) {
        for (int i = 0, size = array.length; i < size; i++) {
            array[i] = x;
        }
    }

    /**
     * Fill an array with the given value.
     *
     * @param array the array
     * @param x the value
     */
    public static void fill(byte[] array, byte x) {
        for (int i = 0; i < array.length; i++) {
            array[i] = x;
        }
    }

    /**
     * Fill an array with the given value.
     *
     * @param array the array
     * @param x the value
     */
    public static void fill(int[] array, int x) {
        for (int i = 0; i < array.length; i++) {
            array[i] = x;
        }
    }


    /**
     * Fill an array with the given value.
     *
     * @param array the array
     * @param x the value
     */
    public static void fillByte(byte[] array, byte x) {
        for (int i = 0; i < array.length; i++) {
            array[i] = x;
        }
    }

    /**
     * Fill an array with the given value.
     *
     * @param array the array
     * @param x the value
     */
    public static void fillInt(int[] array, int x) {
        for (int i = 0; i < array.length; i++) {
            array[i] = x;
        }
    }

}
