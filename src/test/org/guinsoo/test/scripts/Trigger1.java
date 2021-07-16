/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.test.scripts;

import java.sql.Connection;
import java.sql.SQLException;

import org.guinsoo.api.Trigger;

/**
 * A trigger for tests.
 */
public class Trigger1 implements Trigger {

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        if (newRow != null) {
            newRow[2] = ((int) newRow[2]) * 10;
        }
    }

}
