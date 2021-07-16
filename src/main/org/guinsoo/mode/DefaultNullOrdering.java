/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.mode;

import org.guinsoo.result.SortOrder;

/**
 * Default ordering of NULL values.
 */
public enum DefaultNullOrdering {

    /**
     * NULL values are considered as smaller than other values during sorting.
     */
    LOW(SortOrder.NULLS_FIRST, SortOrder.NULLS_LAST),

    /**
     * NULL values are considered as larger than other values during sorting.
     */
    HIGH(SortOrder.NULLS_LAST, SortOrder.NULLS_FIRST),

    /**
     * NULL values are sorted before other values, no matter if ascending or
     * descending order is used.
     */
    FIRST(SortOrder.NULLS_FIRST, SortOrder.NULLS_FIRST),

    /**
     * NULL values are sorted after other values, no matter if ascending or
     * descending order is used.
     */
    LAST(SortOrder.NULLS_LAST, SortOrder.NULLS_LAST);

    private static final DefaultNullOrdering[] VALUES = values();

    /**
     * Returns default ordering of NULL values for the specified ordinal number.
     *
     * @param ordinal
     *            ordinal number
     * @return default ordering of NULL values for the specified ordinal number
     * @see #ordinal()
     */
    public static DefaultNullOrdering valueOf(int ordinal) {
        return VALUES[ordinal];
    }

    private final int defaultAscNulls, defaultDescNulls;

    private final int nullAsc, nullDesc;

    private DefaultNullOrdering(int defaultAscNulls, int defaultDescNulls) {
        this.defaultAscNulls = defaultAscNulls;
        this.defaultDescNulls = defaultDescNulls;
        nullAsc = defaultAscNulls == SortOrder.NULLS_FIRST ? -1 : 1;
        nullDesc = defaultDescNulls == SortOrder.NULLS_FIRST ? -1 : 1;
    }

    /**
     * Returns a sort type bit mask with {@link #NULLS_FIRST} or
     * {@link #NULLS_LAST} explicitly set
     *
     * @param sortType
     *            sort type bit mask
     * @return bit mask with {@link #NULLS_FIRST} or {@link #NULLS_LAST}
     *         explicitly set
     */
    public int addExplicitNullOrdering(int sortType) {
        if ((sortType & (SortOrder.NULLS_FIRST | SortOrder.NULLS_LAST)) == 0) {
            sortType |= ((sortType & SortOrder.DESCENDING) == 0 ? defaultAscNulls : defaultDescNulls);
        }
        return sortType;
    }

    /**
     * Compare two expressions where one of them is NULL.
     *
     * @param aNull
     *            whether the first expression is null
     * @param sortType
     *            the sort bit mask to use
     * @return the result of the comparison (-1 meaning the first expression
     *         should appear before the second, 0 if they are equal)
     */
    public int compareNull(boolean aNull, int sortType) {
        if ((sortType & SortOrder.NULLS_FIRST) != 0) {
            return aNull ? -1 : 1;
        } else if ((sortType & SortOrder.NULLS_LAST) != 0) {
            return aNull ? 1 : -1;
        } else if ((sortType & SortOrder.DESCENDING) == 0) {
            return aNull ? nullAsc : -nullAsc;
        } else {
            return aNull ? nullDesc : -nullDesc;
        }
    }

}
