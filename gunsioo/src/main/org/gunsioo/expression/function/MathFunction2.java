/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.TypedValueExpression;
import org.gunsioo.message.DbException;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueDouble;

/**
 * A math function with two arguments and DOUBLE PRECISION result.
 */
public final class MathFunction2 extends Function2 {

    /**
     * ATAN2() (non-standard).
     */
    public static final int ATAN2 = 0;

    /**
     * LOG().
     */
    public static final int LOG = ATAN2 + 1;

    /**
     * POWER().
     */
    public static final int POWER = LOG + 1;

    private static final String[] NAMES = { //
            "ATAN2", "LOG", "POWER" //
    };

    private final int function;

    public MathFunction2(Expression arg1, Expression arg2, int function) {
        super(arg1, arg2);
        this.function = function;
    }

    @Override
    public Value getValue(SessionLocal session, Value v1, Value v2) {
        double d1 = v1.getDouble(), d2 = v2.getDouble();
        switch (function) {
        case ATAN2:
            d1 = Math.atan2(d1, d2);
            break;
        case LOG: {
            if (session.getMode().swapLogFunctionParameters) {
                double t = d2;
                d2 = d1;
                d1 = t;
            }
            if (d2 <= 0) {
                throw DbException.getInvalidValueException("LOG() argument", d2);
            }
            if (d1 <= 0 || d1 == 1) {
                throw DbException.getInvalidValueException("LOG() base", d1);
            }
            if (d1 == Math.E) {
                d1 = Math.log(d2);
            } else if (d1 == 10d) {
                d1 = Math.log10(d2);
            } else {
                d1 = Math.log(d2) / Math.log(d1);
            }
            break;
        }
        case POWER:
            d1 = Math.pow(d1, d2);
            break;
        default:
            throw DbException.getInternalError("function=" + function);
        }
        return ValueDouble.get(d1);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        left = left.optimize(session);
        right = right.optimize(session);
        type = TypeInfo.TYPE_DOUBLE;
        if (left.isConstant() && right.isConstant()) {
            return TypedValueExpression.getTypedIfNull(getValue(session), type);
        }
        return this;
    }

    @Override
    public String getName() {
        return NAMES[function];
    }

}
