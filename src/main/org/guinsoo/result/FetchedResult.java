/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.result;

import org.guinsoo.engine.Session;
import org.guinsoo.value.Value;

/**
 * Abstract fetched result.
 */
public abstract class FetchedResult implements ResultInterface {

    long rowId = -1;

    Value[] currentRow;

    Value[] nextRow;

    boolean afterLast;

    FetchedResult() {
    }

    @Override
    public final Value[] currentRow() {
        return currentRow;
    }

    @Override
    public final boolean next() {
        if (hasNext()) {
            rowId++;
            currentRow = nextRow;
            nextRow = null;
            return true;
        }
        if (!afterLast) {
            rowId++;
            currentRow = null;
            afterLast = true;
        }
        return false;
    }

    @Override
    public final boolean isAfterLast() {
        return afterLast;
    }

    @Override
    public final long getRowId() {
        return rowId;
    }

    @Override
    public final boolean needToClose() {
        return true;
    }

    @Override
    public final ResultInterface createShallowCopy(Session targetSession) {
        // The operation is not supported on fetched result.
        return null;
    }

}
