/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.value;

import org.gunsioo.engine.CastDataProvider;
import org.gunsioo.engine.SysProperties;
import org.gunsioo.util.StringUtils;

/**
 * Implementation of the CHARACTER data type.
 */
public final class ValueChar extends ValueStringBase {

    private ValueChar(String value) {
        super(value);
    }

    @Override
    public int getValueType() {
        return CHAR;
    }

    @Override
    public int compareTypeSafe(Value v, CompareMode mode, CastDataProvider provider) {
        return mode.compareString(convertToChar().getString(), v.convertToChar().getString(), false);
    }

    @Override
    public StringBuilder getSQL(StringBuilder builder, int sqlFlags) {
        if ((sqlFlags & NO_CASTS) == 0) {
            int length = value.length();
            return StringUtils.quoteStringSQL(builder.append("CAST("), value).append(" AS CHAR(")
                    .append(length > 0 ? length : 1).append("))");
        }
        return StringUtils.quoteStringSQL(builder, value);
    }

    /**
     * Get or create a CHAR value for the given string.
     *
     * @param s the string
     * @return the value
     */
    public static ValueChar get(String s) {
        ValueChar obj = new ValueChar(StringUtils.cache(s));
        if (s.length() > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return obj;
        }
        return (ValueChar) Value.cache(obj);
    }

}
