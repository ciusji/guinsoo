/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.test.scripts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gunsioo.api.Trigger;

/**
 * A trigger for tests.
 */
public class Trigger2 implements Trigger {

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        if (oldRow == null && newRow != null) {
            Long id = (Long) newRow[0];
            PreparedStatement prep;
            int i = 0;
            if (id == null) {
                prep = conn.prepareStatement("SELECT * FROM FINAL TABLE (INSERT INTO TEST VALUES (DEFAULT, ?, ?))");
            } else {
                prep = conn.prepareStatement("SELECT * FROM FINAL TABLE (INSERT INTO TEST VALUES (?, ?, ?))");
                prep.setLong(++i, id);
            }
            prep.setInt(++i, (int) newRow[1]);
            prep.setInt(++i, (int) newRow[2]);
            executeAndReadFinalTable(prep, newRow);
        } else if (oldRow != null && newRow != null) {
            PreparedStatement prep = conn.prepareStatement(
                    "SELECT * FROM FINAL TABLE (UPDATE TEST SET (ID, A, B) = (?, ?, ?) WHERE ID = ?)");
            prep.setLong(1, (long) newRow[0]);
            prep.setInt(2, (int) newRow[1]);
            prep.setInt(3, (int) newRow[2]);
            prep.setLong(4, (long) oldRow[0]);
            executeAndReadFinalTable(prep, newRow);
        } else if (oldRow != null && newRow == null) {
            PreparedStatement prep = conn.prepareStatement("DELETE FROM TEST WHERE ID = ?");
            prep.setLong(1, (long) oldRow[0]);
            prep.executeUpdate();
        }
    }

    private static void executeAndReadFinalTable(PreparedStatement prep, Object[] newRow) throws SQLException {
        try (ResultSet rs = prep.executeQuery()) {
            rs.next();
            newRow[0] = rs.getLong(1);
            newRow[1] = rs.getInt(2);
            newRow[2] = rs.getInt(3);
        }
    }

}
