/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import org.guinsoo.engine.Constants;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.expression.Operation0;
import org.guinsoo.message.DbException;
import org.guinsoo.util.Utils;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueBigint;
import org.guinsoo.value.ValueBoolean;
import org.guinsoo.value.ValueInteger;
import org.guinsoo.value.ValueNull;
import org.guinsoo.value.ValueVarchar;

/**
 * Database or session information function.
 */
public final class SysInfoFunction extends Operation0 implements NamedExpression {

    /**
     * AUTOCOMMIT().
     */
    public static final int AUTOCOMMIT = 0;

    /**
     * DATABASE_PATH().
     */
    public static final int DATABASE_PATH = AUTOCOMMIT + 1;

    /**
     * H2VERSION().
     */
    public static final int H2VERSION = DATABASE_PATH + 1;

    /**
     * LOCK_MODE().
     */
    public static final int LOCK_MODE = H2VERSION + 1;

    /**
     * LOCK_TIMEOUT().
     */
    public static final int LOCK_TIMEOUT = LOCK_MODE + 1;

    /**
     * MEMORY_FREE().
     */
    public static final int MEMORY_FREE = LOCK_TIMEOUT + 1;

    /**
     * MEMORY_USED().
     */
    public static final int MEMORY_USED = MEMORY_FREE + 1;

    /**
     * READONLY().
     */
    public static final int READONLY = MEMORY_USED + 1;

    /**
     * SESSION_ID().
     */
    public static final int SESSION_ID = READONLY + 1;

    /**
     * TRANSACTION_ID().
     */
    public static final int TRANSACTION_ID = SESSION_ID + 1;

    private static final int[] TYPES = { Value.BOOLEAN, Value.VARCHAR, Value.VARCHAR, Value.INTEGER, Value.INTEGER,
            Value.BIGINT, Value.BIGINT, Value.BOOLEAN, Value.INTEGER, Value.VARCHAR };

    private static final String[] NAMES = { "AUTOCOMMIT", "DATABASE_PATH", "H2VERSION", "LOCK_MODE", "LOCK_TIMEOUT",
            "MEMORY_FREE", "MEMORY_USED", "READONLY", "SESSION_ID", "TRANSACTION_ID" };

    /**
     * Get the name for this function id.
     *
     * @param function
     *            the function id
     * @return the name
     */
    public static String getName(int function) {
        return NAMES[function];
    }

    private final int function;

    private final TypeInfo type;

    public SysInfoFunction(int function) {
        this.function = function;
        type = TypeInfo.getTypeInfo(TYPES[function]);
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value result;
        switch (function) {
        case AUTOCOMMIT:
            result = ValueBoolean.get(session.getAutoCommit());
            break;
        case DATABASE_PATH: {
            String path = session.getDatabase().getDatabasePath();
            result = path != null ? ValueVarchar.get(path, session) : ValueNull.INSTANCE;
            break;
        }
        case H2VERSION:
            result = ValueVarchar.get(Constants.VERSION, session);
            break;
        case LOCK_MODE:
            result = ValueInteger.get(session.getDatabase().getLockMode());
            break;
        case LOCK_TIMEOUT:
            result = ValueInteger.get(session.getLockTimeout());
            break;
        case MEMORY_FREE:
            session.getUser().checkAdmin();
            result = ValueBigint.get(Utils.getMemoryFree());
            break;
        case MEMORY_USED:
            session.getUser().checkAdmin();
            result = ValueBigint.get(Utils.getMemoryUsed());
            break;
        case READONLY:
            result = ValueBoolean.get(session.getDatabase().isReadOnly());
            break;
        case SESSION_ID:
            result = ValueInteger.get(session.getId());
            break;
        case TRANSACTION_ID:
            result = session.getTransactionId();
            break;
        default:
            throw DbException.getInternalError("function=" + function);
        }
        return result;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return builder.append(getName()).append("()");
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
