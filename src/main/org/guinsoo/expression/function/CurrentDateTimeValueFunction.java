/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.expression.Operation0;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueTime;
import org.guinsoo.value.ValueTimestamp;

/**
 * Current datetime value function.
 */
public final class CurrentDateTimeValueFunction extends Operation0 implements NamedExpression {

    /**
     * The function "CURRENT_DATE"
     */
    public static final int CURRENT_DATE = 0;

    /**
     * The function "CURRENT_TIME"
     */
    public static final int CURRENT_TIME = 1;

    /**
     * The function "LOCALTIME"
     */
    public static final int LOCALTIME = 2;

    /**
     * The function "CURRENT_TIMESTAMP"
     */
    public static final int CURRENT_TIMESTAMP = 3;

    /**
     * The function "LOCALTIMESTAMP"
     */
    public static final int LOCALTIMESTAMP = 4;

    private static final int[] TYPES = { Value.DATE, Value.TIME_TZ, Value.TIME, Value.TIMESTAMP_TZ, Value.TIMESTAMP };

    private static final String[] NAMES = { "CURRENT_DATE", "CURRENT_TIME", "LOCALTIME", "CURRENT_TIMESTAMP",
            "LOCALTIMESTAMP" };

    /**
     * Get the name for this function id.
     *
     * @param function the function id
     * @return the name
     */
    public static String getName(int function) {
        return NAMES[function];
    }

    private final int function, scale;

    private final TypeInfo type;

    public CurrentDateTimeValueFunction(int function, int scale) {
        this.function = function;
        this.scale = scale;
        if (scale < 0) {
            scale = function >= CURRENT_TIMESTAMP ? ValueTimestamp.DEFAULT_SCALE : ValueTime.DEFAULT_SCALE;
        }
        type = TypeInfo.getTypeInfo(TYPES[function], 0L, scale, null);
    }

    @Override
    public Value getValue(SessionLocal session) {
        return session.currentTimestamp().castTo(type, session);
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        builder.append(getName());
        if (scale >= 0) {
            builder.append('(').append(scale).append(')');
        }
        return builder;
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        switch (visitor.getType()) {
        case ExpressionVisitor.DETERMINISTIC:
            return false;
        }
        return true;
    }

    @Override
    public TypeInfo getType() {
        return type;
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getName() {
        return NAMES[function];
    }

}
