/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.table.ColumnResolver;
import org.gunsioo.table.TableFilter;

/**
 * Operation without subexpressions.
 */
public abstract class Operation0 extends Expression {

    protected Operation0() {
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        // Nothing to do
    }

    @Override
    public Expression optimize(SessionLocal session) {
        return this;
    }

    @Override
    public void setEvaluatable(TableFilter tableFilter, boolean value) {
        // Nothing to do
    }

    @Override
    public void updateAggregate(SessionLocal session, int stage) {
        // Nothing to do
    }

}
