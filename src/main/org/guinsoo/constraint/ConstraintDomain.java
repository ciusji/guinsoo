/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.constraint;

import java.util.HashSet;

import org.guinsoo.command.ddl.AlterDomain;
import org.guinsoo.command.query.AllColumnsForPlan;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.Parser;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.index.Index;
import org.guinsoo.message.DbException;
import org.guinsoo.result.Row;
import org.guinsoo.schema.Domain;
import org.guinsoo.schema.Schema;
import org.guinsoo.table.Column;
import org.guinsoo.table.PlanItem;
import org.guinsoo.table.Table;
import org.guinsoo.table.TableFilter;
import org.guinsoo.util.StringUtils;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;

/**
 * A domain constraint.
 */
public class ConstraintDomain extends Constraint {

    private Domain domain;

    private Expression expr;

    private DomainColumnResolver resolver;

    public ConstraintDomain(Schema schema, int id, String name, Domain domain) {
        super(schema, id, name, null);
        this.domain = domain;
        resolver = new DomainColumnResolver(domain.getDataType());
    }

    @Override
    public Type getConstraintType() {
        return Constraint.Type.DOMAIN;
    }

    /**
     * Returns the domain of this constraint.
     *
     * @return the domain
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Set the expression.
     *
     * @param session the session
     * @param expr the expression
     */
    public void setExpression(SessionLocal session, Expression expr) {
        expr.mapColumns(resolver, 0, Expression.MAP_INITIAL);
        expr = expr.optimize(session);
        // check if the column is mapped
        synchronized (this) {
            resolver.setValue(ValueNull.INSTANCE);
            expr.getValue(session);
        }
        this.expr = expr;
    }

    @Override
    public String getCreateSQLForCopy(Table forTable, String quotedName) {
        throw DbException.getInternalError(toString());
    }

    @Override
    public String getCreateSQLWithoutIndexes() {
        return getCreateSQL();
    }

    @Override
    public String getCreateSQL() {
        StringBuilder builder = new StringBuilder("ALTER DOMAIN ");
        domain.getSQL(builder, DEFAULT_SQL_FLAGS).append(" ADD CONSTRAINT ");
        getSQL(builder, DEFAULT_SQL_FLAGS);
        if (comment != null) {
            builder.append(" COMMENT ");
            StringUtils.quoteStringSQL(builder, comment);
        }
        builder.append(" CHECK");
        expr.getEnclosedSQL(builder, DEFAULT_SQL_FLAGS).append(" NOCHECK");
        return builder.toString();
    }

    @Override
    public void removeChildrenAndResources(SessionLocal session) {
        domain.removeConstraint(this);
        database.removeMeta(session, getId());
        domain = null;
        expr = null;
        invalidate();
    }

    @Override
    public void checkRow(SessionLocal session, Table t, Row oldRow, Row newRow) {
        throw DbException.getInternalError(toString());
    }

    /**
     * Check the specified value.
     *
     * @param session
     *            the session
     * @param value
     *            the value to check
     */
    public void check(SessionLocal session, Value value) {
        Value v;
        synchronized (this) {
            resolver.setValue(value);
            v = expr.getValue(session);
        }
        // Both TRUE and NULL are OK
        if (v != ValueNull.INSTANCE && !v.getBoolean()) {
            throw DbException.get(ErrorCode.CHECK_CONSTRAINT_VIOLATED_1, expr.getTraceSQL());
        }
    }

    /**
     * Get the check constraint expression for this column.
     *
     * @param session the session
     * @param columnName the column name
     * @return the expression
     */
    public Expression getCheckConstraint(SessionLocal session, String columnName) {
        String sql;
        if (columnName != null) {
            synchronized (this) {
                try {
                    resolver.setColumnName(columnName);
                    sql = expr.getSQL(DEFAULT_SQL_FLAGS);
                } finally {
                    resolver.resetColumnName();
                }
            }
            return new Parser(session).parseExpression(sql);
        } else {
            synchronized (this) {
                sql = expr.getSQL(DEFAULT_SQL_FLAGS);
            }
            return new Parser(session).parseDomainConstraintExpression(sql);
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
        new CheckExistingData(session, domain);
    }

    @Override
    public void rebuild() {
        // nothing to do
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        return expr.isEverything(visitor);
    }

    private class CheckExistingData {

        private final SessionLocal session;

        CheckExistingData(SessionLocal session, Domain domain) {
            this.session = session;
            checkDomain(null, domain);
        }

        private boolean checkColumn(Domain domain, Column targetColumn) {
            Table table = targetColumn.getTable();
            TableFilter filter = new TableFilter(session, table, null, true, null, 0, null);
            TableFilter[] filters = { filter };
            PlanItem item = filter.getBestPlanItem(session, filters, 0, new AllColumnsForPlan(filters));
            filter.setPlanItem(item);
            filter.prepare();
            filter.startQuery(session);
            filter.reset();
            while (filter.next()) {
                check(session, filter.getValue(targetColumn));
            }
            return false;
        }

        private boolean checkDomain(Domain domain, Domain targetDomain) {
            AlterDomain.forAllDependencies(session, targetDomain, this::checkColumn, this::checkDomain, false);
            return false;
        }

    }

}
