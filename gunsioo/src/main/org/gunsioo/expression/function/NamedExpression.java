/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

/**
 * A function-like expression with a name.
 */
public interface NamedExpression {

    /**
     * Get the name.
     *
     * @return the name in uppercase
     */
    String getName();

}
