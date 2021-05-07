/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.mvstore.db;

import org.guinsoo.mvstore.tx.Transaction;
import org.guinsoo.mvstore.MVStore;
import org.guinsoo.store.InDoubtTransaction;

/**
 * An in-doubt transaction.
 */
final class MVInDoubtTransaction implements InDoubtTransaction {

    private final MVStore store;
    private final Transaction transaction;
    private int state = InDoubtTransaction.IN_DOUBT;

    MVInDoubtTransaction(MVStore store, Transaction transaction) {
        this.store = store;
        this.transaction = transaction;
    }

    @Override
    public void setState(int state) {
        if (state == InDoubtTransaction.COMMIT) {
            transaction.commit();
        } else {
            transaction.rollback();
        }
        store.commit();
        this.state = state;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public String getTransactionName() {
        return transaction.getName();
    }

}
