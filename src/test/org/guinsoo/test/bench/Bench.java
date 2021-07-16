/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.bench;

import java.sql.SQLException;

/**
 * The interface for benchmark tests.
 */
public interface Bench {

    /**
     * Initialize the database. This includes creating tables and inserting
     * data.
     *
     * @param db the database object
     * @param size the amount of data
     */
    void init(Database db, int size) throws SQLException;

    /**
     * Run the test.
     */
    void runTest() throws Exception;

    /**
     * Get the name of the test.
     *
     * @return the test name
     */
    String getName();

}
