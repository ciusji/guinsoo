/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.pagestore.db;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.index.Cursor;
import org.gunsioo.result.Row;
import org.gunsioo.result.SearchRow;

/**
 * The cursor implementation for the page b-tree index.
 */
public class PageBtreeCursor implements Cursor {

    private final SessionLocal session;
    private final PageBtreeIndex index;
    private final SearchRow last;
    private PageBtreeLeaf current;
    private int i;
    private SearchRow currentSearchRow;
    private Row currentRow;

    PageBtreeCursor(SessionLocal session, PageBtreeIndex index, SearchRow last) {
        this.session = session;
        this.index = index;
        this.last = last;
    }

    /**
     * Set the position of the current row.
     *
     * @param current the leaf page
     * @param i the index within the page
     */
    void setCurrent(PageBtreeLeaf current, int i) {
        this.current = current;
        this.i = i;
    }

    @Override
    public Row get() {
        if (currentRow == null && currentSearchRow != null) {
            currentRow = index.getRow(session, currentSearchRow.getKey());
        }
        return currentRow;
    }

    @Override
    public SearchRow getSearchRow() {
        return currentSearchRow;
    }

    @Override
    public boolean next() {
        if (current == null) {
            return false;
        }
        if (i >= current.getEntryCount()) {
            current.nextPage(this);
            if (current == null) {
                return false;
            }
        }
        currentSearchRow = current.getRow(i);
        currentRow = null;
        if (last != null && index.compareRows(currentSearchRow, last) > 0) {
            currentSearchRow = null;
            return false;
        }
        i++;
        return true;
    }

    @Override
    public boolean previous() {
        if (current == null) {
            return false;
        }
        if (i < 0) {
            current.previousPage(this);
            if (current == null) {
                return false;
            }
        }
        currentSearchRow = current.getRow(i);
        currentRow = null;
        i--;
        return true;
    }

}
