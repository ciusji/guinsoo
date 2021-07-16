/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.scripts;

import java.sql.SQLException;

import org.guinsoo.api.Aggregate;
import org.guinsoo.api.DBType;

/**
 * An aggregate function for tests.
 */
public class Aggregate1 implements Aggregate {

    @Override
    public int getInternalType(int[] inputTypes) throws SQLException {
        return DBType.INTEGER.getVendorTypeNumber();
    }

    @Override
    public void add(Object value) throws SQLException {
    }

    @Override
    public Object getResult() throws SQLException {
        return 0;
    }

}
