/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.java.io;

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
