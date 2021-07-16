/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import org.guinsoo.message.DbException;

/**
 * An expression with variable number of parameters.
 */
public interface ExpressionWithVariableParameters {

    /**
     * Adds the parameter expression.
     *
     * @param param
     *            the expression
     */
    void addParameter(Expression param);

    /**
     * This method must be called after all the parameters have been set. It
     * checks if the parameter count is correct when required by the
     * implementation.
     *
     * @throws DbException
     *             if the parameter count is incorrect.
     */
    void doneWithParameters() throws DbException;

}
