/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.mode;

import org.guinsoo.engine.Database;
import org.guinsoo.engine.Mode;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.message.DbException;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.expression.function.FunctionN;

/**
 * Base class for mode-specific functions.
 */
public abstract class ModeFunction extends FunctionN {

    /**
     * Constant for variable number of arguments.
     */
    protected static final int VAR_ARGS = -1;

    /**
     * The information about this function.
     */
    protected final FunctionInfo info;

    /**
     * Get an instance of the given function for this database.
     * If no function with this name is found, null is returned.
     *
     * @param database the database
     * @param name the upper case function name
     * @return the function object or null
     */
    public static ModeFunction getFunction(Database database, String name) {
        Mode.ModeEnum modeEnum = database.getMode().getEnum();
        if (modeEnum != Mode.ModeEnum.REGULAR) {
            return getCompatibilityModeFunction(name, modeEnum);
        }
        return null;
    }

    private static ModeFunction getCompatibilityModeFunction(String name, Mode.ModeEnum modeEnum) {
        switch (modeEnum) {
        case DB2:
        case Derby:
            return FunctionsDB2Derby.getFunction(name);
        case MSSQLServer:
            return FunctionsMSSQLServer.getFunction(name);
        case MySQL:
            return FunctionsMySQL.getFunction(name);
        case Oracle:
            return FunctionsOracle.getFunction(name);
        case PostgreSQL:
            return FunctionsPostgreSQL.getFunction(name);
        default:
            return null;
        }
    }


    /**
     * Creates a new instance of function.
     *
     * @param info function information
     */
    ModeFunction(FunctionInfo info) {
        super(new Expression[info.parameterCount != VAR_ARGS ? info.parameterCount : 4]);
        this.info = info;
    }

    /**
     * Get value transformed by expression, or null if i is out of range or
     * the input value is null.
     *
     * @param session database session
     * @param args expressions
     * @param values array of input values
     * @param i index of value of transform
     * @return value or null
     */
    static Value getNullOrValue(SessionLocal session, Expression[] args,
                                Value[] values, int i) {
        if (i >= args.length) {
            return null;
        }
        Value v = values[i];
        if (v == null) {
            Expression e = args[i];
            if (e == null) {
                return null;
            }
            v = values[i] = e.getValue(session);
        }
        return v;
    }

    /**
     * Gets values of arguments and checks them for NULL values if function
     * returns NULL on NULL argument.
     *
     * @param session
     *            the session
     * @param args
     *            the arguments
     * @return the values, or {@code null} if function should return NULL due to
     *         NULL argument
     */
    final Value[] getArgumentsValues(SessionLocal session, Expression[] args) {
        Value[] values = new Value[args.length];
        if (info.nullIfParameterIsNull) {
            for (int i = 0, l = args.length; i < l; i++) {
                Value v = args[i].getValue(session);
                if (v == ValueNull.INSTANCE) {
                    return null;
                }
                values[i] = v;
            }
        }
        return values;
    }

    /**
     * Check if the parameter count is correct.
     *
     * @param len the number of parameters set
     * @throws DbException if the parameter count is incorrect
     */
    void checkParameterCount(int len) {
        throw DbException.getInternalError("type=" + info.type);
    }

    @Override
    public void doneWithParameters() {
        int count = info.parameterCount;
        if (count == VAR_ARGS) {
            checkParameterCount(argsCount);
            super.doneWithParameters();
        } else if (count != argsCount) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, info.name, Integer.toString(argsCount));
        }
    }

    /**
     * Optimizes arguments.
     *
     * @param session
     *            the session
     * @return whether all arguments are constants and function is deterministic
     */
    final boolean optimizeArguments(SessionLocal session) {
        return optimizeArguments(session, info.deterministic);
    }

    @Override
    public String getName() {
        return info.name;
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        if (!super.isEverything(visitor)) {
            return false;
        }
        switch (visitor.getType()) {
        case ExpressionVisitor.DETERMINISTIC:
        case ExpressionVisitor.QUERY_COMPARABLE:
        case ExpressionVisitor.READONLY:
            return info.deterministic;
        case ExpressionVisitor.EVALUATABLE:
        case ExpressionVisitor.GET_DEPENDENCIES:
        case ExpressionVisitor.INDEPENDENT:
        case ExpressionVisitor.NOT_FROM_RESOLVER:
        case ExpressionVisitor.OPTIMIZABLE_AGGREGATE:
        case ExpressionVisitor.SET_MAX_DATA_MODIFICATION_ID:
        case ExpressionVisitor.GET_COLUMNS1:
        case ExpressionVisitor.GET_COLUMNS2:
            return true;
        default:
            throw DbException.getInternalError("type=" + visitor.getType());
        }
    }

}
