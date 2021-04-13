/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

import org.gunsioo.command.Command;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.ExpressionVisitor;
import org.gunsioo.message.DbException;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueBoolean;
import org.gunsioo.value.ValueNull;

/**
 * An ABORT_SESSION() or CANCEL_SESSION() function.
 */
public final class SessionControlFunction extends Function1 {

    /**
     * ABORT_SESSION().
     */
    public static final int ABORT_SESSION = 0;

    /**
     * CANCEL_SESSION().
     */
    public static final int CANCEL_SESSION = ABORT_SESSION + 1;

    private static final String[] NAMES = { //
            "ABORT_SESSION", "CANCEL_SESSION" //
    };

    private final int function;

    public SessionControlFunction(Expression arg, int function) {
        super(arg);
        this.function = function;
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value v = arg.getValue(session);
        if (v == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        int targetSessionId = v.getInt();
        session.getUser().checkAdmin();
        loop: for (SessionLocal s : session.getDatabase().getSessions(false)) {
            if (s.getId() == targetSessionId) {
                Command c = s.getCurrentCommand();
                switch (function) {
                case ABORT_SESSION:
                    if (c != null) {
                        c.cancel();
                    }
                    s.close();
                    return ValueBoolean.TRUE;
                case CANCEL_SESSION:
                    if (c != null) {
                        c.cancel();
                        return ValueBoolean.TRUE;
                    }
                    break loop;
                default:
                    throw DbException.getInternalError("function=" + function);
                }
            }
        }
        return ValueBoolean.FALSE;
    }

    @Override
    public Expression optimize(SessionLocal session) {
        arg = arg.optimize(session);
        type = TypeInfo.TYPE_BOOLEAN;
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
