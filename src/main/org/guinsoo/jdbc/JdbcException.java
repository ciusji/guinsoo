/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.jdbc;

/**
 * This interface contains additional methods for database exceptions.
 */
public interface JdbcException {

    /**
     * Returns the H2-specific error code.
     *
     * @return the H2-specific error code
     */
    public int getErrorCode();

    /**
     * INTERNAL
     */
    String getOriginalMessage();

    /**
     * Returns the SQL statement.
     * <p>
     * SQL statements that contain '--hide--' are not listed.
     * </p>
     *
     * @return the SQL statement
     */
    String getSQL();

    /**
     * INTERNAL
     */
    void setSQL(String sql);

    /**
     * Returns the class name, the message, and in the server mode, the stack
     * trace of the server
     *
     * @return the string representation
     */
    @Override
    String toString();

}
