/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.jdbc;

import java.sql.SQLException;

/**
 * Allows us to compile on older platforms, while still implementing the methods
 * from the newer JDBC API.
 */
public interface JdbcStatementBackwardsCompat {

    // compatibility interface

    // JDBC 4.3 (incomplete)

    /**
     * Enquotes the specified identifier.
     *
     * @param identifier
     *            identifier to quote if required
     * @param alwaysQuote
     *            if {@code true} identifier will be quoted unconditionally
     * @return specified identifier quoted if required or explicitly requested
     */
    String enquoteIdentifier(String identifier, boolean alwaysQuote) throws SQLException;

    /**
     * Checks if specified identifier may be used without quotes.
     *
     * @param identifier
     *            identifier to check
     * @return is specified identifier may be used without quotes
     */
    boolean isSimpleIdentifier(String identifier) throws SQLException;
}
