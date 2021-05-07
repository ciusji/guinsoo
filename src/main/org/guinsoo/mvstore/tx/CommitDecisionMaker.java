/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.mvstore.tx;

import org.guinsoo.mvstore.MVMap;
import org.guinsoo.value.VersionedValue;

/**
 * Class CommitDecisionMaker makes a decision during post-commit processing
 * about how to transform uncommitted map entry into committed one,
 * based on undo log information.
 *
 * @author <a href='mailto:andrei.tokar@gmail.com'>Andrei Tokar</a>
 */
final class CommitDecisionMaker<V> extends MVMap.DecisionMaker<VersionedValue<V>> {
    private long undoKey;
    private MVMap.Decision decision;

    void setUndoKey(long undoKey) {
        this.undoKey = undoKey;
        reset();
    }

    @Override
    public MVMap.Decision decide(VersionedValue<V> existingValue, VersionedValue<V> providedValue) {
        assert decision == null;
        if (existingValue == null ||
            // map entry was treated as already committed, and then
            // it has been removed by another transaction (committed and closed by now)
            existingValue.getOperationId() != undoKey) {
            // this is not a final undo log entry for this key,
            // or map entry was treated as already committed and then
            // overwritten by another transaction
            // see TxDecisionMaker.decide()

            decision = MVMap.Decision.ABORT;
        } else /* this is final undo log entry for this key */ if (existingValue.getCurrentValue() == null) {
            decision = MVMap.Decision.REMOVE;
        } else {
            decision = MVMap.Decision.PUT;
        }
        return decision;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends VersionedValue<V>> T selectValue(T existingValue, T providedValue) {
        assert decision == MVMap.Decision.PUT;
        assert existingValue != null;
        return (T) VersionedValueCommitted.getInstance(existingValue.getCurrentValue());
    }

    @Override
    public void reset() {
        decision = null;
    }

    @Override
    public String toString() {
        return "commit " + TransactionStore.getTransactionId(undoKey);
    }
}
