/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import java.util.Map.Entry;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.util.ParserUtil;
import org.guinsoo.value.ExtTypeInfoRow;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;
import org.guinsoo.value.ValueRow;

/**
 * Field reference.
 */
public final class FieldReference extends Operation1 {

    private final String fieldName;

    private int ordinal;

    public FieldReference(Expression arg, String fieldName) {
        super(arg);
        this.fieldName = fieldName;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return ParserUtil.quoteIdentifier(arg.getEnclosedSQL(builder, sqlFlags).append('.'), fieldName, sqlFlags);
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value l = arg.getValue(session);
        if (l != ValueNull.INSTANCE) {
            return ((ValueRow) l).getList()[ordinal];
        }
        return ValueNull.INSTANCE;
    }

    @Override
    public Expression optimize(SessionLocal session) {
        arg = arg.optimize(session);
        TypeInfo type = arg.getType();
        if (type.getValueType() != Value.ROW) {
            throw DbException.getInvalidValueException("ROW", type.getTraceSQL());
        }
        int ordinal = 0;
        for (Entry<String, TypeInfo> entry : ((ExtTypeInfoRow) type.getExtTypeInfo()).getFields()) {
            if (fieldName.equals(entry.getKey())) {
                type = entry.getValue();
                this.type = type;
                this.ordinal = ordinal;
                if (arg.isConstant()) {
                    return TypedValueExpression.get(getValue(session), type);
                }
                return this;
            }
            ordinal++;
        }
        throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, fieldName);
    }

}
