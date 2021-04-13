/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression;

import java.util.ArrayList;
import java.util.HashMap;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.message.DbException;
import org.gunsioo.table.Column;
import org.gunsioo.table.ColumnResolver;
import org.gunsioo.table.TableFilter;
import org.gunsioo.util.StringUtils;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;

/**
 * A wildcard expression as in SELECT * FROM TEST.
 * This object is only used temporarily during the parsing phase, and later
 * replaced by column expressions.
 */
public final class Wildcard extends Expression {

    private final String schema;
    private final String table;

    private ArrayList<ExpressionColumn> exceptColumns;

    public Wildcard(String schema, String table) {
        this.schema = schema;
        this.table = table;
    }

    public ArrayList<ExpressionColumn> getExceptColumns() {
        return exceptColumns;
    }

    public void setExceptColumns(ArrayList<ExpressionColumn> exceptColumns) {
        this.exceptColumns = exceptColumns;
    }

    /**
     * Returns map of excluded table columns to expression columns and validates
     * that all columns are resolved and not duplicated.
     *
     * @return map of excluded table columns to expression columns
     */
    public HashMap<Column, ExpressionColumn> mapExceptColumns() {
        HashMap<Column, ExpressionColumn> exceptTableColumns = new HashMap<>();
        for (ExpressionColumn ec : exceptColumns) {
            Column column = ec.getColumn();
            if (column == null) {
                throw ec.getColumnException(ErrorCode.COLUMN_NOT_FOUND_1);
            }
            if (exceptTableColumns.putIfAbsent(column, ec) != null) {
                throw ec.getColumnException(ErrorCode.DUPLICATE_COLUMN_NAME_1);
            }
        }
        return exceptTableColumns;
    }

    @Override
    public Value getValue(SessionLocal session) {
        throw DbException.getInternalError(toString());
    }

    @Override
    public TypeInfo getType() {
        throw DbException.getInternalError(toString());
    }

    @Override
    public void mapColumns(ColumnResolver resolver, int level, int state) {
        if (exceptColumns != null) {
            for (ExpressionColumn column : exceptColumns) {
                column.mapColumns(resolver, level, state);
            }
        }
    }

    @Override
    public Expression optimize(SessionLocal session) {
        throw DbException.get(ErrorCode.SYNTAX_ERROR_1, table);
    }

    @Override
    public void setEvaluatable(TableFilter tableFilter, boolean b) {
        throw DbException.getInternalError(toString());
    }

    @Override
    public String getTableAlias() {
        return table;
    }

    @Override
    public String getSchemaName() {
        return schema;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        if (table != null) {
            StringUtils.quoteIdentifier(builder, table).append('.');
        }
        builder.append('*');
        if (exceptColumns != null) {
            writeExpressions(builder.append(" EXCEPT ("), exceptColumns, sqlFlags).append(')');
        }
        return builder;
    }

    @Override
    public void updateAggregate(SessionLocal session, int stage) {
        throw DbException.getInternalError(toString());
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        if (visitor.getType() == ExpressionVisitor.QUERY_COMPARABLE) {
            return true;
        }
        throw DbException.getInternalError(Integer.toString(visitor.getType()));
    }

    @Override
    public int getCost() {
        throw DbException.getInternalError(toString());
    }

}
