/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.query;

import org.guinsoo.expression.Expression;
import org.guinsoo.result.SortOrder;

/**
 * Describes one element of the ORDER BY clause of a query.
 */
public class QueryOrderBy {

    /**
     * The order by expression.
     */
    public Expression expression;

    /**
     * The column index expression. This can be a column index number (1 meaning
     * the first column of the select list) or a parameter (the parameter is a
     * number representing the column index number).
     */
    public Expression columnIndexExpr;

    /**
     * Sort type for this column.
     */
    public int sortType;

    /**
     * Appends the order by expression to the specified builder.
     *
     * @param builder the string builder
     * @param sqlFlags formatting flags
     */
    public void getSQL(StringBuilder builder, int sqlFlags) {
        (expression != null ? expression : columnIndexExpr).getUnenclosedSQL(builder, sqlFlags);
        SortOrder.typeToString(builder, sortType);
    }

}
