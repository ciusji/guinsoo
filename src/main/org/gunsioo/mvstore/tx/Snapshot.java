/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.mvstore.tx;

import java.util.BitSet;

import org.gunsioo.mvstore.RootReference;

/**
 * Snapshot of the map root and committing transactions.
 */
final class Snapshot<K,V> {

    /**
     * The root reference.
     */
    final RootReference<K,V> root;

    /**
     * The committing transactions (see also TransactionStore.committingTransactions).
     */
    final BitSet committingTransactions;

    Snapshot(RootReference<K,V> root, BitSet committingTransactions) {
        this.root = root;
        this.committingTransactions = committingTransactions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + committingTransactions.hashCode();
        result = prime * result + root.hashCode();
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Snapshot)) {
            return false;
        }
        Snapshot<K,V> other = (Snapshot<K,V>) obj;
        return committingTransactions == other.committingTransactions && root == other.root;
    }

}