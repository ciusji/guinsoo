/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.java.lang;

/**
 * A java.lang.String implementation.
 */
public class StringBuilder {

    private int length;
    private char[] buffer;

    public StringBuilder(String s) {
        char[] chars = s.chars;
        int len = chars.length;
        buffer = new char[len];
        System.arraycopy(chars, 0, buffer, 0, len);
        this.length = len;
    }

    public StringBuilder() {
        buffer = new char[10];
    }

    /**
     * Append the given value.
     *
     * @param x the value
     * @return this
     */
    public StringBuilder append(String x) {
        int l = x.length();
        ensureCapacity(l);
        System.arraycopy(x.chars, 0, buffer, length, l);
        length += l;
        return this;
    }

    /**
     * Append the given value.
     *
     * @param x the value
     * @return this
     */
    public StringBuilder append(int x) {
        append(Integer.toString(x));
        return this;
    }

    @Override
    public java.lang.String toString() {
        return new java.lang.String(buffer, 0, length);
    }

    private void ensureCapacity(int plus) {
        if (buffer.length < length + plus) {
            char[] b = new char[Math.max(length + plus, buffer.length * 2)];
            System.arraycopy(buffer, 0, b, 0, length);
            buffer = b;
        }
    }

}
