/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.aggregate;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueBigint;
import org.gunsioo.value.ValueNull;

/**
 * Data stored while calculating a COUNT aggregate.
 */
class AggregateDataCount extends AggregateData {

    private final boolean all;

    private long count;

    AggregateDataCount(boolean all) {
        this.all = all;
    }

    @Override
    void add(SessionLocal session, Value v) {
        if (all || v != ValueNull.INSTANCE) {
            count++;
        }
    }

    @Override
    Value getValue(SessionLocal session) {
        return ValueBigint.get(count);
    }

}
