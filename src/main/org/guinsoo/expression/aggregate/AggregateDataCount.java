/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.aggregate;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueBigint;
import org.guinsoo.value.ValueNull;

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
