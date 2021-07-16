/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.mvstore.type;

import java.nio.ByteBuffer;
import org.guinsoo.mvstore.DataUtils;
import org.guinsoo.mvstore.WriteBuffer;

/**
 * A string type.
 */
public class StringDataType extends BasicDataType<String> {

    public static final StringDataType INSTANCE = new StringDataType();

    private static final String[] EMPTY_STRING_ARR = new String[0];

    @Override
    public String[] createStorage(int size) {
        return size == 0 ? EMPTY_STRING_ARR : new String[size];
    }

    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }

    @Override
    public int binarySearch(String key, Object storageObj, int size, int initialGuess) {
        String[] storage = cast(storageObj);
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
            int compare = key.compareTo(storage[x]);
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
    public int getMemory(String obj) {
        return 24 + 2 * obj.length();
    }

    @Override
    public String read(ByteBuffer buff) {
        return DataUtils.readString(buff);
    }

    @Override
    public void write(WriteBuffer buff, String s) {
        int len = s.length();
        buff.putVarInt(len).putStringData(s, len);
    }
}

