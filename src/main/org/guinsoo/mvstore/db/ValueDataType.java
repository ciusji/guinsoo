/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.mvstore.db;

import static org.guinsoo.mvstore.DataUtils.readString;
import static org.guinsoo.mvstore.DataUtils.readVarInt;
import static org.guinsoo.mvstore.DataUtils.readVarLong;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

import org.guinsoo.mvstore.rtree.Spatial;
import org.guinsoo.mvstore.rtree.SpatialDataType;
import org.guinsoo.mvstore.type.BasicDataType;
import org.guinsoo.mvstore.type.DataType;
import org.guinsoo.mvstore.type.MetaType;
import org.guinsoo.mvstore.type.StatefulDataType;
import org.guinsoo.api.ErrorCode;
import org.guinsoo.api.IntervalQualifier;
import org.guinsoo.engine.CastDataProvider;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.Mode;
import org.guinsoo.message.DbException;
import org.guinsoo.mode.DefaultNullOrdering;
import org.guinsoo.mvstore.DataUtils;
import org.guinsoo.mvstore.WriteBuffer;
import org.guinsoo.result.RowFactory;
import org.guinsoo.result.SearchRow;
import org.guinsoo.result.SortOrder;
import org.guinsoo.store.DataHandler;
import org.guinsoo.util.DateTimeUtils;
import org.guinsoo.util.Utils;
import org.guinsoo.value.CompareMode;
import org.guinsoo.value.ExtTypeInfoEnum;
import org.guinsoo.value.ExtTypeInfoRow;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueArray;
import org.guinsoo.value.ValueBigint;
import org.guinsoo.value.ValueBinary;
import org.guinsoo.value.ValueBoolean;
import org.guinsoo.value.ValueChar;
import org.guinsoo.value.ValueCollectionBase;
import org.guinsoo.value.ValueDate;
import org.guinsoo.value.ValueDecfloat;
import org.guinsoo.value.ValueDouble;
import org.guinsoo.value.ValueGeometry;
import org.guinsoo.value.ValueInteger;
import org.guinsoo.value.ValueInterval;
import org.guinsoo.value.ValueJavaObject;
import org.guinsoo.value.ValueJson;
import org.guinsoo.value.ValueLob;
import org.guinsoo.value.ValueLobDatabase;
import org.guinsoo.value.ValueLobInMemory;
import org.guinsoo.value.ValueNull;
import org.guinsoo.value.ValueNumeric;
import org.guinsoo.value.ValueReal;
import org.guinsoo.value.ValueRow;
import org.guinsoo.value.ValueSmallint;
import org.guinsoo.value.ValueTime;
import org.guinsoo.value.ValueTimeTimeZone;
import org.guinsoo.value.ValueTimestamp;
import org.guinsoo.value.ValueTimestampTimeZone;
import org.guinsoo.value.ValueTinyint;
import org.guinsoo.value.ValueUuid;
import org.guinsoo.value.ValueVarbinary;
import org.guinsoo.value.ValueVarchar;
import org.guinsoo.value.ValueVarcharIgnoreCase;

/**
 * A row type.
 */
public final class ValueDataType extends BasicDataType<Value> implements StatefulDataType<Database> {

    private static final byte NULL = 0;
    private static final byte TINYINT = 2;
    private static final byte SMALLINT = 3;
    private static final byte INTEGER = 4;
    private static final byte BIGINT = 5;
    private static final byte NUMERIC = 6;
    private static final byte DOUBLE = 7;
    private static final byte REAL = 8;
    private static final byte TIME = 9;
    private static final byte DATE = 10;
    private static final byte TIMESTAMP = 11;
    private static final byte VARBINARY = 12;
    private static final byte VARCHAR = 13;
    private static final byte VARCHAR_IGNORECASE = 14;
    private static final byte BLOB = 15;
    private static final byte CLOB = 16;
    private static final byte ARRAY = 17;
    private static final byte JAVA_OBJECT = 19;
    private static final byte UUID = 20;
    private static final byte CHAR = 21;
    private static final byte GEOMETRY = 22;
    private static final byte TIMESTAMP_TZ_OLD = 24;
    private static final byte ENUM = 25;
    private static final byte INTERVAL = 26;
    private static final byte ROW = 27;
    private static final byte INT_0_15 = 32;
    private static final byte BIGINT_0_7 = 48;
    private static final byte NUMERIC_0_1 = 56;
    private static final byte NUMERIC_SMALL_0 = 58;
    private static final byte NUMERIC_SMALL = 59;
    private static final byte DOUBLE_0_1 = 60;
    private static final byte REAL_0_1 = 62;
    private static final byte BOOLEAN_FALSE = 64;
    private static final byte BOOLEAN_TRUE = 65;
    private static final byte INT_NEG = 66;
    private static final byte BIGINT_NEG = 67;
    private static final byte VARCHAR_0_31 = 68;
    private static final int VARBINARY_0_31 = 100;
    private static final int SPATIAL_KEY_2D = 132;
    // 133 was used for CUSTOM_DATA_TYPE
    private static final int JSON = 134;
    private static final int TIMESTAMP_TZ = 135;
    private static final int TIME_TZ = 136;
    private static final int BINARY = 137;
    private static final int DECFLOAT = 138;

    final DataHandler handler;
    final CastDataProvider provider;
    final CompareMode compareMode;
    protected final Mode mode;
    final int[] sortTypes;
    SpatialDataType spatialType;
    private RowFactory rowFactory;

    public ValueDataType() {
        this(null, CompareMode.getInstance(null, 0), null, null, null);
    }

    public ValueDataType(Database database, int[] sortTypes) {
        this(database, database.getCompareMode(), database.getMode(), database, sortTypes);
    }

    public ValueDataType(CastDataProvider provider, CompareMode compareMode, Mode mode, DataHandler handler,
            int[] sortTypes) {
        this.provider = provider;
        this.compareMode = compareMode;
        this.mode = mode;
        this.handler = handler;
        this.sortTypes = sortTypes;
    }

    public RowFactory getRowFactory() {
        return rowFactory;
    }

    public void setRowFactory(RowFactory rowFactory) {
        this.rowFactory = rowFactory;
    }

    private SpatialDataType getSpatialDataType() {
        if (spatialType == null) {
            spatialType = new SpatialDataType(2) {
                @Override
                protected Spatial create(long id, float... minMax) {
                    return new SpatialKey(id, minMax);
                }
            };
        }
        return spatialType;
    }

    @Override
    public Value[] createStorage(int size) {
        return new Value[size];
    }

    @Override
    public int compare(Value a, Value b) {
        if (a == b) {
            return 0;
        }
        if (a instanceof SearchRow && b instanceof SearchRow) {
            return compare((SearchRow)a, (SearchRow)b);
        } else if (a instanceof ValueCollectionBase && b instanceof ValueCollectionBase) {
            Value[] ax = ((ValueCollectionBase) a).getList();
            Value[] bx = ((ValueCollectionBase) b).getList();
            int al = ax.length;
            int bl = bx.length;
            int len = Math.min(al, bl);
            for (int i = 0; i < len; i++) {
                int sortType = sortTypes == null ? SortOrder.ASCENDING : sortTypes[i];
                Value one = ax[i];
                Value two = bx[i];
                if (one == null || two == null) {
                    return compareValues(ax[len - 1], bx[len - 1], SortOrder.ASCENDING);
                }

                int comp = compareValues(one, two, sortType);
                if (comp != 0) {
                    return comp;
                }
            }
            if (len < al) {
                return -1;
            } else if (len < bl) {
                return 1;
            }
            return 0;
        }
        return compareValues(a, b, SortOrder.ASCENDING);
    }

    private int compare(SearchRow a, SearchRow b) {
        if (a == b) {
            return 0;
        }
        int[] indexes = rowFactory.getIndexes();
        if (indexes == null) {
            int len = a.getColumnCount();
            assert len == b.getColumnCount() : len + " != " + b.getColumnCount();
            for (int i = 0; i < len; i++) {
                int comp = compareValues(a.getValue(i), b.getValue(i), sortTypes[i]);
                if (comp != 0) {
                    return comp;
                }
            }
            return 0;
        } else {
            assert sortTypes.length == indexes.length;
            for (int i = 0; i < indexes.length; i++) {
                int index = indexes[i];
                Value v1 = a.getValue(index);
                Value v2 = b.getValue(index);
                if (v1 == null || v2 == null) {
                    // can't compare further
                    break;
                }
                int comp = compareValues(a.getValue(index), b.getValue(index), sortTypes[i]);
                if (comp != 0) {
                    return comp;
                }
            }
            long aKey = a.getKey();
            long bKey = b.getKey();
            return aKey == SearchRow.MATCH_ALL_ROW_KEY || bKey == SearchRow.MATCH_ALL_ROW_KEY ?
                    0 : Long.compare(aKey, bKey);
        }
    }

    /**
     * Compares the specified values.
     *
     * @param a the first value
     * @param b the second value
     * @param sortType the sorting type
     * @return 0 if equal, -1 if first value is smaller for ascending or larger
     *         for descending sort type, 1 otherwise
     */
    public int compareValues(Value a, Value b, int sortType) {
        if (a == b) {
            return 0;
        }
        boolean aNull = a == ValueNull.INSTANCE;
        if (aNull || b == ValueNull.INSTANCE) {
            /*
             * Indexes with nullable values should have explicit null ordering,
             * so default should not matter.
             */
            return DefaultNullOrdering.LOW.compareNull(aNull, sortType);
        }

        int comp = a.compareTo(b, provider, compareMode);

        if ((sortType & SortOrder.DESCENDING) != 0) {
            comp = -comp;
        }
        return comp;
    }

    @Override
    public int getMemory(Value v) {
        if (v instanceof SpatialKey) {
            return getSpatialDataType().getMemory((SpatialKey) v);
        }
        return v == null ? 0 : v.getMemory();
    }

    @Override
    public Value read(ByteBuffer buff) {
        return readValue(buff, true, null);
    }

    @Override
    public void write(WriteBuffer buff, Value obj) {
        if (obj instanceof SpatialKey) {
            buff.put((byte) SPATIAL_KEY_2D);
            getSpatialDataType().write(buff, (SpatialKey) obj);
            return;
        }
        writeValue(buff, obj, true);
    }

    private void writeValue(WriteBuffer buff, Value v, boolean rowAsRow) {
        if (v == ValueNull.INSTANCE) {
            buff.put((byte) 0);
            return;
        }
        int type = v.getValueType();
        switch (type) {
        case Value.BOOLEAN:
            buff.put(v.getBoolean() ? BOOLEAN_TRUE : BOOLEAN_FALSE);
            break;
        case Value.TINYINT:
            buff.put(TINYINT).put(v.getByte());
            break;
        case Value.SMALLINT:
            buff.put(SMALLINT).putShort(v.getShort());
            break;
        case Value.ENUM:
        case Value.INTEGER: {
            int x = v.getInt();
            if (x < 0) {
                buff.put(INT_NEG).putVarInt(-x);
            } else if (x < 16) {
                buff.put((byte) (INT_0_15 + x));
            } else {
                buff.put(type == Value.INTEGER ? INTEGER : ENUM).putVarInt(x);
            }
            break;
        }
        case Value.BIGINT:
            writeLong(buff, v.getLong());
            break;
        case Value.NUMERIC: {
            BigDecimal x = v.getBigDecimal();
            if (BigDecimal.ZERO.equals(x)) {
                buff.put(NUMERIC_0_1);
            } else if (BigDecimal.ONE.equals(x)) {
                buff.put((byte) (NUMERIC_0_1 + 1));
            } else {
                int scale = x.scale();
                BigInteger b = x.unscaledValue();
                int bits = b.bitLength();
                if (bits <= 63) {
                    if (scale == 0) {
                        buff.put(NUMERIC_SMALL_0).
                            putVarLong(b.longValue());
                    } else {
                        buff.put(NUMERIC_SMALL).
                            putVarInt(scale).
                            putVarLong(b.longValue());
                    }
                } else {
                    byte[] bytes = b.toByteArray();
                    buff.put(NUMERIC).
                        putVarInt(scale).
                        putVarInt(bytes.length).
                        put(bytes);
                }
            }
            break;
        }
        case Value.DECFLOAT: {
            ValueDecfloat d = (ValueDecfloat) v;
            buff.put((byte) DECFLOAT);
            if (d.isFinite()) {
                BigDecimal x = d.getBigDecimal();
                byte[] bytes = x.unscaledValue().toByteArray();
                buff.putVarInt(x.scale()).
                    putVarInt(bytes.length).
                    put(bytes);
            } else {
                int c;
                if (d == ValueDecfloat.NEGATIVE_INFINITY) {
                    c = -3;
                } else if (d == ValueDecfloat.POSITIVE_INFINITY) {
                    c = -2;
                } else {
                    c = -1;
                }
                buff.putVarInt(0).putVarInt(c);
            }
            break;
        }
        case Value.TIME:
            writeTimestampTime(buff.put(TIME), ((ValueTime) v).getNanos());
            break;
        case Value.TIME_TZ: {
            ValueTimeTimeZone t = (ValueTimeTimeZone) v;
            long nanosOfDay = t.getNanos();
            buff.put((byte) TIME_TZ).
                putVarInt((int) (nanosOfDay / DateTimeUtils.NANOS_PER_SECOND)).
                putVarInt((int) (nanosOfDay % DateTimeUtils.NANOS_PER_SECOND));
            writeTimeZone(buff, t.getTimeZoneOffsetSeconds());
            break;
        }
        case Value.DATE:
            buff.put(DATE).putVarLong(((ValueDate) v).getDateValue());
            break;
        case Value.TIMESTAMP: {
            ValueTimestamp ts = (ValueTimestamp) v;
            buff.put(TIMESTAMP).putVarLong(ts.getDateValue());
            writeTimestampTime(buff, ts.getTimeNanos());
            break;
        }
        case Value.TIMESTAMP_TZ: {
            ValueTimestampTimeZone ts = (ValueTimestampTimeZone) v;
            buff.put((byte) TIMESTAMP_TZ).putVarLong(ts.getDateValue());
            writeTimestampTime(buff, ts.getTimeNanos());
            writeTimeZone(buff, ts.getTimeZoneOffsetSeconds());
            break;
        }
        case Value.JAVA_OBJECT:
            writeBinary(JAVA_OBJECT, buff, v);
            break;
        case Value.VARBINARY: {
            byte[] b = v.getBytesNoCopy();
            int len = b.length;
            if (len < 32) {
                buff.put((byte) (VARBINARY_0_31 + len)).put(b);
            } else {
                buff.put(VARBINARY).putVarInt(len).put(b);
            }
            break;
        }
        case Value.BINARY:
            writeBinary((byte) BINARY, buff, v);
            break;
        case Value.UUID: {
            ValueUuid uuid = (ValueUuid) v;
            buff.put(UUID).
                putLong(uuid.getHigh()).
                putLong(uuid.getLow());
            break;
        }
        case Value.VARCHAR: {
            String s = v.getString();
            int len = s.length();
            if (len < 32) {
                buff.put((byte) (VARCHAR_0_31 + len)).putStringData(s, len);
            } else {
                writeString(buff.put(VARCHAR), s);
            }
            break;
        }
        case Value.VARCHAR_IGNORECASE:
            writeString(buff.put(VARCHAR_IGNORECASE), v.getString());
            break;
        case Value.CHAR:
            writeString(buff.put(CHAR), v.getString());
            break;
        case Value.DOUBLE: {
            double x = v.getDouble();
            if (x == 1.0d) {
                buff.put((byte) (DOUBLE_0_1 + 1));
            } else {
                long d = Double.doubleToLongBits(x);
                if (d == ValueDouble.ZERO_BITS) {
                    buff.put(DOUBLE_0_1);
                } else {
                    buff.put(DOUBLE).
                        putVarLong(Long.reverse(d));
                }
            }
            break;
        }
        case Value.REAL: {
            float x = v.getFloat();
            if (x == 1.0f) {
                buff.put((byte) (REAL_0_1 + 1));
            } else {
                int f = Float.floatToIntBits(x);
                if (f == ValueReal.ZERO_BITS) {
                    buff.put(REAL_0_1);
                } else {
                    buff.put(REAL).
                        putVarInt(Integer.reverse(f));
                }
            }
            break;
        }
        case Value.BLOB:
        case Value.CLOB: {
            buff.put(type == Value.BLOB ? BLOB : CLOB);
            ValueLob lob = (ValueLob) v;
            if (lob instanceof ValueLobDatabase) {
                ValueLobDatabase lobDb = (ValueLobDatabase) lob;
                buff.putVarInt(-3).
                    putVarInt(lobDb.getTableId()).
                    putVarLong(lobDb.getLobId()).
                    putVarLong(lob.getType().getPrecision());
            } else {
                byte[] small = ((ValueLobInMemory)lob).getSmall();
                buff.putVarInt(small.length).
                    put(small);
            }
            break;
        }
        case Value.ARRAY:
            if (rowAsRow && rowFactory != null && v instanceof SearchRow) {
                SearchRow row = (SearchRow) v;
                int[] indexes = rowFactory.getIndexes();
                writeRow(buff, row, indexes);
                break;
            }
            //$FALL-THROUGH$
        case Value.ROW: {
            Value[] list = ((ValueCollectionBase) v).getList();
            buff.put(type == Value.ARRAY ? ARRAY : ROW)
                    .putVarInt(list.length);
            for (Value x : list) {
                writeValue(buff, x, false);
            }
            break;
        }
        case Value.GEOMETRY:
            writeBinary(GEOMETRY, buff, v);
            break;
        case Value.INTERVAL_YEAR:
        case Value.INTERVAL_MONTH:
        case Value.INTERVAL_DAY:
        case Value.INTERVAL_HOUR:
        case Value.INTERVAL_MINUTE: {
            ValueInterval interval = (ValueInterval) v;
            int ordinal = type - Value.INTERVAL_YEAR;
            if (interval.isNegative()) {
                ordinal = ~ordinal;
            }
            buff.put(INTERVAL).
                put((byte) ordinal).
                putVarLong(interval.getLeading());
            break;
        }
        case Value.INTERVAL_SECOND:
        case Value.INTERVAL_YEAR_TO_MONTH:
        case Value.INTERVAL_DAY_TO_HOUR:
        case Value.INTERVAL_DAY_TO_MINUTE:
        case Value.INTERVAL_DAY_TO_SECOND:
        case Value.INTERVAL_HOUR_TO_MINUTE:
        case Value.INTERVAL_HOUR_TO_SECOND:
        case Value.INTERVAL_MINUTE_TO_SECOND: {
            ValueInterval interval = (ValueInterval) v;
            int ordinal = type - Value.INTERVAL_YEAR;
            if (interval.isNegative()) {
                ordinal = ~ordinal;
            }
            buff.put(INTERVAL).
                put((byte) (ordinal)).
                putVarLong(interval.getLeading()).
                putVarLong(interval.getRemaining());
            break;
        }
        case Value.JSON:
            writeBinary((byte) JSON, buff, v);
            break;
        default:
            throw DbException.getInternalError("type=" + v.getValueType());
        }
    }

    private static void writeBinary(byte type, WriteBuffer buff, Value v) {
        byte[] b = v.getBytesNoCopy();
        buff.put(type).putVarInt(b.length).put(b);
    }

    void writeRow(WriteBuffer buff, SearchRow row, int[] indexes) {
        buff.put(ARRAY);
        if (indexes == null) {
            int columnCount = row.getColumnCount();
            buff.putVarInt(columnCount + 1);
            for (int i = 0; i < columnCount; i++) {
                writeValue(buff, row.getValue(i), false);
            }
        } else {
            buff.putVarInt(indexes.length + 1);
            for (int i : indexes) {
                writeValue(buff, row.getValue(i), false);
            }
        }
        writeValue(buff, ValueBigint.get(row.getKey()), false);
    }

    /**
     * Writes a long.
     *
     * @param buff the target buffer
     * @param x the long value
     */
    public static void writeLong(WriteBuffer buff, long x) {
        if (x < 0) {
            buff.put(BIGINT_NEG).putVarLong(-x);
        } else if (x < 8) {
            buff.put((byte) (BIGINT_0_7 + x));
        } else {
            buff.put(BIGINT).putVarLong(x);
        }
    }

    private static void writeString(WriteBuffer buff, String s) {
        int len = s.length();
        buff.putVarInt(len).putStringData(s, len);
    }

    private static void writeTimestampTime(WriteBuffer buff, long nanos) {
        long millis = nanos / 1_000_000L;
        buff.putVarLong(millis).putVarInt((int) (nanos - millis * 1_000_000L));
    }

    private static void writeTimeZone(WriteBuffer buff, int timeZoneOffset) {
        // Valid JSR-310 offsets are -64,800..64,800
        // Use 1 byte for common time zones (including +8:45 etc.)
        if (timeZoneOffset % 900 == 0) {
            // -72..72
            buff.put((byte) (timeZoneOffset / 900));
        } else if (timeZoneOffset > 0) {
            buff.put(Byte.MAX_VALUE).putVarInt(timeZoneOffset);
        } else {
            buff.put(Byte.MIN_VALUE).putVarInt(-timeZoneOffset);
        }
    }

    /**
     * Read a value.
     *
     * @param buff the source buffer
     * @param rowAsRow read ARRAY as a search row
     * @param columnType the data type of value, or {@code null}
     * @return the value
     */
    private Value readValue(ByteBuffer buff, boolean rowAsRow, TypeInfo columnType) {
        int type = buff.get() & 255;
        switch (type) {
        case NULL:
            return ValueNull.INSTANCE;
        case BOOLEAN_TRUE:
            return ValueBoolean.TRUE;
        case BOOLEAN_FALSE:
            return ValueBoolean.FALSE;
        case INT_NEG:
            return ValueInteger.get(-readVarInt(buff));
        case INTEGER:
            return ValueInteger.get(readVarInt(buff));
        case BIGINT_NEG:
            return ValueBigint.get(-readVarLong(buff));
        case BIGINT:
            return ValueBigint.get(readVarLong(buff));
        case TINYINT:
            return ValueTinyint.get(buff.get());
        case SMALLINT:
            return ValueSmallint.get(buff.getShort());
        case NUMERIC_0_1:
            return ValueNumeric.ZERO;
        case NUMERIC_0_1 + 1:
            return ValueNumeric.ONE;
        case NUMERIC_SMALL_0:
            return ValueNumeric.get(BigDecimal.valueOf(readVarLong(buff)));
        case NUMERIC_SMALL: {
            int scale = readVarInt(buff);
            return ValueNumeric.get(BigDecimal.valueOf(readVarLong(buff), scale));
        }
        case NUMERIC: {
            int scale = readVarInt(buff);
            return ValueNumeric.get(new BigDecimal(new BigInteger(readVarBytes(buff)), scale));
        }
        case DECFLOAT: {
            int scale = readVarInt(buff), len = readVarInt(buff);
            switch (len) {
            case -3:
                return ValueDecfloat.NEGATIVE_INFINITY;
            case -2:
                return ValueDecfloat.POSITIVE_INFINITY;
            case -1:
                return ValueDecfloat.NAN;
            default:
                byte[] b = Utils.newBytes(len);
                buff.get(b, 0, len);
                return ValueDecfloat.get(new BigDecimal(new BigInteger(b), scale));
            }
        }
        case DATE:
            return ValueDate.fromDateValue(readVarLong(buff));
        case TIME:
            return ValueTime.fromNanos(readTimestampTime(buff));
        case TIME_TZ:
            return ValueTimeTimeZone.fromNanos(readVarInt(buff) * DateTimeUtils.NANOS_PER_SECOND + readVarInt(buff),
                    readTimeZone(buff));
        case TIMESTAMP:
            return ValueTimestamp.fromDateValueAndNanos(readVarLong(buff), readTimestampTime(buff));
        case TIMESTAMP_TZ_OLD:
            return ValueTimestampTimeZone.fromDateValueAndNanos(readVarLong(buff), readTimestampTime(buff),
                    readVarInt(buff) * 60);
        case TIMESTAMP_TZ:
            return ValueTimestampTimeZone.fromDateValueAndNanos(readVarLong(buff), readTimestampTime(buff),
                    readTimeZone(buff));
        case VARBINARY:
            return ValueVarbinary.getNoCopy(readVarBytes(buff));
        case BINARY:
            return ValueBinary.getNoCopy(readVarBytes(buff));
        case JAVA_OBJECT:
            return ValueJavaObject.getNoCopy(readVarBytes(buff));
        case UUID:
            return ValueUuid.get(buff.getLong(), buff.getLong());
        case VARCHAR:
            return ValueVarchar.get(readString(buff));
        case VARCHAR_IGNORECASE:
            return ValueVarcharIgnoreCase.get(readString(buff));
        case CHAR:
            return ValueChar.get(readString(buff));
        case ENUM: {
            int ordinal = readVarInt(buff);
            if (columnType != null) {
                return ((ExtTypeInfoEnum) columnType.getExtTypeInfo()).getValue(ordinal, provider);
            }
            return ValueInteger.get(ordinal);
        }
        case INTERVAL: {
            int ordinal = buff.get();
            boolean negative = ordinal < 0;
            if (negative) {
                ordinal = ~ordinal;
            }
            return ValueInterval.from(IntervalQualifier.valueOf(ordinal), negative, readVarLong(buff),
                    ordinal < 5 ? 0 : readVarLong(buff));
        }
        case REAL_0_1:
            return ValueReal.ZERO;
        case REAL_0_1 + 1:
            return ValueReal.ONE;
        case DOUBLE_0_1:
            return ValueDouble.ZERO;
        case DOUBLE_0_1 + 1:
            return ValueDouble.ONE;
        case DOUBLE:
            return ValueDouble.get(Double.longBitsToDouble(Long.reverse(readVarLong(buff))));
        case REAL:
            return ValueReal.get(Float.intBitsToFloat(Integer.reverse(readVarInt(buff))));
        case BLOB:
        case CLOB: {
            int smallLen = readVarInt(buff);
            if (smallLen >= 0) {
                byte[] small = Utils.newBytes(smallLen);
                buff.get(small, 0, smallLen);
                return ValueLobInMemory.createSmallLob(type == BLOB ? Value.BLOB : Value.CLOB, small);
            } else if (smallLen == -3) {
                int tableId = readVarInt(buff);
                long lobId = readVarLong(buff);
                long precision = readVarLong(buff);
                return ValueLobDatabase.create(type == BLOB ? Value.BLOB : Value.CLOB,
                        handler, tableId, lobId, precision);
            } else {
                throw DbException.get(ErrorCode.FILE_CORRUPTED_1, "lob type: " + smallLen);
            }
        }
        case ARRAY: {
            if (rowAsRow) {
                if (rowFactory != null) {
                    int valueCount = readVarInt(buff) - 1;
                    SearchRow row = rowFactory.createRow();
                    int[] indexes = rowFactory.getIndexes();
                    boolean hasRowKey;
                    TypeInfo[] columnTypes = rowFactory.getColumnTypes();
                    if (indexes == null) {
                        int columnCount = row.getColumnCount();
                        for (int i = 0; i < columnCount; i++) {
                            row.setValue(i, readValue(buff, false, columnTypes != null ? columnTypes[i] : null));
                        }
                        hasRowKey = valueCount == columnCount;
                    } else {
                        for (int i : indexes) {
                            row.setValue(i, readValue(buff, false, columnTypes != null ? columnTypes[i] : null));
                        }
                        hasRowKey = valueCount == indexes.length;
                    }
                    if (hasRowKey) {
                        row.setKey(readValue(buff, false, null).getLong());
                    }
                    return row;
                }
                return ValueRow.get(readArrayElements(buff, null));
            }
            if (columnType != null) {
                TypeInfo elementType = (TypeInfo) columnType.getExtTypeInfo();
                return ValueArray.get(elementType, readArrayElements(buff, elementType), provider);
            }
            return ValueArray.get(readArrayElements(buff, null), provider);
        }
        case ROW: {
            int len = readVarInt(buff);
            Value[] list = new Value[len];
            if (columnType != null) {
                ExtTypeInfoRow extTypeInfoRow = (ExtTypeInfoRow) columnType.getExtTypeInfo();
                Iterator<Entry<String, TypeInfo>> fields = extTypeInfoRow.getFields().iterator();
                for (int i = 0; i < len; i++) {
                    list[i] = readValue(buff, false, fields.next().getValue());
                }
                return ValueRow.get(columnType, list);
            }
            TypeInfo[] columnTypes = rowFactory.getColumnTypes();
            for (int i = 0; i < len; i++) {
                list[i] = readValue(buff, false, columnTypes[i]);
            }
            return ValueRow.get(list);
        }
        case GEOMETRY:
            return ValueGeometry.get(readVarBytes(buff));
        case SPATIAL_KEY_2D:
            return (SpatialKey) getSpatialDataType().read(buff);
        case JSON:
            return ValueJson.getInternal(readVarBytes(buff));
        default:
            if (type >= INT_0_15 && type < INT_0_15 + 16) {
                int i = type - INT_0_15;
                if (columnType != null && columnType.getValueType() == Value.ENUM) {
                    return ((ExtTypeInfoEnum) columnType.getExtTypeInfo()).getValue(i, provider);
                }
                return ValueInteger.get(i);
            } else if (type >= BIGINT_0_7 && type < BIGINT_0_7 + 8) {
                return ValueBigint.get(type - BIGINT_0_7);
            } else if (type >= VARBINARY_0_31 && type < VARBINARY_0_31 + 32) {
                int len = type - VARBINARY_0_31;
                byte[] b = Utils.newBytes(len);
                buff.get(b, 0, len);
                return ValueVarbinary.getNoCopy(b);
            } else if (type >= VARCHAR_0_31 && type < VARCHAR_0_31 + 32) {
                return ValueVarchar.get(readString(buff, type - VARCHAR_0_31));
            }
            throw DbException.get(ErrorCode.FILE_CORRUPTED_1, "type: " + type);
        }
    }

    private Value[] readArrayElements(ByteBuffer buff, TypeInfo elementType) {
        int len = readVarInt(buff);
        Value[] list = new Value[len];
        for (int i = 0; i < len; i++) {
            list[i] = readValue(buff, false, elementType);
        }
        return list;
    }

    private static byte[] readVarBytes(ByteBuffer buff) {
        int len = readVarInt(buff);
        byte[] b = Utils.newBytes(len);
        buff.get(b, 0, len);
        return b;
    }

    private static long readTimestampTime(ByteBuffer buff) {
        return readVarLong(buff) * 1_000_000L + readVarInt(buff);
    }

    private static int readTimeZone(ByteBuffer buff) {
        byte b = buff.get();
        if (b == Byte.MAX_VALUE) {
            return readVarInt(buff);
        } else if (b == Byte.MIN_VALUE) {
            return -readVarInt(buff);
        } else {
            return b * 900;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof ValueDataType)) {
            return false;
        }
        ValueDataType v = (ValueDataType) obj;
        if (!compareMode.equals(v.compareMode)) {
            return false;
        }
        int[] indexes = rowFactory == null ? null : rowFactory.getIndexes();
        int[] indexes2 = v.rowFactory == null ? null : v.rowFactory.getIndexes();
        return Arrays.equals(sortTypes, v.sortTypes)
            && Arrays.equals(indexes, indexes2);
    }

    @Override
    public int hashCode() {
        int[] indexes = rowFactory == null ? null : rowFactory.getIndexes();
        return super.hashCode() ^ Arrays.hashCode(indexes)
            ^ compareMode.hashCode() ^ Arrays.hashCode(sortTypes);
    }

    @Override
    public void save(WriteBuffer buff, MetaType<Database> metaType) {
        writeIntArray(buff, sortTypes);
        int columnCount = rowFactory == null ? 0 : rowFactory.getColumnCount();
        buff.putVarInt(columnCount);
        int[] indexes = rowFactory == null ? null : rowFactory.getIndexes();
        writeIntArray(buff, indexes);
    }

    private static void writeIntArray(WriteBuffer buff, int[] array) {
        if(array == null) {
            buff.putVarInt(0);
        } else {
            buff.putVarInt(array.length + 1);
            for (int i : array) {
                buff.putVarInt(i);
            }
        }
    }

    @Override
    public Factory getFactory() {
        return FACTORY;
    }



    private static final Factory FACTORY = new Factory();

    public static final class Factory implements StatefulDataType.Factory<Database> {

        @Override
        public DataType<?> create(ByteBuffer buff, MetaType<Database> metaType, Database database) {
            int[] sortTypes = readIntArray(buff);
            int columnCount = DataUtils.readVarInt(buff);
            int[] indexes = readIntArray(buff);
            CompareMode compareMode = database == null ? CompareMode.getInstance(null, 0) : database.getCompareMode();
            Mode mode = database == null ? Mode.getRegular() : database.getMode();
            if (database == null) {
                return new ValueDataType();
            } else if (sortTypes == null) {
                return new ValueDataType(database, null);
            }
            RowFactory rowFactory = RowFactory.getDefaultRowFactory()
                    .createRowFactory(database, compareMode, mode, database, sortTypes, indexes, null, columnCount);
            return rowFactory.getRowDataType();
        }

        private static int[] readIntArray(ByteBuffer buff) {
            int len = DataUtils.readVarInt(buff) - 1;
            if(len < 0) {
                return null;
            }
            int[] res = new int[len];
            for (int i = 0; i < res.length; i++) {
                res[i] = DataUtils.readVarInt(buff);
            }
            return res;
        }
    }

}
