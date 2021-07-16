/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.mode;

import org.guinsoo.command.dml.Update;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.expression.Operation0;
import org.guinsoo.message.DbException;
import org.guinsoo.table.Column;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;

/**
 * VALUES(column) function for ON DUPLICATE KEY UPDATE clause.
 */
public final class OnDuplicateKeyValues extends Operation0 {

    private final Column column;

    private final Update update;

    public OnDuplicateKeyValues(Column column, Update update) {
        this.column = column;
        this.update = update;
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value v = update.getOnDuplicateKeyInsert().getOnDuplicateKeyValue(column.getColumnId());
        if (v == null) {
            throw DbException.getUnsupportedException(getTraceSQL());
        }
        return v;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return column.getSQL(builder.append("VALUES("), sqlFlags).append(')');
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
        return column.getType();
    }

    @Override
    public int getCost() {
        return 1;
    }

}
