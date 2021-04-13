/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.TypedValueExpression;
import org.gunsioo.value.DataType;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueNull;
import org.gunsioo.value.ValueVarchar;

/**
 * A CONCAT or CONCAT_WS function.
 */
public final class ConcatFunction extends FunctionN {

    /**
     * CONCAT() (non-standard).
     */
    public static final int CONCAT = 0;

    /**
     * CONCAT_WS() (non-standard).
     */
    public static final int CONCAT_WS = CONCAT + 1;

    private static final String[] NAMES = { //
            "CONCAT", "CONCAT_WS" //
    };

    private final int function;

    public ConcatFunction(int function) {
        this(function, new Expression[4]);
    }

    public ConcatFunction(int function, Expression... args) {
        super(args);
        this.function = function;
    }

    @Override
    public Value getValue(SessionLocal session) {
        int i = 0;
        String separator = null;
        if (function == CONCAT_WS) {
            i = 1;
            separator = args[0].getValue(session).getString();
        }
        StringBuilder builder = new StringBuilder();
        boolean f = false;
        for (int l = args.length; i < l; i++) {
            Value v = args[i].getValue(session);
            if (v != ValueNull.INSTANCE) {
                if (separator != null) {
                    if (f) {
                        builder.append(separator);
                    }
                    f = true;
                }
                builder.append(v.getString());
            }
        }
        return ValueVarchar.get(builder.toString(), session);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        boolean allConst = optimizeArguments(session, true);
        int i = 0;
        long extra = 0L;
        if (function == CONCAT_WS) {
            i = 1;
            extra = getPrecision(0);
        }
        long precision = 0L;
        int l = args.length;
        boolean f = false;
        for (; i < l; i++) {
            if (args[i].isNullConstant()) {
                continue;
            }
            precision = DataType.addPrecision(precision, getPrecision(i));
            if (extra != 0L && f) {
                precision = DataType.addPrecision(precision, extra);
            }
            f = true;
        }
        type = TypeInfo.getTypeInfo(Value.VARCHAR, precision, 0, null);
        if (allConst) {
            return TypedValueExpression.getTypedIfNull(getValue(session), type);
        }
        return this;
    }

    private long getPrecision(int i) {
        TypeInfo t = args[i].getType();
        int valueType = t.getValueType();
        if (valueType == Value.NULL) {
            return 0L;
        } else if (DataType.isCharacterStringType(valueType)) {
            return t.getPrecision();
        } else {
            return Long.MAX_VALUE;
        }
    }

    @Override
    public String getName() {
        return NAMES[function];
    }

}
