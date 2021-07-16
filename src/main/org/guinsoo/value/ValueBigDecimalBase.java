/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.value;

import java.math.BigDecimal;

import org.guinsoo.engine.Constants;
import org.guinsoo.message.DbException;
import org.guinsoo.api.ErrorCode;

/**
 * Base class for BigDecimal-based values.
 */
abstract class ValueBigDecimalBase extends Value {

    final BigDecimal value;

    TypeInfo type;

    ValueBigDecimalBase(BigDecimal value) {
        if (value != null) {
            if (value.getClass() != BigDecimal.class) {
                throw DbException.get(ErrorCode.INVALID_CLASS_2, BigDecimal.class.getName(),
                        value.getClass().getName());
            }
            int length = value.precision();
            if (length > Constants.MAX_NUMERIC_PRECISION) {
                throw DbException.getValueTooLongException(getTypeName(getValueType()), value.toString(), length);
            }
        }
        this.value = value;
    }

    /**
     * Evaluates the scale of the quotient.
     *
     * @param dividerScale
     *            the scale of the divider
     * @param divisorPrecision
     *            the precision of the divisor
     * @param divisorScale
     *            the scale of the divisor
     * @return the scale of the quotient
     */
    public static int getQuotientScale(int dividerScale, long divisorPrecision, int divisorScale) {
        long scale = dividerScale - divisorScale + divisorPrecision * 2;
        return scale >= ValueNumeric.MAXIMUM_SCALE ? ValueNumeric.MAXIMUM_SCALE : (int) scale;
    }

}
