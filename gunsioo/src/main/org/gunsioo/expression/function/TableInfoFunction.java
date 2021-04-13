/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

import java.util.ArrayList;

import org.gunsioo.command.Parser;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.ExpressionVisitor;
import org.gunsioo.index.Index;
import org.gunsioo.message.DbException;
import org.gunsioo.mvstore.db.MVSpatialIndex;
import org.gunsioo.table.Column;
import org.gunsioo.table.Table;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueBigint;
import org.gunsioo.value.ValueNull;

/**
 * A table information function.
 */
public final class TableInfoFunction extends Function1_2 {

    /**
     * DISK_SPACE_USED() (non-standard).
     */
    public static final int DISK_SPACE_USED = 0;

    /**
     * ESTIMATED_ENVELOPE().
     */
    public static final int ESTIMATED_ENVELOPE = DISK_SPACE_USED + 1;

    private static final String[] NAMES = { //
            "DISK_SPACE_USED", "ESTIMATED_ENVELOPE" //
    };

    private final int function;

    public TableInfoFunction(Expression arg1, Expression arg2, int function) {
        super(arg1, arg2);
        this.function = function;
    }

    @Override
    public Value getValue(SessionLocal session, Value v1, Value v2) {
        Table table = new Parser(session).parseTableName(v1.getString());
        l: switch (function) {
        case DISK_SPACE_USED:
            v1 = ValueBigint.get(table.getDiskSpaceUsed());
            break;
        case ESTIMATED_ENVELOPE: {
            Column column = table.getColumn(v2.getString());
            ArrayList<Index> indexes = table.getIndexes();
            if (indexes != null) {
                for (int i = 1, size = indexes.size(); i < size; i++) {
                    Index index = indexes.get(i);
                    if (index instanceof MVSpatialIndex && index.isFirstColumn(column)) {
                        v1 = ((MVSpatialIndex) index).getEstimatedBounds(session);
                        break l;
                    }
                }
            }
            v1 = ValueNull.INSTANCE;
            break;
        }
        default:
            throw DbException.getInternalError("function=" + function);
        }
        return v1;
    }

    @Override
    public Expression optimize(SessionLocal session) {
        left = left.optimize(session);
        if (right != null) {
            right = right.optimize(session);
        }
        switch (function) {
        case DISK_SPACE_USED:
            type = TypeInfo.TYPE_BIGINT;
            break;
        case ESTIMATED_ENVELOPE:
            type = TypeInfo.TYPE_GEOMETRY;
            break;
        default:
            throw DbException.getInternalError("function=" + function);
        }
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
