/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.util;

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
