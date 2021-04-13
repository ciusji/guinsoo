/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.dml;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.command.Prepared;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.message.DbException;
import org.gunsioo.result.ResultInterface;

/**
 * This class represents the statement
 * EXECUTE IMMEDIATE.
 */
public class ExecuteImmediate extends Prepared {

    private Expression statement;

    public ExecuteImmediate(SessionLocal session, Expression statement) {
        super(session);
        this.statement = statement.optimize(session);
    }

    @Override
    public long update() {
        String sql = statement.getValue(session).getString();
        if (sql == null) {
            throw DbException.getInvalidValueException("SQL command", null);
        }
        Prepared command = session.prepare(sql);
        if (command.isQuery()) {
            throw DbException.get(ErrorCode.SYNTAX_ERROR_2, sql, "<not a query>");
        }
        return command.update();
    }

    @Override
    public boolean isTransactional() {
        return true;
    }

    @Override
    public int getType() {
        return CommandInterface.EXECUTE_IMMEDIATELY;
    }

    @Override
    public ResultInterface queryMeta() {
        return null;
    }

}
