/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.tools;

import java.sql.SQLException;

/**
 * This interface is for classes that create rows on demand.
 * It is used together with SimpleResultSet to create a dynamic result set.
 */
public interface SimpleRowSource {

    /**
     * Get the next row. Must return null if no more rows are available.
     *
     * @return the row or null
     */
    Object[] readRow() throws SQLException;

    /**
     * Close the row source.
     */
    void close();

    /**
     * Reset the position (before the first row).
     *
     * @throws SQLException if this operation is not supported
     */
    void reset() throws SQLException;
}
