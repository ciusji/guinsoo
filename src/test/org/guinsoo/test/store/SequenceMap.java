/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.store;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.guinsoo.mvstore.MVMap;
import org.guinsoo.mvstore.type.DataType;

/**
 * A custom map returning the keys and values 1 .. 10.
 */
public class SequenceMap extends MVMap<Long, Long> {

    /**
     * The minimum value.
     */
    int min = 1;

    /**
     * The maximum value.
     */
    int max = 10;

    public SequenceMap(Map<String, Object> config, DataType<Long> keyType, DataType<Long> valueType) {
        super(config, keyType, valueType);
    }

    @Override
    public Set<Long> keySet() {
        return new AbstractSet<Long>() {

            @Override
            public Iterator<Long> iterator() {
                return new Iterator<Long>() {

                    long x = min;

                    @Override
                    public boolean hasNext() {
                        return x <= max;
                    }

                    @Override
                    public Long next() {
                        return Long.valueOf(x++);
                    }

                };
            }

            @Override
            public int size() {
                return max - min + 1;
            }
        };
    }

    /**
     * A builder for this class.
     */
    public static class Builder extends MVMap.Builder<Long, Long> {
        @Override
        public SequenceMap create(Map<String, Object> config) {
            return new SequenceMap(config, getKeyType(), getValueType());
        }

    }
}
