/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.store;

public enum FileLockMethod {
    /**
     * This locking method means no locking is used at all.
     */
    NO,

    /**
     * This locking method means the cooperative file locking protocol should be
     * used.
     */
    FILE,

    /**
     * This locking method means a socket is created on the given machine.
     */
    SOCKET,

    /**
     * Use the file system to lock the file; don't use a separate lock file.
     */
    FS
}