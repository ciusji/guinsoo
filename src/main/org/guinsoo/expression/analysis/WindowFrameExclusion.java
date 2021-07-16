/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.analysis;

import org.guinsoo.expression.Expression;

/**
 * Window frame exclusion clause.
 */
public enum WindowFrameExclusion {

    /**
     * EXCLUDE CURRENT ROW exclusion clause.
     */
    EXCLUDE_CURRENT_ROW("EXCLUDE CURRENT ROW"),

    /**
     * EXCLUDE GROUP exclusion clause.
     */
    EXCLUDE_GROUP("EXCLUDE GROUP"),

    /**
     * EXCLUDE TIES exclusion clause.
     */
    EXCLUDE_TIES("EXCLUDE TIES"),

    /**
     * EXCLUDE NO OTHERS exclusion clause.
     */
    EXCLUDE_NO_OTHERS("EXCLUDE NO OTHERS"),

    ;

    private final String sql;

    private WindowFrameExclusion(String sql) {
        this.sql = sql;
    }

    /**
     * Returns true if this exclusion clause excludes or includes the whole
     * group.
     *
     * @return true if this exclusion clause is {@link #EXCLUDE_GROUP} or
     *         {@link #EXCLUDE_NO_OTHERS}
     */
    public boolean isGroupOrNoOthers() {
        return this == WindowFrameExclusion.EXCLUDE_GROUP || this == EXCLUDE_NO_OTHERS;
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
