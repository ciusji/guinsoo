/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;

/**
 * A searched case.
 */
public final class SearchedCase extends OperationN {

    public SearchedCase() {
        super(new Expression[4]);
    }

    public SearchedCase(Expression[] args) {
        super(args);
    }

    @Override
    public Value getValue(SessionLocal session) {
        int len = args.length - 1;
        for (int i = 0; i < len; i += 2) {
            if (args[i].getBooleanValue(session)) {
                return args[i + 1].getValue(session).convertTo(type, session);
            }
        }
        if ((len & 1) == 0) {
            return args[len].getValue(session).convertTo(type, session);
        }
        return ValueNull.INSTANCE;
    }

    @Override
    public Expression optimize(SessionLocal session) {
        TypeInfo typeInfo = TypeInfo.TYPE_UNKNOWN;
        int len = args.length - 1;
        boolean allConst = true;
        for (int i = 0; i < len; i += 2) {
            Expression condition = args[i].optimize(session);
            Expression result = args[i + 1].optimize(session);
            if (allConst) {
                if (condition.isConstant()) {
                    if (condition.getBooleanValue(session)) {
                        return result;
                    }
                } else {
                    allConst = false;
                }
            }
            args[i] = condition;
            args[i + 1] = result;
            typeInfo = SimpleCase.combineTypes(typeInfo, result);
        }
        if ((len & 1) == 0) {
            Expression result = args[len].optimize(session);
            if (allConst) {
                return result;
            }
            args[len] = result;
            typeInfo = SimpleCase.combineTypes(typeInfo, result);
        } else if (allConst) {
            return ValueExpression.NULL;
        }
        if (typeInfo.getValueType() == Value.UNKNOWN) {
            typeInfo = TypeInfo.TYPE_VARCHAR;
        }
        type = typeInfo;
        return this;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        builder.append("CASE");
        int len = args.length - 1;
        for (int i = 0; i < len; i += 2) {
            builder.append(" WHEN ");
            args[i].getUnenclosedSQL(builder, sqlFlags);
            builder.append(" THEN ");
            args[i + 1].getUnenclosedSQL(builder, sqlFlags);
        }
        if ((len & 1) == 0) {
            builder.append(" ELSE ");
            args[len].getUnenclosedSQL(builder, sqlFlags);
        }
        return builder.append(" END");
    }

}
