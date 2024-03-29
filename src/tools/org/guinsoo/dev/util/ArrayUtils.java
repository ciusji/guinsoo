/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.dev.util;

import java.util.Comparator;

/**
 * Array utility methods.
 */
public class ArrayUtils {

    /**
     * Sort an array using binary insertion sort
     *
     * @param <T> the type
     * @param d the data
     * @param left the index of the leftmost element
     * @param right the index of the rightmost element
     * @param comp the comparison class
     */
    public static <T> void binaryInsertionSort(T[] d, int left, int right,
            Comparator<T> comp) {
        for (int i = left + 1; i <= right; i++) {
            T t = d[i];
            int l = left;
            for (int r = i; l < r;) {
                int m = (l + r) >>> 1;
                if (comp.compare(t, d[m]) >= 0) {
                    l = m + 1;
                } else {
                    r = m;
                }
            }
            for (int n = i - l; n > 0;) {
                d[l + n--] = d[l + n];
            }
            d[l] = t;
        }
    }

    /**
     * Sort an array using insertion sort
     *
     * @param <T> the type
     * @param d the data
     * @param left the index of the leftmost element
     * @param right the index of the rightmost element
     * @param comp the comparison class
     */
    public static <T> void insertionSort(T[] d, int left, int right,
            Comparator<T> comp) {
        for (int i = left + 1, j; i <= right; i++) {
            T t = d[i];
            for (j = i - 1; j >= left && comp.compare(d[j], t) > 0; j--) {
                d[j + 1] = d[j];
            }
            d[j + 1] = t;
        }
    }


}
