/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.index;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.result.Row;
import org.guinsoo.result.SearchRow;
import org.guinsoo.value.Value;

/**
 * A cursor for a virtual table. This implementation filters the rows (only
 * returns entries that are larger or equal to "first", and smaller than last or
 * equal to "last").
 */
class VirtualTableCursor implements Cursor {

    private final VirtualTableIndex index;

    private final SearchRow first;

    private final SearchRow last;

    final SessionLocal session;

    private final ResultInterface result;

    Value[] values;

    Row row;

    /**
     * @param index
     *            index
     * @param first
     *            first row
     * @param last
     *            last row
     * @param session
     *            session
     * @param result
     *            the result
     */
    VirtualTableCursor(VirtualTableIndex index, SearchRow first, SearchRow last, SessionLocal session,
            ResultInterface result) {
        this.index = index;
        this.first = first;
        this.last = last;
        this.session = session;
        this.result = result;
    }

    @Override
    public Row get() {
        if (values == null) {
            return null;
        }
        if (row == null) {
            row = Row.get(values, 1);
        }
        return row;
    }

    @Override
    public SearchRow getSearchRow() {
        return get();
    }

    @Override
    public boolean next() {
        final SearchRow first = this.first, last = this.last;
        if (first == null && last == null) {
            return nextImpl();
        }
        while (nextImpl()) {
            Row current = get();
            if (first != null) {
                int comp = index.compareRows(current, first);
                if (comp < 0) {
                    continue;
                }
            }
            if (last != null) {
                int comp = index.compareRows(current, last);
                if (comp > 0) {
                    continue;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Skip to the next row if one is available. This method does not filter.
     *
     * @return true if another row is available
     */
    private boolean nextImpl() {
        row = null;
        if (result != null && result.next()) {
            values = result.currentRow();
        } else {
            values = null;
        }
        return values != null;
    }

    @Override
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }

}
