/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.api;

import java.sql.SQLType;

import org.guinsoo.value.ExtTypeInfoRow;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;

/**
 * Data types of Guinsoo.
 */
public final class DBType implements SQLType {

    // Character strings

    /**
     * The CHARACTER data type.
     */
    public static final DBType CHAR = new DBType(TypeInfo.getTypeInfo(Value.CHAR), "CHARACTER");

    /**
     * The CHARACTER VARYING data type.
     */
    public static final DBType VARCHAR = new DBType(TypeInfo.TYPE_VARCHAR, "CHARACTER VARYING");

    /**
     * The CHARACTER LARGE OBJECT data type.
     */
    public static final DBType CLOB = new DBType(TypeInfo.TYPE_CLOB, "CHARACTER LARGE OBJECT");

    /**
     * The VARCHAR_IGNORECASE data type.
     */
    public static final DBType VARCHAR_IGNORECASE = new DBType(TypeInfo.TYPE_VARCHAR_IGNORECASE, "VARCHAR_IGNORECASE");

    // Binary strings

    /**
     * The BINARY data type.
     */
    public static final DBType BINARY = new DBType(TypeInfo.getTypeInfo(Value.BINARY), "BINARY");

    /**
     * The BINARY VARYING data type.
     */
    public static final DBType VARBINARY = new DBType(TypeInfo.TYPE_VARBINARY, "BINARY VARYING");

    /**
     * The BINARY LARGE OBJECT data type.
     */
    public static final DBType BLOB = new DBType(TypeInfo.TYPE_BLOB, "BINARY LARGE OBJECT");

    // Boolean

    /**
     * The BOOLEAN data type
     */
    public static final DBType BOOLEAN = new DBType(TypeInfo.TYPE_BOOLEAN, "BOOLEAN");

    // Exact numeric data types

    /**
     * The TINYINT data type.
     */
    public static final DBType TINYINT = new DBType(TypeInfo.TYPE_TINYINT, "TINYINT");

    /**
     * The SMALLINT data type.
     */
    public static final DBType SMALLINT = new DBType(TypeInfo.TYPE_SMALLINT, "SMALLINT");

    /**
     * The INTEGER data type.
     */
    public static final DBType INTEGER = new DBType(TypeInfo.TYPE_INTEGER, "INTEGER");

    /**
     * The BIGINT data type.
     */
    public static final DBType BIGINT = new DBType(TypeInfo.TYPE_BIGINT, "BIGINT");

    /**
     * The NUMERIC data type.
     */
    public static final DBType NUMERIC = new DBType(TypeInfo.TYPE_NUMERIC, "NUMERIC");

    // Approximate numeric data types

    /**
     * The REAL data type.
     */
    public static final DBType REAL = new DBType(TypeInfo.TYPE_REAL, "REAL");

    /**
     * The DOUBLE PRECISION data type.
     */
    public static final DBType DOUBLE_PRECISION = new DBType(TypeInfo.TYPE_DOUBLE, "DOUBLE PRECISION");

    // Decimal floating-point type

    /**
     * The DECFLOAT data type.
     */
    public static final DBType DECFLOAT = new DBType(TypeInfo.TYPE_DECFLOAT, "DECFLOAT");

    // Date-time data types

    /**
     * The DATE data type.
     */
    public static final DBType DATE = new DBType(TypeInfo.TYPE_DATE, "DATE");

    /**
     * The TIME data type.
     */
    public static final DBType TIME = new DBType(TypeInfo.TYPE_TIME, "TIME");

    /**
     * The TIME WITH TIME ZONE data type.
     */
    public static final DBType TIME_WITH_TIME_ZONE = new DBType(TypeInfo.TYPE_TIME_TZ, "TIME WITH TIME ZONE");

    /**
     * The TIMESTAMP data type.
     */
    public static final DBType TIMESTAMP = new DBType(TypeInfo.TYPE_TIMESTAMP, "TIMESTAMP");

    /**
     * The TIMESTAMP WITH TIME ZONE data type.
     */
    public static final DBType TIMESTAMP_WITH_TIME_ZONE = new DBType(TypeInfo.TYPE_TIMESTAMP_TZ,
            "TIMESTAMP WITH TIME ZONE");

    // Intervals

    /**
     * The INTERVAL YEAR data type.
     */
    public static final DBType INTERVAL_YEAR = new DBType(TypeInfo.getTypeInfo(Value.INTERVAL_YEAR), "INTERVAL_YEAR");

    /**
     * The INTERVAL MONTH data type.
     */
    public static final DBType INTERVAL_MONTH = new DBType(TypeInfo.getTypeInfo(Value.INTERVAL_MONTH),
            "INTERVAL_MONTH");

    /**
     * The INTERVAL DAY data type.
     */
    public static final DBType INTERVAL_DAY = new DBType(TypeInfo.TYPE_INTERVAL_DAY, "INTERVAL_DAY");

    /**
     * The INTERVAL HOUR data type.
     */
    public static final DBType INTERVAL_HOUR = new DBType(TypeInfo.getTypeInfo(Value.INTERVAL_HOUR), "INTERVAL_HOUR");

    /**
     * The INTERVAL MINUTE data type.
     */
    public static final DBType INTERVAL_MINUTE = new DBType(TypeInfo.getTypeInfo(Value.INTERVAL_MINUTE),
            "INTERVAL_MINUTE");

    /**
     * The INTERVAL SECOND data type.
     */
    public static final DBType INTERVAL_SECOND = new DBType(TypeInfo.getTypeInfo(Value.INTERVAL_SECOND),
            "INTERVAL_SECOND");

    /**
     * The INTERVAL YEAR TO MONTH data type.
     */
    public static final DBType INTERVAL_YEAR_TO_MONTH = new DBType(TypeInfo.TYPE_INTERVAL_YEAR_TO_MONTH,
            "INTERVAL_YEAR_TO_MONTH");

    /**
     * The INTERVAL DAY TO HOUR data type.
     */
    public static final DBType INTERVAL_DAY_TO_HOUR = new DBType(TypeInfo.getTypeInfo(Value.INTERVAL_DAY_TO_HOUR),
            "INTERVAL_DAY_TO_HOUR");

    /**
     * The INTERVAL DAY TO MINUTE data type.
     */
    public static final DBType INTERVAL_DAY_TO_MINUTE = new DBType(TypeInfo.getTypeInfo(Value.INTERVAL_DAY_TO_MINUTE),
            "INTERVAL_DAY_TO_MINUTE");

    /**
     * The INTERVAL DAY TO SECOND data type.
     */
    public static final DBType INTERVAL_DAY_TO_SECOND = new DBType(TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND,
            "INTERVAL_DAY_TO_SECOND");

    /**
     * The INTERVAL HOUR TO MINUTE data type.
     */
    public static final DBType INTERVAL_HOUR_TO_MINUTE = new DBType( //
            TypeInfo.getTypeInfo(Value.INTERVAL_HOUR_TO_MINUTE), "INTERVAL_HOUR_TO_MINUTE");

    /**
     * The INTERVAL HOUR TO SECOND data type.
     */
    public static final DBType INTERVAL_HOUR_TO_SECOND = new DBType(TypeInfo.TYPE_INTERVAL_HOUR_TO_SECOND,
            "INTERVAL_HOUR_TO_SECOND");

    /**
     * The INTERVAL MINUTE TO SECOND data type.
     */
    public static final DBType INTERVAL_MINUTE_TO_SECOND = new DBType(
            TypeInfo.getTypeInfo(Value.INTERVAL_MINUTE_TO_SECOND), "INTERVAL_MINUTE_TO_SECOND");

    // Other JDBC

    /**
     * The JAVA_OBJECT data type.
     */
    public static final DBType JAVA_OBJECT = new DBType(TypeInfo.TYPE_JAVA_OBJECT, "JAVA_OBJECT");

    // Other non-standard

    /**
     * The ENUM data type.
     */
    public static final DBType ENUM = new DBType(TypeInfo.TYPE_ENUM_UNDEFINED, "ENUM");

    /**
     * The GEOMETRY data type.
     */
    public static final DBType GEOMETRY = new DBType(TypeInfo.TYPE_GEOMETRY, "GEOMETRY");

    /**
     * The JSON data type.
     */
    public static final DBType JSON = new DBType(TypeInfo.TYPE_JSON, "JSON");

    /**
     * The UUID data type.
     */
    public static final DBType UUID = new DBType(TypeInfo.TYPE_UUID, "UUID");

    // Collections

    // Use arrayOf() for ARRAY

    // Use row() for ROW

    /**
     * Returns ARRAY data type with the specified component type.
     *
     * @param componentType
     *            the type of elements
     * @return ARRAY data type
     */
    public static DBType array(DBType componentType) {
        return new DBType(TypeInfo.getTypeInfo(Value.ARRAY, -1L, -1, componentType.typeInfo),
                "array(" + componentType.field + ')');
    }

    /**
     * Returns ROW data type with specified types of fields and default names.
     *
     * @param fieldTypes
     *            the type of fields
     * @return ROW data type
     */
    public static DBType row(DBType... fieldTypes) {
        int degree = fieldTypes.length;
        TypeInfo[] row = new TypeInfo[degree];
        StringBuilder builder = new StringBuilder("row(");
        for (int i = 0; i < degree; i++) {
            DBType t = fieldTypes[i];
            row[i] = t.typeInfo;
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(t.field);
        }
        return new DBType(TypeInfo.getTypeInfo(Value.ROW, -1L, -1, new ExtTypeInfoRow(row)),
                builder.append(')').toString());
    }

    private TypeInfo typeInfo;

    private String field;

    private DBType(TypeInfo typeInfo, String field) {
        this.typeInfo = typeInfo;
        this.field = "DBType." + field;
    }

    @Override
    public String getName() {
        return typeInfo.toString();
    }

    @Override
    public String getVendor() {
        return "com.guinsoodatabase";
    }

    /**
     * Returns the vendor specific type number for the data type. The returned
     * value is actual only for the current version of H2.
     *
     * @return the vendor specific data type
     */
    @Override
    public Integer getVendorTypeNumber() {
        return typeInfo.getValueType();
    }

    @Override
    public String toString() {
        return field;
    }

}
