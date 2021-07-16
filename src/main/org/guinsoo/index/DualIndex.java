/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.index;

import org.guinsoo.command.query.AllColumnsForPlan;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.result.Row;
import org.guinsoo.result.SearchRow;
import org.guinsoo.result.SortOrder;
import org.guinsoo.table.DualTable;
import org.guinsoo.table.IndexColumn;
import org.guinsoo.table.TableFilter;
import org.guinsoo.value.Value;

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
