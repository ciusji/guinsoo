/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import org.guinsoo.engine.Database;
import org.guinsoo.engine.DbObject;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Schema;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueInteger;
import org.guinsoo.value.ValueNull;
import org.guinsoo.value.ValueVarchar;

/**
 * DB_OBJECT_ID() and DB_OBJECT_SQL() functions.
 */
public final class DBObjectFunction extends FunctionN {

    /**
     * DB_OBJECT_ID() (non-standard).
     */
    public static final int DB_OBJECT_ID = 0;

    /**
     * DB_OBJECT_SQL() (non-standard).
     */
    public static final int DB_OBJECT_SQL = DB_OBJECT_ID + 1;

    private static final String[] NAMES = { //
            "DB_OBJECT_ID", "DB_OBJECT_SQL" //
    };

    private final int function;

    public DBObjectFunction(Expression objectType, Expression arg1, Expression arg2, int function) {
        super(arg2 == null ? new Expression[] { objectType, arg1, } : new Expression[] { objectType, arg1, arg2 });
        this.function = function;
    }

    @Override
    public Value getValue(SessionLocal session, Value v1, Value v2, Value v3) {
        session.getUser().checkAdmin();
        String objectType = v1.getString();
        DbObject object;
        if (v3 != null) {
            Schema schema = session.getDatabase().findSchema(v2.getString());
            if (schema == null) {
                return ValueNull.INSTANCE;
            }
            String objectName = v3.getString();
            switch (objectType) {
            case "CONSTANT":
                object = schema.findConstant(objectName);
                break;
            case "CONSTRAINT":
                object = schema.findConstraint(session, objectName);
                break;
            case "DOMAIN":
                object = schema.findDomain(objectName);
                break;
            case "INDEX":
                object = schema.findIndex(session, objectName);
                break;
            case "ROUTINE":
                object = schema.findFunctionOrAggregate(objectName);
                break;
            case "SEQUENCE":
                object = schema.findSequence(objectName);
                break;
            case "SYNONYM":
                object = schema.getSynonym(objectName);
                break;
            case "TABLE":
                object = schema.findTableOrView(session, objectName);
                break;
            case "TRIGGER":
                object = schema.findTrigger(objectName);
                break;
            default:
                return ValueNull.INSTANCE;
            }
        } else {
            String objectName = v2.getString();
            Database database = session.getDatabase();
            switch (objectType) {
            case "ROLE":
                object = database.findRole(objectName);
                break;
            case "SETTING":
                object = database.findSetting(objectName);
                break;
            case "SCHEMA":
                object = database.findSchema(objectName);
                break;
            case "USER":
                object = database.findUser(objectName);
                break;
            default:
                return ValueNull.INSTANCE;
            }
        }
        if (object == null) {
            return ValueNull.INSTANCE;
        }
        switch (function) {
        case DB_OBJECT_ID:
            return ValueInteger.get(object.getId());
        case DB_OBJECT_SQL:
            String sql = object.getCreateSQLForMeta();
            return sql != null ? ValueVarchar.get(sql, session) : ValueNull.INSTANCE;
        default:
            throw DbException.getInternalError("function=" + function);
        }
    }

    @Override
    public Expression optimize(SessionLocal session) {
        optimizeArguments(session, false);
        type = function == DB_OBJECT_ID ? TypeInfo.TYPE_INTEGER : TypeInfo.TYPE_VARCHAR;
        return this;
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        switch (visitor.getType()) {
        case ExpressionVisitor.DETERMINISTIC:
            return false;
        }
        return super.isEverything(visitor);
    }

    @Override
    public String getName() {
        return NAMES[function];
    }

}
