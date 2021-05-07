/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.table;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.schema.Schema;
import org.guinsoo.index.Index;
import org.guinsoo.index.VirtualConstructedTableIndex;

/**
 * A base class for virtual tables that construct all their content at once.
 */
public abstract class VirtualConstructedTable extends VirtualTable {

    protected VirtualConstructedTable(Schema schema, int id, String name) {
        super(schema, id, name);
    }

    /**
     * Read the rows from the table.
     *
     * @param session
     *            the session
     * @return the result
     */
    public abstract ResultInterface getResult(SessionLocal session);

    @Override
    public Index getScanIndex(SessionLocal session) {
        return new VirtualConstructedTableIndex(this, IndexColumn.wrap(columns));
    }

    @Override
    public long getMaxDataModificationId() {
        // TODO optimization: virtual table currently doesn't know the
        // last modified date
        return Long.MAX_VALUE;
    }

}
