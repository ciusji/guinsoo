/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.condition.Comparison;
import org.guinsoo.message.DbException;
import org.guinsoo.table.Column;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;
import org.guinsoo.value.ValueVarchar;

/**
 * A parameter of a prepared statement.
 */
public final class Parameter extends Operation0 implements ParameterInterface {

    private Value value;
    private Column column;
    private final int index;

    public Parameter(int index) {
        this.index = index;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return builder.append('?').append(index + 1);
    }

    @Override
    public void setValue(Value v, boolean closeOld) {
        // don't need to close the old value as temporary files are anyway
        // removed
        this.value = v;
    }

    public void setValue(Value v) {
        this.value = v;
    }

    @Override
    public Value getParamValue() {
        if (value == null) {
            // to allow parameters in function tables
            return ValueNull.INSTANCE;
        }
        return value;
    }

    @Override
    public Value getValue(SessionLocal session) {
        return getParamValue();
    }

    @Override
    public TypeInfo getType() {
        if (value != null) {
            return value.getType();
        }
        if (column != null) {
            return column.getType();
        }
        return TypeInfo.TYPE_UNKNOWN;
    }

    @Override
    public void checkSet() {
        if (value == null) {
            throw DbException.get(ErrorCode.PARAMETER_NOT_SET_1, "#" + (index + 1));
        }
    }

    @Override
    public Expression optimize(SessionLocal session) {
        if (session.getDatabase().getMode().treatEmptyStringsAsNull) {
            if (value instanceof ValueVarchar && value.getString().isEmpty()) {
                value = ValueNull.INSTANCE;
            }
        }
        return this;
    }

    @Override
    public boolean isValueSet() {
        return value != null;
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        switch (visitor.getType()) {
        case ExpressionVisitor.EVALUATABLE:
            // the parameter _will_be_ evaluatable at execute time
        case ExpressionVisitor.SET_MAX_DATA_MODIFICATION_ID:
            // it is checked independently if the value is the same as the last
            // time
        case ExpressionVisitor.NOT_FROM_RESOLVER:
        case ExpressionVisitor.QUERY_COMPARABLE:
        case ExpressionVisitor.GET_DEPENDENCIES:
        case ExpressionVisitor.OPTIMIZABLE_AGGREGATE:
        case ExpressionVisitor.DETERMINISTIC:
        case ExpressionVisitor.READONLY:
        case ExpressionVisitor.GET_COLUMNS1:
        case ExpressionVisitor.GET_COLUMNS2:
            return true;
        case ExpressionVisitor.INDEPENDENT:
            return value != null;
        default:
            throw DbException.getInternalError("type="+visitor.getType());
        }
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public Expression getNotIfPossible(SessionLocal session) {
        return new Comparison(Comparison.EQUAL, this, ValueExpression.FALSE, false);
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public int getIndex() {
        return index;
    }

}
