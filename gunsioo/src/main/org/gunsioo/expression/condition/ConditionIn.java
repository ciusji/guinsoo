/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.condition;

import java.util.ArrayList;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.ExpressionColumn;
import org.gunsioo.expression.ExpressionVisitor;
import org.gunsioo.expression.Parameter;
import org.gunsioo.expression.TypedValueExpression;
import org.gunsioo.expression.ValueExpression;
import org.gunsioo.index.IndexCondition;
import org.gunsioo.table.ColumnResolver;
import org.gunsioo.table.TableFilter;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueBoolean;
import org.gunsioo.value.ValueNull;

/**
 * An 'in' condition with a list of values, as in WHERE NAME IN(...)
 */
public final class ConditionIn extends Condition {

    private Expression left;
    private final boolean not;
    private final boolean whenOperand;
    private final ArrayList<Expression> valueList;

    /**
     * Create a new IN(..) condition.
     *
     * @param left the expression before IN
     * @param not whether the result should be negated
     * @param whenOperand whether this is a when operand
     * @param values the value list (at least one element)
     */
    public ConditionIn(Expression left, boolean not, boolean whenOperand, ArrayList<Expression> values) {
        this.left = left;
        this.not = not;
        this.whenOperand = whenOperand;
        this.valueList = values;
    }

    @Override
    public Value getValue(SessionLocal session) {
        return getValue(session, left.getValue(session));
    }

    @Override
    public boolean getWhenValue(SessionLocal session, Value left) {
        if (!whenOperand) {
            return super.getWhenValue(session, left);
        }
        return getValue(session, left).getBoolean();
    }

    private Value getValue(SessionLocal session, Value left) {
        if (left.containsNull()) {
            return ValueNull.INSTANCE;
        }
        boolean hasNull = false;
        for (Expression e : valueList) {
            Value r = e.getValue(session);
            Value cmp = Comparison.compare(session, left, r, Comparison.EQUAL);
            if (cmp == ValueNull.INSTANCE) {
                hasNull = true;
            } else if (cmp == ValueBoolean.TRUE) {
                return ValueBoolean.get(!not);
            }
        }
        if (hasNull) {
            return ValueNull.INSTANCE;
        }
        return ValueBoolean.get(not);
    }

    @Override
    public boolean isWhenConditionOperand() {
        return whenOperand;
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        left.mapColumns(resolver, level, state);
        for (Expression e : valueList) {
            e.mapColumns(resolver, level, state);
        }
    }

    @Override
    public Expression optimize(SessionLocal session) {
        left = left.optimize(session);
        boolean constant = !whenOperand && left.isConstant();
        if (constant && left.isNullConstant()) {
            return TypedValueExpression.UNKNOWN;
        }
        boolean allValuesConstant = true;
        boolean allValuesNull = true;
        TypeInfo leftType = left.getType();
        for (int i = 0, l = valueList.size(); i < l; i++) {
            Expression e = valueList.get(i);
            e = e.optimize(session);
            TypeInfo.checkComparable(leftType, e.getType());
            if (e.isConstant() && !e.getValue(session).containsNull()) {
                allValuesNull = false;
            }
            if (allValuesConstant && !e.isConstant()) {
                allValuesConstant = false;
            }
            if (left instanceof ExpressionColumn && e instanceof Parameter) {
                ((Parameter) e).setColumn(((ExpressionColumn) left).getColumn());
            }
            valueList.set(i, e);
        }
        return optimize2(session, constant, allValuesConstant, allValuesNull, valueList);
    }

    private Expression optimize2(SessionLocal session, boolean constant, boolean allValuesConstant,
            boolean allValuesNull, ArrayList<Expression> values) {
        if (constant && allValuesConstant) {
            return ValueExpression.getBoolean(getValue(session));
        }
        if (values.size() == 1) {
            return new Comparison(not ? Comparison.NOT_EQUAL : Comparison.EQUAL, left, values.get(0), whenOperand)
                    .optimize(session);
        }
        if (allValuesConstant && !allValuesNull) {
            int leftType = left.getType().getValueType();
            if (leftType == Value.UNKNOWN) {
                return this;
            }
            if (leftType == Value.ENUM && !(left instanceof ExpressionColumn)) {
                return this;
            }
            return new ConditionInConstantSet(session, left, not, whenOperand, values).optimize(session);
        }
        return this;
    }

    @Override
    public Expression getNotIfPossible(SessionLocal session) {
        if (whenOperand) {
            return null;
        }
        return new ConditionIn(left, !not, false, valueList);
    }

    @Override
    public void createIndexConditions(SessionLocal session, TableFilter filter) {
        if (not || whenOperand || !(left instanceof ExpressionColumn)) {
            return;
        }
        ExpressionColumn l = (ExpressionColumn) left;
        if (filter != l.getTableFilter()) {
            return;
        }
        if (session.getDatabase().getSettings().optimizeInList) {
            ExpressionVisitor visitor = ExpressionVisitor.getNotFromResolverVisitor(filter);
            TypeInfo colType = l.getType();
            for (Expression e : valueList) {
                if (!e.isEverything(visitor)
                        || !TypeInfo.haveSameOrdering(colType, TypeInfo.getHigherType(colType, e.getType()))) {
                    return;
                }
            }
            filter.addIndexCondition(IndexCondition.getInList(l, valueList));
        }
    }

    @Override
    public void setEvaluatable(TableFilter tableFilter, boolean b) {
        left.setEvaluatable(tableFilter, b);
        for (Expression e : valueList) {
            e.setEvaluatable(tableFilter, b);
        }
    }

    @Override
    public boolean needParentheses() {
        return true;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return getWhenSQL(left.getSQL(builder, sqlFlags, AUTO_PARENTHESES), sqlFlags);
    }

    @Override
    public StringBuilder getWhenSQL(StringBuilder builder, int sqlFlags) {
        if (not) {
            builder.append(" NOT");
        }
        return writeExpressions(builder.append(" IN("), valueList, sqlFlags).append(')');
    }

    @Override
    public void updateAggregate(SessionLocal session, int stage) {
        left.updateAggregate(session, stage);
        for (Expression e : valueList) {
            e.updateAggregate(session, stage);
        }
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        if (!left.isEverything(visitor)) {
            return false;
        }
        return areAllValues(visitor);
    }

    private boolean areAllValues(ExpressionVisitor visitor) {
        for (Expression e : valueList) {
            if (!e.isEverything(visitor)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getCost() {
        int cost = left.getCost();
        for (Expression e : valueList) {
            cost += e.getCost();
        }
        return cost;
    }

    /**
     * Add an additional element if possible. Example: given two conditions
     * A IN(1, 2) OR A=3, the constant 3 is added: A IN(1, 2, 3).
     *
     * @param other the second condition
     * @return null if the condition was not added, or the new condition
     */
    Expression getAdditional(Comparison other) {
        if (!not && !whenOperand && left.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
            Expression add = other.getIfEquals(left);
            if (add != null) {
                ArrayList<Expression> list = new ArrayList<>(valueList.size() + 1);
                list.addAll(valueList);
                list.add(add);
                return new ConditionIn(left, false, false, list);
            }
        }
        return null;
    }

    @Override
    public int getSubexpressionCount() {
        return 1 + valueList.size();
    }

    @Override
    public Expression getSubexpression(int index) {
        if (index == 0) {
            return left;
        } else if (index > 0 && index <= valueList.size()) {
            return valueList.get(index - 1);
        }
        throw new IndexOutOfBoundsException();
    }

}
