/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.mvstore.tx;

import org.gunsioo.mvstore.MVMap;
import org.gunsioo.value.VersionedValue;

/**
 * Class RollbackDecisionMaker process undo log record during transaction rollback.
 *
 * @author <a href='mailto:andrei.tokar@gmail.com'>Andrei Tokar</a>
 */
final class RollbackDecisionMaker extends MVMap.DecisionMaker<Record<?,?>> {
    private final TransactionStore store;
    private final long transactionId;
    private final long toLogId;
    private final TransactionStore.RollbackListener listener;
    private MVMap.Decision decision;

    RollbackDecisionMaker(TransactionStore store, long transactionId, long toLogId,
                            TransactionStore.RollbackListener listener) {
        this.store = store;
        this.transactionId = transactionId;
        this.toLogId = toLogId;
        this.listener = listener;
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    @Override
    public MVMap.Decision decide(Record existingValue, Record providedValue) {
        assert decision == null;
        if (existingValue == null) {
            // normally existingValue will always be there except of db initialization
            // where some undo log entry was captured on disk but actual map entry was not
            decision = MVMap.Decision.ABORT;
        } else {
            VersionedValue<Object> valueToRestore = existingValue.oldValue;
            long operationId;
            if (valueToRestore == null ||
                    (operationId = valueToRestore.getOperationId()) == 0 ||
                    TransactionStore.getTransactionId(operationId) == transactionId
                            && TransactionStore.getLogId(operationId) < toLogId) {
                int mapId = existingValue.mapId;
                MVMap<Object, VersionedValue<Object>> map = store.openMap(mapId);
                if (map != null && !map.isClosed()) {
                    Object key = existingValue.key;
                    VersionedValue<Object> previousValue = map.operate(key, valueToRestore,
                            MVMap.DecisionMaker.DEFAULT);
                    listener.onRollback(map, key, previousValue, valueToRestore);
                }
            }
            decision = MVMap.Decision.REMOVE;
        }
        return decision;
    }

    @Override
    public void reset() {
        decision = null;
    }

    @Override
    public String toString() {
        return "rollback-" + transactionId;
    }
}
