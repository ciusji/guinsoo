/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.Operation1_2;
import org.guinsoo.message.DbException;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;

/**
 * Function with two arguments.
 */
public abstract class Function1_2 extends Operation1_2 implements NamedExpression {

    protected Function1_2(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value v1 = left.getValue(session);
        if (v1 == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        Value v2;
        if (right != null) {
            v2 = right.getValue(session);
            if (v2 == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
        } else {
            v2 = null;
        }
        return getValue(session, v1, v2);
    }

    /**
     * Returns the value of this function.
     *
     * @param session
     *            the session
     * @param v1
     *            the value of first argument
     * @param v2
     *            the value of second argument, or {@code null}
     * @return the resulting value
     */
    protected Value getValue(SessionLocal session, Value v1, Value v2) {
        throw DbException.getInternalError();
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        left.getUnenclosedSQL(builder.append(getName()).append('('), sqlFlags);
        if (right != null) {
            right.getUnenclosedSQL(builder.append(", "), sqlFlags);
        }
        return builder.append(')');
    }

}
