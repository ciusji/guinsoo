/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.dml;

import java.util.ArrayList;

import org.gunsioo.command.CommandInterface;
import org.gunsioo.command.Prepared;
import org.gunsioo.engine.Procedure;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.Parameter;
import org.gunsioo.result.ResultInterface;
import org.gunsioo.util.Utils;

/**
 * This class represents the statement
 * EXECUTE
 */
public class ExecuteProcedure extends Prepared {

    private final ArrayList<Expression> expressions = Utils.newSmallArrayList();
    private Procedure procedure;

    public ExecuteProcedure(SessionLocal session) {
        super(session);
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    /**
     * Set the expression at the given index.
     *
     * @param index the index (0 based)
     * @param expr the expression
     */
    public void setExpression(int index, Expression expr) {
        expressions.add(index, expr);
    }

    private void setParameters() {
        Prepared prepared = procedure.getPrepared();
        ArrayList<Parameter> params = prepared.getParameters();
        for (int i = 0; params != null && i < params.size() &&
                i < expressions.size(); i++) {
            Expression expr = expressions.get(i);
            Parameter p = params.get(i);
            p.setValue(expr.getValue(session));
        }
    }

    @Override
    public boolean isQuery() {
        Prepared prepared = procedure.getPrepared();
        return prepared.isQuery();
    }

    @Override
    public long update() {
        setParameters();
        Prepared prepared = procedure.getPrepared();
        return prepared.update();
    }

    @Override
    public ResultInterface query(long limit) {
        setParameters();
        Prepared prepared = procedure.getPrepared();
        return prepared.query(limit);
    }

    @Override
    public boolean isTransactional() {
        return true;
    }

    @Override
    public ResultInterface queryMeta() {
        Prepared prepared = procedure.getPrepared();
        return prepared.queryMeta();
    }

    @Override
    public int getType() {
        return CommandInterface.EXECUTE;
    }

}
