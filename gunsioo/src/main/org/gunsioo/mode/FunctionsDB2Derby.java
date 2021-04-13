/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.mode;

import java.util.HashMap;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.message.DbException;
import org.gunsioo.value.ExtTypeInfoNumeric;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;

/**
 * Functions for {@link org.gunsioo.engine.Mode.ModeEnum#DB2} and
 * {@link org.gunsioo.engine.Mode.ModeEnum#Derby} compatibility modes.
 */
public final class FunctionsDB2Derby extends ModeFunction {

    private static final int IDENTITY_VAL_LOCAL = 5001;

    private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap<>();

    private static final TypeInfo IDENTITY_VAL_LOCAL_TYPE = TypeInfo.getTypeInfo(Value.NUMERIC, 31, 0,
            ExtTypeInfoNumeric.DECIMAL);

    static {
        FUNCTIONS.put("IDENTITY_VAL_LOCAL",
                new FunctionInfo("IDENTITY_VAL_LOCAL", IDENTITY_VAL_LOCAL, 0, Value.BIGINT, true, false));
    }

    /**
     * Returns mode-specific function for a given name, or {@code null}.
     *
     * @param upperName
     *            the upper-case name of a function
     * @return the function with specified name or {@code null}
     */
    public static FunctionsDB2Derby getFunction(String upperName) {
        FunctionInfo info = FUNCTIONS.get(upperName);
        return info != null ? new FunctionsDB2Derby(info) : null;
    }

    private FunctionsDB2Derby(FunctionInfo info) {
        super(info);
    }

    @Override
    public Value getValue(SessionLocal session) {
        switch (info.type) {
        case IDENTITY_VAL_LOCAL:
            return session.getLastIdentity().convertTo(type);
        default:
            throw DbException.getInternalError("type=" + info.type);
        }
    }

    @Override
    public Expression optimize(SessionLocal session) {
        switch (info.type) {
        case IDENTITY_VAL_LOCAL:
            type = IDENTITY_VAL_LOCAL_TYPE;
            break;
        default:
            throw DbException.getInternalError("type=" + info.type);
        }
        return this;
    }

}
