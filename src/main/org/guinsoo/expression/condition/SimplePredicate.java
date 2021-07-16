/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.condition;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.expression.ValueExpression;
import org.guinsoo.table.ColumnResolver;
import org.guinsoo.table.TableFilter;

/**
 * Base class for simple predicates.
 */
public abstract class SimplePredicate extends Condition {

    /**
     * The left hand side of the expression.
     */
    Expression left;

    /**
     * Whether it is a "not" condition (e.g. "is not null").
     */
    final boolean not;

    /**
     * Where this is the when operand of the simple case.
     */
    final boolean whenOperand;

    SimplePredicate(Expression left, boolean not, boolean whenOperand) {
        this.left = left;
        this.not = not;
        this.whenOperand = whenOperand;
    }

    @Override
    public Expression optimize(SessionLocal session) {
        left = left.optimize(session);
        if (!whenOperand && left.isConstant()) {
            return ValueExpression.getBoolean(getValue(session));
        }
        return this;
    }

    @Override
    public void setEvaluatable(TableFilter tableFilter, boolean b) {
        left.setEvaluatable(tableFilter, b);
    }

    @Override
    public final boolean needParentheses() {
        return true;
    }

    @Override
    public void updateAggregate(SessionLocal session, int stage) {
        left.updateAggregate(session, stage);
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        left.mapColumns(resolver, level, state);
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        return left.isEverything(visitor);
    }

    @Override
    public int getCost() {
        return left.getCost() + 1;
    }

    @Override
    public int getSubexpressionCount() {
        return 1;
    }

    @Override
    public Expression getSubexpression(int index) {
        if (index == 0) {
            return left;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public final boolean isWhenConditionOperand() {
        return whenOperand;
    }

}
