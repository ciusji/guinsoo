/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.table.ColumnResolver;
import org.guinsoo.table.TableFilter;

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
