/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueJson;

/**
 * A format clause such as FORMAT JSON.
 */
public final class Format extends Operation1 {

    /**
     * Supported formats.
     */
    public enum FormatEnum {
        /**
         * JSON.
         */
        JSON;
    }

    private final FormatEnum format;

    public Format(Expression arg, FormatEnum format) {
        super(arg);
        this.format = format;
    }

    @Override
    public Value getValue(SessionLocal session) {
        return getValue(arg.getValue(session));
    }

    /**
     * Returns the value with applied format.
     *
     * @param value
     *            the value
     * @return the value with applied format
     */
    public Value getValue(Value value) {
        switch (value.getValueType()) {
        case Value.NULL:
            return ValueJson.NULL;
        case Value.VARCHAR:
        case Value.VARCHAR_IGNORECASE:
        case Value.CHAR:
        case Value.CLOB:
            return ValueJson.fromJson(value.getString());
        default:
            return value.convertTo(TypeInfo.TYPE_JSON);
        }
    }

    @Override
    public Expression optimize(SessionLocal session) {
        arg = arg.optimize(session);
        if (arg.isConstant()) {
            return ValueExpression.get(getValue(session));
        }
        if (arg instanceof Format && format == ((Format) arg).format) {
            return arg;
        }
        type = TypeInfo.TYPE_JSON;
        return this;
    }

    @Override
    public boolean isIdentity() {
        return arg.isIdentity();
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return arg.getSQL(builder, sqlFlags, AUTO_PARENTHESES).append(" FORMAT ").append(format.name());
    }

    @Override
    public int getNullable() {
        return arg.getNullable();
    }

    @Override
    public String getTableName() {
        return arg.getTableName();
    }

    @Override
    public String getColumnName(SessionLocal session, int columnIndex) {
        return arg.getColumnName(session, columnIndex);
    }

}
