/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.value;

import org.guinsoo.engine.SysProperties;
import org.guinsoo.message.DbException;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.util.Utils;

/**
 * Implementation of the JAVA_OBJECT data type.
 */
public final class ValueJavaObject extends ValueBytesBase {

    private static final ValueJavaObject EMPTY = new ValueJavaObject(Utils.EMPTY_BYTES);

    protected ValueJavaObject(byte[] v) {
        super(v);
    }

    /**
     * Get or create a java object value for the given byte array.
     * Do not clone the data.
     *
     * @param b the byte array
     * @return the value
     */
    public static ValueJavaObject getNoCopy(byte[] b) {
        int length = b.length;
        if (length == 0) {
            return EMPTY;
        }
        ValueJavaObject obj = new ValueJavaObject(b);
        if (length > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return obj;
        }
        return (ValueJavaObject) Value.cache(obj);
    }

    @Override
    public TypeInfo getType() {
        return TypeInfo.TYPE_JAVA_OBJECT;
    }

    @Override
    public int getValueType() {
        return JAVA_OBJECT;
    }

    @Override
    public StringBuilder getSQL(StringBuilder builder, int sqlFlags) {
        if ((sqlFlags & NO_CASTS) == 0) {
            return super.getSQL(builder.append("CAST("), DEFAULT_SQL_FLAGS).append(" AS JAVA_OBJECT)");
        }
        return super.getSQL(builder, DEFAULT_SQL_FLAGS);
    }

    @Override
    public String getString() {
        throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, "JAVA_OBJECT to CHARACTER VARYING");
    }

}
