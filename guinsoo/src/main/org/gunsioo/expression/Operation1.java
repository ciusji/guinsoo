/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.table.ColumnResolver;
import org.gunsioo.table.TableFilter;
import org.gunsioo.value.TypeInfo;

/**
 * Operation with one argument.
 */
public abstract class Operation1 extends Expression {

    /**
     * The argument of the operation.
     */
    protected Expression arg;

    /**
     * The type of the result.
     */
    protected TypeInfo type;

    protected Operation1(Expression arg) {
        this.arg = arg;
    }

    @Override
    public TypeInfo getType() {
        return type;
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        arg.mapColumns(resolver, level, state);
    }

    @Override
    public void setEvaluatable(TableFilter tableFilter, boolean value) {
        arg.setEvaluatable(tableFilter, value);
    }

    @Override
    public void updateAggregate(SessionLocal session, int stage) {
        arg.updateAggregate(session, stage);
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        return arg.isEverything(visitor);
    }

    @Override
    public int getCost() {
        return arg.getCost() + 1;
    }

    @Override
    public int getSubexpressionCount() {
        return 1;
    }

    @Override
    public Expression getSubexpression(int index) {
        if (index == 0) {
            return arg;
        }
        throw new IndexOutOfBoundsException();
    }

}
