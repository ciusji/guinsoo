/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0, and the
 * EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt). Initial Developer: H2
 * Group
 */
package org.guinsoo.value;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.guinsoo.engine.CastDataProvider;
import org.guinsoo.engine.Constants;
import org.guinsoo.message.DbException;
import org.guinsoo.store.DataHandler;
import org.guinsoo.store.LobStorageInterface;
import org.guinsoo.util.Bits;
import org.guinsoo.util.IOUtils;
import org.guinsoo.util.MathUtils;
import org.guinsoo.util.StringUtils;
import org.guinsoo.util.Utils;

/**
 * A implementation of the BLOB and CLOB data types. Small objects are kept in
 * memory and stored in the record. Large objects are either stored in the
 * database, or in temporary files.
 */
public final class ValueLobInMemory extends ValueLob {

    /**
     * If the LOB is below the inline size, we just store/load it directly here.
     */
    private final byte[] small;

    private ValueLobInMemory(int type, byte[] small, long precision) {
        super(type, precision);
        if (small == null) {
            throw new IllegalStateException();
        }
        this.small = small;
    }

    /**
     * Copy a large value, to be used in the given table. For values that are
     * kept fully in memory this method has no effect.
     *
     * @param database the data handler
     * @param tableId the table where this object is used
     * @return the new value or itself
     */
    @Override
    public ValueLob copy(DataHandler database, int tableId) {
        if (small.length > database.getMaxLengthInplaceLob()) {
            LobStorageInterface s = database.getLobStorage();
            ValueLob v;
            if (valueType == BLOB) {
                v = s.createBlob(getInputStream(), precision);
            } else {
                v = s.createClob(getReader(), precision);
            }
            ValueLob v2 = v.copy(database, tableId);
            v.remove();
            return v2;
        }
        return this;
    }

    @Override
    public String getString() {
        if (valueType == CLOB) {
            if (precision > Constants.MAX_STRING_LENGTH) {
                throw DbException.getValueTooLongException("CHARACTER VARYING",
                        new String(small, 0, 81 * 3, StandardCharsets.UTF_8), precision);
            }
        } else {
            long p = otherPrecision;
            if (p > Constants.MAX_STRING_LENGTH) {
                throw DbException.getValueTooLongException("CHARACTER VARYING",
                        new String(small, 0, 81 * 3, StandardCharsets.UTF_8), p);
            } else if (p < 0L) {
                String s = new String(small, StandardCharsets.UTF_8);
                otherPrecision = p = s.length();
                if (p > Constants.MAX_STRING_LENGTH) {
                    throw DbException.getValueTooLongException("CHARACTER VARYING", s, p);
                }
                return s;
            }
        }
        return new String(small, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] getBytes() {
        return Utils.cloneByteArray(getBytesNoCopy());
    }

    @Override
    public byte[] getBytesNoCopy() {
        int p = small.length;
        if (p > Constants.MAX_STRING_LENGTH) {
            throw DbException.getValueTooLongException("BINARY VARYING", StringUtils.convertBytesToHex(small, 41), p);
        }
        return small;
    }

    @Override
    public int compareTypeSafe(Value v, CompareMode mode, CastDataProvider provider) {
        if (v == this) {
            return 0;
        }
        ValueLobInMemory v2 = (ValueLobInMemory) v;
        if (v2 != null) {
            if (valueType == BLOB) {
                return Bits.compareNotNullUnsigned(small, v2.small);
            } else {
                return Integer.signum(getString().compareTo(v2.getString()));
            }
        }
        return compare(this, v2);
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(small);
    }

    @Override
    public InputStream getInputStream(long oneBasedOffset, long length) {
        final long byteCount = (valueType == BLOB) ? precision : -1;
        return rangeInputStream(getInputStream(), oneBasedOffset, length, byteCount);
    }

    /**
     * Get the data if this a small lob value.
     *
     * @return the data
     */
    public byte[] getSmall() {
        return small;
    }

    @Override
    public int getMemory() {
        /*
         * Java 11 with -XX:-UseCompressedOops 0 bytes: 120 bytes 1 byte: 128
         * bytes
         */
        return small.length + 127;
    }

    /**
     * Convert the precision to the requested value.
     *
     * @param precision the new precision
     * @return the truncated or this value
     */
    @Override
    ValueLob convertPrecision(long precision) {
        if (this.precision <= precision) {
            return this;
        }
        ValueLob lob;
        if (valueType == CLOB) {
            try {
                int p = MathUtils.convertLongToInt(precision);
                String s = IOUtils.readStringAndClose(getReader(), p);
                byte[] data = s.getBytes(StandardCharsets.UTF_8);
                lob = createSmallLob(valueType, data, s.length());
            } catch (IOException e) {
                throw DbException.convertIOException(e, null);
            }
        } else {
            try {
                int p = MathUtils.convertLongToInt(precision);
                byte[] data = IOUtils.readBytesAndClose(getInputStream(), p);
                lob = createSmallLob(valueType, data, data.length);
            } catch (IOException e) {
                throw DbException.convertIOException(e, null);
            }
        }
        return lob;
    }

    /**
     * Create a LOB object that fits in memory.
     *
     * @param type the type (Value.BLOB or CLOB)
     * @param small the byte array
     * @return the LOB
     */
    public static ValueLobInMemory createSmallLob(int type, byte[] small) {
        int precision;
        if (type == CLOB) {
            precision = new String(small, StandardCharsets.UTF_8).length();
        } else {
            precision = small.length;
        }
        return createSmallLob(type, small, precision);
    }

    /**
     * Create a LOB object that fits in memory.
     *
     * @param type the type (Value.BLOB or CLOB)
     * @param small the byte array
     * @param precision the precision
     * @return the LOB
     */
    public static ValueLobInMemory createSmallLob(int type, byte[] small, long precision) {
        return new ValueLobInMemory(type, small, precision);
    }

    @Override
    public long charLength() {
        if (valueType == CHAR) {
            return precision;
        } else {
            long p = otherPrecision;
            if (p < 0L) {
                otherPrecision = p = getString().length();
            }
            return p;
        }
    }

    @Override
    public long octetLength() {
        return small.length;
    }

}
