/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.pagestore.db;

import java.util.BitSet;

import org.guinsoo.command.dml.Set;
import org.guinsoo.command.dml.SetTypes;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.IsolationLevel;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.engine.User;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;
import org.guinsoo.value.ValueVarchar;

/**
 * Local session for databases with PageStore engine.
 */
public final class SessionPageStore extends SessionLocal {

    /**
     * This special log position means that the log entry has been written.
     */
    public static final int LOG_WRITTEN = -1;

    private int firstUncommittedLog = LOG_WRITTEN;

    private int firstUncommittedPos = LOG_WRITTEN;

    private boolean redoLogBinary = true;

    public SessionPageStore(Database database, User user, int id) {
        super(database, user, id);
    }

    /**
     * Called when a log entry for this session is added. The session keeps
     * track of the first entry in the transaction log that is not yet
     * committed.
     *
     * @param logId the transaction log id
     * @param pos the position of the log entry in the transaction log
     */
    public void addLogPos(int logId, int pos) {
        if (firstUncommittedLog == LOG_WRITTEN) {
            firstUncommittedLog = logId;
            firstUncommittedPos = pos;
        }
    }

    public int getFirstUncommittedLog() {
        return firstUncommittedLog;
    }

    /**
     * This method is called after the transaction log has written the commit
     * entry for this session.
     */
    public void setAllCommitted() {
        firstUncommittedLog = LOG_WRITTEN;
        firstUncommittedPos = LOG_WRITTEN;
    }

    @Override
    public boolean containsUncommitted() {
        return firstUncommittedLog != LOG_WRITTEN;
    }

    public void setRedoLogBinary(boolean b) {
        this.redoLogBinary = b;
    }

    public boolean isRedoLogBinaryEnabled() {
        return redoLogBinary;
    }

    @Override
    public Value getTransactionId() {
        if (!getDatabase().isPersistent() || undoLog == null || undoLog.size() == 0) {
            return ValueNull.INSTANCE;
        }
        return ValueVarchar.get(new StringBuilder().append(firstUncommittedLog).append('-') //
                .append(firstUncommittedPos).append('-').append(getId()).toString());
    }

    @Override
    protected void scheduleDatabaseObjectIdForRelease(int id) {
        // PageStore requires immediate id release
        BitSet set = new BitSet();
        set.set(id);
        getDatabase().releaseDatabaseObjectIds(set);
    }

    @Override
    public IsolationLevel getIsolationLevel() {
        return IsolationLevel.fromLockMode(getDatabase().getLockMode());
    }

    @Override
    public void setIsolationLevel(IsolationLevel isolationLevel) {
        commit(false);
        int lockMode = isolationLevel.getLockMode();
        Set set = new Set(this, SetTypes.LOCK_MODE);
        set.setInt(lockMode);
        synchronized (getDatabase()) {
            set.update();
        }
    }

}
