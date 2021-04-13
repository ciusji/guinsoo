/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.value;

import org.gunsioo.util.HasSQL;

/**
 * Extended parameters of a data type.
 */
public abstract class ExtTypeInfo implements HasSQL {

    @Override
    public String toString() {
        return getSQL(QUOTE_ONLY_WHEN_REQUIRED);
    }

}
