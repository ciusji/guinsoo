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

/**
 * The cursor implementation for the DUAL index.
 */
class DualCursor implements Cursor {

    private Row currentRow;

    DualCursor() {
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
        if (currentRow == null) {
            currentRow = Row.get(Value.EMPTY_VALUES, 1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }

}
