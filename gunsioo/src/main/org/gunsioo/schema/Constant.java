/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.schema;

import org.gunsioo.engine.DbObject;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.ValueExpression;
import org.gunsioo.message.DbException;
import org.gunsioo.message.Trace;
import org.gunsioo.table.Table;
import org.gunsioo.value.Value;

/**
 * A user-defined constant as created by the SQL statement
 * CREATE CONSTANT
 */
public final class Constant extends SchemaObject {

    private Value value;
    private ValueExpression expression;

    public Constant(Schema schema, int id, String name) {
        super(schema, id, name, Trace.SCHEMA);
    }

    @Override
    public String getCreateSQLForCopy(Table table, String quotedName) {
        throw DbException.getInternalError(toString());
    }

    @Override
    public String getCreateSQL() {
        StringBuilder builder = new StringBuilder("CREATE CONSTANT ");
        getSQL(builder, DEFAULT_SQL_FLAGS).append(" VALUE ");
        return value.getSQL(builder, DEFAULT_SQL_FLAGS).toString();
    }

    @Override
    public int getType() {
        return DbObject.CONSTANT;
    }

    @Override
    public void removeChildrenAndResources(SessionLocal session) {
        database.removeMeta(session, getId());
        invalidate();
    }

    public void setValue(Value value) {
        this.value = value;
        expression = ValueExpression.get(value);
    }

    public ValueExpression getValue() {
        return expression;
    }

}
