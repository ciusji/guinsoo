/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.dml;

import org.guinsoo.command.CommandInterface;
import org.guinsoo.command.Prepared;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.message.DbException;
import org.guinsoo.result.ResultInterface;

/**
 * Represents a transactional statement.
 */
public class TransactionCommand extends Prepared {

    private final int type;
    private String savepointName;
    private String transactionName;

    public TransactionCommand(SessionLocal session, int type) {
        super(session);
        this.type = type;
    }

    public void setSavepointName(String name) {
        this.savepointName = name;
    }

    @Override
    public long update() {
        switch (type) {
        case CommandInterface.SET_AUTOCOMMIT_TRUE:
            session.setAutoCommit(true);
            break;
        case CommandInterface.SET_AUTOCOMMIT_FALSE:
            session.setAutoCommit(false);
            break;
        case CommandInterface.BEGIN:
            session.begin();
            break;
        case CommandInterface.COMMIT:
            session.commit(false);
            break;
        case CommandInterface.ROLLBACK:
            session.rollback();
            break;
        case CommandInterface.CHECKPOINT:
            session.getUser().checkAdmin();
            session.getDatabase().checkpoint();
            break;
        case CommandInterface.SAVEPOINT:
            session.addSavepoint(savepointName);
            break;
        case CommandInterface.ROLLBACK_TO_SAVEPOINT:
            session.rollbackToSavepoint(savepointName);
            break;
        case CommandInterface.CHECKPOINT_SYNC:
            session.getUser().checkAdmin();
            session.getDatabase().sync();
            break;
        case CommandInterface.PREPARE_COMMIT:
            session.prepareCommit(transactionName);
            break;
        case CommandInterface.COMMIT_TRANSACTION:
            session.getUser().checkAdmin();
            session.setPreparedTransaction(transactionName, true);
            break;
        case CommandInterface.ROLLBACK_TRANSACTION:
            session.getUser().checkAdmin();
            session.setPreparedTransaction(transactionName, false);
            break;
        case CommandInterface.SHUTDOWN:
        case CommandInterface.SHUTDOWN_COMPACT:
        case CommandInterface.SHUTDOWN_DEFRAG:
            session.commit(false);
            //$FALL-THROUGH$
        case CommandInterface.SHUTDOWN_IMMEDIATELY: {
            session.getUser().checkAdmin();
            // throttle, to allow testing concurrent
            // execution of shutdown and query
            session.throttle();
            Database db = session.getDatabase();
            if (db.setExclusiveSession(session, true)) {
                db.setCompactMode(type);
                // close the database, but don't update the persistent setting
                db.setCloseDelay(0);
                session.close();
            }
            break;
        }
        default:
            throw DbException.getInternalError("type=" + type);
        }
        return 0;
    }

    @Override
    public boolean isTransactional() {
        return true;
    }

    @Override
    public boolean needRecompile() {
        return false;
    }

    public void setTransactionName(String string) {
        this.transactionName = string;
    }

    @Override
    public ResultInterface queryMeta() {
        return null;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

}