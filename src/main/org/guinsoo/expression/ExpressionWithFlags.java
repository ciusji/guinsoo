/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

/**
 * Expression with flags.
 */
public interface ExpressionWithFlags {

    /**
     * Set the flags for this expression.
     *
     * @param flags
     *            the flags to set
     */
    void setFlags(int flags);

    /**
     * Returns the flags.
     *
     * @return the flags
     */
    int getFlags();

}
