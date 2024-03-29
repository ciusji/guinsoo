/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.index;

import java.util.ArrayList;

import org.guinsoo.command.query.AllColumnsForPlan;
import org.guinsoo.engine.Constants;
import org.guinsoo.engine.DbObject;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.message.Trace;
import org.guinsoo.result.Row;
import org.guinsoo.result.RowFactory;
import org.guinsoo.result.SearchRow;
import org.guinsoo.result.SortOrder;
import org.guinsoo.schema.SchemaObject;
import org.guinsoo.table.Column;
import org.guinsoo.table.IndexColumn;
import org.guinsoo.table.Table;
import org.guinsoo.table.TableFilter;
import org.guinsoo.value.DataType;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.util.StringUtils;

/**
 * An index. Indexes are used to speed up searching data.
 */
public abstract class Index extends SchemaObject {

    /**
     * Check that the index columns are not CLOB or BLOB.
     *
     * @param columns the columns
     */
    protected static void checkIndexColumnTypes(IndexColumn[] columns) {
        for (IndexColumn c : columns) {
            if (!DataType.isIndexable(c.column.getType())) {
                throw DbException.getUnsupportedException("Index on column: " + c.column.getCreateSQL());
            }
        }
    }

    /**
     * Columns of this index.
     */
    protected IndexColumn[] indexColumns;

    /**
     * Table columns used in this index.
     */
    protected Column[] columns;

    /**
     * Identities of table columns.
     */
    protected int[] columnIds;

    /**
     * The table.
     */
    protected final Table table;

    /**
     * The index type.
     */
    protected final IndexType indexType;

    private final RowFactory rowFactory;

    /**
     * Initialize the index.
     *
     * @param newTable the table
     * @param id the object id
     * @param name the index name
     * @param newIndexColumns the columns that are indexed or null if this is
     *            not yet known
     * @param newIndexType the index type
     */
    protected Index(Table newTable, int id, String name, IndexColumn[] newIndexColumns, IndexType newIndexType) {
        super(newTable.getSchema(), id, name, Trace.INDEX);
        this.indexType = newIndexType;
        this.table = newTable;
        if (newIndexColumns != null) {
            this.indexColumns = newIndexColumns;
            columns = new Column[newIndexColumns.length];
            int len = columns.length;
            columnIds = new int[len];
            for (int i = 0; i < len; i++) {
                Column col = newIndexColumns[i].column;
                columns[i] = col;
                columnIds[i] = col.getColumnId();
            }
        }
        rowFactory = database.getRowFactory().createRowFactory(
                database, database.getCompareMode(), database.getMode(),
                database, table.getColumns(),
                newIndexType.isScan() ? null : newIndexColumns);
    }

    @Override
    public final int getType() {
        return DbObject.INDEX;
    }

    @Override
    public void removeChildrenAndResources(SessionLocal session) {
        table.removeIndex(this);
        remove(session);
        database.removeMeta(session, getId());
    }

    @Override
    public final boolean isHidden() {
        return table.isHidden();
    }

    @Override
    public String getCreateSQLForCopy(Table targetTable, String quotedName) {
        StringBuilder buff = new StringBuilder("CREATE ");
        buff.append(indexType.getSQL());
        buff.append(' ');
        if (table.isHidden()) {
            buff.append("IF NOT EXISTS ");
        }
        buff.append(quotedName);
        buff.append(" ON ");
        targetTable.getSQL(buff, DEFAULT_SQL_FLAGS);
        if (comment != null) {
            buff.append(" COMMENT ");
            StringUtils.quoteStringSQL(buff, comment);
        }
        buff.append('(').append(getColumnListSQL(DEFAULT_SQL_FLAGS)).append(')');
        return buff.toString();
    }

    /**
     * Get the list of columns as a string.
     *
     * @param sqlFlags formatting flags
     * @return the list of columns
     */
    private String getColumnListSQL(int sqlFlags) {
        return IndexColumn.writeColumns(new StringBuilder(), indexColumns, sqlFlags).toString();
    }

    @Override
    public String getCreateSQL() {
        return getCreateSQLForCopy(table, getSQL(DEFAULT_SQL_FLAGS));
    }

    /**
     * Get the message to show in a EXPLAIN statement.
     *
     * @return the plan
     */
    public String getPlanSQL() {
        return getSQL(TRACE_SQL_FLAGS | ADD_PLAN_INFORMATION);
    }

    /**
     * Close this index.
     *
     * @param session the session used to write data
     */
    public abstract void close(SessionLocal session);

    /**
     * Add a row to the index.
     *
     * @param session the session to use
     * @param row the row to add
     */
    public abstract void add(SessionLocal session, Row row);

    /**
     * Remove a row from the index.
     *
     * @param session the session
     * @param row the row
     */
    public abstract void remove(SessionLocal session, Row row);

    /**
     * Update index after row change.
     *
     * @param session the session
     * @param oldRow row before the update
     * @param newRow row after the update
     */
    public void update(SessionLocal session, Row oldRow, Row newRow) {
        remove(session, oldRow);
        add(session, newRow);
    }

    /**
     * Returns {@code true} if {@code find()} implementation performs scan over all
     * index, {@code false} if {@code find()} performs the fast lookup.
     *
     * @return {@code true} if {@code find()} implementation performs scan over all
     *         index, {@code false} if {@code find()} performs the fast lookup
     */
    public boolean isFindUsingFullTableScan() {
        return false;
    }

    /**
     * Find a row or a list of rows and create a cursor to iterate over the
     * result.
     *
     * @param session the session
     * @param first the first row, or null for no limit
     * @param last the last row, or null for no limit
     * @return the cursor to iterate over the results
     */
    public abstract Cursor find(SessionLocal session, SearchRow first, SearchRow last);

    /**
     * Estimate the cost to search for rows given the search mask.
     * There is one element per column in the search mask.
     * For possible search masks, see IndexCondition.
     *
     * @param session the session
     * @param masks per-column comparison bit masks, null means 'always false',
     *              see constants in IndexCondition
     * @param filters all joined table filters
     * @param filter the current table filter index
     * @param sortOrder the sort order
     * @param allColumnsSet the set of all columns
     * @return the estimated cost
     */
    public abstract double getCost(SessionLocal session, int[] masks, TableFilter[] filters, int filter,
                                   SortOrder sortOrder, AllColumnsForPlan allColumnsSet);

    /**
     * Remove the index.
     *
     * @param session the session
     */
    public abstract void remove(SessionLocal session);

    /**
     * Remove all rows from the index.
     *
     * @param session the session
     */
    public abstract void truncate(SessionLocal session);

    /**
     * Check if the index can directly look up the lowest or highest value of a
     * column.
     *
     * @return true if it can
     */
    public boolean canGetFirstOrLast() {
        return false;
    }

    /**
     * Check if the index can get the next higher value.
     *
     * @return true if it can
     */
    public boolean canFindNext() {
        return false;
    }

    /**
     * Find a row or a list of rows that is larger and create a cursor to
     * iterate over the result.
     *
     * @param session the session
     * @param higherThan the lower limit (excluding)
     * @param last the last row, or null for no limit
     * @return the cursor
     */
    public Cursor findNext(SessionLocal session, SearchRow higherThan, SearchRow last) {
        throw DbException.getInternalError(toString());
    }

    /**
     * Find the first (or last) value of this index. The cursor returned is
     * positioned on the correct row, or on null if no row has been found.
     *
     * @param session the session
     * @param first true if the first (lowest for ascending indexes) or last
     *            value should be returned
     * @return a cursor (never null)
     */
    public Cursor findFirstOrLast(SessionLocal session, boolean first) {
        throw DbException.getInternalError(toString());
    }

    /**
     * Check if the index needs to be rebuilt.
     * This method is called after opening an index.
     *
     * @return true if a rebuild is required.
     */
    public abstract boolean needRebuild();

    /**
     * Get the row count of this table, for the given session.
     *
     * @param session the session
     * @return the row count
     */
    public abstract long getRowCount(SessionLocal session);

    /**
     * Get the approximated row count for this table.
     *
     * @param session the session
     * @return the approximated row count
     */
    public abstract long getRowCountApproximation(SessionLocal session);

    /**
     * Get the used disk space for this index.
     *
     * @return the estimated number of bytes
     */
    public long getDiskSpaceUsed() {
        return 0L;
    }

    /**
     * Compare two rows.
     *
     * @param rowData the first row
     * @param compare the second row
     * @return 0 if both rows are equal, -1 if the first row is smaller,
     *         otherwise 1
     */
    public final int compareRows(SearchRow rowData, SearchRow compare) {
        if (rowData == compare) {
            return 0;
        }
        for (int i = 0, len = indexColumns.length; i < len; i++) {
            int index = columnIds[i];
            Value v1 = rowData.getValue(index);
            Value v2 = compare.getValue(index);
            if (v1 == null || v2 == null) {
                // can't compare further
                return 0;
            }
            int c = compareValues(v1, v2, indexColumns[i].sortType);
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    private int compareValues(Value a, Value b, int sortType) {
        if (a == b) {
            return 0;
        }
        boolean aNull = a == ValueNull.INSTANCE;
        if (aNull || b == ValueNull.INSTANCE) {
            return table.getDatabase().getDefaultNullOrdering().compareNull(aNull, sortType);
        }
        int comp = table.compareValues(database, a, b);
        if ((sortType & SortOrder.DESCENDING) != 0) {
            comp = -comp;
        }
        return comp;
    }

    /**
     * Get the index of a column in the list of index columns
     *
     * @param col the column
     * @return the index (0 meaning first column)
     */
    public int getColumnIndex(Column col) {
        for (int i = 0, len = columns.length; i < len; i++) {
            if (columns[i].equals(col)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Check if the given column is the first for this index
     *
     * @param column the column
     * @return true if the given columns is the first
     */
    public boolean isFirstColumn(Column column) {
        return column.equals(columns[0]);
    }

    /**
     * Get the indexed columns as index columns (with ordering information).
     *
     * @return the index columns
     */
    public final IndexColumn[] getIndexColumns() {
        return indexColumns;
    }

    /**
     * Get the indexed columns.
     *
     * @return the columns
     */
    public final Column[] getColumns() {
        return columns;
    }

    /**
     * Get the index type.
     *
     * @return the index type
     */
    public final IndexType getIndexType() {
        return indexType;
    }

    /**
     * Get the table on which this index is based.
     *
     * @return the table
     */
    public Table getTable() {
        return table;
    }

    /**
     * Get the row with the given key.
     *
     * @param session the session
     * @param key the unique key
     * @return the row
     */
    public Row getRow(SessionLocal session, long key) {
        throw DbException.getUnsupportedException(toString());
    }

    /**
     * Does this index support lookup by row id?
     *
     * @return true if it does
     */
    public boolean isRowIdIndex() {
        return false;
    }

    /**
     * Can this index iterate over all rows?
     *
     * @return true if it can
     */
    public boolean canScan() {
        return true;
    }

    /**
     * Enable or disable the 'sorted insert' optimizations (rows are inserted in
     * ascending or descending order) if applicable for this index
     * implementation.
     *
     * @param sortedInsertMode the new value
     */
    public void setSortedInsertMode(boolean sortedInsertMode) {
        // ignore
    }

    /**
     * Create a duplicate key exception with a message that contains the index
     * name.
     *
     * @param key the key values
     * @return the exception
     */
    public DbException getDuplicateKeyException(String key) {
        StringBuilder builder = new StringBuilder();
        getSQL(builder, TRACE_SQL_FLAGS).append(" ON ");
        table.getSQL(builder, TRACE_SQL_FLAGS).append('(');
        builder.append(getColumnListSQL(TRACE_SQL_FLAGS));
        builder.append(')');
        if (key != null) {
            builder.append(" VALUES ").append(key);
        }
        DbException e = DbException.get(ErrorCode.DUPLICATE_KEY_1, builder.toString());
        e.setSource(this);
        return e;
    }

    /**
     * Get "PRIMARY KEY ON <table> [(column)]".
     *
     * @param mainIndexColumn the column index
     * @return the message
     */
    protected StringBuilder getDuplicatePrimaryKeyMessage(int mainIndexColumn) {
        StringBuilder builder = new StringBuilder("PRIMARY KEY ON ");
        table.getSQL(builder, TRACE_SQL_FLAGS);
        if (mainIndexColumn >= 0 && mainIndexColumn < indexColumns.length) {
            builder.append('(');
            indexColumns[mainIndexColumn].getSQL(builder, TRACE_SQL_FLAGS).append(')');
        }
        return builder;
    }

    /**
     * Calculate the cost for the given mask as if this index was a typical
     * b-tree range index. This is the estimated cost required to search one
     * row, and then iterate over the given number of rows.
     *
     * @param masks the IndexCondition search masks, one for each column in the
     *            table
     * @param rowCount the number of rows in the index
     * @param filters all joined table filters
     * @param filter the current table filter index
     * @param sortOrder the sort order
     * @param isScanIndex whether this is a "table scan" index
     * @param allColumnsSet the set of all columns
     * @return the estimated cost
     */
    protected final long getCostRangeIndex(int[] masks, long rowCount, TableFilter[] filters, int filter,
            SortOrder sortOrder, boolean isScanIndex, AllColumnsForPlan allColumnsSet) {
        rowCount += Constants.COST_ROW_OFFSET;
        int totalSelectivity = 0;
        long rowsCost = rowCount;
        if (masks != null) {
            int i = 0, len = columns.length;
            boolean tryAdditional = false;
            while (i < len) {
                Column column = columns[i++];
                int index = column.getColumnId();
                int mask = masks[index];
                if ((mask & IndexCondition.EQUALITY) == IndexCondition.EQUALITY) {
                    if (i == len && getIndexType().isUnique()) {
                        rowsCost = 3;
                        break;
                    }
                    totalSelectivity = 100 - ((100 - totalSelectivity) *
                            (100 - column.getSelectivity()) / 100);
                    long distinctRows = rowCount * totalSelectivity / 100;
                    if (distinctRows <= 0) {
                        distinctRows = 1;
                    }
                    rowsCost = 2 + Math.max(rowCount / distinctRows, 1);
                } else if ((mask & IndexCondition.RANGE) == IndexCondition.RANGE) {
                    rowsCost = 2 + rowsCost / 4;
                    tryAdditional = true;
                    break;
                } else if ((mask & IndexCondition.START) == IndexCondition.START) {
                    rowsCost = 2 + rowsCost / 3;
                    tryAdditional = true;
                    break;
                } else if ((mask & IndexCondition.END) == IndexCondition.END) {
                    rowsCost = rowsCost / 3;
                    tryAdditional = true;
                    break;
                } else {
                    if (mask == 0) {
                        // Adjust counter of used columns (i)
                        i--;
                    }
                    break;
                }
            }
            // Some additional columns can still be used
            if (tryAdditional) {
                while (i < len && masks[columns[i].getColumnId()] != 0) {
                    i++;
                    rowsCost--;
                }
            }
            // Increase cost of indexes with additional unused columns
            rowsCost += len - i;
        }
        // If the ORDER BY clause matches the ordering of this index,
        // it will be cheaper than another index, so adjust the cost
        // accordingly.
        long sortingCost = 0;
        if (sortOrder != null) {
            sortingCost = 100 + rowCount / 10;
        }
        if (sortOrder != null && !isScanIndex) {
            boolean sortOrderMatches = true;
            int coveringCount = 0;
            int[] sortTypes = sortOrder.getSortTypesWithNullOrdering();
            TableFilter tableFilter = filters == null ? null : filters[filter];
            for (int i = 0, len = sortTypes.length; i < len; i++) {
                if (i >= indexColumns.length) {
                    // We can still use this index if we are sorting by more
                    // than it's columns, it's just that the coveringCount
                    // is lower than with an index that contains
                    // more of the order by columns.
                    break;
                }
                Column col = sortOrder.getColumn(i, tableFilter);
                if (col == null) {
                    sortOrderMatches = false;
                    break;
                }
                IndexColumn indexCol = indexColumns[i];
                if (!col.equals(indexCol.column)) {
                    sortOrderMatches = false;
                    break;
                }
                int sortType = sortTypes[i];
                if (sortType != indexCol.sortType) {
                    sortOrderMatches = false;
                    break;
                }
                coveringCount++;
            }
            if (sortOrderMatches) {
                // "coveringCount" makes sure that when we have two
                // or more covering indexes, we choose the one
                // that covers more.
                sortingCost = 100 - coveringCount;
            }
        }
        // If we have two indexes with the same cost, and one of the indexes can
        // satisfy the query without needing to read from the primary table
        // (scan index), make that one slightly lower cost.
        boolean needsToReadFromScanIndex;
        if (!isScanIndex && allColumnsSet != null) {
            needsToReadFromScanIndex = false;
            ArrayList<Column> foundCols = allColumnsSet.get(getTable());
            if (foundCols != null) {
                int main = table.getMainIndexColumn();
                loop: for (Column c : foundCols) {
                    int id = c.getColumnId();
                    if (id == SearchRow.ROWID_INDEX || id == main) {
                        continue;
                    }
                    for (Column c2 : columns) {
                        if (c == c2) {
                            continue loop;
                        }
                    }
                    needsToReadFromScanIndex = true;
                    break;
                }
            }
        } else {
            needsToReadFromScanIndex = true;
        }
        long rc;
        if (isScanIndex) {
            rc = rowsCost + sortingCost + 20;
        } else if (needsToReadFromScanIndex) {
            rc = rowsCost + rowsCost + sortingCost + 20;
        } else {
            // The (20-x) calculation makes sure that when we pick a covering
            // index, we pick the covering index that has the smallest number of
            // columns (the more columns we have in index - the higher cost).
            // This is faster because a smaller index will fit into fewer data
            // blocks.
            rc = rowsCost + sortingCost + columns.length;
        }
        return rc;
    }


    /**
     * Check if this row may have duplicates with the same indexed values in the
     * current compatibility mode. Duplicates with {@code NULL} values are
     * allowed in some modes.
     *
     * @param searchRow
     *            the row to check
     * @return {@code true} if specified row may have duplicates,
     *         {@code false otherwise}
     */
    public final boolean mayHaveNullDuplicates(SearchRow searchRow) {
        switch (database.getMode().uniqueIndexNullsHandling) {
        case ALLOW_DUPLICATES_WITH_ANY_NULL:
            for (int index : columnIds) {
                if (searchRow.getValue(index) == ValueNull.INSTANCE) {
                    return true;
                }
            }
            return false;
        case ALLOW_DUPLICATES_WITH_ALL_NULLS:
            for (int index : columnIds) {
                if (searchRow.getValue(index) != ValueNull.INSTANCE) {
                    return false;
                }
            }
            return true;
        default:
            return false;
        }
    }

    public RowFactory getRowFactory() {
        return rowFactory;
    }

}
