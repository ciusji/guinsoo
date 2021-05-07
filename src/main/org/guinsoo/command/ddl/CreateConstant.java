/*
 * Copyright 2004-2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.command.ddl;

import org.guinsoo.api.ErrorCode;
import org.guinsoo.command.CommandInterface;
import org.guinsoo.engine.Database;
import org.guinsoo.engine.SessionLocal;
import org.guinsoo.expression.Expression;
import org.guinsoo.message.DbException;
import org.guinsoo.schema.Constant;
import org.guinsoo.schema.Schema;
import org.guinsoo.value.Value;

/**
 * This class represents the statement
 * CREATE CONSTANT
 */
public class CreateConstant extends SchemaOwnerCommand {

    private String constantName;
    private Expression expression;
    private boolean ifNotExists;

    public CreateConstant(SessionLocal session, Schema schema) {
        super(session, schema);
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    @Override
    long update(Schema schema) {
        Database db = session.getDatabase();
        if (schema.findConstant(constantName) != null) {
            if (ifNotExists) {
                return 0;
            }
            throw DbException.get(ErrorCode.CONSTANT_ALREADY_EXISTS_1, constantName);
        }
        int id = getObjectId();
        Constant constant = new Constant(schema, id, constantName);
        expression = expression.optimize(session);
        Value value = expression.getValue(session);
        constant.setValue(value);
        db.addSchemaObject(session, constant);
        return 0;
    }

    public void setConstantName(String constantName) {
        this.constantName = constantName;
    }

    public void setExpression(Expression expr) {
        this.expression = expr;
    }

    @Override
    public int getType() {
        return CommandInterface.CREATE_CONSTANT;
    }

}
