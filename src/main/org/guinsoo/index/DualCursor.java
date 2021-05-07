/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.index;

import org.guinsoo.message.DbException;
import org.guinsoo.result.Row;
import org.guinsoo.result.SearchRow;
import org.guinsoo.value.Value;

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
