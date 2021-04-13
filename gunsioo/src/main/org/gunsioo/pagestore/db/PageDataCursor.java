/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.pagestore.db;

import org.gunsioo.index.Cursor;
import org.gunsioo.message.DbException;
import org.gunsioo.result.Row;
import org.gunsioo.result.SearchRow;

/**
 * The cursor implementation for the page scan index.
 */
class PageDataCursor implements Cursor {

    private PageDataLeaf current;
    private int idx;
    private final long maxKey;
    private Row row;

    PageDataCursor(PageDataLeaf current, int idx, long maxKey) {
        this.current = current;
        this.idx = idx;
        this.maxKey = maxKey;
    }

    @Override
    public Row get() {
        return row;
    }

    @Override
    public SearchRow getSearchRow() {
        return get();
    }

    @Override
    public boolean next() {
        nextRow();
        return checkMax();
    }

    private boolean checkMax() {
        if (row != null) {
            if (maxKey != Long.MAX_VALUE) {
                long x = current.index.getKey(row, Long.MAX_VALUE, Long.MAX_VALUE);
                if (x > maxKey) {
                    row = null;
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void nextRow() {
        if (idx >= current.getEntryCount()) {
            current = current.getNextPage();
            idx = 0;
            if (current == null) {
                row = null;
                return;
            }
        }
        row = current.getRowAt(idx);
        idx++;
    }

    @Override
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }

}
