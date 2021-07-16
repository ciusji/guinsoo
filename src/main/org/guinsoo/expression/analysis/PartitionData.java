/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.analysis;

import java.util.HashMap;

import org.guinsoo.value.Value;

/**
 * Partition data of a window aggregate.
 */
public final class PartitionData {

    /**
     * Aggregate data.
     */
    private Object data;

    /**
     * Evaluated result.
     */
    private Value result;

    /**
     * Evaluated ordered result.
     */
    private HashMap<Integer, Value> orderedResult;

    /**
     * Creates new instance of partition data.
     *
     * @param data
     *            aggregate data
     */
    PartitionData(Object data) {
        this.data = data;
    }

    /**
     * Returns the aggregate data.
     *
     * @return the aggregate data
     */
    Object getData() {
        return data;
    }

    /**
     * Returns the result.
     *
     * @return the result
     */
    Value getResult() {
        return result;
    }

    /**
     * Sets the result.
     *
     * @param result
     *            the result to set
     */
    void setResult(Value result) {
        this.result = result;
        data = null;
    }

    /**
     * Returns the ordered result.
     *
     * @return the ordered result
     */
    HashMap<Integer, Value> getOrderedResult() {
        return orderedResult;
    }

    /**
     * Sets the ordered result.
     *
     * @param orderedResult
     *            the ordered result to set
     */
    void setOrderedResult(HashMap<Integer, Value> orderedResult) {
        this.orderedResult = orderedResult;
        data = null;
    }

}
