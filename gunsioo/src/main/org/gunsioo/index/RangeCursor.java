/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.index;

import org.gunsioo.message.DbException;
import org.gunsioo.result.Row;
import org.gunsioo.result.SearchRow;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueBigint;

/**
 * The cursor implementation for the range index.
 */
class RangeCursor implements Cursor {

    private boolean beforeFirst;
    private long current;
    private Row currentRow;
    private final long start, end, step;

    RangeCursor(long start, long end) {
        this(start, end, 1);
    }

    RangeCursor(long start, long end, long step) {
        this.start = start;
        this.end = end;
        this.step = step;
        beforeFirst = true;
    }

    @Override
    public Row get() {
        return currentRow;
    }

    @Override
    public SearchRow getSearchRow() {
        return currentRow;
    }

    @Override
    public boolean next() {
        if (beforeFirst) {
            beforeFirst = false;
            current = start;
        } else {
            current += step;
        }
        currentRow = Row.get(new Value[]{ValueBigint.get(current)}, 1);
        return step > 0 ? current <= end : current >= end;
    }

    @Override
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }

}
