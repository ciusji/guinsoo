/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.mvstore.type;

import java.nio.ByteBuffer;
import org.gunsioo.mvstore.DataUtils;
import org.gunsioo.mvstore.WriteBuffer;

/**
 * Class LongDataType.
 * <UL>
 * <LI> 8/21/17 6:52 PM initial creation
 * </UL>
 *
 * @author <a href='mailto:andrei.tokar@gmail.com'>Andrei Tokar</a>
 */
public class LongDataType extends BasicDataType<Long> {

    public static final LongDataType INSTANCE = new LongDataType();

    private static final Long[] EMPTY_LONG_ARR = new Long[0];

    public LongDataType() {}

    @Override
    public int getMemory(Long obj) {
        return 8;
    }

    @Override
    public void write(WriteBuffer buff, Long data) {
        buff.putVarLong(data);
    }

    @Override
    public Long read(ByteBuffer buff) {
        return DataUtils.readVarLong(buff);
    }

    @Override
    public Long[] createStorage(int size) {
        return size == 0 ? EMPTY_LONG_ARR : new Long[size];
    }

    @Override
    public int compare(Long one, Long two) {
        return Long.compare(one, two);
    }

    @Override
    public int binarySearch(Long keyObj, Object storageObj, int size, int initialGuess) {
        long key = keyObj;
        Long[] storage = cast(storageObj);
        int low = 0;
        int high = size - 1;
        // the cached index minus one, so that
        // for the first time (when cachedCompare is 0),
        // the default value is used
        int x = initialGuess - 1;
        if (x < 0 || x > high) {
            x = high >>> 1;
        }
        return binarySearch(key, storage, low, high, x);
    }

    private static int binarySearch(long key, Long[] storage, int low, int high, int x) {
        while (low <= high) {
            long midVal = storage[x];
            if (key > midVal) {
                low = x + 1;
            } else if (key < midVal) {
                high = x - 1;
            } else {
                return x;
            }
            x = (low + high) >>> 1;
        }
        return -(low + 1);
    }
}
