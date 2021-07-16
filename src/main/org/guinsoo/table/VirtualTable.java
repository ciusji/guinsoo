/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.table;

import java.util.ArrayList;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.result.Row;
import org.guinsoo.schema.Schema;
import org.guinsoo.index.Index;
import org.guinsoo.index.IndexType;

/**
 * A base class for virtual tables.
 */
public abstract class VirtualTable extends Table {

    protected VirtualTable(Schema schema, int id, String name) {
        super(schema, id, name, false, true);
    }

    @Override
    public void close(SessionLocal session) {
        // Nothing to do
    }

    @Override
    public Index addIndex(SessionLocal session, String indexName, int indexId, IndexColumn[] cols, IndexType indexType,
            boolean create, String indexComment) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override
    public boolean isInsertable() {
        return false;
    }

    @Override
    public void removeRow(SessionLocal session, Row row) {
        throw DbException.getUnsupportedException("Virtual table");

    }

    @Override
    public long truncate(SessionLocal session) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override
    public void addRow(SessionLocal session, Row row) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override
    public void checkSupportAlter() {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override
    public TableType getTableType() {
        return null;
    }

    @Override
    public Index getUniqueIndex() {
        return null;
    }

    @Override
    public ArrayList<Index> getIndexes() {
        return null;
    }

    @Override
    public boolean canReference() {
        return false;
    }

    @Override
    public boolean canDrop() {
        throw DbException.getInternalError(toString());
    }

    @Override
    public String getCreateSQL() {
        return null;
    }

    @Override
    public void checkRename() {
        throw DbException.getUnsupportedException("Virtual table");
    }

}
