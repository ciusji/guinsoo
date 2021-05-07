/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.analysis;

import org.guinsoo.expression.Expression;

/**
 * Window frame units.
 */
public enum WindowFrameUnits {

    /**
     * ROWS unit.
     */
    ROWS,

    /**
     * RANGE unit.
     */
    RANGE,

    /**
     * GROUPS unit.
     */
    GROUPS,

    ;

    /**
     * Returns SQL representation.
     *
     * @return SQL representation.
     * @see Expression#getSQL(int)
     */
    public String getSQL() {
        return name();
    }

}
