/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.expression.function;

import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.expression.ExpressionVisitor;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Constant;
import org.guinsoo.schema.Domain;
import org.guinsoo.schema.FunctionAlias;
import org.guinsoo.schema.Schema;
import org.guinsoo.table.Column;
import org.guinsoo.table.Table;
import org.guinsoo.value.TypeInfo;
import org.guinsoo.value.Value;
import org.guinsoo.value.ValueNull;
import org.guinsoo.value.ValueToObjectConverter2;
import org.guinsoo.value.ValueVarchar;

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
            FunctionAlias.JavaMethod[] methods;
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
