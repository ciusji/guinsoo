/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.util;

import java.util.Arrays;

import org.guinsoo.message.DbException;

/**
 * A hash map with int key and int values. There is a restriction: the
 * value -1 (NOT_FOUND) cannot be stored in the map. 0 can be stored.
 * An empty record has key=0 and value=0.
 * A deleted record has key=0 and value=DELETED
 */
public class IntIntHashMap extends HashBase {

    /**
     * The value indicating that the entry has not been found.
     */
    public static final int NOT_FOUND = -1;

    private static final int DELETED = 1;
    private int[] keys;
    private int[] values;
    private int zeroValue;

    /**
     * Creates a new instance of a hash map.
     */
    public IntIntHashMap() {
        this(true);
    }

    /**
     * Creates a new instance of a hash map.
     *
     * @param autoShrink enable auto-shrink
     */
    public IntIntHashMap(boolean autoShrink) {
        super(autoShrink);
    }

    @Override
    protected void reset(int newLevel) {
        super.reset(newLevel);
        keys = new int[len];
        values = new int[len];
    }

    /**
     * Store the given key-value pair. The value is overwritten or added.
     *
     * @param key the key
     * @param value the value (-1 is not supported)
     */
    public void put(int key, int value) {
        if (key == 0) {
            zeroKey = true;
            zeroValue = value;
            return;
        }
        checkSizePut();
        internalPut(key, value);
    }

    private void internalPut(int key, int value) {
        int index = getIndex(key);
        int plus = 1;
        int deleted = -1;
        do {
            int k = keys[index];
            if (k == 0) {
                if (values[index] != DELETED) {
                    // found an empty record
                    if (deleted >= 0) {
                        index = deleted;
                        deletedCount--;
                    }
                    size++;
                    keys[index] = key;
                    values[index] = value;
                    return;
                }
                // found a deleted record
                if (deleted < 0) {
                    deleted = index;
                }
            } else if (k == key) {
                // update existing
                values[index] = value;
                return;
            }
            index = (index + plus++) & mask;
        } while (plus <= len);
        // no space
        throw DbException.getInternalError("hashmap is full");
    }

    /**
     * Remove the key-value pair with the given key.
     *
     * @param key the key
     */
    public void remove(int key) {
        if (key == 0) {
            zeroKey = false;
            return;
        }
        checkSizeRemove();
        int index = getIndex(key);
        int plus = 1;
        do {
            int k = keys[index];
            if (k == key) {
                // found the record
                keys[index] = 0;
                values[index] = DELETED;
                deletedCount++;
                size--;
                return;
            } else if (k == 0 && values[index] == 0) {
                // found an empty record
                return;
            }
            index = (index + plus++) & mask;
        } while (plus <= len);
        // not found
    }

    @Override
    protected void rehash(int newLevel) {
        int[] oldKeys = keys;
        int[] oldValues = values;
        reset(newLevel);
        for (int i = 0; i < oldKeys.length; i++) {
            int k = oldKeys[i];
            if (k != 0) {
                // skip the checkSizePut so we don't end up
                // accidentally recursing
                internalPut(k, oldValues[i]);
            }
        }
    }

    /**
     * Get the value for the given key. This method returns NOT_FOUND if the
     * entry has not been found.
     *
     * @param key the key
     * @return the value or NOT_FOUND
     */
    public int get(int key) {
        if (key == 0) {
            return zeroKey ? zeroValue : NOT_FOUND;
        }
        int index = getIndex(key);
        int plus = 1;
        do {
            int k = keys[index];
            if (k == 0 && values[index] == 0) {
                // found an empty record
                return NOT_FOUND;
            } else if (k == key) {
                // found it
                return values[index];
            }
            index = (index + plus++) & mask;
        } while (plus <= len);
        return NOT_FOUND;
    }

    @Override
    public void clear() {
        if (autoShrink && level > 2) {
            reset(2);
        } else {
            Arrays.fill(keys, 0);
            Arrays.fill(values, 0);
            deletedCount = 0;
            size = 0;
        }
        zeroKey = false;
    }

}
