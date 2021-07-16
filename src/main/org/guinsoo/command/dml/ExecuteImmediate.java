/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.dml;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.command.Prepared;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.message.DbException;
import org.guinsoo.result.ResultInterface;

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
