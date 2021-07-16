/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.value;

import java.nio.charset.StandardCharsets;

import org.guinsoo.engine.SysProperties;
import org.guinsoo.util.Utils;

/**
 * Implementation of the BINARY data type.
 */
public final class ValueBinary extends ValueBytesBase {

    /**
     * Associated TypeInfo.
     */
    private TypeInfo type;

    protected ValueBinary(byte[] value) {
        super(value);
    }

    /**
     * Get or create a VARBINARY value for the given byte array.
     * Clone the data.
     *
     * @param b the byte array
     * @return the value
     */
    public static ValueBinary get(byte[] b) {
        return getNoCopy(Utils.cloneByteArray(b));
    }

    /**
     * Get or create a VARBINARY value for the given byte array.
     * Do not clone the date.
     *
     * @param b the byte array
     * @return the value
     */
    public static ValueBinary getNoCopy(byte[] b) {
        ValueBinary obj = new ValueBinary(b);
        if (b.length > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return obj;
        }
        return (ValueBinary) cache(obj);
    }

    @Override
    public TypeInfo getType() {
        TypeInfo type = this.type;
        if (type == null) {
            long precision = value.length;
            this.type = type = new TypeInfo(BINARY, precision, 0, null);
        }
        return type;
    }

    @Override
    public int getValueType() {
        return BINARY;
    }

    @Override
    public StringBuilder getSQL(StringBuilder builder, int sqlFlags) {
        if ((sqlFlags & NO_CASTS) == 0) {
            int length = value.length;
            return super.getSQL(builder.append("CAST("), sqlFlags).append(" AS BINARY(")
                    .append(length > 0 ? length : 1).append("))");
        }
        return super.getSQL(builder, sqlFlags);
    }

    @Override
    public String getString() {
        return new String(value, StandardCharsets.UTF_8);
    }

}
