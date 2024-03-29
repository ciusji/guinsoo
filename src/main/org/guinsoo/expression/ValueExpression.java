/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.condition.Comparison;
import org.guinsoo.index.IndexCondition;
import org.guinsoo.message.DbException;
import org.guinsoo.table.TableFilter;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueBoolean;
import org.guinsoo.value.ValueNull;

/**
 * An expression representing a constant value.
 */
public class ValueExpression extends Operation0 {

    /**
     * The expression represents ValueNull.INSTANCE.
     */
    public static final ValueExpression NULL = new ValueExpression(ValueNull.INSTANCE);

    /**
     * This special expression represents the default value. It is used for
     * UPDATE statements of the form SET COLUMN = DEFAULT. The value is
     * ValueNull.INSTANCE, but should never be accessed.
     */
    public static final ValueExpression DEFAULT = new ValueExpression(ValueNull.INSTANCE);

    /**
     * The expression represents ValueBoolean.TRUE.
     */
    public static final ValueExpression TRUE = new ValueExpression(ValueBoolean.TRUE);

    /**
     * The expression represents ValueBoolean.FALSE.
     */
    public static final ValueExpression FALSE = new ValueExpression(ValueBoolean.FALSE);

    /**
     * The value.
     */
    final Value value;

    ValueExpression(Value value) {
        this.value = value;
    }

    /**
     * Create a new expression with the given value.
     *
     * @param value the value
     * @return the expression
     */
    public static ValueExpression get(Value value) {
        if (value == ValueNull.INSTANCE) {
            return NULL;
        }
        if (value.getValueType() == Value.BOOLEAN) {
            return getBoolean(value.getBoolean());
        }
        return new ValueExpression(value);
    }

    /**
     * Create a new expression with the given boolean value.
     *
     * @param value the boolean value
     * @return the expression
     */
    public static ValueExpression getBoolean(Value value) {
        if (value == ValueNull.INSTANCE) {
            return TypedValueExpression.UNKNOWN;
        }
        return getBoolean(value.getBoolean());
    }

    /**
     * Create a new expression with the given boolean value.
     *
     * @param value the boolean value
     * @return the expression
     */
    public static ValueExpression getBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public Value getValue(SessionLocal session) {
        return value;
    }

    @Override
    public TypeInfo getType() {
        return value.getType();
    }

    @Override
    public void createIndexConditions(SessionLocal session, TableFilter filter) {
        if (value.getValueType() == Value.BOOLEAN && !value.getBoolean()) {
            filter.addIndexCondition(IndexCondition.get(Comparison.FALSE, null, this));
        }
    }

    @Override
    public Expression getNotIfPossible(SessionLocal session) {
        if (value == ValueNull.INSTANCE) {
            return TypedValueExpression.UNKNOWN;
        }
        return getBoolean(!value.getBoolean());
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public boolean isNullConstant() {
        return this == NULL;
    }

    @Override
    public boolean isValueSet() {
        return true;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        if (this == DEFAULT) {
            builder.append("DEFAULT");
        } else {
            value.getSQL(builder, sqlFlags);
        }
        return builder;
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        switch (visitor.getType()) {
        case ExpressionVisitor.OPTIMIZABLE_AGGREGATE:
        case ExpressionVisitor.DETERMINISTIC:
        case ExpressionVisitor.READONLY:
        case ExpressionVisitor.INDEPENDENT:
        case ExpressionVisitor.EVALUATABLE:
        case ExpressionVisitor.SET_MAX_DATA_MODIFICATION_ID:
        case ExpressionVisitor.NOT_FROM_RESOLVER:
        case ExpressionVisitor.GET_DEPENDENCIES:
        case ExpressionVisitor.QUERY_COMPARABLE:
        case ExpressionVisitor.GET_COLUMNS1:
        case ExpressionVisitor.GET_COLUMNS2:
            return true;
        default:
            throw DbException.getInternalError("type=" + visitor.getType());
        }
    }

    @Override
    public int getCost() {
        return 0;
    }

}
