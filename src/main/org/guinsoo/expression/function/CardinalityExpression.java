/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.TypedValueExpression;
import org.guinsoo.message.DbException;
import org.guinsoo.util.MathUtils;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueArray;
import org.guinsoo.value.ValueInteger;
import org.guinsoo.value.ValueNull;

/**
 * Cardinality expression.
 */
public final class CardinalityExpression extends Function1 {

    private final boolean max;

    /**
     * Creates new instance of cardinality expression.
     *
     * @param arg
     *            argument
     * @param max
     *            {@code false} for {@code CARDINALITY}, {@code true} for
     *            {@code ARRAY_MAX_CARDINALITY}
     */
    public CardinalityExpression(Expression arg, boolean max) {
        super(arg);
        this.max = max;
    }

    @Override
    public Value getValue(SessionLocal session) {
        int result;
        if (max) {
            TypeInfo t = arg.getType();
            if (t.getValueType() == Value.ARRAY) {
                result = MathUtils.convertLongToInt(t.getPrecision());
            } else {
                throw DbException.getInvalidValueException("array", arg.getValue(session).getTraceSQL());
            }
        } else {
            Value v = arg.getValue(session);
            if (v == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
            if (v.getValueType() != Value.ARRAY) {
                throw DbException.getInvalidValueException("array", v.getTraceSQL());
            }
            result = ((ValueArray) v).getList().length;
        }
        return ValueInteger.get(result);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        arg = arg.optimize(session);
        type = TypeInfo.TYPE_INTEGER;
        if (arg.isConstant()) {
            return TypedValueExpression.getTypedIfNull(getValue(session), type);
        }
        return this;
    }

    @Override
    public String getName() {
        return max ? "ARRAY_MAX_CARDINALITY" : "CARDINALITY";
    }

}
