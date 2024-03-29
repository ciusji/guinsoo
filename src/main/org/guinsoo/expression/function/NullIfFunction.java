/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.TypedValueExpression;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;

/**
 * A NULLIF function.
 */
public final class NullIfFunction extends Function2 {

    public NullIfFunction(Expression arg1, Expression arg2) {
        super(arg1, arg2);
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value v = left.getValue(session);
        if (session.compareWithNull(v, right.getValue(session), true) == 0) {
            v = ValueNull.INSTANCE;
        }
        return v;
    }

    @Override
    public Expression optimize(SessionLocal session) {
        left = left.optimize(session);
        right = right.optimize(session);
        type = left.getType();
        TypeInfo.checkComparable(type, right.getType());
        if (left.isConstant() && right.isConstant()) {
            return TypedValueExpression.getTypedIfNull(getValue(session), type);
        }
        return this;
    }

    @Override
    public String getName() {
        return "NULLIF";
    }

}
