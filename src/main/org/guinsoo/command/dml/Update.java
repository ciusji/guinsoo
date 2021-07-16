/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.dml;

import java.util.HashSet;

import org.guinsoo.command.query.AllColumnsForPlan;
import org.guinsoo.api.Trigger;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.command.Prepared;
import org.guinsoo.engine.DbObject;
import org.guinsoo.engine.Right;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.result.ResultTarget;
import org.guinsoo.result.Row;
import org.guinsoo.result.RowList;
import org.guinsoo.table.PlanItem;
import org.guinsoo.table.Table;
import org.guinsoo.table.TableFilter;
import org.guinsoo.table.DataChangeDeltaTable.ResultOption;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;

/**
 * This class represents the statement
 * UPDATE
 */
public final class Update extends FilteredDataChangeStatement {

    private SetClauseList setClauseList;

    private Insert onDuplicateKeyInsert;

    private TableFilter fromTableFilter;

    public Update(SessionLocal session) {
        super(session);
    }

    public void setSetClauseList(SetClauseList setClauseList) {
        this.setClauseList = setClauseList;
    }

    public void setFromTableFilter(TableFilter tableFilter) {
        this.fromTableFilter = tableFilter;
    }

    @Override
    public long update(ResultTarget deltaChangeCollector, ResultOption deltaChangeCollectionMode) {
        targetTableFilter.startQuery(session);
        targetTableFilter.reset();
        Table table = targetTableFilter.getTable();
        try (RowList rows = new RowList(session, table)) {
            session.getUser().checkTableRight(table, Right.UPDATE);
            table.fire(session, Trigger.UPDATE, true);
            table.lock(session, true, false);
            // get the old rows, compute the new rows
            setCurrentRowNumber(0);
            long count = 0;
            long limitRows = -1;
            if (fetchExpr != null) {
                Value v = fetchExpr.getValue(session);
                if (v != ValueNull.INSTANCE) {
                    limitRows = v.getLong();
                }
            }
            while (nextRow(limitRows, count)) {
                Row oldRow = targetTableFilter.get();
                if (table.isMVStore()) {
                    Row lockedRow = table.lockRow(session, oldRow);
                    if (lockedRow == null) {
                        continue;
                    }
                    if (!oldRow.hasSharedData(lockedRow)) {
                        oldRow = lockedRow;
                        targetTableFilter.set(oldRow);
                        if (condition != null && !condition.getBooleanValue(session)) {
                            continue;
                        }
                    }
                }
                if (setClauseList.prepareUpdate(table, session, deltaChangeCollector, deltaChangeCollectionMode,
                        rows, oldRow, onDuplicateKeyInsert != null)) {
                    count++;
                }
            }
            doUpdate(this, session, table, rows);
            table.fire(session, Trigger.UPDATE, false);
            return count;
        }
    }

    static void doUpdate(Prepared prepared, SessionLocal session, Table table, RowList rows) {
        // TODO self referencing referential integrity constraints
        // don't work if update is multi-row and 'inversed' the condition!
        // probably need multi-row triggers with 'deleted' and 'inserted'
        // at the same time. anyway good for sql compatibility
        // TODO update in-place (but if the key changes,
        // we need to update all indexes) before row triggers

        // the cached row is already updated - we need the old values
        table.updateRows(prepared, session, rows);
        if (table.fireRow()) {
            for (rows.reset(); rows.hasNext();) {
                Row o = rows.next();
                Row n = rows.next();
                table.fireAfterRow(session, o, n, false);
            }
        }
    }

    @Override
    public String getPlanSQL(int sqlFlags) {
        StringBuilder builder = new StringBuilder("UPDATE ");
        targetTableFilter.getPlanSQL(builder, false, sqlFlags);
        if (fromTableFilter != null) {
            builder.append("\nFROM ");
            fromTableFilter.getPlanSQL(builder, false, sqlFlags);
        }
        setClauseList.getSQL(builder, sqlFlags);
        appendFilterCondition(builder, sqlFlags);
        return builder.toString();
    }

    @Override
    public void prepare() {
        if (fromTableFilter != null) {
            targetTableFilter.addJoin(fromTableFilter, false, null);
        }
        if (condition != null) {
            condition.mapColumns(targetTableFilter, 0, Expression.MAP_INITIAL);
            if (fromTableFilter != null) {
                condition.mapColumns(fromTableFilter, 0, Expression.MAP_INITIAL);
            }
            condition = condition.optimizeCondition(session);
            if (condition != null) {
                condition.createIndexConditions(session, targetTableFilter);
            }
        }
        setClauseList.mapAndOptimize(session, targetTableFilter, fromTableFilter);
        TableFilter[] filters = null;
        if (fromTableFilter == null) {
            filters = new TableFilter[] { targetTableFilter };
        } else {
            filters = new TableFilter[] { targetTableFilter, fromTableFilter };
        }
        PlanItem item = targetTableFilter.getBestPlanItem(session, filters, 0, new AllColumnsForPlan(filters));
        targetTableFilter.setPlanItem(item);
        targetTableFilter.prepare();
    }

    @Override
    public int getType() {
        return CommandInterface.UPDATE;
    }

    @Override
    public String getStatementName() {
        return "UPDATE";
    }

    @Override
    public void collectDependencies(HashSet<DbObject> dependencies) {
        ExpressionVisitor visitor = ExpressionVisitor.getDependenciesVisitor(dependencies);
        if (condition != null) {
            condition.isEverything(visitor);
        }
        setClauseList.isEverything(visitor);
    }

    public Insert getOnDuplicateKeyInsert() {
        return onDuplicateKeyInsert;
    }

    void setOnDuplicateKeyInsert(Insert onDuplicateKeyInsert) {
        this.onDuplicateKeyInsert = onDuplicateKeyInsert;
    }

}
