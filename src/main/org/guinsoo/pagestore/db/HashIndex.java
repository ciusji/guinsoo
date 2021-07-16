/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.pagestore.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.guinsoo.command.query.AllColumnsForPlan;
import org.guinsoo.engine.Mode;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.result.Row;
import org.guinsoo.result.SearchRow;
import org.guinsoo.result.SortOrder;
import org.guinsoo.table.Column;
import org.guinsoo.table.IndexColumn;
import org.guinsoo.table.TableFilter;
import org.guinsoo.value.DataType;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;
import org.guinsoo.index.Cursor;
import org.guinsoo.index.Index;
import org.guinsoo.index.IndexCondition;
import org.guinsoo.index.IndexType;
import org.guinsoo.index.SingleRowCursor;

/**
 * A unique index based on an in-memory hash map.
 */
public class HashIndex extends Index {

    /**
     * The index of the indexed column.
     */
    private final int indexColumn;
    private final boolean totalOrdering;
    private final PageStoreTable tableData;
    private Map<Value, Long> rows;
    private final ArrayList<Long> nullRows = new ArrayList<>();

    public HashIndex(PageStoreTable table, int id, String indexName, IndexColumn[] columns, IndexType indexType) {
        super(table, id, indexName, columns, indexType);
        Column column = columns[0].column;
        indexColumn = column.getColumnId();
        totalOrdering = DataType.hasTotalOrdering(column.getType().getValueType());
        this.tableData = table;
        reset();
    }

    private void reset() {
        rows = totalOrdering ? new HashMap<>() : new TreeMap<>(database.getCompareMode());
    }

    @Override
    public void truncate(SessionLocal session) {
        reset();
    }

    @Override
    public void add(SessionLocal session, Row row) {
        Value key = row.getValue(indexColumn);
        if (key != ValueNull.INSTANCE
                || database.getMode().uniqueIndexNullsHandling == Mode.UniqueIndexNullsHandling.FORBID_ANY_DUPLICATES) {
            Object old = rows.get(key);
            if (old != null) {
                // TODO index duplicate key for hash indexes: is this allowed?
                throw getDuplicateKeyException(key.toString());
            }
            rows.put(key, row.getKey());
        } else {
            nullRows.add(row.getKey());
        }
    }

    @Override
    public void remove(SessionLocal session, Row row) {
        Value key = row.getValue(indexColumn);
        if (key != ValueNull.INSTANCE
                || database.getMode().uniqueIndexNullsHandling == Mode.UniqueIndexNullsHandling.FORBID_ANY_DUPLICATES) {
            rows.remove(key);
        } else {
            nullRows.remove(row.getKey());
        }
    }

    @Override
    public Cursor find(SessionLocal session, SearchRow first, SearchRow last) {
        if (first == null || last == null) {
            // TODO hash index: should additionally check if values are the same
            throw DbException.getInternalError(first + " " + last);
        }
        Value v = first.getValue(indexColumn);
        if (v == ValueNull.INSTANCE
                && database.getMode().uniqueIndexNullsHandling != Mode.UniqueIndexNullsHandling.FORBID_ANY_DUPLICATES) {
            return new NonUniqueHashCursor(session, tableData, nullRows);
        }
        /*
         * Sometimes the incoming search is a similar, but not the same type
         * e.g. the search value is INT, but the index column is LONG. In which
         * case we need to convert, otherwise the HashMap will not find the
         * result.
         */
        v = v.convertTo(tableData.getColumn(indexColumn).getType(), session);
        Row result;
        Long pos = rows.get(v);
        if (pos == null) {
            result = null;
        } else {
            result = tableData.getRow(session, pos.intValue());
        }
        return new SingleRowCursor(result);
    }

    @Override
    public long getRowCount(SessionLocal session) {
        return getRowCountApproximation(session);
    }

    @Override
    public long getRowCountApproximation(SessionLocal session) {
        return rows.size() + nullRows.size();
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
