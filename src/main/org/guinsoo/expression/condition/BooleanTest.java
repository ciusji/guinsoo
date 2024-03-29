/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.condition;

import java.util.ArrayList;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionColumn;
import org.guinsoo.expression.TypedValueExpression;
import org.guinsoo.expression.ValueExpression;
import org.guinsoo.index.IndexCondition;
import org.guinsoo.table.TableFilter;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueBoolean;
import org.guinsoo.value.ValueNull;

/**
 * Boolean test (IS [NOT] { TRUE | FALSE | UNKNOWN }).
 */
public final class BooleanTest extends SimplePredicate {

    private final Boolean right;

    public BooleanTest(Expression left, boolean not, boolean whenOperand, Boolean right) {
        super(left, not, whenOperand);
        this.right = right;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return getWhenSQL(left.getSQL(builder, sqlFlags, AUTO_PARENTHESES), sqlFlags);
    }

    @Override
    public StringBuilder getWhenSQL(StringBuilder builder, int sqlFlags) {
        return builder.append(not ? " IS NOT " : " IS ").append(right == null ? "UNKNOWN" : right ? "TRUE" : "FALSE");
    }

    @Override
    public Value getValue(SessionLocal session) {
        return ValueBoolean.get(getValue(left.getValue(session)));
    }

    @Override
    public boolean getWhenValue(SessionLocal session, Value left) {
        if (!whenOperand) {
            return super.getWhenValue(session, left);
        }
        return getValue(left);
    }

    private boolean getValue(Value left) {
        return (left == ValueNull.INSTANCE ? right == null : right != null && right == left.getBoolean()) ^ not;
    }

    @Override
    public Expression getNotIfPossible(SessionLocal session) {
        if (whenOperand) {
            return null;
        }
        return new BooleanTest(left, !not, false, right);
    }

    @Override
    public void createIndexConditions(SessionLocal session, TableFilter filter) {
        if (whenOperand || !filter.getTable().isQueryComparable()) {
            return;
        }
        if (left instanceof ExpressionColumn) {
            ExpressionColumn c = (ExpressionColumn) left;
            if (c.getType().getValueType() == Value.BOOLEAN && filter == c.getTableFilter()) {
                if (not) {
                    if (right == null && c.getColumn().isNullable()) {
                        ArrayList<Expression> list = new ArrayList<>(2);
                        list.add(ValueExpression.FALSE);
                        list.add(ValueExpression.TRUE);
                        filter.addIndexCondition(IndexCondition.getInList(c, list));
                    }
                } else {
                    filter.addIndexCondition(IndexCondition.get(Comparison.EQUAL_NULL_SAFE, c,
                            right == null ? TypedValueExpression.UNKNOWN : ValueExpression.getBoolean(right)));
                }
            }
        }
    }

}
