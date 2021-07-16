/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.table;

import org.guinsoo.index.Index;

/**
 * The plan item describes the index to be used, and the estimated cost when
 * using it.
 */
public class PlanItem {

    /**
     * The cost.
     */
    double cost;

    private int[] masks;
    private Index index;
    private PlanItem joinPlan;
    private PlanItem nestedJoinPlan;

    void setMasks(int[] masks) {
        this.masks = masks;
    }

    int[] getMasks() {
        return masks;
    }

    void setIndex(Index index) {
        this.index = index;
    }

    public Index getIndex() {
        return index;
    }

    PlanItem getJoinPlan() {
        return joinPlan;
    }

    PlanItem getNestedJoinPlan() {
        return nestedJoinPlan;
    }

    void setJoinPlan(PlanItem joinPlan) {
        this.joinPlan = joinPlan;
    }

    void setNestedJoinPlan(PlanItem nestedJoinPlan) {
        this.nestedJoinPlan = nestedJoinPlan;
    }

}
