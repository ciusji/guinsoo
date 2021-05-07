/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import org.guinsoo.command.Prepared;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueBigint;

/**
 * Represents the ROWNUM function.
 */
public final class Rownum extends Operation0 {

    private final Prepared prepared;

    public Rownum(Prepared prepared) {
        if (prepared == null) {
            throw DbException.getInternalError();
        }
        this.prepared = prepared;
    }

    @Override
    public Value getValue(SessionLocal session) {
        return ValueBigint.get(prepared.getCurrentRowNumber());
    }

    @Override
    public TypeInfo getType() {
        return TypeInfo.TYPE_BIGINT;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return builder.append("ROWNUM()");
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        switch (visitor.getType()) {
        case ExpressionVisitor.QUERY_COMPARABLE:
        case ExpressionVisitor.OPTIMIZABLE_AGGREGATE:
        case ExpressionVisitor.DETERMINISTIC:
        case ExpressionVisitor.INDEPENDENT:
            return false;
        case ExpressionVisitor.EVALUATABLE:
        case ExpressionVisitor.READONLY:
        case ExpressionVisitor.NOT_FROM_RESOLVER:
        case ExpressionVisitor.GET_DEPENDENCIES:
        case ExpressionVisitor.SET_MAX_DATA_MODIFICATION_ID:
        case ExpressionVisitor.GET_COLUMNS1:
        case ExpressionVisitor.GET_COLUMNS2:
            // if everything else is the same, the rownum is the same
            return true;
        default:
            throw DbException.getInternalError("type="+visitor.getType());
        }
    }

    @Override
    public int getCost() {
        return 0;
    }

}
