/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.table;

import java.util.ArrayList;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.index.Index;
import org.gunsioo.index.IndexType;
import org.gunsioo.message.DbException;
import org.gunsioo.result.Row;
import org.gunsioo.schema.Schema;

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
