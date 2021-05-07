/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.pagestore.db;

import org.guinsoo.command.query.AllColumnsForPlan;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.index.Cursor;
import org.guinsoo.index.IndexType;
import org.guinsoo.message.DbException;
import org.guinsoo.pagestore.PageStore;
import org.guinsoo.result.Row;
import org.guinsoo.result.SearchRow;
import org.guinsoo.result.SortOrder;
import org.guinsoo.table.Column;
import org.guinsoo.table.IndexColumn;
import org.guinsoo.table.TableFilter;

/**
 * An index that delegates indexing to the page data index.
 */
public class PageDelegateIndex extends PageIndex {

    private final PageDataIndex mainIndex;

    public PageDelegateIndex(PageStoreTable table, int id, String name,
            IndexType indexType, PageDataIndex mainIndex, boolean create,
            SessionLocal session) {
        super(table, id, name,
                IndexColumn.wrap(new Column[] { table.getColumn(mainIndex.getMainIndexColumn()) }),
                indexType);
        this.mainIndex = mainIndex;
        if (!database.isPersistent() || id < 0) {
            throw DbException.getInternalError(name);
        }
        PageStore store = database.getPageStore();
        store.addIndex(this);
        if (create) {
            store.addMeta(this, session);
        }
    }

    @Override
    public void add(SessionLocal session, Row row) {
        // nothing to do
    }

    @Override
    public boolean canGetFirstOrLast() {
        return true;
    }

    @Override
    public void close(SessionLocal session) {
        // nothing to do
    }

    @Override
    public Cursor find(SessionLocal session, SearchRow first, SearchRow last) {
        long min = mainIndex.getKey(first, Long.MIN_VALUE, Long.MIN_VALUE);
        // ifNull is MIN_VALUE as well, because the column is never NULL
        // so avoid returning all rows (returning one row is OK)
        long max = mainIndex.getKey(last, Long.MAX_VALUE, Long.MIN_VALUE);
        return mainIndex.find(session, min, max);
    }

    @Override
    public Cursor findFirstOrLast(SessionLocal session, boolean first) {
        Cursor cursor;
        if (first) {
            cursor = mainIndex.find(session, Long.MIN_VALUE, Long.MAX_VALUE);
        } else  {
            long x = mainIndex.getLastKey();
            cursor = mainIndex.find(session, x, x);
        }
        cursor.next();
        return cursor;
    }

    @Override
    public int getColumnIndex(Column col) {
        if (col.getColumnId() == mainIndex.getMainIndexColumn()) {
            return 0;
        }
        return -1;
    }

    @Override
    public boolean isFirstColumn(Column column) {
        return getColumnIndex(column) == 0;
    }

    @Override
    public double getCost(SessionLocal session, int[] masks,
            TableFilter[] filters, int filter, SortOrder sortOrder,
            AllColumnsForPlan allColumnsSet) {
        return 10 * getCostRangeIndex(masks, mainIndex.getRowCount(session),
                filters, filter, sortOrder, false, allColumnsSet);
    }

    @Override
    public boolean needRebuild() {
        return false;
    }

    @Override
    public void remove(SessionLocal session, Row row) {
        // nothing to do
    }

    @Override
    public void update(SessionLocal session, Row oldRow, Row newRow) {
        // nothing to do
    }

    @Override
    public void remove(SessionLocal session) {
        mainIndex.setMainIndexColumn(-1);
        session.getDatabase().getPageStore().removeMeta(this, session);
    }

    @Override
    public void truncate(SessionLocal session) {
        // nothing to do
    }

    @Override
    public long getRowCount(SessionLocal session) {
        return mainIndex.getRowCount(session);
    }

    @Override
    public long getRowCountApproximation(SessionLocal session) {
        return mainIndex.getRowCountApproximation(session);
    }

    @Override
    public long getDiskSpaceUsed() {
        return mainIndex.getDiskSpaceUsed();
    }

    @Override
    public void writeRowCount() {
        // ignore
    }

}
