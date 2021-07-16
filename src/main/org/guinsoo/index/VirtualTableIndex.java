/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.index;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.result.Row;
import org.guinsoo.table.IndexColumn;
import org.guinsoo.table.VirtualTable;

/**
 * An base class for indexes of virtual tables.
 */
public abstract class VirtualTableIndex extends Index {

    protected VirtualTableIndex(VirtualTable table, String name, IndexColumn[] columns) {
        super(table, 0, name, columns, IndexType.createNonUnique(true));
    }

    @Override
    public void close(SessionLocal session) {
        // nothing to do
    }

    @Override
    public void add(SessionLocal session, Row row) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override
    public void remove(SessionLocal session, Row row) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override
    public void remove(SessionLocal session) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override
    public void truncate(SessionLocal session) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override
    public boolean needRebuild() {
        return false;
    }

    @Override
    public void checkRename() {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override
    public long getRowCount(SessionLocal session) {
        return table.getRowCount(session);
    }

    @Override
    public long getRowCountApproximation(SessionLocal session) {
        return table.getRowCountApproximation(session);
    }

}
