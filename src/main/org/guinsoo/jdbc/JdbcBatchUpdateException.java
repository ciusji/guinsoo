/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.jdbc;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.BatchUpdateException;
import java.sql.SQLException;

/**
 * Represents a batch update database exception.
 */
public final class JdbcBatchUpdateException extends BatchUpdateException {

    private static final long serialVersionUID = 1L;

    /**
     * INTERNAL
     */
    JdbcBatchUpdateException(SQLException next, int[] updateCounts) {
        super(next.getMessage(), next.getSQLState(), next.getErrorCode(), updateCounts);
        setNextException(next);
    }

    /**
     * INTERNAL
     */
    JdbcBatchUpdateException(SQLException next, long[] updateCounts) {
        super(next.getMessage(), next.getSQLState(), next.getErrorCode(), updateCounts, null);
        setNextException(next);
    }

    /**
     * INTERNAL
     */
    @Override
    public void printStackTrace() {
        // The default implementation already does that,
        // but we do it again to avoid problems.
        // If it is not implemented, somebody might implement it
        // later on which would be a problem if done in the wrong way.
        printStackTrace(System.err);
    }

    /**
     * INTERNAL
     */
    @Override
    public void printStackTrace(PrintWriter s) {
        if (s != null) {
            super.printStackTrace(s);
            if (getNextException() != null) {
                getNextException().printStackTrace(s);
            }
        }
    }

    /**
     * INTERNAL
     */
    @Override
    public void printStackTrace(PrintStream s) {
        if (s != null) {
            super.printStackTrace(s);
            if (getNextException() != null) {
                getNextException().printStackTrace(s);
            }
        }
    }

}
