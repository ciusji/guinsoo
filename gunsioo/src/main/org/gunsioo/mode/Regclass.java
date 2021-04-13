/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.mode;

import org.gunsioo.api.ErrorCode;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.Operation1;
import org.gunsioo.expression.ValueExpression;
import org.gunsioo.index.Index;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.Schema;
import org.gunsioo.table.Table;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueInteger;
import org.gunsioo.value.ValueNull;

/**
 * A ::regclass expression.
 */
public final class Regclass extends Operation1 {

    public Regclass(Expression arg) {
        super(arg);
    }

    @Override
    public Value getValue(SessionLocal session) {
        Value value = arg.getValue(session);
        if (value == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        int valueType = value.getValueType();
        if (valueType >= Value.TINYINT && valueType <= Value.INTEGER) {
            return value.convertToInt(null);
        }
        if (valueType == Value.BIGINT) {
            return ValueInteger.get((int) value.getLong());
        }
        String name = value.getString();
        for (Schema schema : session.getDatabase().getAllSchemas()) {
            Table table = schema.findTableOrView(session, name);
            if (table != null && !table.isHidden()) {
                return ValueInteger.get(table.getId());
            }
            Index index = schema.findIndex(session, name);
            if (index != null && index.getCreateSQL() != null) {
                return ValueInteger.get(index.getId());
            }
        }
        throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, name);
    }

    @Override
    public TypeInfo getType() {
        return TypeInfo.TYPE_INTEGER;
    }

    @Override
    public Expression optimize(SessionLocal session) {
        arg = arg.optimize(session);
        if (arg.isConstant()) {
            return ValueExpression.get(getValue(session));
        }
        return this;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        return arg.getSQL(builder, sqlFlags, AUTO_PARENTHESES).append("::REGCLASS");
    }

    @Override
    public int getCost() {
        return arg.getCost() + 100;
    }

}
