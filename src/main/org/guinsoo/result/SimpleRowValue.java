/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.result;

import org.guinsoo.engine.Constants;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueBigint;
import org.guinsoo.value.ValueNull;

/**
 * A simple row that contains data for only one column.
 */
public class SimpleRowValue extends SearchRow {

    private int index;
    private final int virtualColumnCount;
    private Value data;

    public SimpleRowValue(int columnCount) {
        this.virtualColumnCount = columnCount;
    }

    public SimpleRowValue(int columnCount, int index) {
        this.virtualColumnCount = columnCount;
        this.index = index;
    }

    @Override
    public int getColumnCount() {
        return virtualColumnCount;
    }

    @Override
    public Value getValue(int idx) {
        if (idx == ROWID_INDEX) {
            return ValueBigint.get(getKey());
        }
        return idx == index ? data : null;
    }

    @Override
    public void setValue(int idx, Value v) {
        if (idx == ROWID_INDEX) {
            setKey(v.getLong());
        }
        index = idx;
        data = v;
    }

    @Override
    public String toString() {
        return "( /* " + key + " */ " + (data == null ?
                "null" : data.getTraceSQL()) + " )";
    }

    @Override
    public int getMemory() {
        return Constants.MEMORY_ROW + (data == null ? 0 : data.getMemory());
    }

    @Override
    public boolean isNull(int index) {
        return index != this.index || data == null || data == ValueNull.INSTANCE;
    }

    @Override
    public void copyFrom(SearchRow source) {
        setKey(source.getKey());
        setValue(index, source.getValue(index));
    }
}
