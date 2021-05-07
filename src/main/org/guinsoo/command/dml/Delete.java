/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.dml;

import java.util.HashSet;

import org.guinsoo.command.query.AllColumnsForPlan;
import org.guinsoo.api.Trigger;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.DbObject;
import org.guinsoo.engine.Right;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.engine.UndoLogRecord;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.result.ResultTarget;
import org.guinsoo.result.Row;
import org.guinsoo.result.RowList;
import org.guinsoo.table.DataChangeDeltaTable.ResultOption;
import org.guinsoo.table.PlanItem;
import org.guinsoo.table.Table;
import org.guinsoo.table.TableFilter;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;

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
