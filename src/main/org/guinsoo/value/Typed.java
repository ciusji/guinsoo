/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.value;

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
