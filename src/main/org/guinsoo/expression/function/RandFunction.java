/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import java.util.Random;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.message.DbException;
import org.guinsoo.util.MathUtils;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueDouble;
import org.guinsoo.value.ValueNull;
import org.guinsoo.value.ValueUuid;
import org.guinsoo.value.ValueVarbinary;

/**
 * A RAND, SECURE_RAND, or RANDOM_UUID function.
 */
public final class RandFunction extends Function0_1 {

    /**
     * RAND() (non-standard).
     */
    public static final int RAND = 0;

    /**
     * SECURE_RAND() (non-standard).
     */
    public static final int SECURE_RAND = RAND + 1;

    /**
     * RANDOM_UUID() (non-standard).
     */
    public static final int RANDOM_UUID = SECURE_RAND + 1;

    private static final String[] NAMES = { //
            "RAND", "SECURE_RAND", "RANDOM_UUID" //
    };

    private final int function;

    public RandFunction(Expression arg, int function) {
        super(arg);
        this.function = function;
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value v;
        if (arg != null) {
            v = arg.getValue(session);
            if (v == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
        } else {
            v = null;
        }
        switch (function) {
        case RAND: {
            Random random = session.getRandom();
            if (v != null) {
                random.setSeed(v.getInt());
            }
            v = ValueDouble.get(random.nextDouble());
            break;
        }
        case SECURE_RAND:
            v = ValueVarbinary.getNoCopy(MathUtils.secureRandomBytes(v.getInt()));
            break;
        case RANDOM_UUID:
            v = ValueUuid.getNewRandom();
            break;
        default:
            throw DbException.getInternalError("function=" + function);
        }
        return v;
    }

    @Override
    public Expression optimize(SessionLocal session) {
        if (arg != null) {
            arg = arg.optimize(session);
        }
        switch (function) {
        case RAND:
            type = TypeInfo.TYPE_DOUBLE;
            break;
        case SECURE_RAND:
            type = arg.isConstant()
                    ? TypeInfo.getTypeInfo(Value.VARBINARY, Math.max(arg.getValue(session).getInt(), 1), 0, null)
                    : TypeInfo.TYPE_VARBINARY;
            break;
        case RANDOM_UUID:
            type = TypeInfo.TYPE_UUID;
            break;
        default:
            throw DbException.getInternalError("function=" + function);
        }
        return this;
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        switch (visitor.getType()) {
        case ExpressionVisitor.DETERMINISTIC:
            return false;
        }
        return super.isEverything(visitor);
    }

    @Override
    public String getName() {
        return NAMES[function];
    }

}
