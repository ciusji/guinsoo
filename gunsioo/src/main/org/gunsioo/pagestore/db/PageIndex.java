/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.pagestore.db;

import org.gunsioo.index.Index;
import org.gunsioo.index.IndexType;
import org.gunsioo.table.IndexColumn;
import org.gunsioo.table.Table;

/**
 * A page store index.
 */
public abstract class PageIndex extends Index {

    /**
     * The root page of this index.
     */
    protected int rootPageId;

    private boolean sortedInsertMode;

    /**
     * Initialize the page store index.
     *
     * @param newTable the table
     * @param id the object id
     * @param name the index name
     * @param newIndexColumns the columns that are indexed or null if this is
     *            not yet known
     * @param newIndexType the index type
     */
    protected PageIndex(Table newTable, int id, String name, IndexColumn[] newIndexColumns, IndexType newIndexType) {
        super(newTable, id, name, newIndexColumns, newIndexType);
    }

    /**
     * Get the root page of this index.
     *
     * @return the root page id
     */
    public int getRootPageId() {
        return rootPageId;
    }

    /**
     * Write back the row count if it has changed.
     */
    public abstract void writeRowCount();

    @Override
    public void setSortedInsertMode(boolean sortedInsertMode) {
        this.sortedInsertMode = sortedInsertMode;
    }

    boolean isSortedInsertMode() {
        return sortedInsertMode;
    }

}
