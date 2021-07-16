/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.value;

import java.util.Arrays;

import org.guinsoo.engine.CastDataProvider;
import org.guinsoo.engine.Constants;
import org.guinsoo.message.DbException;
import org.guinsoo.util.Bits;
import org.guinsoo.util.StringUtils;
import org.guinsoo.util.Utils;

/**
 * Base implementation of byte array based data types.
 */
abstract class ValueBytesBase extends Value {

    /**
     * The value.
     */
    byte[] value;

    /**
     * The hash code.
     */
    int hash;

    ValueBytesBase(byte[] value) {
        int length = value.length;
        if (length > Constants.MAX_STRING_LENGTH) {
            throw DbException.getValueTooLongException(getTypeName(getValueType()),
                    StringUtils.convertBytesToHex(value, 41), length);
        }
        this.value = value;
    }

    @Override
    public final byte[] getBytes() {
        return Utils.cloneByteArray(value);
    }

    @Override
    public final byte[] getBytesNoCopy() {
        return value;
    }

    @Override
    public final int compareTypeSafe(Value v, CompareMode mode, CastDataProvider provider) {
        return Bits.compareNotNullUnsigned(value, ((ValueBytesBase) v).value);
    }

    @Override
    public StringBuilder getSQL(StringBuilder builder, int sqlFlags) {
        return StringUtils.convertBytesToHex(builder.append("X'"), value).append('\'');
    }

    @Override
    public final int hashCode() {
        int h = hash;
        if (h == 0) {
            h = getClass().hashCode() ^ Utils.getByteArrayHash(value);
            if (h == 0) {
                h = 1_234_570_417;
            }
            hash = h;
        }
        return h;
    }

    @Override
    public int getMemory() {
        return value.length + 24;
    }

    @Override
    public final boolean equals(Object other) {
        return other != null && getClass() == other.getClass() && Arrays.equals(value, ((ValueBytesBase) other).value);
    }

}
