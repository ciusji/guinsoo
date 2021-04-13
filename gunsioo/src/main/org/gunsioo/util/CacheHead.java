/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.util;

/**
 * The head element of the linked list.
 */
public class CacheHead extends CacheObject {

    @Override
    public boolean canRemove() {
        return false;
    }

    @Override
    public int getMemory() {
        return 0;
    }

}
