/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.command.dml;

import java.util.HashSet;

import org.gunsioo.api.Trigger;
import org.gunsioo.command.CommandInterface;
import org.gunsioo.command.query.AllColumnsForPlan;
import org.gunsioo.engine.DbObject;
import org.gunsioo.engine.Right;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.engine.UndoLogRecord;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.ExpressionVisitor;
import org.gunsioo.result.ResultTarget;
import org.gunsioo.result.Row;
import org.gunsioo.result.RowList;
import org.gunsioo.table.DataChangeDeltaTable.ResultOption;
import org.gunsioo.table.PlanItem;
import org.gunsioo.table.Table;
import org.gunsioo.table.TableFilter;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueNull;

/**
 * This class represents the statement
 * DELETE
 */
public final class Delete extends FilteredDataChangeStatement {

    public Delete(SessionLocal session) {
        super(session);
    }


    @Override
    public long update(ResultTarget deltaChangeCollector, ResultOption deltaChangeCollectionMode) {
        targetTableFilter.startQuery(session);
        targetTableFilter.reset();
        Table table = targetTableFilter.getTable();
        session.getUser().checkTableRight(table, Right.DELETE);
        table.fire(session, Trigger.DELETE, true);
        table.lock(session, true, false);
        long limitRows = -1;
        if (fetchExpr != null) {
            Value v = fetchExpr.getValue(session);
            if (v != ValueNull.INSTANCE) {
                limitRows = v.getLong();
            }
        }
        try (RowList rows = new RowList(session, table)) {
            setCurrentRowNumber(0);
            long count = 0;
            while (nextRow(limitRows, count)) {
                Row row = targetTableFilter.get();
                if (table.isMVStore()) {
                    Row lockedRow = table.lockRow(session, row);
                    if (lockedRow == null) {
                        continue;
                    }
                    if (!row.hasSharedData(lockedRow)) {
                        row = lockedRow;
                        targetTableFilter.set(row);
                        if (condition != null && !condition.getBooleanValue(session)) {
                            continue;
                        }
                    }
                }
                if (deltaChangeCollectionMode == ResultOption.OLD) {
                    deltaChangeCollector.addRow(row.getValueList());
                }
                if (!table.fireRow() || !table.fireBeforeRow(session, row, null)) {
                    rows.add(row);
                }
                count++;
            }
            long rowScanCount = 0;
            for (rows.reset(); rows.hasNext();) {
                if ((++rowScanCount & 127) == 0) {
                    checkCanceled();
                }
                Row row = rows.next();
                table.removeRow(session, row);
                session.log(table, UndoLogRecord.DELETE, row);
            }
            if (table.fireRow()) {
                for (rows.reset(); rows.hasNext();) {
                    Row row = rows.next();
                    table.fireAfterRow(session, row, null, false);
                }
            }
            table.fire(session, Trigger.DELETE, false);
            return count;
        }
    }

    @Override
    public String getPlanSQL(int sqlFlags) {
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        targetTableFilter.getPlanSQL(builder, false, sqlFlags);
        appendFilterCondition(builder, sqlFlags);
        return builder.toString();
    }

    @Override
    public void prepare() {
        if (condition != null) {
            condition.mapColumns(targetTableFilter, 0, Expression.MAP_INITIAL);
            condition = condition.optimizeCondition(session);
            if (condition != null) {
                condition.createIndexConditions(session, targetTableFilter);
            }
        }
        TableFilter[] filters = new TableFilter[] { targetTableFilter };
        PlanItem item = targetTableFilter.getBestPlanItem(session, filters, 0, new AllColumnsForPlan(filters));
        targetTableFilter.setPlanItem(item);
        targetTableFilter.prepare();
    }

    @Override
    public int getType() {
        return CommandInterface.DELETE;
    }

    @Override
    public String getStatementName() {
        return "DELETE";
    }

    @Override
    public void collectDependencies(HashSet<DbObject> dependencies) {
        ExpressionVisitor visitor = ExpressionVisitor.getDependenciesVisitor(dependencies);
        if (condition != null) {
            condition.isEverything(visitor);
        }
    }
}
