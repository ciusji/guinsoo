/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.pagestore.db;

import java.util.ArrayList;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.query.AllColumnsForPlan;
import org.guinsoo.engine.Constants;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.index.Cursor;
import org.guinsoo.index.Index;
import org.guinsoo.index.IndexType;
import org.guinsoo.message.DbException;
import org.guinsoo.result.Row;
import org.guinsoo.result.SearchRow;
import org.guinsoo.result.SortOrder;
import org.guinsoo.table.Column;
import org.guinsoo.table.IndexColumn;
import org.guinsoo.table.TableFilter;
import org.guinsoo.util.Utils;

/**
 * The scan index is not really an 'index' in the strict sense, because it can
 * not be used for direct lookup. It can only be used to iterate over all rows
 * of a table. Each regular table has one such object, even if no primary key or
 * indexes are defined.
 */
public class ScanIndex extends Index {
    private long firstFree = -1;
    private ArrayList<Row> rows = Utils.newSmallArrayList();
    private final PageStoreTable tableData;
    private long rowCount;

    public ScanIndex(PageStoreTable table, int id, IndexColumn[] columns,
            IndexType indexType) {
        super(table, id, table.getName() + "_DATA", columns, indexType);
        tableData = table;
    }

    @Override
    public void remove(SessionLocal session) {
        truncate(session);
    }

    @Override
    public void truncate(SessionLocal session) {
        rows = Utils.newSmallArrayList();
        firstFree = -1;
        if (tableData.getContainsLargeObject() && tableData.isPersistData()) {
            database.getLobStorage().removeAllForTable(table.getId());
        }
        tableData.setRowCount(0);
        rowCount = 0;
    }

    @Override
    public String getCreateSQL() {
        return null;
    }

    @Override
    public void close(SessionLocal session) {
        // nothing to do
    }

    @Override
    public Row getRow(SessionLocal session, long key) {
        return rows.get((int) key);
    }

    @Override
    public void add(SessionLocal session, Row row) {
        // in-memory
        if (firstFree == -1) {
            int key = rows.size();
            row.setKey(key);
            rows.add(row);
        } else {
            long key = firstFree;
            Row free = rows.get((int) key);
            firstFree = free.getKey();
            row.setKey(key);
            rows.set((int) key, row);
        }
        rowCount++;
    }

    @Override
    public void remove(SessionLocal session, Row row) {
        // in-memory
        if (rowCount == 1) {
            rows = Utils.newSmallArrayList();
            firstFree = -1;
        } else {
            Row free = new PageStoreRow.RemovedRow(firstFree);
            long key = row.getKey();
            if (rows.size() <= key) {
                throw DbException.get(ErrorCode.ROW_NOT_FOUND_WHEN_DELETING_1,
                        rows.size() + ": " + key);
            }
            rows.set((int) key, free);
            firstFree = key;
        }
        rowCount--;
    }

    @Override
    public Cursor find(SessionLocal session, SearchRow first, SearchRow last) {
        return new ScanCursor(this);
    }

    @Override
    public double getCost(SessionLocal session, int[] masks,
            TableFilter[] filters, int filter, SortOrder sortOrder,
            AllColumnsForPlan allColumnsSet) {
        return tableData.getRowCountApproximation(session) + Constants.COST_ROW_OFFSET;
    }

    @Override
    public long getRowCount(SessionLocal session) {
        return rowCount;
    }

    /**
     * Get the next row that is stored after this row.
     *
     * @param row the current row or null to start the scan
     * @return the next row or null if there are no more rows
     */
    Row getNextRow(Row row) {
        long key;
        if (row == null) {
            key = -1;
        } else {
            key = row.getKey();
        }
        while (true) {
            key++;
            if (key >= rows.size()) {
                return null;
            }
            row = rows.get((int) key);
            if (row.getValueList() != null) {
                return row;
            }
        }
    }

    @Override
    public int getColumnIndex(Column col) {
        // the scan index cannot use any columns
        return -1;
    }

    @Override
    public boolean isFirstColumn(Column column) {
        return false;
    }

    @Override
    public void checkRename() {
        throw DbException.getUnsupportedException("SCAN");
    }

    @Override
    public boolean needRebuild() {
        return false;
    }

    @Override
    public long getRowCountApproximation(SessionLocal session) {
        return rowCount;
    }

    @Override
    public String getPlanSQL() {
        return table.getSQL(new StringBuilder(), TRACE_SQL_FLAGS).append(".tableScan").toString();
    }

}
