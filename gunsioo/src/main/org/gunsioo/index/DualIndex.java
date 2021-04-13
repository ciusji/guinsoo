/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.index;

import org.gunsioo.command.query.AllColumnsForPlan;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.result.Row;
import org.gunsioo.result.SearchRow;
import org.gunsioo.result.SortOrder;
import org.gunsioo.table.DualTable;
import org.gunsioo.table.IndexColumn;
import org.gunsioo.table.TableFilter;
import org.gunsioo.value.Value;

/**
 * An index for the DUAL table.
 */
public class DualIndex extends VirtualTableIndex {

    public DualIndex(DualTable table) {
        super(table, "DUAL_INDEX", new IndexColumn[0]);
    }

    @Override
    public Cursor find(SessionLocal session, SearchRow first, SearchRow last) {
        return new DualCursor();
    }

    @Override
    public double getCost(SessionLocal session, int[] masks, TableFilter[] filters, int filter, SortOrder sortOrder,
            AllColumnsForPlan allColumnsSet) {
        return 1d;
    }

    @Override
    public String getCreateSQL() {
        return null;
    }

    @Override
    public boolean canGetFirstOrLast() {
        return true;
    }

    @Override
    public Cursor findFirstOrLast(SessionLocal session, boolean first) {
        return new SingleRowCursor(Row.get(Value.EMPTY_VALUES, 1));
    }

    @Override
    public String getPlanSQL() {
        return "dual index";
    }

}
