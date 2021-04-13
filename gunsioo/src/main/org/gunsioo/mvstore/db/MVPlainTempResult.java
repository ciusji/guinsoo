/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.mvstore.db;

import org.gunsioo.engine.Database;
import org.gunsioo.expression.Expression;
import org.gunsioo.message.DbException;
import org.gunsioo.mvstore.Cursor;
import org.gunsioo.mvstore.MVMap;
import org.gunsioo.mvstore.MVMap.Builder;
import org.gunsioo.result.ResultExternal;
import org.gunsioo.result.RowFactory.DefaultRowFactory;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueRow;

/**
 * Plain temporary result.
 */
class MVPlainTempResult extends MVTempResult {

    /**
     * Map with identities of rows as keys rows as values.
     */
    private final MVMap<Long, ValueRow> map;

    /**
     * Counter for the identities of rows. A separate counter is used instead of
     * {@link #rowCount} because rows due to presence of {@link #removeRow(Value[])}
     * method to ensure that each row will have an own identity.
     */
    private long counter;

    /**
     * Cursor for the {@link #next()} method.
     */
    private Cursor<Long, ValueRow> cursor;

    /**
     * Creates a shallow copy of the result.
     *
     * @param parent
     *                   parent result
     */
    private MVPlainTempResult(MVPlainTempResult parent) {
        super(parent);
        this.map = parent.map;
    }

    /**
     * Creates a new plain temporary result. This result does not sort its rows,
     * but it can be used in index-sorted queries and it can preserve additional
     * columns for WITH TIES processing.
     *
     * @param database
     *            database
     * @param expressions
     *            column expressions
     * @param visibleColumnCount
     *            count of visible columns
     * @param resultColumnCount
     *            the number of columns including visible columns and additional
     *            virtual columns for ORDER BY clause
     */
    MVPlainTempResult(Database database, Expression[] expressions, int visibleColumnCount, int resultColumnCount) {
        super(database, expressions, visibleColumnCount, resultColumnCount);
        ValueDataType valueType = new ValueDataType(database, new int[resultColumnCount]);
        valueType.setRowFactory(DefaultRowFactory.INSTANCE.createRowFactory(database, database.getCompareMode(),
                database.getMode(), database, expressions, null));
        Builder<Long, ValueRow> builder = new MVMap.Builder<Long, ValueRow>().valueType(valueType).singleWriter();
        map = store.openMap("tmp", builder);
    }

    @Override
    public int addRow(Value[] values) {
        assert parent == null;
        map.append(counter++, ValueRow.get(values));
        return ++rowCount;
    }

    @Override
    public boolean contains(Value[] values) {
        throw DbException.getUnsupportedException("contains()");
    }

    @Override
    public synchronized ResultExternal createShallowCopy() {
        if (parent != null) {
            return parent.createShallowCopy();
        }
        if (closed) {
            return null;
        }
        childCount++;
        return new MVPlainTempResult(this);
    }

    @Override
    public Value[] next() {
        if (cursor == null) {
            cursor = map.cursor(null);
        }
        if (!cursor.hasNext()) {
            return null;
        }
        cursor.next();
        return cursor.getValue().getList();
    }

    @Override
    public int removeRow(Value[] values) {
        throw DbException.getUnsupportedException("removeRow()");
    }

    @Override
    public void reset() {
        cursor = null;
    }

}
