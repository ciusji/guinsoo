/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.value;

import org.gunsioo.util.StringUtils;

/**
 * ENUM value.
 */
public final class ValueEnum extends ValueEnumBase {

    private final ExtTypeInfoEnum enumerators;

    ValueEnum(ExtTypeInfoEnum enumerators, String label, int ordinal) {
        super(label, ordinal);
        this.enumerators = enumerators;
    }

    @Override
    public TypeInfo getType() {
        return enumerators.getType();
    }

    public ExtTypeInfoEnum getEnumerators() {
        return enumerators;
    }

    @Override
    public StringBuilder getSQL(StringBuilder builder, int sqlFlags) {
        if ((sqlFlags & NO_CASTS) == 0) {
            StringUtils.quoteStringSQL(builder.append("CAST("), label).append(" AS ");
            return enumerators.getType().getSQL(builder, sqlFlags).append(')');
        }
        return StringUtils.quoteStringSQL(builder, label);
    }

}
