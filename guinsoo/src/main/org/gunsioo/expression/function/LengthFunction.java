/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.TypedValueExpression;
import org.gunsioo.message.DbException;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueBigint;
import org.gunsioo.value.ValueNull;

/**
 * CHAR_LENGTH(), or OCTET_LENGTH() function.
 */
public final class LengthFunction extends Function1 {

    /**
     * CHAR_LENGTH().
     */
    public static final int CHAR_LENGTH = 0;

    /**
     * OCTET_LENGTH().
     */
    public static final int OCTET_LENGTH = CHAR_LENGTH + 1;

    /**
     * BIT_LENGTH() (non-standard).
     */
    public static final int BIT_LENGTH = OCTET_LENGTH + 1;

    private static final String[] NAMES = { //
            "CHAR_LENGTH", "OCTET_LENGTH", "BIT_LENGTH" //
    };

    private final int function;

    public LengthFunction(Expression arg, int function) {
        super(arg);
        this.function = function;
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value v = arg.getValue(session);
        if (v == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        long l;
        switch (function) {
        case CHAR_LENGTH:
            l = v.charLength();
            break;
        case OCTET_LENGTH:
            l = v.octetLength();
            break;
        case BIT_LENGTH:
            l = v.octetLength() * 8;
            break;
        default:
            throw DbException.getInternalError("function=" + function);
        }
        return ValueBigint.get(l);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        arg = arg.optimize(session);
        type = TypeInfo.TYPE_BIGINT;
        if (arg.isConstant()) {
            return TypedValueExpression.getTypedIfNull(getValue(session), type);
        }
        return this;
    }

    @Override
    public String getName() {
        return NAMES[function];
    }

}
