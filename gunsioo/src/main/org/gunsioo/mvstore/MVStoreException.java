/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.mvstore;

/**
 * Various kinds of MVStore problems, along with associated error code.
 */
public class MVStoreException extends RuntimeException {

    private static final long serialVersionUID = 2847042930249663807L;

    private final int errorCode;

    public MVStoreException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
