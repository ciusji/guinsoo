/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.index;

import org.gunsioo.message.DbException;
import org.gunsioo.result.Row;
import org.gunsioo.result.SearchRow;

/**
 * A cursor with at most one row.
 */
public class SingleRowCursor implements Cursor {
    private Row row;
    private boolean end;

    /**
     * Create a new cursor.
     *
     * @param row - the single row (if null then cursor is empty)
     */
    public SingleRowCursor(Row row) {
        this.row = row;
    }

    @Override
    public Row get() {
        return row;
    }

    @Override
    public SearchRow getSearchRow() {
        return row;
    }

    @Override
    public boolean next() {
        if (row == null || end) {
            row = null;
            return false;
        }
        end = true;
        return true;
    }

    @Override
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }

}
