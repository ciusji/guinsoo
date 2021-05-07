/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.mvstore.db;

import java.util.List;

import org.guinsoo.command.query.AllColumnsForPlan;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.index.Cursor;
import org.guinsoo.index.IndexType;
import org.guinsoo.message.DbException;
import org.guinsoo.mvstore.MVMap;
import org.guinsoo.result.Row;
import org.guinsoo.result.RowFactory;
import org.guinsoo.result.SearchRow;
import org.guinsoo.result.SortOrder;
import org.guinsoo.table.Column;
import org.guinsoo.table.IndexColumn;
import org.guinsoo.table.TableFilter;
import org.guinsoo.value.VersionedValue;

/**
 * An index that delegates indexing to another index.
 */
public class MVDelegateIndex extends MVIndex<Long, SearchRow> {

    private final MVPrimaryIndex mainIndex;

    public MVDelegateIndex(MVTable table, int id, String name,
            MVPrimaryIndex mainIndex,
            IndexType indexType) {
        super(table, id, name,
                IndexColumn.wrap(new Column[] { table.getColumn(mainIndex.getMainIndexColumn()) }),
                indexType);
        this.mainIndex = mainIndex;
        if (id < 0) {
            throw DbException.getInternalError(name);
        }
    }

    @Override
    public RowFactory getRowFactory() {
        return mainIndex.getRowFactory();
    }

    @Override
    public void addRowsToBuffer(List<Row> rows, String bufferName) {
        throw DbException.getInternalError();
    }

    @Override
    public void addBufferedRows(List<String> bufferNames) {
        throw DbException.getInternalError();
    }

    @Override
    public MVMap<Long,VersionedValue<SearchRow>> getMVMap() {
        return mainIndex.getMVMap();
    }

    @Override
    public void add(SessionLocal session, Row row) {
        // nothing to do
    }

    @Override
    public Row getRow(SessionLocal session, long key) {
        return mainIndex.getRow(session, key);
    }

    @Override
    public boolean isRowIdIndex() {
        return true;
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
        return mainIndex.find(session, first, last);
    }

    @Override
    public Cursor findFirstOrLast(SessionLocal session, boolean first) {
        return mainIndex.findFirstOrLast(session, first);
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
        return 10 * getCostRangeIndex(masks, mainIndex.getRowCountApproximation(session),
                filters, filter, sortOrder, true, allColumnsSet);
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
        mainIndex.setMainIndexColumn(SearchRow.ROWID_INDEX);
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

}
