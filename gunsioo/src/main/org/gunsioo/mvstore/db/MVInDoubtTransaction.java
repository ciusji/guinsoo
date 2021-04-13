/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.mvstore.db;

import org.gunsioo.mvstore.MVStore;
import org.gunsioo.mvstore.tx.Transaction;
import org.gunsioo.store.InDoubtTransaction;

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
