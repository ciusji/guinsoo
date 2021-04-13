/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.mvstore.tx;

import org.gunsioo.value.VersionedValue;

/**
 * Class VersionedValueUncommitted.
 *
 * @author <a href='mailto:andrei.tokar@gmail.com'>Andrei Tokar</a>
 */
class VersionedValueUncommitted<T> extends VersionedValueCommitted<T> {
    private final long operationId;
    private final T committedValue;

    private VersionedValueUncommitted(long operationId, T value, T committedValue) {
        super(value);
        assert operationId != 0;
        this.operationId = operationId;
        this.committedValue = committedValue;
    }

    /**
     * Create new VersionedValueUncommitted.
     *
     * @param operationId combined log/transaction id
     * @param value value before commit
     * @param committedValue value after commit
     * @return VersionedValue instance
     */
    static <X> VersionedValue<X> getInstance(long operationId, X value, X committedValue) {
        return new VersionedValueUncommitted<>(operationId, value, committedValue);
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public long getOperationId() {
        return operationId;
    }

    @Override
    public T getCommittedValue() {
        return committedValue;
    }

    @Override
    public String toString() {
        return super.toString() +
                " " + TransactionStore.getTransactionId(operationId) + "/" +
                TransactionStore.getLogId(operationId) + " " + committedValue;
    }
}
