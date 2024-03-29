/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.dml;

import org.guinsoo.command.Prepared;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.result.ResultInterface;
import org.guinsoo.result.ResultTarget;
import org.guinsoo.table.DataChangeDeltaTable.ResultOption;
import org.guinsoo.table.Table;

/**
 * Data change statement.
 */
public abstract class DataChangeStatement extends Prepared {

    /**
     * Creates new instance of DataChangeStatement.
     *
     * @param session
     *            the session
     */
    protected DataChangeStatement(SessionLocal session) {
        super(session);
    }

    /**
     * Return the name of this statement.
     *
     * @return the short name of this statement.
     */
    public abstract String getStatementName();

    /**
     * Return the target table.
     *
     * @return the target table
     */
    public abstract Table getTable();

    @Override
    public final boolean isTransactional() {
        return true;
    }

    @Override
    public final ResultInterface queryMeta() {
        return null;
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

    @Override
    public final long update() {
        // !!!
        return update(null, null);
    }

    /**
     * Execute the statement with specified delta change collector and collection mode.
     *
     * @param deltaChangeCollector
     *            target result
     * @param deltaChangeCollectionMode
     *            collection mode
     * @return the update count
     */
    public abstract long update(ResultTarget deltaChangeCollector, ResultOption deltaChangeCollectionMode);

}
