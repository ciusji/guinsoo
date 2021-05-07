/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.value;

import java.util.concurrent.ConcurrentHashMap;

import org.guinsoo.util.StringUtils;

/**
 * A concurrent hash map with case-insensitive string keys.
 *
 * @param <V> the value type
 */
public class CaseInsensitiveConcurrentMap<V> extends ConcurrentHashMap<String, V> {

    private static final long serialVersionUID = 1L;

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
