/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

import java.math.BigDecimal;
import java.math.MathContext;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.TypedValueExpression;
import org.gunsioo.message.DbException;
import org.gunsioo.util.MathUtils;
import org.gunsioo.value.DataType;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueDecfloat;
import org.gunsioo.value.ValueNumeric;

/**
 * A TRUNCATE_VALUE function.
 */
public final class TruncateValueFunction extends FunctionN {

    public TruncateValueFunction(Expression arg1, Expression arg2, Expression arg3) {
        super(new Expression[] { arg1, arg2, arg3 });
    }

    @Override
    public Value getValue(SessionLocal session, Value v1, Value v2, Value v3) {
        long precision = v2.getLong();
        boolean force = v3.getBoolean();
        if (precision <= 0) {
            throw DbException.get(ErrorCode.INVALID_VALUE_PRECISION, Long.toString(precision), "1",
                    "" + Integer.MAX_VALUE);
        }
        TypeInfo t = v1.getType();
        int valueType = t.getValueType();
        if (DataType.getDataType(valueType).supportsPrecision) {
            if (precision < t.getPrecision()) {
                switch (valueType) {
                case Value.NUMERIC: {
                    BigDecimal bd = v1.getBigDecimal().round(new MathContext(MathUtils.convertLongToInt(precision)));
                    if (bd.scale() < 0) {
                        bd = bd.setScale(0);
                    }
                    return ValueNumeric.get(bd);
                }
                case Value.DECFLOAT:
                    return ValueDecfloat
                            .get(v1.getBigDecimal().round(new MathContext(MathUtils.convertLongToInt(precision))));
                default:
                    return v1.castTo(TypeInfo.getTypeInfo(valueType, precision, t.getScale(), t.getExtTypeInfo()),
                            session);
                }
            }
        } else if (force) {
            BigDecimal bd;
            switch (valueType) {
            case Value.TINYINT:
            case Value.SMALLINT:
            case Value.INTEGER:
                bd = BigDecimal.valueOf(v1.getInt());
                break;
            case Value.BIGINT:
                bd = BigDecimal.valueOf(v1.getLong());
                break;
            case Value.REAL:
            case Value.DOUBLE:
                bd = v1.getBigDecimal();
                break;
            default:
                return v1;
            }
            bd = bd.round(new MathContext(MathUtils.convertLongToInt(precision)));
            if (valueType == Value.DECFLOAT) {
                return ValueDecfloat.get(bd);
            }
            if (bd.scale() < 0) {
                bd = bd.setScale(0);
            }
            return ValueNumeric.get(bd).convertTo(valueType);
        }
        return v1;
    }

    @Override
    public Expression optimize(SessionLocal session) {
        boolean allConst = optimizeArguments(session, true);
        type = args[0].getType();
        if (allConst) {
            return TypedValueExpression.getTypedIfNull(getValue(session), type);
        }
        return this;
    }

    @Override
    public String getName() {
        return "TRUNCATE_VALUE";
    }

}
