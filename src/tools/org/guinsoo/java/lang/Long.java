/*
/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.java.lang;

/**
 * A java.lang.Long implementation.
 */
public class Long {

    /**
     * The smallest possible value.
     */
    public static final long MIN_VALUE = 1L << 63;

    /**
     * The largest possible value.
     */
    public static final long MAX_VALUE = (1L << 63) - 1;

    /**
     * Convert a value to a String.
     *
     * @param x the value
     * @return the String
     */
    public static String toString(long x) {
        // c: wchar_t ch[30];
        // c: swprintf(ch, 30, L"%" PRId64, x);
        // c: return STRING(ch);
        // c: return;
        if (x == MIN_VALUE) {
            return String.wrap("-9223372036854775808");
        }
        char[] ch = new char[30];
        int i = 30 - 1, count = 0;
        boolean negative;
        if (x < 0) {
            negative = true;
            x = -x;
        } else {
            negative = false;
        }
        for (; i >= 0; i--) {
            ch[i] = (char) ('0' + (x % 10));
            x /= 10;
            count++;
            if (x == 0) {
                break;
            }
        }
        if (negative) {
            ch[--i] = '-';
            count++;
        }
        return new String(ch, i, count);
    }

}
