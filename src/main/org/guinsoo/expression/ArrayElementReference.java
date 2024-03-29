/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueArray;
import org.guinsoo.value.ValueNull;

/**
 * Array element reference.
 */
public final class ArrayElementReference extends Operation2 {

    public ArrayElementReference(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        left.getSQL(builder, sqlFlags, AUTO_PARENTHESES).append('[');
        return right.getUnenclosedSQL(builder, sqlFlags).append(']');
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value l = left.getValue(session);
        Value r = right.getValue(session);
        if (l != ValueNull.INSTANCE && r != ValueNull.INSTANCE) {
            Value[] list = ((ValueArray) l).getList();
            int element = r.getInt();
            int cardinality = list.length;
            if (element >= 1 && element <= cardinality) {
                return list[element - 1];
            }
            throw DbException.get(ErrorCode.ARRAY_ELEMENT_ERROR_2, Integer.toString(element), "1.." + cardinality);
        }
        return ValueNull.INSTANCE;
    }

    @Override
    public Expression optimize(SessionLocal session) {
        left = left.optimize(session);
        right = right.optimize(session);
        TypeInfo leftType = left.getType();
        switch (leftType.getValueType()) {
        case Value.NULL:
            return ValueExpression.NULL;
        case Value.ARRAY:
            type = (TypeInfo) leftType.getExtTypeInfo();
            if (left.isConstant() && right.isConstant()) {
                return TypedValueExpression.get(getValue(session), type);
            }
            break;
        default:
            throw DbException.getInvalidValueException("Array", leftType.getTraceSQL());
        }
        return this;
    }

}
