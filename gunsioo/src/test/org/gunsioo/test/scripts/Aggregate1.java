/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.scripts;

import java.sql.SQLException;

import org.gunsioo.api.Aggregate;
import org.gunsioo.api.H2Type;

/**
 * An aggregate function for tests.
 */
public class Aggregate1 implements Aggregate {

    @Override
    public int getInternalType(int[] inputTypes) throws SQLException {
        return H2Type.INTEGER.getVendorTypeNumber();
    }

    @Override
    public void add(Object value) throws SQLException {
    }

    @Override
    public Object getResult() throws SQLException {
        return 0;
    }

}
