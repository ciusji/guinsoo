/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.java.lang;

/**
 * A java.lang.Object implementation.
 */
public class Object {

    @Override
    public int hashCode() {
        return 0;
    }

    public boolean equals(Object other) {
        return other == this;
    }

    @Override
    public java.lang.String toString() {
        return "?";
    }

}
