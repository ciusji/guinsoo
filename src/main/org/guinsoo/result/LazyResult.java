/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.result;

import org.guinsoo.expression.Expression;
import org.guinsoo.message.DbException;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;

/**
 * Lazy execution support for queries.
 *
 * @author Sergi Vladykin
 */
public abstract class LazyResult extends FetchedResult {

    private final SessionLocal session;
    private final Expression[] expressions;
    private boolean closed;
    private long limit;

    public LazyResult(SessionLocal session, Expression[] expressions) {
        this.session = session;
        this.expressions = expressions;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    @Override
    public boolean isLazy() {
        return true;
    }

    @Override
    public void reset() {
        if (closed) {
            throw DbException.getInternalError();
        }
        rowId = -1L;
        afterLast = false;
        currentRow = null;
        nextRow = null;
    }

    /**
     * Go to the next row and skip it.
     *
     * @return true if a row exists
     */
    public boolean skip() {
        if (closed || afterLast) {
            return false;
        }
        currentRow = null;
        if (nextRow != null) {
            nextRow = null;
            return true;
        }
        if (skipNextRow()) {
            return true;
        }
        afterLast = true;
        return false;
    }

    @Override
    public boolean hasNext() {
        if (closed || afterLast) {
            return false;
        }
        if (nextRow == null && (limit <= 0 || rowId + 1 < limit)) {
            nextRow = fetchNextRow();
        }
        return nextRow != null;
    }

    /**
     * Fetch next row or null if none available.
     *
     * @return next row or null
     */
    protected abstract Value[] fetchNextRow();

    /**
     * Skip next row.
     *
     * @return true if next row was available
     */
    protected boolean skipNextRow() {
        return fetchNextRow() != null;
    }

    @Override
    public long getRowCount() {
        throw DbException.getUnsupportedException("Row count is unknown for lazy result.");
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public String getAlias(int i) {
        return expressions[i].getAlias(session, i);
    }

    @Override
    public String getSchemaName(int i) {
        return expressions[i].getSchemaName();
    }

    @Override
    public String getTableName(int i) {
        return expressions[i].getTableName();
    }

    @Override
    public String getColumnName(int i) {
        return expressions[i].getColumnName(session, i);
    }

    @Override
    public TypeInfo getColumnType(int i) {
        return expressions[i].getType();
    }

    @Override
    public boolean isIdentity(int i) {
        return expressions[i].isIdentity();
    }

    @Override
    public int getNullable(int i) {
        return expressions[i].getNullable();
    }

    @Override
    public void setFetchSize(int fetchSize) {
        // ignore
    }

    @Override
    public int getFetchSize() {
        // We always fetch rows one by one.
        return 1;
    }

}
