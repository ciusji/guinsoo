/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.analysis;

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
     * @see org.gunsioo.expression.Expression#getSQL(int)
     */
    public String getSQL() {
        return sql;
    }

}
