/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.table.ColumnResolver;
import org.guinsoo.table.TableFilter;
import org.guinsoo.value.TypeInfo;

/**
 * Function with one optional argument.
 */
public abstract class Function0_1 extends Expression implements NamedExpression {

    /**
     * The argument of the operation.
     */
    protected Expression arg;

    /**
     * The type of the result.
     */
    protected TypeInfo type;

    protected Function0_1(Expression arg) {
        this.arg = arg;
    }

    @Override
    public TypeInfo getType() {
        return type;
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        if (arg != null) {
            arg.mapColumns(resolver, level, state);
        }
    }

    @Override
    public void setEvaluatable(TableFilter tableFilter, boolean value) {
        if (arg != null) {
            arg.setEvaluatable(tableFilter, value);
        }
    }

    @Override
    public void updateAggregate(SessionLocal session, int stage) {
        if (arg != null) {
            arg.updateAggregate(session, stage);
        }
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        return arg == null || arg.isEverything(visitor);
    }

    @Override
    public int getCost() {
        int cost = 1;
        if (arg != null) {
            cost += arg.getCost();
        }
        return cost;
    }

    @Override
    public int getSubexpressionCount() {
        return arg != null ? 1 : 0;
    }

    @Override
    public Expression getSubexpression(int index) {
        if (index == 0 && arg != null) {
            return arg;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        builder.append(getName()).append('(');
        if (arg != null) {
            arg.getUnenclosedSQL(builder, sqlFlags);
        }
        return builder.append(')');
    }

}
