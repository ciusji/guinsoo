/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.pagestore.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.gunsioo.command.query.AllColumnsForPlan;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.index.Cursor;
import org.gunsioo.index.Index;
import org.gunsioo.index.IndexCondition;
import org.gunsioo.index.IndexType;
import org.gunsioo.message.DbException;
import org.gunsioo.result.Row;
import org.gunsioo.result.SearchRow;
import org.gunsioo.result.SortOrder;
import org.gunsioo.table.Column;
import org.gunsioo.table.IndexColumn;
import org.gunsioo.table.TableFilter;
import org.gunsioo.util.Utils;
import org.gunsioo.value.DataType;
import org.gunsioo.value.Value;

/**
 * A non-unique index based on an in-memory hash map.
 *
 * @author Sergi Vladykin
 */
public class NonUniqueHashIndex extends Index {

    /**
     * The index of the indexed column.
     */
    private final int indexColumn;
    private final boolean totalOrdering;
    private Map<Value, ArrayList<Long>> rows;
    private final PageStoreTable tableData;
    private long rowCount;

    public NonUniqueHashIndex(PageStoreTable table, int id, String indexName,
            IndexColumn[] columns, IndexType indexType) {
        super(table, id, indexName, columns, indexType);
        Column column = columns[0].column;
        indexColumn = column.getColumnId();
        totalOrdering = DataType.hasTotalOrdering(column.getType().getValueType());
        tableData = table;
        reset();
    }

    private void reset() {
        rows = totalOrdering ? new HashMap<>() : new TreeMap<>(database.getCompareMode());
        rowCount = 0;
    }

    @Override
    public void truncate(SessionLocal session) {
        reset();
    }

    @Override
    public void add(SessionLocal session, Row row) {
        Value key = row.getValue(indexColumn);
        ArrayList<Long> positions = rows.get(key);
        if (positions == null) {
            positions = Utils.newSmallArrayList();
            rows.put(key, positions);
        }
        positions.add(row.getKey());
        rowCount++;
    }

    @Override
    public void remove(SessionLocal session, Row row) {
        if (rowCount == 1) {
            // last row in table
            reset();
        } else {
            Value key = row.getValue(indexColumn);
            ArrayList<Long> positions = rows.get(key);
            if (positions.size() == 1) {
                // last row with such key
                rows.remove(key);
            } else {
                positions.remove(row.getKey());
            }
            rowCount--;
        }
    }

    @Override
    public Cursor find(SessionLocal session, SearchRow first, SearchRow last) {
        if (first == null || last == null) {
            throw DbException.getInternalError(first + " " + last);
        }
        if (first != last) {
            if (TreeIndex.compareKeys(first, last) != 0) {
                throw DbException.getInternalError();
            }
        }
        Value v = first.getValue(indexColumn);
        /*
         * Sometimes the incoming search is a similar, but not the same type
         * e.g. the search value is INT, but the index column is LONG. In which
         * case we need to convert, otherwise the HashMap will not find the
         * result.
         */
        v = v.convertTo(tableData.getColumn(indexColumn).getType(), session);
        ArrayList<Long> positions = rows.get(v);
        return new NonUniqueHashCursor(session, tableData, positions);
    }

    @Override
    public long getRowCount(SessionLocal session) {
        return rowCount;
    }

    @Override
    public long getRowCountApproximation(SessionLocal session) {
        return rowCount;
    }

    @Override
    public void close(SessionLocal session) {
        // nothing to do
    }

    @Override
    public void remove(SessionLocal session) {
        // nothing to do
    }

    @Override
    public double getCost(SessionLocal session, int[] masks,
            TableFilter[] filters, int filter, SortOrder sortOrder,
            AllColumnsForPlan allColumnsSet) {
        for (Column column : columns) {
            int index = column.getColumnId();
            int mask = masks[index];
            if ((mask & IndexCondition.EQUALITY) != IndexCondition.EQUALITY) {
                return Long.MAX_VALUE;
            }
        }
        return 2;
    }

    @Override
    public boolean needRebuild() {
        return true;
    }

    @Override
    public boolean canScan() {
        return false;
    }

}
