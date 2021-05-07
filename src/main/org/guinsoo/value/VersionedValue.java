/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.value;

/**
 * A versioned value (possibly null).
 * It contains current value and latest committed value if current one is uncommitted.
 * Also for uncommitted values it contains operationId - a combination of
 * transactionId and logId.
 */
public class VersionedValue<T> {

    protected VersionedValue() {}

    public boolean isCommitted() {
        return true;
    }

    public long getOperationId() {
        return 0L;
    }

    @SuppressWarnings("unchecked")
    public T getCurrentValue() {
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T getCommittedValue() {
        return (T)this;
    }

}
