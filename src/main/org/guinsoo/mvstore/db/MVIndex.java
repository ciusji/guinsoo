/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.mvstore.db;

import java.util.List;

import org.guinsoo.index.Index;
import org.guinsoo.index.IndexType;
import org.guinsoo.mvstore.MVMap;
import org.guinsoo.result.Row;
import org.guinsoo.table.IndexColumn;
import org.guinsoo.table.Table;
import org.guinsoo.value.VersionedValue;

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
