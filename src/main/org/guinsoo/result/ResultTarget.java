/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.result;

import org.guinsoo.value.Value;

/**
 * A object where rows are written to.
 */
public interface ResultTarget {

    /**
     * Add the row to the result set.
     *
     * @param values the values
     */
    void addRow(Value... values);

    /**
     * Get the number of rows.
     *
     * @return the number of rows
     */
    long getRowCount();

    /**
     * A hint that sorting, offset and limit may be ignored by this result
     * because they were applied during the query. This is useful for WITH TIES
     * clause because result may contain tied rows above limit.
     */
    void limitsWereApplied();

}
