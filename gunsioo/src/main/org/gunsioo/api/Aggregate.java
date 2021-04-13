/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.api;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A user-defined aggregate function needs to implement this interface.
 * The class must be public and must have a public non-argument constructor.
 */
public interface Aggregate {

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
     * This method must return the Gunsioo data type, {@link org.gunsioo.value.Value},
     * of the aggregate function, given the Gunsioo data type of the input data.
     * The method should check here if the number of parameters
     * passed is correct, and if not it should throw an exception.
     *
     * @param inputTypes the Gunsioo data type of the parameters,
     * @return the Gunsioo data type of the result
     * @throws SQLException if the number/type of parameters passed is incorrect
     */
    int getInternalType(int[] inputTypes) throws SQLException;

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