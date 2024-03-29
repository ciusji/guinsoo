/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.mvstore.db;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.guinsoo.mvstore.type.BasicDataType;
import org.guinsoo.mvstore.type.MetaType;
import org.guinsoo.mvstore.type.StatefulDataType;
import org.guinsoo.engine.CastDataProvider;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.Mode;
import org.guinsoo.mvstore.DataUtils;
import org.guinsoo.mvstore.WriteBuffer;
import org.guinsoo.result.RowFactory;
import org.guinsoo.result.SearchRow;
import org.guinsoo.store.DataHandler;
import org.guinsoo.value.CompareMode;
import org.guinsoo.value.Value;

/**
 * The data type for rows.
 *
 * @author <a href='mailto:andrei.tokar@gmail.com'>Andrei Tokar</a>
 */
public final class RowDataType extends BasicDataType<SearchRow> implements StatefulDataType<Database> {

    private final ValueDataType valueDataType;
    private final int[]         sortTypes;
    private final int[]         indexes;
    private final int           columnCount;

    public RowDataType(CastDataProvider provider, CompareMode compareMode, Mode mode, DataHandler handler,
            int[] sortTypes, int[] indexes, int columnCount) {
        this.valueDataType = new ValueDataType(provider, compareMode, mode, handler, sortTypes);
        this.sortTypes = sortTypes;
        this.indexes = indexes;
        this.columnCount = columnCount;
        assert indexes == null || sortTypes.length == indexes.length;
    }

    public int[] getIndexes() {
        return indexes;
    }

    public RowFactory getRowFactory() {
        return valueDataType.getRowFactory();
    }

    public void setRowFactory(RowFactory rowFactory) {
        valueDataType.setRowFactory(rowFactory);
    }

    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public SearchRow[] createStorage(int capacity) {
        return new SearchRow[capacity];
    }

    @Override
    public int compare(SearchRow a, SearchRow b) {
        if (a == b) {
            return 0;
        }
        if (indexes == null) {
            int len = a.getColumnCount();
            assert len == b.getColumnCount() : len + " != " + b.getColumnCount();
            for (int i = 0; i < len; i++) {
                int comp = valueDataType.compareValues(a.getValue(i), b.getValue(i), sortTypes[i]);
                if (comp != 0) {
                    return comp;
                }
            }
            return 0;
        } else {
            return compareSearchRows(a, b);
        }
    }

    private int compareSearchRows(SearchRow a, SearchRow b) {
        for (int i = 0; i < indexes.length; i++) {
            int index = indexes[i];
            Value v1 = a.getValue(index);
            Value v2 = b.getValue(index);
            if (v1 == null || v2 == null) {
                // can't compare further
                break;
            }
            int comp = valueDataType.compareValues(v1, v2, sortTypes[i]);
            if (comp != 0) {
                return comp;
            }
        }
        long aKey = a.getKey();
        long bKey = b.getKey();
        return aKey == SearchRow.MATCH_ALL_ROW_KEY || bKey == SearchRow.MATCH_ALL_ROW_KEY ?
                0 : Long.compare(aKey, bKey);
    }

    @Override
    public int binarySearch(SearchRow key, Object storage, int size, int initialGuess) {
        return binarySearch(key, (SearchRow[])storage, size, initialGuess);
    }

    public int binarySearch(SearchRow key, SearchRow[] keys, int size, int initialGuess) {
        int low = 0;
        int high = size - 1;
        // the cached index minus one, so that
        // for the first time (when cachedCompare is 0),
        // the default value is used
        int x = initialGuess - 1;
        if (x < 0 || x > high) {
            x = high >>> 1;
        }
        while (low <= high) {
            int compare = compareSearchRows(key, keys[x]);
            if (compare > 0) {
                low = x + 1;
            } else if (compare < 0) {
                high = x - 1;
            } else {
                return x;
            }
            x = (low + high) >>> 1;
        }
        return -(low + 1);
    }

    @Override
    public int getMemory(SearchRow row) {
        return row.getMemory();
    }

    @Override
    public SearchRow read(ByteBuffer buff) {
        //TODO: switch to compact format when format backward-compatibility is not required
        return readCompatible(buff);
/*
        SearchRow row = valueDataType.getRowFactory().createRow();
        row.setKey(DataUtils.readVarLong(buff));
        if (indexes == null) {
            int columnCount = DataUtils.readVarInt(buff);
            for (int i = 0; i < columnCount; i++) {
                row.setValue(i, valueDataType.read(buff));
            }
        } else {
            for (int i : indexes) {
                row.setValue(i, valueDataType.read(buff));
            }
        }
        return row;
*/
    }

    /**
     * Reads a row.
     *
     * @param buff the source buffer
     * @return the row
     */
    public SearchRow readCompatible(ByteBuffer buff) {
        return (SearchRow)valueDataType.read(buff);
    }


    @Override
    public void write(WriteBuffer buff, SearchRow row) {
        //TODO: switch to compact format when format backward-compatibility is not required
        writeCompatible(buff, row);
//        buff.putVarLong(row.getKey());
//        if (indexes == null) {
//            int columnCount = row.getColumnCount();
//            buff.putVarInt(columnCount);
//            for (int i = 0; i < columnCount; i++) {
//                valueDataType.write(buff, row.getValue(i));
//            }
//        } else {
//            for (int i : indexes) {
//                valueDataType.write(buff, row.getValue(i));
//            }
//        }
    }

    /**
     * Writes a row.
     *
     * @param buff the target buffer
     * @param row the row
     */
    public void writeCompatible(WriteBuffer buff, SearchRow row) {
        valueDataType.writeRow(buff, row, indexes);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || obj.getClass() != RowDataType.class) {
            return false;
        }
        RowDataType other = (RowDataType) obj;
        return columnCount == other.columnCount
            && Arrays.equals(indexes, other.indexes)
            && Arrays.equals(sortTypes, other.sortTypes)
            && valueDataType.equals(other.valueDataType);
    }

    @Override
    public int hashCode() {
        int res = super.hashCode();
        res = res * 31 + columnCount;
        res = res * 31 + Arrays.hashCode(indexes);
        res = res * 31 + Arrays.hashCode(sortTypes);
        res = res * 31 + valueDataType.hashCode();
        return res;
    }

    @Override
    public void save(WriteBuffer buff, MetaType<Database> metaType) {
        buff.putVarInt(columnCount);
        writeIntArray(buff, sortTypes);
        writeIntArray(buff, indexes);
    }

    private static void writeIntArray(WriteBuffer buff, int[] array) {
        if(array == null) {
            buff.putVarInt(0);
        } else {
            buff.putVarInt(array.length + 1);
            for (int i : array) {
                buff.putVarInt(i);
            }
        }
    }

    @Override
    public Factory getFactory() {
        return FACTORY;
    }



    private static final Factory FACTORY = new Factory();

    public static final class Factory implements StatefulDataType.Factory<Database> {

        @Override
        public RowDataType create(ByteBuffer buff, MetaType<Database> metaDataType, Database database) {
            int columnCount = DataUtils.readVarInt(buff);
            int[] sortTypes = readIntArray(buff);
            int[] indexes = readIntArray(buff);
            CompareMode compareMode = database == null ? CompareMode.getInstance(null, 0) : database.getCompareMode();
            Mode mode = database == null ? Mode.getRegular() : database.getMode();
            RowFactory rowFactory = RowFactory.getDefaultRowFactory()
                    .createRowFactory(database, compareMode, mode, database, sortTypes, indexes, null, columnCount);
            return rowFactory.getRowDataType();
        }

        private static int[] readIntArray(ByteBuffer buff) {
            int len = DataUtils.readVarInt(buff) - 1;
            if(len < 0) {
                return null;
            }
            int[] res = new int[len];
            for (int i = 0; i < res.length; i++) {
                res[i] = DataUtils.readVarInt(buff);
            }
            return res;
        }
    }
}
