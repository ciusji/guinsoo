/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;

import org.guinsoo.message.DbException;
import org.guinsoo.message.Trace;
import org.guinsoo.message.TraceObject;
import org.guinsoo.util.StringUtils;
import org.guinsoo.api.ErrorCode;

/**
 * A savepoint is a point inside a transaction to where a transaction can be
 * rolled back. The tasks that where done before the savepoint are not rolled
 * back in this case.
 */
public final class JdbcSavepoint extends TraceObject implements Savepoint {

    private static final String SYSTEM_SAVEPOINT_PREFIX = "SYSTEM_SAVEPOINT_";

    private final int savepointId;
    private final String name;
    private JdbcConnection conn;

    JdbcSavepoint(JdbcConnection conn, int savepointId, String name,
                  Trace trace, int id) {
        setTrace(trace, TraceObject.SAVEPOINT, id);
        this.conn = conn;
        this.savepointId = savepointId;
        this.name = name;
    }

    /**
     * Release this savepoint. This method only set the connection to null and
     * does not execute a statement.
     */
    void release() {
        this.conn = null;
    }

    /**
     * Get the savepoint name for this name or id.
     * If the name is null, the id is used.
     *
     * @param name the name (may be null)
     * @param id the id
     * @return the savepoint name
     */
    static String getName(String name, int id) {
        if (name != null) {
            return StringUtils.quoteJavaString(name);
        }
        return SYSTEM_SAVEPOINT_PREFIX + id;
    }

    /**
     * Roll back to this savepoint.
     */
    void rollback() {
        checkValid();
        conn.prepareCommand(
                "ROLLBACK TO SAVEPOINT " + getName(name, savepointId),
                Integer.MAX_VALUE).executeUpdate(null);
    }

    private void checkValid() {
        if (conn == null) {
            throw DbException.get(ErrorCode.SAVEPOINT_IS_INVALID_1,
                    getName(name, savepointId));
        }
    }

    /**
     * Get the generated id of this savepoint.
     * @return the id
     */
    @Override
    public int getSavepointId() throws SQLException {
        try {
            debugCodeCall("getSavepointId");
            checkValid();
            if (name != null) {
                throw DbException.get(ErrorCode.SAVEPOINT_IS_NAMED);
            }
            return savepointId;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    /**
     * Get the name of this savepoint.
     * @return the name
     */
    @Override
    public String getSavepointName() throws SQLException {
        try {
            debugCodeCall("getSavepointName");
            checkValid();
            if (name == null) {
                throw DbException.get(ErrorCode.SAVEPOINT_IS_UNNAMED);
            }
            return name;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    /**
     * INTERNAL
     */
    @Override
    public String toString() {
        return getTraceObjectName() + ": id=" + savepointId + " name=" + name;
    }


}
