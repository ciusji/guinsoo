/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

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
