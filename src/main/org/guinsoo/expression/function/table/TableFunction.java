/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function.table;

import java.util.Arrays;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionWithVariableParameters;
import org.guinsoo.message.DbException;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.table.ColumnResolver;
import org.guinsoo.util.HasSQL;
import org.guinsoo.expression.function.NamedExpression;

/**
 * A table value function.
 */
public abstract class TableFunction implements HasSQL, NamedExpression, ExpressionWithVariableParameters {

    protected Expression[] args;

    private int argsCount;

    protected TableFunction(Expression[] args) {
        this.args = args;
    }

    @Override
    public void addParameter(Expression param) {
        int capacity = args.length;
        if (argsCount >= capacity) {
            args = Arrays.copyOf(args, capacity * 2);
        }
        args[argsCount++] = param;
    }

    @Override
    public void doneWithParameters() throws DbException {
        if (args.length != argsCount) {
            args = Arrays.copyOf(args, argsCount);
        }
    }

    /**
     * Get a result with.
     *
     * @param session
     *            the session
     * @return the result
     */
    public abstract ResultInterface getValue(SessionLocal session);

    /**
     * Get an empty result with the column names set.
     *
     * @param session
     *            the session
     * @return the empty result
     */
    public abstract ResultInterface getValueTemplate(SessionLocal session);

    /**
     * Map the columns of the resolver to expression columns.
     *
     * @param resolver
     *            the column resolver
     * @param level
     *            the subquery nesting level
     * @param state
     *            current state for nesting checks, initial value is
     *            {@link Expression#MAP_INITIAL}
     */
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        for (Expression arg : args) {
            arg.mapColumns(resolver, level, state);
        }
    }

    /**
     * Try to optimize this table function
     *
     * @param session
     *            the session
     */
    public void optimize(SessionLocal session) {
        for (int i = 0, l = args.length; i < l; i++) {
            args[i] = args[i].optimize(session);
        }
    }

    /**
     * Whether the function always returns the same result for the same
     * parameters.
     *
     * @return true if it does
     */
    public abstract boolean isDeterministic();

    @Override
    public StringBuilder getSQL(StringBuilder builder, int sqlFlags) {
        return Expression.writeExpressions(builder.append(getName()).append('('), args, sqlFlags).append(')');
    }

}
