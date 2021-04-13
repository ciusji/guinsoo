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
 * The cursor implementation for the scan index.
 */
public class ScanCursor implements Cursor {
    private final ScanIndex scan;
    private Row row;

    ScanCursor(ScanIndex scan) {
        this.scan = scan;
        row = null;
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
        row = scan.getNextRow(row);
        return row != null;
    }

    @Override
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }

}
