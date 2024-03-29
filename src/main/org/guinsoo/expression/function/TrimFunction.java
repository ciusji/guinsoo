/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.TypedValueExpression;
import org.guinsoo.util.StringUtils;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueVarchar;

/**
 * A TRIM function.
 */
public final class TrimFunction extends Function1_2 {

    /**
     * The LEADING flag.
     */
    public static final int LEADING = 1;

    /**
     * The TRAILING flag.
     */
    public static final int TRAILING = 2;

    private int flags;

    public TrimFunction(Expression from, Expression space, int flags) {
        super(from, space);
        this.flags = flags;
    }

    @Override
    public Value getValue(SessionLocal session, Value v1, Value v2) {
        return ValueVarchar.get(StringUtils.trim(v1.getString(), (flags & LEADING) != 0, (flags & TRAILING) != 0,
                v2 != null ? v2.getString() : " "), session);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        left = left.optimize(session);
        if (right != null) {
            right = right.optimize(session);
        }
        type = TypeInfo.getTypeInfo(Value.VARCHAR, left.getType().getPrecision(), 0, null);
        if (left.isConstant() && (right == null || right.isConstant())) {
            return TypedValueExpression.getTypedIfNull(getValue(session), type);
        }
        return this;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        builder.append(getName()).append('(');
        boolean needFrom = false;
        switch (flags) {
        case LEADING:
            builder.append("LEADING ");
            needFrom = true;
            break;
        case TRAILING:
            builder.append("TRAILING ");
            needFrom = true;
            break;
        }
        if (right != null) {
            right.getUnenclosedSQL(builder, sqlFlags);
            needFrom = true;
        }
        if (needFrom) {
            builder.append(" FROM ");
        }
        return left.getUnenclosedSQL(builder, sqlFlags).append(')');
    }

    @Override
    public String getName() {
        return "TRIM";
    }

}
