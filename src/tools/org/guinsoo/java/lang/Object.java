/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.java.lang;

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
