/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.function;

import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.ExpressionVisitor;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.Constant;
import org.gunsioo.schema.Domain;
import org.gunsioo.schema.FunctionAlias;
import org.gunsioo.schema.Schema;
import org.gunsioo.schema.FunctionAlias.JavaMethod;
import org.gunsioo.table.Column;
import org.gunsioo.table.Table;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueNull;
import org.gunsioo.value.ValueToObjectConverter2;
import org.gunsioo.value.ValueVarchar;

/**
 * DATA_TYPE_SQL() function.
 */
public final class DataTypeSQLFunction extends FunctionN {

    public DataTypeSQLFunction(Expression objectSchema, Expression objectName, Expression objectType,
            Expression typeIdentifier) {
        super(new Expression[] { objectSchema, objectName, objectType, typeIdentifier });
    }

    @Override
    public Value getValue(SessionLocal session, Value v1, Value v2, Value v3) {
        Schema schema = session.getDatabase().findSchema(v1.getString());
        if (schema == null) {
            return ValueNull.INSTANCE;
        }
        String objectName = v2.getString();
        String objectType = v3.getString();
        String typeIdentifier = args[3].getValue(session).getString();
        if (typeIdentifier == null) {
            return ValueNull.INSTANCE;
        }
        TypeInfo t;
        switch (objectType) {
        case "CONSTANT": {
            Constant constant = schema.findConstant(objectName);
            if (constant == null || !typeIdentifier.equals("TYPE")) {
                return ValueNull.INSTANCE;
            }
            t = constant.getValue().getType();
            break;
        }
        case "DOMAIN": {
            Domain domain = schema.findDomain(objectName);
            if (domain == null || !typeIdentifier.equals("TYPE")) {
                return ValueNull.INSTANCE;
            }
            t = domain.getDataType();
            break;
        }
        case "ROUTINE": {
            int idx = objectName.lastIndexOf('_');
            if (idx < 0) {
                return ValueNull.INSTANCE;
            }
            FunctionAlias function = schema.findFunction(objectName.substring(0, idx));
            if (function == null) {
                return ValueNull.INSTANCE;
            }
            int ordinal;
            try {
                ordinal = Integer.parseInt(objectName.substring(idx + 1));
            } catch (NumberFormatException e) {
                return ValueNull.INSTANCE;
            }
            JavaMethod[] methods;
            try {
                methods = function.getJavaMethods();
            } catch (DbException e) {
                return ValueNull.INSTANCE;
            }
            if (ordinal < 1 || ordinal > methods.length) {
                return ValueNull.INSTANCE;
            }
            FunctionAlias.JavaMethod method = methods[ordinal - 1];
            if (typeIdentifier.equals("RESULT")) {
                t = method.getDataType();
            } else {
                try {
                    ordinal = Integer.parseInt(typeIdentifier);
                } catch (NumberFormatException e) {
                    return ValueNull.INSTANCE;
                }
                if (ordinal < 1) {
                    return ValueNull.INSTANCE;
                }
                if (!method.hasConnectionParam()) {
                    ordinal--;
                }
                Class<?>[] columnList = method.getColumnClasses();
                if (ordinal >= columnList.length) {
                    return ValueNull.INSTANCE;
                }
                t = ValueToObjectConverter2.classToType(columnList[ordinal]);
            }
            break;
        }
        case "TABLE": {
            Table table = schema.findTableOrView(session, objectName);
            if (table == null) {
                return ValueNull.INSTANCE;
            }
            int ordinal;
            try {
                ordinal = Integer.parseInt(typeIdentifier);
            } catch (NumberFormatException e) {
                return ValueNull.INSTANCE;
            }
            Column[] columns = table.getColumns();
            if (ordinal < 1 || ordinal > columns.length) {
                return ValueNull.INSTANCE;
            }
            t = columns[ordinal - 1].getType();
            break;
        }
        default:
            return ValueNull.INSTANCE;
        }
        return ValueVarchar.get(t.getSQL(DEFAULT_SQL_FLAGS));
    }

    @Override
    public Expression optimize(SessionLocal session) {
        optimizeArguments(session, false);
        type = TypeInfo.TYPE_VARCHAR;
        return this;
    }

    @Override
    public String getName() {
        return "DATA_TYPE_SQL";
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        return isEverythingNonDeterministic(visitor);
    }

}
