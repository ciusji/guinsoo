/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.api;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A user-defined aggregate function needs to implement this interface.
 * The class must be public and must have a public non-argument constructor.
 * <p>
 * Please note this interface only has limited support for data types.
 * If you need data types that don't have a corresponding SQL type
 * (for example GEOMETRY), then use the {@link Aggregate} interface.
 * </p>
 */
public interface AggregateFunction {

    /**
     * This method is called when the aggregate function is used.
     * A new object is created for each invocation.
     *
     * @param conn a connection to the database
     * @throws SQLException on SQL exception
     */
    default void init(Connection conn) throws SQLException {
        // Do nothing by default
    }

    /**
     * This method must return the SQL type of the method, given the SQL type of
     * the input data. The method should check here if the number of parameters
     * passed is correct, and if not it should throw an exception.
     *
     * @param inputTypes the SQL type of the parameters, {@link java.sql.Types}
     * @return the SQL type of the result
     */
    int getType(int[] inputTypes) throws SQLException;

    /**
     * This method is called once for each row.
     * If the aggregate function is called with multiple parameters,
     * those are passed as array.
     *
     * @param value the value(s) for this row
     */
    void add(Object value) throws SQLException;

    /**
     * This method returns the computed aggregate value. This method must
     * preserve previously added values and must be able to reevaluate result if
     * more values were added since its previous invocation.
     *
     * @return the aggregated value
     */
    Object getResult() throws SQLException;

}