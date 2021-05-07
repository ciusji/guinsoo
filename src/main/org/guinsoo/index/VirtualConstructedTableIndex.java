/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.index;

import org.guinsoo.command.query.AllColumnsForPlan;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.result.SearchRow;
import org.guinsoo.result.SortOrder;
import org.guinsoo.table.FunctionTable;
import org.guinsoo.table.IndexColumn;
import org.guinsoo.table.TableFilter;
import org.guinsoo.table.VirtualConstructedTable;

/**
 * An index for a virtual table that returns a result set. Search in this index
 * performs scan over all rows and should be avoided.
 */
public class VirtualConstructedTableIndex extends VirtualTableIndex {

    private final VirtualConstructedTable table;

    public VirtualConstructedTableIndex(VirtualConstructedTable table, IndexColumn[] columns) {
        super(table, null, columns);
        this.table = table;
    }

    @Override
    public boolean isFindUsingFullTableScan() {
        return true;
    }

    @Override
    public Cursor find(SessionLocal session, SearchRow first, SearchRow last) {
        return new VirtualTableCursor(this, first, last, session, table.getResult(session));
    }

    @Override
    public double getCost(SessionLocal session, int[] masks, TableFilter[] filters, int filter, SortOrder sortOrder,
                          AllColumnsForPlan allColumnsSet) {
        if (masks != null) {
            throw DbException.getUnsupportedException("Virtual table");
        }
        long expectedRows;
        if (table.canGetRowCount(session)) {
            expectedRows = table.getRowCountApproximation(session);
        } else {
            expectedRows = database.getSettings().estimatedFunctionTableRows;
        }
        return expectedRows * 10;
    }

    @Override
    public String getPlanSQL() {
        return table instanceof FunctionTable ? "function" : "table scan";
    }

    @Override
    public boolean canScan() {
        return false;
    }

}
