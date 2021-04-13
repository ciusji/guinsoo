/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.aggregate;

import java.util.TreeMap;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueNull;

/**
 * Data stored while calculating an aggregate that needs distinct values with
 * their counts.
 */
class AggregateDataDistinctWithCounts extends AggregateData {

    private final boolean ignoreNulls;

    private final int maxDistinctCount;

    private TreeMap<Value, LongDataCounter> values;

    /**
     * Creates new instance of data for aggregate that needs distinct values
     * with their counts.
     *
     * @param ignoreNulls
     *            whether NULL values should be ignored
     * @param maxDistinctCount
     *            maximum count of distinct values to collect
     */
    AggregateDataDistinctWithCounts(boolean ignoreNulls, int maxDistinctCount) {
        this.ignoreNulls = ignoreNulls;
        this.maxDistinctCount = maxDistinctCount;
    }

    @Override
    void add(SessionLocal session, Value v) {
        if (ignoreNulls && v == ValueNull.INSTANCE) {
            return;
        }
        if (values == null) {
            values = new TreeMap<>(session.getDatabase().getCompareMode());
        }
        LongDataCounter a = values.get(v);
        if (a == null) {
            if (values.size() >= maxDistinctCount) {
                return;
            }
            a = new LongDataCounter();
            values.put(v, a);
        }
        a.count++;
    }

    @Override
    Value getValue(SessionLocal session) {
        return null;
    }

    /**
     * Returns map with values and their counts.
     *
     * @return map with values and their counts
     */
    TreeMap<Value, LongDataCounter> getValues() {
        return values;
    }

}
