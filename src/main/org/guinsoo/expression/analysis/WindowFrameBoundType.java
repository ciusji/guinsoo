/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.analysis;

import org.guinsoo.expression.Expression;

/**
 * Window frame bound type.
 */
public enum WindowFrameBoundType {

    /**
     * UNBOUNDED PRECEDING clause.
     */
    UNBOUNDED_PRECEDING("UNBOUNDED PRECEDING"),

    /**
     * PRECEDING clause.
     */
    PRECEDING("PRECEDING"),

    /**
     * CURRENT_ROW clause.
     */
    CURRENT_ROW("CURRENT ROW"),

    /**
     * FOLLOWING clause.
     */
    FOLLOWING("FOLLOWING"),

    /**
     * UNBOUNDED FOLLOWING clause.
     */
    UNBOUNDED_FOLLOWING("UNBOUNDED FOLLOWING");

    private final String sql;

    private WindowFrameBoundType(String sql) {
        this.sql = sql;
    }

    /**
     * Returns SQL representation.
     *
     * @return SQL representation.
     * @see Expression#getSQL(int)
     */
    public String getSQL() {
        return sql;
    }

}
