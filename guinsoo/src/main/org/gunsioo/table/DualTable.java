/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.table;

import org.gunsioo.engine.Database;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.index.DualIndex;
import org.gunsioo.index.Index;

/**
 * The DUAL table for selects without a FROM clause.
 */
public class DualTable extends VirtualTable {

    /**
     * The name of the range table.
     */
    public static final String NAME = "DUAL";

    /**
     * Create a new range with the given start and end expressions.
     *
     * @param database
     *            the database
     */
    public DualTable(Database database) {
        super(database.getMainSchema(), 0, NAME);
        setColumns(new Column[0]);
    }

    @Override
    public StringBuilder getSQL(StringBuilder builder, int sqlFlags) {
        return builder.append(NAME);
    }

    @Override
    public boolean canGetRowCount(SessionLocal session) {
        return true;
    }

    @Override
    public long getRowCount(SessionLocal session) {
        return 1L;
    }

    @Override
    public TableType getTableType() {
        return TableType.SYSTEM_TABLE;
    }

    @Override
    public Index getScanIndex(SessionLocal session) {
        return new DualIndex(this);
    }

    @Override
    public long getMaxDataModificationId() {
        return 0L;
    }

    @Override
    public long getRowCountApproximation(SessionLocal session) {
        return 1L;
    }

    @Override
    public boolean isDeterministic() {
        return true;
    }

}
