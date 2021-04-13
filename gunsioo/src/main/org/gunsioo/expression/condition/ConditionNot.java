/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.condition;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.ExpressionVisitor;
import org.gunsioo.expression.TypedValueExpression;
import org.gunsioo.expression.ValueExpression;
import org.gunsioo.table.ColumnResolver;
import org.gunsioo.table.TableFilter;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueNull;

/**
 * A NOT condition.
 */
public class ConditionNot extends Condition {

    private Expression condition;

    public ConditionNot(Expression condition) {
        this.condition = condition;
    }

    @Override
    public Expression getNotIfPossible(SessionLocal session) {
        return castToBoolean(session, condition.optimize(session));
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value v = condition.getValue(session);
        if (v == ValueNull.INSTANCE) {
            return v;
        }
        return v.convertToBoolean().negate();
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        condition.mapColumns(resolver, level, state);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        Expression e2 = condition.getNotIfPossible(session);
        if (e2 != null) {
            return e2.optimize(session);
        }
        Expression expr = condition.optimize(session);
        if (expr.isConstant()) {
            Value v = expr.getValue(session);
            if (v == ValueNull.INSTANCE) {
                return TypedValueExpression.UNKNOWN;
            }
            return ValueExpression.getBoolean(!v.getBoolean());
        }
        condition = expr;
        return this;
    }

    @Override
    public void setEvaluatable(TableFilter tableFilter, boolean b) {
        condition.setEvaluatable(tableFilter, b);
    }

    @Override
    public boolean needParentheses() {
        return true;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return condition.getSQL(builder.append("NOT "), sqlFlags, AUTO_PARENTHESES);
    }

    @Override
    public void updateAggregate(SessionLocal session, int stage) {
        condition.updateAggregate(session, stage);
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        return condition.isEverything(visitor);
    }

    @Override
    public int getCost() {
        return condition.getCost();
    }

    @Override
    public int getSubexpressionCount() {
        return 1;
    }

    @Override
    public Expression getSubexpression(int index) {
        if (index == 0) {
            return condition;
        }
        throw new IndexOutOfBoundsException();
    }

}
