/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.table;

import java.util.LinkedHashSet;
import java.util.Set;

import org.gunsioo.index.Index;

/**
 * Contains the hints for which index to use for a specific table. Currently
 * allows a list of "use indexes" to be specified.
 * <p>
 * Use the factory method IndexHints.createUseIndexHints(listOfIndexes) to limit
 * the query planner to only use specific indexes when determining which index
 * to use for a table
 **/
public final class IndexHints {

    private final LinkedHashSet<String> allowedIndexes;

    private IndexHints(LinkedHashSet<String> allowedIndexes) {
        this.allowedIndexes = allowedIndexes;
    }

    /**
     * Create an index hint object.
     *
     * @param allowedIndexes the set of allowed indexes
     * @return the hint object
     */
    public static IndexHints createUseIndexHints(LinkedHashSet<String> allowedIndexes) {
        return new IndexHints(allowedIndexes);
    }

    public Set<String> getAllowedIndexes() {
        return allowedIndexes;
    }

    @Override
    public String toString() {
        return "IndexHints{allowedIndexes=" + allowedIndexes + '}';
    }

    /**
     * Allow an index to be used.
     *
     * @param index the index
     * @return whether it was already allowed
     */
    public boolean allowIndex(Index index) {
        return allowedIndexes.contains(index.getName());
    }
}
