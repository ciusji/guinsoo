/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.value;

/**
 * An object with data type.
 */
public interface Typed {

    /**
     * Returns the data type.
     *
     * @return the data type
     */
    TypeInfo getType();

}
