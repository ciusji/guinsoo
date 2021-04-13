/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.mvstore.db;

import java.util.List;

import org.gunsioo.index.Index;
import org.gunsioo.index.IndexType;
import org.gunsioo.mvstore.MVMap;
import org.gunsioo.result.Row;
import org.gunsioo.table.IndexColumn;
import org.gunsioo.table.Table;
import org.gunsioo.value.VersionedValue;

/**
 * An index that stores the data in an MVStore.
 */
public abstract class MVIndex<K,V> extends Index {

    protected MVIndex(Table newTable, int id, String name, IndexColumn[] newIndexColumns, IndexType newIndexType) {
        super(newTable, id, name, newIndexColumns, newIndexType);
    }

    /**
     * Add the rows to a temporary storage (not to the index yet). The rows are
     * sorted by the index columns. This is to more quickly build the index.
     *
     * @param rows the rows
     * @param bufferName the name of the temporary storage
     */
    public abstract void addRowsToBuffer(List<Row> rows, String bufferName);

    /**
     * Add all the index data from the buffers to the index. The index will
     * typically use merge sort to add the data more quickly in sorted order.
     *
     * @param bufferNames the names of the temporary storage
     */
    public abstract void addBufferedRows(List<String> bufferNames);

    public abstract MVMap<K,VersionedValue<V>> getMVMap();

}
