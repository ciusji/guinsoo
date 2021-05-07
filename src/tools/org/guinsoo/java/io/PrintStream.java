/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.java.io;

/**
 * A print stream.
 */
public class PrintStream {

    /**
     * Print the given string.
     *
     * @param s the string
     */
    @SuppressWarnings("unused")
    public void println(String s) {
        // c: int x = s->chars->length();
        // c: printf("%.*S\n", x, s->chars->getPointer());
    }

}
