/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.result;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Comparator;

import org.guinsoo.engine.Session;
import org.guinsoo.util.Utils;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;

/**
 * Simple in-memory result.
 */
public class SimpleResult implements ResultInterface, ResultTarget {

    /**
     * Column info for the simple result.
     */
    static final class Column {
        /** Column alias. */
        final String alias;

        /** Column name. */
        final String columnName;

        /** Column type. */
        final TypeInfo columnType;

        Column(String alias, String columnName, TypeInfo columnType) {
            if (alias == null || columnName == null) {
                throw new NullPointerException();
            }
            this.alias = alias;
            this.columnName = columnName;
            this.columnType = columnType;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + alias.hashCode();
            result = prime * result + columnName.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Column other = (Column) obj;
            return alias.equals(other.alias) && columnName.equals(other.columnName);
        }

        @Override
        public String toString() {
            if (alias.equals(columnName)) {
                return columnName;
            }
            return columnName + ' ' + alias;
        }

    }

    private final ArrayList<Column> columns;

    private final ArrayList<Value[]> rows;

    private int rowId;

    /**
     * Creates new instance of simple result.
     */
    public SimpleResult() {
        this.columns = Utils.newSmallArrayList();
        this.rows = new ArrayList<>();
        this.rowId = -1;
    }

    private SimpleResult(ArrayList<Column> columns, ArrayList<Value[]> rows) {
        this.columns = columns;
        this.rows = rows;
        this.rowId = -1;
    }

    /**
     * Add column to the result.
     *
     * @param alias
     *            Column's alias.
     * @param columnName
     *            Column's name.
     * @param columnType
     *            Column's value type.
     * @param columnPrecision
     *            Column's precision.
     * @param columnScale
     *            Column's scale.
     */
    public void addColumn(String alias, String columnName, int columnType, long columnPrecision, int columnScale) {
        addColumn(alias, columnName, TypeInfo.getTypeInfo(columnType, columnPrecision, columnScale, null));
    }

    /**
     * Add column to the result.
     *
     * @param columnName
     *            Column's name.
     * @param columnType
     *            Column's type.
     */
    public void addColumn(String columnName, TypeInfo columnType) {
        addColumn(new Column(columnName, columnName, columnType));
    }

    /**
     * Add column to the result.
     *
     * @param alias
     *            Column's alias.
     * @param columnName
     *            Column's name.
     * @param columnType
     *            Column's type.
     */
    public void addColumn(String alias, String columnName, TypeInfo columnType) {
        addColumn(new Column(alias, columnName, columnType));
    }

    /**
     * Add column to the result.
     *
     * @param column
     *            Column info.
     */
    void addColumn(Column column) {
        assert rows.isEmpty();
        columns.add(column);
    }

    @Override
    public void addRow(Value... values) {
        assert values.length == columns.size();
        rows.add(values);
    }

    @Override
    public void reset() {
        rowId = -1;
    }

    @Override
    public Value[] currentRow() {
        return rows.get(rowId);
    }

    @Override
    public boolean next() {
        int count = rows.size();
        if (rowId < count) {
            return ++rowId < count;
        }
        return false;
    }

    @Override
    public long getRowId() {
        return rowId;
    }

    @Override
    public boolean isAfterLast() {
        return rowId >= rows.size();
    }

    @Override
    public int getVisibleColumnCount() {
        return columns.size();
    }

    @Override
    public long getRowCount() {
        return rows.size();
    }

    @Override
    public boolean hasNext() {
        return rowId < rows.size() - 1;
    }

    @Override
    public boolean needToClose() {
        return false;
    }

    @Override
    public void close() {
        // Do nothing for now
    }

    @Override
    public String getAlias(int i) {
        return columns.get(i).alias;
    }

    @Override
    public String getSchemaName(int i) {
        return "";
    }

    @Override
    public String getTableName(int i) {
        return "";
    }

    @Override
    public String getColumnName(int i) {
        return columns.get(i).columnName;
    }

    @Override
    public TypeInfo getColumnType(int i) {
        return columns.get(i).columnType;
    }

    @Override
    public boolean isIdentity(int i) {
        return false;
    }

    @Override
    public int getNullable(int i) {
        return ResultSetMetaData.columnNullableUnknown;
    }

    @Override
    public void setFetchSize(int fetchSize) {
        // Ignored
    }

    @Override
    public int getFetchSize() {
        return 1;
    }

    @Override
    public boolean isLazy() {
        return false;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public SimpleResult createShallowCopy(Session targetSession) {
        return new SimpleResult(columns, rows);
    }

    @Override
    public void limitsWereApplied() {
        // Nothing to do
    }

    /**
     * Sort rows in the list.
     *
     * @param comparator
     *            the comparator
     */
    public void sortRows(Comparator<? super Value[]> comparator) {
        rows.sort(comparator);
    }

}
