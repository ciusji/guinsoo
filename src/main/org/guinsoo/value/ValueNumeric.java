/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.guinsoo.engine.CastDataProvider;
import org.guinsoo.message.DbException;
import org.guinsoo.api.ErrorCode;

/**
 * Implementation of the NUMERIC data type.
 */
public final class ValueNumeric extends ValueBigDecimalBase {

    /**
     * The value 'zero'.
     */
    public static final ValueNumeric ZERO = new ValueNumeric(BigDecimal.ZERO);

    /**
     * The value 'one'.
     */
    public static final ValueNumeric ONE = new ValueNumeric(BigDecimal.ONE);

    /**
     * The default scale for a NUMERIC value.
     */
    public static final int DEFAULT_SCALE = 0;

    /**
     * The maximum scale.
     */
    public static final int MAXIMUM_SCALE = 100_000;

    private ValueNumeric(BigDecimal value) {
        super(value);
        if (value == null) {
            throw new IllegalArgumentException("null");
        }
        int scale = value.scale();
        if (scale < 0 || scale > MAXIMUM_SCALE) {
            throw DbException.get(ErrorCode.INVALID_VALUE_SCALE, Integer.toString(scale), "0", "" + MAXIMUM_SCALE);
        }
    }

    @Override
    public String getString() {
        return value.toPlainString();
    }

    @Override
    public StringBuilder getSQL(StringBuilder builder, int sqlFlags) {
        String s = getString();
        if ((sqlFlags & NO_CASTS) == 0 && s.indexOf('.') < 0 && value.compareTo(MAX_LONG_DECIMAL) <= 0
                && value.compareTo(MIN_LONG_DECIMAL) >= 0) {
            return builder.append("CAST(").append(value).append(" AS NUMERIC(").append(value.precision()).append("))");
        }
        return builder.append(s);
    }

    @Override
    public TypeInfo getType() {
        TypeInfo type = this.type;
        if (type == null) {
            this.type = type = new TypeInfo(NUMERIC, value.precision(), value.scale(), null);
        }
        return type;
    }

    @Override
    public int getValueType() {
        return NUMERIC;
    }

    @Override
    public Value add(Value v) {
        return get(value.add(((ValueNumeric) v).value));
    }

    @Override
    public Value subtract(Value v) {
        return get(value.subtract(((ValueNumeric) v).value));
    }

    @Override
    public Value negate() {
        return get(value.negate());
    }

    @Override
    public Value multiply(Value v) {
        return get(value.multiply(((ValueNumeric) v).value));
    }

    @Override
    public Value divide(Value v, long divisorPrecision) {
        BigDecimal divisor = ((ValueNumeric) v).value;
        if (divisor.signum() == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return get(value.divide(divisor,
                getQuotientScale(value.scale(), divisorPrecision, divisor.scale()), RoundingMode.HALF_DOWN));
    }

    @Override
    public Value modulus(Value v) {
        ValueBigDecimalBase dec = (ValueNumeric) v;
        if (dec.value.signum() == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return get(value.remainder(dec.value));
    }

    @Override
    public int compareTypeSafe(Value o, CompareMode mode, CastDataProvider provider) {
        return value.compareTo(((ValueNumeric) o).value);
    }

    @Override
    public int getSignum() {
        return value.signum();
    }

    @Override
    public BigDecimal getBigDecimal() {
        return value;
    }

    @Override
    public float getFloat() {
        return value.floatValue();
    }

    @Override
    public double getDouble() {
        return value.doubleValue();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() * 31 + value.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ValueNumeric && value.equals(((ValueNumeric) other).value);
    }

    @Override
    public int getMemory() {
        return value.precision() + 120;
    }

    /**
     * Get or create a NUMERIC value for the given big decimal.
     *
     * @param dec the big decimal
     * @return the value
     */
    public static ValueNumeric get(BigDecimal dec) {
        if (BigDecimal.ZERO.equals(dec)) {
            return ZERO;
        } else if (BigDecimal.ONE.equals(dec)) {
            return ONE;
        }
        return (ValueNumeric) cache(new ValueNumeric(dec));
    }

    /**
     * Get or create a NUMERIC value for the given big integer.
     *
     * @param bigInteger the big integer
     * @return the value
     */
    public static ValueNumeric get(BigInteger bigInteger) {
        if (bigInteger.signum() == 0) {
            return ZERO;
        } else if (BigInteger.ONE.equals(bigInteger)) {
            return ONE;
        }
        return (ValueNumeric) cache(new ValueNumeric(new BigDecimal(bigInteger)));
    }

    /**
     * Set the scale of a BigDecimal value.
     *
     * @param bd the BigDecimal value
     * @param scale the new scale
     * @return the scaled value
     */
    public static BigDecimal setScale(BigDecimal bd, int scale) {
        if (scale < 0 || scale > MAXIMUM_SCALE) {
            throw DbException.getInvalidValueException("scale", scale);
        }
        return bd.setScale(scale, RoundingMode.HALF_UP);
    }

}
