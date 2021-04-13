/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.constraint;

import java.util.HashSet;
import org.gunsioo.api.ErrorCode;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.ExpressionVisitor;
import org.gunsioo.index.Index;
import org.gunsioo.message.DbException;
import org.gunsioo.result.ResultInterface;
import org.gunsioo.result.Row;
import org.gunsioo.schema.Schema;
import org.gunsioo.table.Column;
import org.gunsioo.table.Table;
import org.gunsioo.table.TableFilter;
import org.gunsioo.util.StringUtils;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueNull;

/**
 * A check constraint.
 */
public class ConstraintCheck extends Constraint {

    private TableFilter filter;
    private Expression expr;

    public ConstraintCheck(Schema schema, int id, String name, Table table) {
        super(schema, id, name, table);
    }

    @Override
    public Type getConstraintType() {
        return Constraint.Type.CHECK;
    }

    public void setTableFilter(TableFilter filter) {
        this.filter = filter;
    }

    public void setExpression(Expression expr) {
        this.expr = expr;
    }

    @Override
    public String getCreateSQLForCopy(Table forTable, String quotedName) {
        StringBuilder buff = new StringBuilder("ALTER TABLE ");
        forTable.getSQL(buff, DEFAULT_SQL_FLAGS).append(" ADD CONSTRAINT ");
        if (forTable.isHidden()) {
            buff.append("IF NOT EXISTS ");
        }
        buff.append(quotedName);
        if (comment != null) {
            buff.append(" COMMENT ");
            StringUtils.quoteStringSQL(buff, comment);
        }
        buff.append(" CHECK");
        expr.getEnclosedSQL(buff, DEFAULT_SQL_FLAGS).append(" NOCHECK");
        return buff.toString();
    }

    private String getShortDescription() {
        StringBuilder builder = new StringBuilder().append(getName()).append(": ");
        expr.getTraceSQL();
        return builder.toString();
    }

    @Override
    public String  getCreateSQLWithoutIndexes() {
        return getCreateSQL();
    }

    @Override
    public String getCreateSQL() {
        return getCreateSQLForCopy(table, getSQL(DEFAULT_SQL_FLAGS));
    }

    @Override
    public void removeChildrenAndResources(SessionLocal session) {
        table.removeConstraint(this);
        database.removeMeta(session, getId());
        filter = null;
        expr = null;
        table = null;
        invalidate();
    }

    @Override
    public void checkRow(SessionLocal session, Table t, Row oldRow, Row newRow) {
        if (newRow == null) {
            return;
        }
        boolean b;
        try {
            Value v;
            synchronized (this) {
                filter.set(newRow);
                v = expr.getValue(session);
            }
            // Both TRUE and NULL are ok
            b = v == ValueNull.INSTANCE || v.getBoolean();
        } catch (DbException ex) {
            throw DbException.get(ErrorCode.CHECK_CONSTRAINT_INVALID, ex,
                    getShortDescription());
        }
        if (!b) {
            throw DbException.get(ErrorCode.CHECK_CONSTRAINT_VIOLATED_1,
                    getShortDescription());
        }
    }

    @Override
    public boolean usesIndex(Index index) {
        return false;
    }

    @Override
    public void setIndexOwner(Index index) {
        throw DbException.getInternalError(toString());
    }

    @Override
    public HashSet<Column> getReferencedColumns(Table table) {
        HashSet<Column> columns = new HashSet<>();
        expr.isEverything(ExpressionVisitor.getColumnsVisitor(columns, table));
        return columns;
    }

    @Override
    public Expression getExpression() {
        return expr;
    }

    @Override
    public boolean isBefore() {
        return true;
    }

    @Override
    public void checkExistingData(SessionLocal session) {
        if (session.getDatabase().isStarting()) {
            // don't check at startup
            return;
        }
        StringBuilder builder = new StringBuilder().append("SELECT NULL FROM ");
        filter.getTable().getSQL(builder, DEFAULT_SQL_FLAGS).append(" WHERE NOT ");
        expr.getSQL(builder, DEFAULT_SQL_FLAGS, Expression.AUTO_PARENTHESES);
        String sql = builder.toString();
        ResultInterface r = session.prepare(sql).query(1);
        if (r.next()) {
            throw DbException.get(ErrorCode.CHECK_CONSTRAINT_VIOLATED_1, getName());
        }
    }

    @Override
    public void rebuild() {
        // nothing to do
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        return expr.isEverything(visitor);
    }

}
