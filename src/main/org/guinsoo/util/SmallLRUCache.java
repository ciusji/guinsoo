/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class implements a small LRU object cache.
 *
 * @param <K> the key
 * @param <V> the value
 */
public class SmallLRUCache<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 1L;
    private int size;

    private SmallLRUCache(int size) {
        super(size, (float) 0.75, true);
        this.size = size;
    }

    /**
     * Create a new object with all elements of the given collection.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param size the number of elements
     * @return the object
     */
    public static <K, V> SmallLRUCache<K, V> newInstance(int size) {
        return new SmallLRUCache<>(size);
    }

    public void setMaxSize(int size) {
        this.size = size;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > size;
    }

}
