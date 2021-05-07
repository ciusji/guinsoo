/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.index;

import java.util.ArrayList;

import org.guinsoo.message.DbException;
import org.guinsoo.result.Row;
import org.guinsoo.result.SearchRow;

/**
 * An index for a meta data table.
 * This index can only scan through all rows, search is not supported.
 */
public class MetaCursor implements Cursor {

    private Row current;
    private final ArrayList<Row> rows;
    private int index;

    MetaCursor(ArrayList<Row> rows) {
        this.rows = rows;
    }

    @Override
    public Row get() {
        return current;
    }

    @Override
    public SearchRow getSearchRow() {
        return current;
    }

    @Override
    public boolean next() {
        current = index >= rows.size() ? null : rows.get(index++);
        return current != null;
    }

    @Override
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }

}
