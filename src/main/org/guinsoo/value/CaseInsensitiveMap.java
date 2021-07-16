/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.value;

import java.util.HashMap;
import org.guinsoo.util.StringUtils;

/**
 * A hash map with a case-insensitive string key.
 *
 * @param <V> the value type
 */
public class CaseInsensitiveMap<V> extends HashMap<String, V> {

    private static final long serialVersionUID = 1L;

    /**
     * Creates new instance of case-insensitive map.
     */
    public CaseInsensitiveMap() {
    }

    /**
     * Creates new instance of case-insensitive map with specified initial
     * capacity.
     *
     * @param initialCapacity the initial capacity
     */
    public CaseInsensitiveMap(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public V get(Object key) {
        return super.get(StringUtils.toUpperEnglish((String) key));
    }

    @Override
    public V put(String key, V value) {
        return super.put(StringUtils.toUpperEnglish(key), value);
    }

    @Override
    public V putIfAbsent(String key, V value) {
        return super.putIfAbsent(StringUtils.toUpperEnglish(key), value);
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(StringUtils.toUpperEnglish((String) key));
    }

    @Override
    public V remove(Object key) {
        return super.remove(StringUtils.toUpperEnglish((String) key));
    }

}
