/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.dml;

import java.util.ArrayList;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.util.Utils;

/**
 * Command that supports VALUES clause.
 */
public abstract class CommandWithValues extends DataChangeStatement {

    /**
     * Expression data for the VALUES clause.
     */
    protected final ArrayList<Expression[]> valuesExpressionList = Utils.newSmallArrayList();

    /**
     * Creates new instance of command with VALUES clause.
     *
     * @param session
     *            the session
     */
    protected CommandWithValues(SessionLocal session) {
        super(session);
    }

    /**
     * Add a row to this command.
     *
     * @param expr
     *            the list of values
     */
    public void addRow(Expression[] expr) {
        valuesExpressionList.add(expr);
    }

}
